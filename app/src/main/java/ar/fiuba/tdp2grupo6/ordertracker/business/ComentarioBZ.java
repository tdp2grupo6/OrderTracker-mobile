package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.LocalException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 23-04-16.
 */
public class ComentarioBZ {
	private Context mContext;
	private WebDA mWeb;
	private SqlDA mSql;

	public ComentarioBZ(Context context) {
		this.mContext = context;
		this.mWeb = new WebDA(context);
		this.mSql = new SqlDA(context);
	}

	public ComentarioBZ(Context context, WebDA service) {
		this.mContext = context;
		this.mWeb = service;
	}

	public ComentarioBZ(Context context, SqlDA dataBase) {
		this.mContext = context;
		this.mSql = dataBase;
	}

	public ComentarioBZ(Context context, WebDA service, SqlDA dataBase) {
		this.mContext = context;
		this.mWeb = service;
		this.mSql = dataBase;
	}

	public void sincronizar() throws AutorizationException, BusinessException {
		try {
			ArrayList<Comentario> listaPendientes = mSql.comentarioObtenerNoEnviado();

			for (Comentario c: listaPendientes) {
				enviarComentario(c);
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public void enviarComentario(Comentario comentario) throws AutorizationException, BusinessException {
		try {
			if (comentario.enviado == false) {

				AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
				ResponseObject response = mWeb.sendComentario(autenticacionBZ.getAutenticacion(), comentario);
				if (response.getData() != null) {
					//Actualiza el estado del comentario
					mSql.comentarioCambiarEstadoEnviado(comentario, true);
				}
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public Comentario guardarComentario(Comentario comentario) throws BusinessException {		// OK
		try {
			return mSql.comentarioGuardar(comentario);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public void actualizarComentario(Comentario comm) {
		try {
			if (comm.id == 0) {
				comm = mSql.comentarioGuardar(comm);
			} else {
				mSql.comentarioActualizar(comm);
			}
		} catch (LocalException e) {
			e.printStackTrace();
		}

	}
}
