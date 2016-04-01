package ar.fiuba.tdp2grupo6.ordertracker.business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
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

	public ArrayList<Cliente> sincronizar() throws ServiceException, BusinessException {
		ArrayList<Cliente> response = new ArrayList<Cliente>();
		try {

			ResponseObject responseDA = mWeb.getClientes();

			if (responseDA.getData() != null) {
                //Elimina todos los clientes
                mSql.clienteVaciar();

				//Graba cada cliente en la BD
				try {
					JSONArray data = new JSONArray(responseDA.getData());
					for (int i = 0; i < data.length(); i++) {
                        JSONObject itemjson = data.getJSONObject(i);

                        Cliente cliente = new Cliente(itemjson);
                        mSql.clienteGuardar(cliente);

                        response.add(cliente);
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

    public ArrayList<Cliente> listar() throws BusinessException {
        ArrayList<Cliente> response = new ArrayList<Cliente>();
        try {
            response = mSql.clienteBuscar(0);
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }
}