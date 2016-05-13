package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Visita;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.LocalException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

public class VisitaBZ {
	private Context mContext;
	private WebDA mWeb;
	private SqlDA mSql;

	public VisitaBZ(Context context) {
		this.mContext = context;
		this.mWeb = new WebDA(context);
		this.mSql = new SqlDA(context);
	}

	public VisitaBZ(Context context, WebDA service) {
		this.mContext = context;
		this.mWeb = service;
	}

	public VisitaBZ(Context context, SqlDA dataBase) {
		this.mContext = context;
		this.mSql = dataBase;
	}

	public VisitaBZ(Context context, WebDA service, SqlDA dataBase) {
		this.mContext = context;
		this.mWeb = service;
		this.mSql = dataBase;
	}

	public void sincronizar() throws AutorizationException, BusinessException {
		try {
			ArrayList<Visita> listaVisita = mSql.visitaBuscar(0,0,null,false);

			for (Visita visita: listaVisita) {
				enviarVisita(visita);
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public void enviarVisita(Visita visita) throws AutorizationException, BusinessException {
		try {
			if (visita.enviado == false) {

				AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
				ResponseObject response = mWeb.sendVisita(autenticacionBZ.getAutenticacion(), visita);
				if (response.getData() != null) {
					visita.serverId = visita.procesarRespuesta(response.getData());
					visita.enviado = true;

					//Actualiza el estado del visita
					mSql.visitaActualizar(visita);
				}
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public Visita guardarVisita(Visita visita) throws BusinessException {
		try {
			return mSql.visitaGuardar(visita);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public void actualizarVisita(Visita visita) {
		try {
			if (visita.id == 0) {
				visita = mSql.visitaGuardar(visita);
			} else {
				mSql.visitaActualizar(visita);
			}
		} catch (LocalException e) {
			e.printStackTrace();
		}

	}
}
