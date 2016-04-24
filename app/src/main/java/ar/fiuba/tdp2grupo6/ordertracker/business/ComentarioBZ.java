package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
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

	public ArrayList<Comentario> buscar(long clienteId) throws BusinessException {
		ArrayList<Comentario> comentarios = null;
		try {
			comentarios = mSql.comentarioBuscar(0, clienteId);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return comentarios;
	}
}
