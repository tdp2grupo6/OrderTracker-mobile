package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;
import android.content.SharedPreferences;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionRequest;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SharedPrefDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 30-03-16.
 */
public class AutenticacionBZ {
    private Context mContext;
    private WebDA mWeb;
    private SqlDA mSql;

    public AutenticacionBZ(Context context) {
        this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
    }

    public AutenticacionBZ(Context context, WebDA service) {
        this.mContext = context;
        this.mWeb = service;
    }

    public AutenticacionBZ(Context context, SqlDA dataBase) {
        this.mContext = context;
        this.mSql = dataBase;
    }

    public AutenticacionBZ(Context context, WebDA service, SqlDA dataBase) {
        this.mContext = context;
        this.mWeb = service;
        this.mSql = dataBase;
    }

    public AutenticacionResponse login(String user, String password) throws ServiceException, BusinessException {
        AutenticacionResponse response = null;
        try {

            AutenticacionRequest autenticacion = new AutenticacionRequest();
            autenticacion = new AutenticacionRequest(user, password);
            ResponseObject responseDA = mWeb.sendAutenticar(autenticacion);

            if (responseDA.getData() != null) {

                //Graba cada item de la agenda en la BD
                try {
                    response = new AutenticacionResponse(responseDA.getData());

                    SharedPrefDA sharedPrefDA = new SharedPrefDA(mContext);
                    sharedPrefDA.setToken(responseDA.getData());
                } catch (Exception jex) {
                    throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), jex.getMessage()));
                }
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return response;
    }

    public void logout() {
        try {
            SharedPrefDA sharedPrefDA = new SharedPrefDA(mContext);
            sharedPrefDA.deleteToken();
        } catch (Exception e) {

        }
    }

    public AutenticacionResponse getAutenticacion() {
        AutenticacionResponse autenticacionResponse = null;
        try {
            SharedPrefDA sharedPrefDA = new SharedPrefDA(mContext);
            if (sharedPrefDA.getToken() != null) {
                autenticacionResponse = new AutenticacionResponse(sharedPrefDA.getToken());
            }
        } catch (Exception e) {

        }
        return autenticacionResponse;
    }
}
