package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Agenda;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Categoria;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Utils;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Visita;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.LocalException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db.DbHelper;

import static ar.fiuba.tdp2grupo6.ordertracker.dataaccess.UtilsDA.DateToLong;
import static ar.fiuba.tdp2grupo6.ordertracker.dataaccess.UtilsDA.LongToDate;


public class SqlDA {

	private Context mContext;
	private DbHelper mDb;

	public SqlDA(Context mContext) {
		this.mContext = mContext;
		this.mDb = DbHelper.getInstance(mContext);
	}

	public SqlDA(Context mContext, DbHelper db) {
		this.mContext = mContext;
		this.mDb = db;
	}


	/******************************************************************************************************/
	// Agenda

	public AgendaItem agendaItemGuardar(AgendaItem agendaItem) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblAgendaItem_colDiaId, agendaItem.diaId);
			cv.put(DbHelper.tblAgendaItem_colClienteId, agendaItem.clienteId);

			agendaItem.id = db.insert(DbHelper.tblAgendaItem, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return agendaItem;
	}

	public long agendaItemActualizar(AgendaItem agendaItem) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblAgendaItem_colDiaId, agendaItem.diaId);
			cv.put(DbHelper.tblAgendaItem_colClienteId, agendaItem.clienteId);

			String where = "";
			if (agendaItem != null) {
				String condition = DbHelper.tblAgendaItem_colId + "=" + String.valueOf(agendaItem.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblAgendaItem, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<AgendaItem> agendaItemBuscar(long id, int diaId, long clienteId, boolean loadCliente) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<AgendaItem> listAgendaItem = new ArrayList<AgendaItem>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblAgendaItem;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblAgendaItem_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (diaId >= 0) {
				String condition = DbHelper.tblAgendaItem_colDiaId + "=" + String.valueOf(diaId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (clienteId > 0) {
				String condition = DbHelper.tblAgendaItem_colClienteId + "=" + String.valueOf(clienteId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

            String order = " order by diaId asc, id asc";

			Cursor c = db.rawQuery(select + where + order, null);
			if (c.moveToFirst()) {
				do {
					AgendaItem agendaItem = new AgendaItem();
					agendaItem.id = c.getLong(c.getColumnIndex(DbHelper.tblAgendaItem_colId));
					agendaItem.diaId = c.getInt(c.getColumnIndex(DbHelper.tblAgendaItem_colDiaId));
					agendaItem.clienteId = c.getLong(c.getColumnIndex(DbHelper.tblAgendaItem_colClienteId));

                    if (loadCliente) {
                        ArrayList<Cliente> clientes = this.clienteBuscar(agendaItem.clienteId, "", "");
                        if (clientes != null && clientes.size() > 0)
                            agendaItem.cliente = clientes.get(0);
                    }

					listAgendaItem.add(agendaItem);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listAgendaItem;
	}

	public long agendaItemEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblAgendaItem_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblAgendaItem, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public void agendaItemVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblAgendaItem, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}
	
	/******************************************************************************************************/
	// Cliente

	public Cliente clienteGuardar(Cliente cliente) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblCliente_colId, cliente.id);
			cv.put(DbHelper.tblCliente_colNombreCompleto, cliente.nombreCompleto);
			cv.put(DbHelper.tblCliente_colNombre, cliente.nombre);
			cv.put(DbHelper.tblCliente_colApellido, cliente.apellido);
			cv.put(DbHelper.tblCliente_colRazonSocial, cliente.razonSocial);
			cv.put(DbHelper.tblCliente_colDireccion, cliente.direccion);
			cv.put(DbHelper.tblCliente_colTelefono, cliente.telefono);
			cv.put(DbHelper.tblCliente_colEmail, cliente.email);
			cv.put(DbHelper.tblCliente_colLat, cliente.lat);
			cv.put(DbHelper.tblCliente_colLng, cliente.lng);
			cv.put(DbHelper.tblCliente_colDisponibilidad, cliente.disponibilidad);
			cv.put(DbHelper.tblCliente_colAgendaCliente, cliente.agendaCliente.toString());
			cv.put(DbHelper.tblCliente_colEstadoServidor, cliente.estadoServidor.toString());
			cv.put(DbHelper.tblCliente_colValidador, cliente.validador);
			cv.put(DbHelper.tblCliente_colEstado, cliente.estado);

            cliente.id = db.insert(DbHelper.tblCliente, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cliente;
	}

	public long clienteActualizar(Cliente cliente) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblCliente_colNombreCompleto, cliente.nombreCompleto);
			cv.put(DbHelper.tblCliente_colNombre, cliente.nombre);
			cv.put(DbHelper.tblCliente_colApellido, cliente.apellido);
			cv.put(DbHelper.tblCliente_colRazonSocial, cliente.razonSocial);
			cv.put(DbHelper.tblCliente_colDireccion, cliente.direccion);
			cv.put(DbHelper.tblCliente_colTelefono, cliente.telefono);
			cv.put(DbHelper.tblCliente_colEmail, cliente.email);
			cv.put(DbHelper.tblCliente_colLat, cliente.lat);
			cv.put(DbHelper.tblCliente_colLng, cliente.lng);
			cv.put(DbHelper.tblCliente_colDisponibilidad, cliente.disponibilidad);
			cv.put(DbHelper.tblCliente_colAgendaCliente, cliente.agendaCliente.toString());
			cv.put(DbHelper.tblCliente_colEstadoServidor, cliente.estadoServidor.toString());
			cv.put(DbHelper.tblCliente_colValidador, cliente.validador);
			cv.put(DbHelper.tblCliente_colEstado, cliente.estado);

			String where = "";
			if (cliente != null) {
				String condition = DbHelper.tblCliente_colId + "=" + String.valueOf(cliente.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblCliente, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<Cliente> clienteBuscar(long id, String nombreCompleto, String validador) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Cliente> listCliente = new ArrayList<Cliente>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblCliente;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblCliente_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (nombreCompleto != null && nombreCompleto.trim().length() > 0) {
				String condition = "lower(" + DbHelper.tblCliente_colNombreCompleto + ")='" + nombreCompleto.toLowerCase() + "'";
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (validador != null && validador.trim().length() > 0) {
				String condition = "lower(" + DbHelper.tblCliente_colValidador + ")='" + validador.toLowerCase() + "'";
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}


			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Cliente cliente = new Cliente();
					cliente.id = c.getLong(c.getColumnIndex(DbHelper.tblCliente_colId));
					cliente.nombreCompleto = c.getString(c.getColumnIndex(DbHelper.tblCliente_colNombreCompleto));
					cliente.nombre = c.getString(c.getColumnIndex(DbHelper.tblCliente_colNombre));
					cliente.apellido = c.getString(c.getColumnIndex(DbHelper.tblCliente_colApellido));
					cliente.razonSocial = c.getString(c.getColumnIndex(DbHelper.tblCliente_colRazonSocial));
					cliente.direccion = c.getString(c.getColumnIndex(DbHelper.tblCliente_colDireccion));
					cliente.telefono = c.getString(c.getColumnIndex(DbHelper.tblCliente_colTelefono));
					cliente.email = c.getString(c.getColumnIndex(DbHelper.tblCliente_colEmail));
					cliente.lat = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLat));
					cliente.lng = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLng));
					cliente.disponibilidad = c.getString(c.getColumnIndex(DbHelper.tblCliente_colDisponibilidad));
					cliente.validador = c.getString(c.getColumnIndex(DbHelper.tblCliente_colValidador));
					cliente.agendaCliente = new JSONArray(c.getString(c.getColumnIndex(DbHelper.tblCliente_colAgendaCliente)));
					cliente.estadoServidor = new JSONObject(c.getString(c.getColumnIndex(DbHelper.tblCliente_colEstadoServidor)));
					cliente.estado = c.getInt(c.getColumnIndex(DbHelper.tblCliente_colEstado));
					listCliente.add(cliente);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listCliente;
	}

	public long clienteEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblCliente_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblCliente, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public void clienteVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblCliente, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	/******************************************************************************************************/
	// Producto

	public Producto productoGuardar(Producto producto) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblProducto_colId, producto.id);
			cv.put(DbHelper.tblProducto_colNombre, producto.nombre);
			cv.put(DbHelper.tblProducto_colMarca, producto.marca);
			cv.put(DbHelper.tblProducto_colCaracteristicas, producto.caracteristicas);
			cv.put(DbHelper.tblProducto_colCategoria, producto.categoria.toString());
			cv.put(DbHelper.tblProducto_colPrecio, producto.precio);
			cv.put(DbHelper.tblProducto_colStock, producto.stock);
			cv.put(DbHelper.tblProducto_colEstado, producto.estado);
			cv.put(DbHelper.tblProducto_colRutaImg, producto.rutaImagen);
			cv.put(DbHelper.tblProducto_colRutaMini, producto.rutaMiniatura);
            producto.id = db.insert(DbHelper.tblProducto, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return producto;
	}

	public long productoActualizar(Producto producto) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblProducto_colNombre, producto.nombre);
			cv.put(DbHelper.tblProducto_colMarca, producto.marca);
			cv.put(DbHelper.tblProducto_colCaracteristicas, producto.caracteristicas);
			cv.put(DbHelper.tblProducto_colCategoria, producto.categoria.toString());
			cv.put(DbHelper.tblProducto_colPrecio, producto.precio);
			cv.put(DbHelper.tblProducto_colStock, producto.stock);
			cv.put(DbHelper.tblProducto_colEstado, producto.estado);
			cv.put(DbHelper.tblProducto_colRutaImg, producto.rutaImagen);
			cv.put(DbHelper.tblProducto_colRutaMini, producto.rutaMiniatura);

			String where = "";
			if (producto != null) {
				String condition = DbHelper.tblProducto_colId + "=" + String.valueOf(producto.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblProducto, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<Producto> productoBuscar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Producto> listProducto = new ArrayList<Producto>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblProducto;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblProducto_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Producto producto = new Producto();
					producto.id = c.getLong(c.getColumnIndex(DbHelper.tblProducto_colId));
					producto.nombre = c.getString(c.getColumnIndex(DbHelper.tblProducto_colNombre));
					producto.marca = c.getString(c.getColumnIndex(DbHelper.tblProducto_colMarca));
					producto.caracteristicas = c.getString(c.getColumnIndex(DbHelper.tblProducto_colCaracteristicas));
					producto.categoria = new Categoria(c.getString(c.getColumnIndex(DbHelper.tblProducto_colCategoria)));
					producto.precio = c.getDouble(c.getColumnIndex(DbHelper.tblProducto_colPrecio));
					producto.stock = c.getInt(c.getColumnIndex(DbHelper.tblProducto_colStock));
					producto.estado = c.getString(c.getColumnIndex(DbHelper.tblProducto_colEstado));
					producto.rutaImagen = c.getString(c.getColumnIndex(DbHelper.tblProducto_colRutaImg));
					producto.rutaMiniatura = c.getString(c.getColumnIndex(DbHelper.tblProducto_colRutaMini));
					listProducto.add(producto);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listProducto;
	}

	public long productoEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblProducto_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblProducto, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public void productoVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblProducto, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	/******************************************************************************************************/
	// Visita

	public Visita visitaGuardar(Visita visita) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblVisita_colServerId, visita.serverId);
			cv.put(DbHelper.tblVisita_colClienteId, visita.clienteId);
			cv.put(DbHelper.tblVisita_colTieneComentario, UtilsDA.BooleanToInt(visita.tieneComentario));
			cv.put(DbHelper.tblVisita_colTienePedido, UtilsDA.BooleanToInt(visita.tienePedido));
			cv.put(DbHelper.tblVisita_colFecha, Utils.date2string(visita.fecha, false));
			cv.put(DbHelper.tblVisita_colEnviado, String.valueOf(visita.enviado));

			visita.id = db.insert(DbHelper.tblVisita, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return visita;
	}

	public long visitaActualizar(Visita visita) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblVisita_colServerId, visita.serverId);
			cv.put(DbHelper.tblVisita_colClienteId, visita.clienteId);
			cv.put(DbHelper.tblVisita_colTieneComentario, UtilsDA.BooleanToInt(visita.tieneComentario));
			cv.put(DbHelper.tblVisita_colTienePedido, UtilsDA.BooleanToInt(visita.tienePedido));
			cv.put(DbHelper.tblVisita_colFecha, Utils.date2string(visita.fecha, false));
			cv.put(DbHelper.tblVisita_colEnviado, String.valueOf(visita.enviado));

			String where = "";
			if (visita != null) {
				String condition = DbHelper.tblVisita_colId + "=" + String.valueOf(visita.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblVisita, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<Visita> visitaBuscar(long visitaId, long clienteId, Date fecha, Boolean enviado, boolean loadCliente) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Visita> listVisita = new ArrayList<Visita>();
		try {

			String select = "SELECT *, substr(" + DbHelper.tblVisita_colFecha + ",1,10) FROM " + DbHelper.tblVisita;

			String where = "";
			if (visitaId > 0) {
				String condition = DbHelper.tblVisita_colId + "=" + String.valueOf(visitaId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (clienteId > 0) {
				String condition = DbHelper.tblVisita_colClienteId + "=" + String.valueOf(clienteId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (fecha != null) {
				String fechaSinTiempo = Utils.date2string(fecha, true);
				String condition = "substr(" + DbHelper.tblVisita_colFecha + ",1,10)='" + fechaSinTiempo + "'";
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (enviado != null) {
				String condition = DbHelper.tblVisita_colEnviado + "='" + String.valueOf(enviado) + "'";
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Visita visita = new Visita();
					visita.id = c.getLong(c.getColumnIndex(DbHelper.tblVisita_colId));
					visita.serverId = c.getLong(c.getColumnIndex(DbHelper.tblVisita_colServerId));
					visita.clienteId = c.getInt(c.getColumnIndex(DbHelper.tblVisita_colClienteId));
					visita.tienePedido = UtilsDA.IntToBoolean(c.getInt(c.getColumnIndex(DbHelper.tblVisita_colTienePedido)));
					visita.tieneComentario = UtilsDA.IntToBoolean(c.getInt(c.getColumnIndex(DbHelper.tblVisita_colTieneComentario)));
					visita.fecha = Utils.string2date(c.getString(c.getColumnIndex(DbHelper.tblVisita_colFecha)));
					visita.enviado = Utils.string2boolean(c.getString(c.getColumnIndex(DbHelper.tblVisita_colEnviado)));

					if (loadCliente) {
						ArrayList<Cliente> clientes = this.clienteBuscar(visita.clienteId, "", "");
						if (clientes != null && clientes.size() > 0)
							visita.cliente = clientes.get(0);
					}

					listVisita.add(visita);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listVisita;
	}

	public long visitaEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblVisita_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblVisita, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}


	public void visitaVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblVisita, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}
	
	/******************************************************************************************************/
	// Pedido

	public Pedido pedidoGuardar(Pedido pedido) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			//cv.put(DbHelper.tblPedido_colId, pedido.id);
			cv.put(DbHelper.tblPedido_colIdServer, pedido.idServer);
			cv.put(DbHelper.tblPedido_colVisitaId, pedido.visitaId);
			cv.put(DbHelper.tblPedido_colClienteId, pedido.clienteId);
			cv.put(DbHelper.tblPedido_colEstado, pedido.estado);
			cv.put(DbHelper.tblPedido_colFecha, Utils.date2string(pedido.fechaRealizado, false));
            pedido.id = db.insert(DbHelper.tblPedido, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return pedido;
	}

	public long pedidoActualizar(Pedido pedido) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblPedido_colIdServer, pedido.idServer);
			cv.put(DbHelper.tblPedido_colVisitaId, pedido.visitaId);
			cv.put(DbHelper.tblPedido_colClienteId, pedido.clienteId);
			cv.put(DbHelper.tblPedido_colEstado, pedido.estado);
			cv.put(DbHelper.tblPedido_colFecha, Utils.date2string(pedido.fechaRealizado, false));

			String where = "";
			if (pedido != null) {
				String condition = DbHelper.tblPedido_colId + "=" + String.valueOf(pedido.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblPedido, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<Pedido> pedidoBuscar(long id, long idServer, long clienteId, int estado) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Pedido> listPedido = new ArrayList<Pedido>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblPedido;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblPedido_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (idServer > 0) {
				String condition = DbHelper.tblPedido_colIdServer + "=" + String.valueOf(idServer);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (clienteId > 0) {
				String condition = DbHelper.tblPedido_colClienteId + "=" + String.valueOf(clienteId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			if (estado >= 0) {
				String condition = DbHelper.tblPedido_colEstado + "=" + String.valueOf(estado);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Pedido pedido = new Pedido();
					pedido.id = c.getLong(c.getColumnIndex(DbHelper.tblPedido_colId));
					pedido.idServer = c.getLong(c.getColumnIndex(DbHelper.tblPedido_colIdServer));
					pedido.visitaId = c.getLong(c.getColumnIndex(DbHelper.tblPedido_colVisitaId));
					pedido.clienteId = c.getLong(c.getColumnIndex(DbHelper.tblPedido_colClienteId));
					pedido.estado = c.getShort(c.getColumnIndex(DbHelper.tblPedido_colEstado));
					pedido.fechaRealizado = Utils.string2date(c.getString(c.getColumnIndex(DbHelper.tblPedido_colFecha)));
					listPedido.add(pedido);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listPedido;
	}

	public long pedidoEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblPedido_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblPedido, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public void pedidoVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblPedido, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

    /******************************************************************************************************/
    // PedidoItem

    public PedidoItem pedidoItemGuardar(long pedidoId, PedidoItem pedidoItem) throws LocalException {
        SQLiteDatabase db = this.mDb.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            //cv.put(DbHelper.tblPedidoItem_colId, pedidoItem.id);
            cv.put(DbHelper.tblPedidoItem_colPedidoId, pedidoId);
            cv.put(DbHelper.tblPedidoItem_colProductoId, pedidoItem.productoId);
            cv.put(DbHelper.tblPedidoItem_colCantidad, pedidoItem.cantidad);
            pedidoItem.id = db.insert(DbHelper.tblPedidoItem, null, cv);
        } catch (Exception e) {
            throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedidoItem;
    }

    public long pedidoItemActualizar(long pedidoId, PedidoItem pedidoItem) throws LocalException {
        SQLiteDatabase db = this.mDb.getWritableDatabase();

        long cant = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put(DbHelper.tblPedidoItem_colPedidoId, pedidoId);
            cv.put(DbHelper.tblPedidoItem_colProductoId, pedidoItem.productoId);
            cv.put(DbHelper.tblPedidoItem_colCantidad, pedidoItem.cantidad);

            String where = "";
            if (pedidoItem != null) {
                String condition = DbHelper.tblPedidoItem_colId + "=" + String.valueOf(pedidoItem.id);
                where = UtilsDA.AddCondition(where, condition, "and");
            }

            cant = db.update(DbHelper.tblPedidoItem, cv, where, null);
        } catch (Exception e) {
            throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return cant;
    }

    public ArrayList<PedidoItem> pedidoItemBuscar(long id, long pedidoId, boolean loadProductos) throws LocalException {
        SQLiteDatabase db = this.mDb.getWritableDatabase();

        ArrayList<PedidoItem> listPedidoItem = new ArrayList<PedidoItem>();
        try {

            String select = "SELECT * FROM " + DbHelper.tblPedidoItem;

            String where = "";
            if (id > 0) {
                String condition = DbHelper.tblPedidoItem_colId + "=" + String.valueOf(id);
                where = UtilsDA.AddWhereCondition(where, condition, "and");
            }

            if (pedidoId > 0) {
                String condition = DbHelper.tblPedidoItem_colPedidoId + "=" + String.valueOf(pedidoId);
                where = UtilsDA.AddWhereCondition(where, condition, "and");
            }

            Cursor c = db.rawQuery(select + where, null);
            if (c.moveToFirst()) {
                do {
                    PedidoItem pedidoItem = new PedidoItem();
                    pedidoItem.id = c.getLong(c.getColumnIndex(DbHelper.tblPedidoItem_colId));
                    pedidoItem.cantidad = c.getInt(c.getColumnIndex(DbHelper.tblPedidoItem_colCantidad));
					pedidoItem.productoId = c.getLong(c.getColumnIndex(DbHelper.tblPedidoItem_colProductoId));

					if (loadProductos) {
						ArrayList<Producto> productos = this.productoBuscar(c.getLong(c.getColumnIndex(DbHelper.tblPedidoItem_colProductoId)));
						if (productos != null && productos.size() > 0)
							pedidoItem.producto = productos.get(0);
					}

                    listPedidoItem.add(pedidoItem);
                } while (c.moveToNext());
            }
            if (c != null && !c.isClosed())
                c.close();

        } catch (Exception e) {
            throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return listPedidoItem;
    }

    public long pedidoItemEliminar(long pedidoId, long id) throws LocalException {
        SQLiteDatabase db = this.mDb.getWritableDatabase();

        long cant = 0;
        try {

            String where = "";

            if (id > 0) {
                String condition = DbHelper.tblPedidoItem_colId + "=" + String.valueOf(id);
                where = UtilsDA.AddCondition(where, condition, "and");
            }

			if (pedidoId > 0) {
				String condition = DbHelper.tblPedidoItem_colPedidoId + "=" + String.valueOf(pedidoId);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

            cant = db.delete(DbHelper.tblPedidoItem, where, null);
        } catch (Exception e) {
            throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return cant;
    }

    public void pedidoItemVaciar() throws LocalException {
        SQLiteDatabase db = this.mDb.getWritableDatabase();

        try {
            db.delete(DbHelper.tblPedidoItem, null, null);
        } catch (Exception e) {
            throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

	/******************************************************************************************************/
	// Comentario

	public Comentario comentarioGuardar(Comentario comentario) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			//cv.put(DbHelper.tblComentario_colId, comentario.id);
			cv.put(DbHelper.tblComentario_colClienteId, comentario.clienteId);
			cv.put(DbHelper.tblComentario_colFechaComentario, Utils.date2string(comentario.fechaComentario, false));
			cv.put(DbHelper.tblComentario_colRazonComun, comentario.razonComun);
			cv.put(DbHelper.tblComentario_colComentario, comentario.comentario);
			cv.put(DbHelper.tblComentario_colEnviado, String.valueOf(comentario.enviado));

			comentario.id = db.insert(DbHelper.tblComentario, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return comentario;
	}

	public long comentarioActualizar(Comentario comentario) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			//cv.put(DbHelper.tblComentario_colId, comentario.id);
			cv.put(DbHelper.tblComentario_colClienteId, comentario.clienteId);
			cv.put(DbHelper.tblComentario_colFechaComentario, Utils.date2string(comentario.fechaComentario, false));
			cv.put(DbHelper.tblComentario_colRazonComun, comentario.razonComun);
			cv.put(DbHelper.tblComentario_colComentario, comentario.comentario);
			cv.put(DbHelper.tblComentario_colEnviado, String.valueOf(comentario.enviado));

			String where = "";
			if (comentario != null) {
				String condition = DbHelper.tblComentario_colId + "=" + String.valueOf(comentario.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblComentario, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public Comentario comentarioBuscar(long comentarioId) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		Comentario listComentario = new Comentario();
		try {

			String select = "SELECT * FROM " + DbHelper.tblComentario;

			String where = "";
			if (comentarioId > 0) {
				String condition = DbHelper.tblComentario_colId + "=" + String.valueOf(comentarioId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Comentario comentario = new Comentario();
					comentario.id = c.getLong(c.getColumnIndex(DbHelper.tblComentario_colId));
					comentario.clienteId = c.getInt(c.getColumnIndex(DbHelper.tblComentario_colClienteId));
					comentario.fechaComentario = Utils.string2date(c.getString(c.getColumnIndex(DbHelper.tblComentario_colFechaComentario)));
					comentario.razonComun = c.getString(c.getColumnIndex(DbHelper.tblComentario_colRazonComun));
					comentario.comentario = c.getString(c.getColumnIndex(DbHelper.tblComentario_colComentario));
					comentario.enviado = Utils.string2boolean(c.getString(c.getColumnIndex(DbHelper.tblComentario_colEnviado)));

					listComentario = comentario;
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listComentario;
	}

	public ArrayList<Comentario> comentarioObtenerNoEnviado() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Comentario> listComentario = new ArrayList<Comentario>();
		try {
			String select = "SELECT * FROM " + DbHelper.tblComentario;

			String where = "";
			String condition = DbHelper.tblComentario_colEnviado + "='false'";
			where = UtilsDA.AddWhereCondition(where, condition, "and");

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Comentario comentario = new Comentario();
					comentario.id = c.getLong(c.getColumnIndex(DbHelper.tblComentario_colId));
					comentario.clienteId = c.getInt(c.getColumnIndex(DbHelper.tblComentario_colClienteId));
					comentario.fechaComentario = Utils.string2date(c.getString(c.getColumnIndex(DbHelper.tblComentario_colFechaComentario)));
					comentario.razonComun = c.getString(c.getColumnIndex(DbHelper.tblComentario_colRazonComun));
					comentario.comentario = c.getString(c.getColumnIndex(DbHelper.tblComentario_colComentario));
					comentario.enviado = Utils.string2boolean(c.getString(c.getColumnIndex(DbHelper.tblComentario_colEnviado)));

					listComentario.add(comentario);
					Log.d("OT-LOG", "Cargando comentario ID:" + comentario.id + " para enviar!");
				} while (c.moveToNext());
			}

			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listComentario;
	}

	public void comentarioCambiarEstadoEnviado(Comentario comm, boolean enviado) throws LocalException {
		try {
			comm.enviado = enviado;
			comentarioActualizar(comm);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}

	public long comentarioEliminar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblComentario_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblComentario, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}


	public void comentarioVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblComentario, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}
}
