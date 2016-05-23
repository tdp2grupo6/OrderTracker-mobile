package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Push;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Visita;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.LocalException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SharedPrefDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

public class PushBZ {
	private Context mContext;
	private WebDA mWeb;
	private SqlDA mSql;

	public PushBZ(Context context) {
		this.mContext = context;
		this.mWeb = new WebDA(context);
		this.mSql = new SqlDA(context);
	}

	public PushBZ(Context context, WebDA service) {
		this.mContext = context;
		this.mWeb = service;
	}

	public PushBZ(Context context, SqlDA dataBase) {
		this.mContext = context;
		this.mSql = dataBase;
	}

	public PushBZ(Context context, WebDA service, SqlDA dataBase) {
		this.mContext = context;
		this.mWeb = service;
		this.mSql = dataBase;
	}

	public void enviarPushToken(String pushToken) throws AutorizationException, BusinessException {
		try {
			if (pushToken.length() > 0) {
				SharedPrefDA.deleteFirebase(mContext);
				SharedPrefDA.setFirebase(mContext, pushToken);

				AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
				AutenticacionResponse autenticacion = autenticacionBZ.getAutenticacion();
				if (autenticacion != null) {
					Push push = new Push(autenticacion.username, pushToken);
					ResponseObject response = mWeb.sendPushToken(autenticacion, push);
				}
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public void reEnviarPushToken(AutenticacionResponse autenticacion) throws AutorizationException, BusinessException {
		try {
			String pushToken = SharedPrefDA.getFirebase(mContext);
			if (pushToken.length() > 0) {
				//Si no tiene el objeto de autenticacion, lo busca
				if (autenticacion == null) {
					AutenticacionBZ autenticacionBZ = new AutenticacionBZ(mContext);
					autenticacion = autenticacionBZ.getAutenticacion();
				}

				//Envia el objeto al servidor
				if (autenticacion != null) {
					Push push = new Push(autenticacion.username, pushToken);
					ResponseObject response = mWeb.sendPushToken(autenticacion, push);
				}
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

}
