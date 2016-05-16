package ar.fiuba.tdp2grupo6.ordertracker.business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SharedPrefDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;

public class ClienteBZ {
	private Context mContext;
	private WebDA mWeb;
    private SqlDA mSql;

	public ClienteBZ(Context context) {
		this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
	}

	public ClienteBZ(Context context, WebDA service, SqlDA dataBase) {
		this.mContext = context;
		this.mWeb = service;
		this.mSql = dataBase;
	}

	public ArrayList<Cliente> sincronizar() throws AutorizationException, BusinessException {
		ArrayList<Cliente> response = new ArrayList<Cliente>();
		try {

			AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
			ResponseObject responseDA = mWeb.getClientes(autenticacionBZ.getAutenticacion());

			if (responseDA.getData() != null) {
				try {
					//Parsea la nueva lista de clientes
					ArrayList<Cliente> clientesNuevos = new ArrayList<Cliente>();
					JSONArray data = new JSONArray(responseDA.getData());
					for (int i = 0; i < data.length(); i++) {
						JSONObject itemjson = data.getJSONObject(i);
						Cliente cliente = new Cliente(itemjson);
						clientesNuevos.add(cliente);
					}

					//Busca la nueva lista de clientes actuales
					ArrayList<Cliente> clientesActuales = mSql.clienteBuscar(0,"","");

					//Busca si se elimino algun cliente, si es asi lo elimina
					//Si no se elimino se actualiza los datos
					for (Cliente clienteActual: clientesActuales) {
						boolean encontrado = false;
						for (Cliente clienteNuevo: clientesNuevos) {
							if (clienteActual.id == clienteNuevo.id) {
								//Actualiza el cliente nuevo
								clienteNuevo.estado = clienteActual.estado;

								//Sale
								encontrado = true;
								break;
							}
						}

						//Si no lo encontro, lo borra
						if (!encontrado){
							eliminar(clienteActual.id);
						}
					}

					//Graba (Inserta o Actualiza segun corresponda)
					for (Cliente cliente: clientesNuevos) {
						grabar(cliente);
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

    public ArrayList<Cliente> listar() throws BusinessException {
        ArrayList<Cliente> response = new ArrayList<Cliente>();
        try {
            response = mSql.clienteBuscar(0, "", "");
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }

	public Cliente obtener(long id, String nombreCompleto) throws BusinessException {
		Cliente response = null;
		try {
			ArrayList<Cliente> lista = mSql.clienteBuscar(id, nombreCompleto, "");
			if (lista != null && lista.size() > 0)
				response = lista.get(0);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
		return response;
	}

	public Cliente validar(String validar) throws BusinessException {
		Cliente response = null;
		try {
			ArrayList<Cliente> lista = mSql.clienteBuscar(0, "", validar);
			if (lista != null && lista.size() > 0)
				response = lista.get(0);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
		return response;
	}

	private void grabar(Cliente cliente) throws BusinessException  {
		try {

			ArrayList<Cliente> clientesActuales = mSql.clienteBuscar(cliente.id,"","");
			if (clientesActuales != null && clientesActuales.size() > 0) {
				//Existe => Actualiza
				mSql.clienteActualizar(cliente);
			} else {
				//No Existe => Graba
				mSql.clienteGuardar(cliente);
			}
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	private void eliminar(long clientId) throws BusinessException {
		try {
			//TODO: Hay que eliminar el resto de las entidades relacionadas
			//mSql.visitaEliminar();
			//mSql.pedidoItemEliminar();
			//mSql.pedidoEliminar();
			//mSql.comentarioEliminar();
			mSql.clienteEliminar(clientId);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}
}