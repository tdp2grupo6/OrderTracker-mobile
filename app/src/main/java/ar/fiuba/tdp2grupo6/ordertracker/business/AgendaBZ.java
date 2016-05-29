package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Agenda;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Visita;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

public class AgendaBZ {
    private Context mContext;
    private WebDA mWeb;
    private SqlDA mSql;

    public AgendaBZ(Context context) {
        this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
    }

    public AgendaBZ(Context context, WebDA service) {
        this.mContext = context;
        this.mWeb = service;
    }

    public AgendaBZ(Context context, SqlDA dataBase) {
        this.mContext = context;
        this.mSql = dataBase;
    }

    public AgendaBZ(Context context, WebDA service, SqlDA dataBase) {
        this.mContext = context;
        this.mWeb = service;
        this.mSql = dataBase;
    }

    public Agenda sincronizar(boolean loadCliente) throws AutorizationException, BusinessException {
        Agenda response = new Agenda();
        try {

            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject responseDA = mWeb.getAgenda(autenticacionBZ.getAutenticacion());

            if (responseDA.getData() != null) {
                //Elimina toda la agenda
                mSql.agendaItemVaciar();

                //Graba cada item de la agenda en la BD
                try {
                    JSONArray data = new JSONArray(responseDA.getData());
                    for (int i = 0; i < data.length(); i++) {
                        //[{"codigoDia":0,"listaClientes":[]},{"codigoDia":1,"listaClientes":[1,4]},
                        // {"codigoDia":2,"listaClientes":[2,3,4,5]},{"codigoDia":3,"listaClientes":[1,3,4,6]},
                        // {"codigoDia":4,"listaClientes":[2,3,5,6]},{"codigoDia":5,"listaClientes":[1,2,6]},
                        // {"codigoDia":6,"listaClientes":[5]}]

                        //Parsea los datos de dia de la semana
                        JSONObject itemjson = data.getJSONObject(i);
                        int codigoDia = itemjson.getInt("codigoDia");
                        JSONArray listaClientes = itemjson.getJSONArray("listaClientes");

                        for (int j = 0; j < listaClientes.length(); j++) {
                            AgendaItem agendaItem = new AgendaItem(codigoDia, listaClientes.getLong(j));
                            if (loadCliente) {
                                ArrayList<Cliente> clientes = mSql.clienteBuscar(agendaItem.clienteId, "", "");
                                if (clientes != null && clientes.size() > 0)
                                    agendaItem.cliente = clientes.get(0);
                            }
                            response.addAgendaItem(agendaItem);

                            //persiste en la BD
                            mSql.agendaItemGuardar(agendaItem);
                        }
                    }
                } catch (JSONException jex) {
                    throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), jex.getMessage()));
                }
            }
        } catch (AutorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return response;
    }

    public Agenda obtener(boolean loadCliente) throws BusinessException {
        Agenda agenda = new Agenda();
        try {
            ArrayList<AgendaItem> lista = mSql.agendaItemBuscar(0, -1, 0, loadCliente);
            if (lista != null && lista.size() > 0){
                //Como es la primer pantalla que se abre, puede ser que tenga la agena pero no
                //todos los cientes descargados, entonces cuelga.
                //Solucion de compromiso, solo muestra la agenda para los cuales tenga clientes
                for (AgendaItem agendaItem: lista) {
                    if ((loadCliente ==  true && agendaItem.cliente != null) ||
                        (loadCliente ==  false && agendaItem.cliente == null)){
                        agenda.addAgendaItem(agendaItem);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return agenda;
    }


    public void actualizarEstados() throws BusinessException {
        try {
            //Verifica si hay visitas cargadas para el dia de la fecha
            Date fechaActual = new Date();
            ArrayList<Visita> visitas = mSql.visitaBuscar(0, 0, fechaActual, null, false);

            if (visitas == null || visitas.size() == 0) {
                //Agrega visita falsa, es una marca para que no lo haga siempre esta actualizacion
                Visita visitaMarca = new Visita(-1, fechaActual);
                visitaMarca.tieneComentario = true;
                visitaMarca.tienePedido = true;
                visitaMarca.enviado = true;
                mSql.visitaGuardar(visitaMarca);


                //Unicamente si no se trabajo para el dia actual se actualizan los clientes
                int diaActualId = AgendaBZ.getCurrentDay();
                ArrayList<AgendaItem> lista = mSql.agendaItemBuscar(0, -1, 0, true);
                if (lista != null && lista.size() > 0){
                    for (AgendaItem agendaItem: lista) {
                        Cliente cliente = agendaItem.cliente;
                        if (agendaItem.diaId == diaActualId) {
                            //Si es el dia actual, lo marca como pendiente
                            cliente.estado = Cliente.ESTADO_PENDIENTE;
                            mSql.clienteActualizar(cliente);
                        } else if (agendaItem.diaId != diaActualId &&
                                cliente.estado == Cliente.ESTADO_PENDIENTE) {
                            //Si no es el dia actual y esta pendiente, lo marca como no visitado
                            cliente.estado = Cliente.ESTADO_NO_VISITADO;
                            mSql.clienteActualizar(cliente);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return currentDay;
    }

}
