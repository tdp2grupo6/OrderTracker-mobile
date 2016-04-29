package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 30-03-16.
 */
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

    public Agenda sincronizar(boolean loadCliente) throws ServiceException, BusinessException {
        Agenda response = new Agenda();
        try {

            ResponseObject responseDA = mWeb.getAgenda();

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
                            AgendaItem agendaItem = new AgendaItem(codigoDia, data.getLong(i));
                            if (loadCliente) {
                                ArrayList<Cliente> clientes = mSql.clienteBuscar(agendaItem.clienteId, "");
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

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return response;
    }

    public Agenda obtener(boolean loadCliente) throws BusinessException {
        Agenda agenda = new Agenda();
        try {
            ArrayList<AgendaItem> lista = mSql.agendaItemBuscar(0, loadCliente);
            if (lista != null && lista.size() > 0){
                for (AgendaItem agendaItem: lista) {
                    agenda.addAgendaItem(agendaItem);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return agenda;
    }

}
