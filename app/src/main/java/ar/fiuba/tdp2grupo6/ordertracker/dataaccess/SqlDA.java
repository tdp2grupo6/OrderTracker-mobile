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
			cv.put(DbHelper.tblCliente_colTelefono, cliente.telefono);
			cv.put(DbHelper.tblCliente_colEmail, cliente.email);
			cv.put(DbHelper.tblCliente_colLat, cliente.lat);
			cv.put(DbHelper.tblCliente_colLng, cliente.lng);

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
			cv.put(DbHelper.tblCliente_colTelefono, cliente.telefono);
			cv.put(DbHelper.tblCliente_colEmail, cliente.email);
			cv.put(DbHelper.tblCliente_colLat, cliente.lat);
			cv.put(DbHelper.tblCliente_colLng, cliente.lng);

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
					cliente.telefono = c.getString(c.getColumnIndex(DbHelper.tblCliente_colTelefono));
					cliente.email = c.getString(c.getColumnIndex(DbHelper.tblCliente_colEmail));
					cliente.lat = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLat));
					cliente.lng = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLng));
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

}
