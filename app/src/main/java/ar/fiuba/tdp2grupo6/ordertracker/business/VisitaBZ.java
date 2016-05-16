package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
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
			ArrayList<Visita> listaVisita = mSql.visitaBuscar(0, 0, null, false, false);

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

	public void modificarEstado(long visitaId, boolean tieneComentario, boolean tienePedido) throws BusinessException {
		try {
			ArrayList<Visita> visitas = mSql.visitaBuscar(visitaId, 0, null, null, false);
			if (visitas != null && visitas.size() > 0) {
				Visita visita = visitas.get(0);
				visita.tieneComentario = tieneComentario;
				visita.tienePedido = tienePedido;
				mSql.visitaActualizar(visita);
			}
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

	}

	public Visita obtener(long clienteId, Date date) throws BusinessException {
		Visita visita = null;
		try {

			ArrayList<Visita> listaVisita = mSql.visitaBuscar(0, clienteId, date, null, false);

			if (listaVisita != null && listaVisita.size() > 0)
				visita = listaVisita.get(0);
		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return visita;
	}

	public Visita obtenerPendiente(long excluirClienteId, Date date) throws BusinessException {
		Visita visitaPendiente = null;
		try {

			ArrayList<Visita> listaVisita = mSql.visitaBuscar(0, 0, date, null, true);

			if (listaVisita != null && listaVisita.size() > 0) {
				for (Visita visita: listaVisita) {
					if (visita.tieneComentario == false &&
						visita.tienePedido ==  false &&
						excluirClienteId !=  visita.clienteId) {

						visitaPendiente = visita;
						break;
					}
				}
			}

		} catch (Exception e) {
			throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return visitaPendiente;
	}

	public Visita generarVisita(long clienteId, String validador) throws AutorizationException, BusinessException {
		Visita visita = null;
		try {
			Date date = new Date();

			//busca si hay un cliente con ese validador
			ArrayList<Cliente> clientes = mSql.clienteBuscar(0, "", validador);
			if (clientes != null && clientes.size() > 0) {

				// Encontro al menos un cliente que tiene ese codigo
				Cliente clienteValidado = clientes.get(0);

				//Compara si ese cliente es el que nos informan
				if (clienteId == clienteValidado.id) {
					//Verifica que se abran clientes del dia actual
					int diaActualId = AgendaBZ.getCurrentDay();
					ArrayList<AgendaItem> agendaItems = mSql.agendaItemBuscar(0, diaActualId, clienteId, false);
					if (agendaItems!= null && agendaItems.size() > 0) {

						//verifica que no haya otra visita para ese cliente en el dia
						visita = obtener(clienteId, date);
						if (visita == null) {
							//Genera la visita y la graba
							visita = new Visita(clienteId, date);
							visita.tienePedido = false;
							visita.tieneComentario = false;
							visita.enviado = false;
							mSql.visitaGuardar(visita);

							//Intenta el envio de la visita al servidor
							enviarVisita(visita);

							//Cambia el estado del cliente
							clienteValidado.estado = Cliente.ESTADO_VISITADO;
							mSql.clienteActualizar(clienteValidado);
						}
					}
				}
			}

			return visita;
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
