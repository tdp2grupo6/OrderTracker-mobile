package ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final String dbName = "ordertracker";
	static final int dbVersion = 1;
	static final String dbDateTimeType = " INTEGER NOT NULL DEFAULT (strftime('%s','now'))";
	static final String dbDateType = " INTEGER";

	private static DbHelper helper;

	// Tabla Cliente
	public static final String tblCliente = "Cliente";
	public static final String tblCliente_colId = "id";
	public static final String tblCliente_colNombre = "nombre";
	public static final String tblCliente_colDireccion = "direccion";

	// Tabla CatalogoActivity
	public static final String tblProducto = "Producto";
	public static final String tblProducto_colId = "id"; // Autoincremental
	public static final String tblProducto_colNombre = "nombre";
	public static final String tblProducto_colMarca = "marca";
	public static final String tblProducto_colCaracteristicas = "caracteristicas";
	public static final String tblProducto_colPrecio = "precio";
	public static final String tblProducto_colStock = "stock";

	public static synchronized DbHelper getInstance(Context context) {
		if (helper == null) {
			helper = new DbHelper(context, null);
		}

		return helper;
	}

	public DbHelper(Context context, CursorFactory factory) {
		super(context, DbHelper.dbName, factory, DbHelper.dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String createTable;

		// Cliente
		createTable = "CREATE TABLE " + tblCliente + " (";
		createTable += tblCliente_colId + " INTEGER PRIMARY KEY";
		createTable += ", " + tblCliente_colNombre + " TEXT";
		createTable += ", " + tblCliente_colDireccion + " TEXT";
		createTable += ")";
		try {
			db.execSQL(createTable);
		} catch (SQLException sql) {

		}

		// CatalogoActivity
		createTable = "CREATE TABLE " + tblProducto + " (";
		createTable += tblProducto_colId + " INTEGER PRIMARY KEY";
		createTable += ", " + tblProducto_colNombre + " TEXT";
		createTable += ", " + tblProducto_colMarca + " TEXT";
		createTable += ", " + tblProducto_colCaracteristicas + " TEXT";
		createTable += ", " + tblProducto_colPrecio + " REAL";
		createTable += ", " + tblProducto_colStock + " INTEGER";
		createTable += ")";
		try {
			db.execSQL(createTable);
		} catch (SQLException sql) {

		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {

			String dropTable = "DROP TABLE IF EXISTS " + tblCliente;
			db.execSQL(dropTable);

			dropTable = "DROP TABLE IF EXISTS " + tblProducto;
			db.execSQL(dropTable);

			onCreate(db);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
}
