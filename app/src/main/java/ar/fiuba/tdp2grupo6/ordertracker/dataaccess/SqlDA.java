package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.LocalException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db.DbHelper;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;


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
	// Cliente

	public Cliente clienteGuardar(Cliente cliente) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblCliente_colId, cliente.id);
			cv.put(DbHelper.tblCliente_colNombre, cliente.nombre);
			cv.put(DbHelper.tblCliente_colDireccion, cliente.direccion);

			db.insert(DbHelper.tblCliente, null, cv);
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
			cv.put(DbHelper.tblCliente_colNombre, cliente.nombre);
			cv.put(DbHelper.tblCliente_colDireccion, cliente.direccion);

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

	public ArrayList<Cliente> clienteBuscar(long id) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Cliente> listCliente = new ArrayList<Cliente>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblCliente;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblCliente_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}

			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					Cliente cliente = new Cliente();
					cliente.id = c.getLong(c.getColumnIndex(DbHelper.tblCliente_colId));
					cliente.nombre = c.getString(c.getColumnIndex(DbHelper.tblCliente_colNombre));
					cliente.direccion = c.getString(c.getColumnIndex(DbHelper.tblCliente_colDireccion));
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
	// Catalogo

	public Producto productoGuardar(Producto producto) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblProducto_colId, producto.id);
			cv.put(DbHelper.tblProducto_colNombre, producto.nombre);
			cv.put(DbHelper.tblProducto_colMarca, producto.marca);
			cv.put(DbHelper.tblProducto_colCaracteristicas, producto.caracteristicas);
			cv.put(DbHelper.tblProducto_colPrecio, producto.precio);
			cv.put(DbHelper.tblProducto_colStock, producto.stock);
			db.insert(DbHelper.tblProducto, null, cv);
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
			cv.put(DbHelper.tblProducto_colPrecio, producto.precio);
			cv.put(DbHelper.tblProducto_colStock, producto.stock);

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
					producto.precio = c.getDouble(c.getColumnIndex(DbHelper.tblProducto_colPrecio));
					producto.stock = c.getInt(c.getColumnIndex(DbHelper.tblProducto_colStock));
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

}
