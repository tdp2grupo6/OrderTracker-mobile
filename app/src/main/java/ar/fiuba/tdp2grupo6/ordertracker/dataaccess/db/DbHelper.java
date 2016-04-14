package ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final String dbName = "ordertracker";
	static final int dbVersion = 7;
	static final String dbDateTimeType = " INTEGER NOT NULL DEFAULT (strftime('%s','now'))";
	static final String dbDateType = " INTEGER";

	private static DbHelper helper;

	// Tabla Cliente
	public static final String tblCliente = "Cliente";
	public static final String tblCliente_colId = "id";
	public static final String tblCliente_colNombreCompleto = "nombreCompleto";
	public static final String tblCliente_colNombre = "nombre";
	public static final String tblCliente_colApellido = "apellido";
	public static final String tblCliente_colRazonSocial = "razonSocial";
	public static final String tblCliente_colDireccion = "direccion";
	public static final String tblCliente_colTelefono = "telefono";
	public static final String tblCliente_colEmail = "email";
	public static final String tblCliente_colLat = "lat";
	public static final String tblCliente_colLng = "lng";
	public static final String tblCliente_colEstado = "estado";

	// Tabla Producto
	public static final String tblProducto = "Producto";
	public static final String tblProducto_colId = "id"; // Autoincremental
	public static final String tblProducto_colNombre = "nombreCompleto";
	public static final String tblProducto_colMarca = "marca";
	public static final String tblProducto_colCaracteristicas = "caracteristicas";
	public static final String tblProducto_colPrecio = "precio";
	public static final String tblProducto_colStock = "stock";
	public static final String tblProducto_colEstado = "estado";
	public static final String tblProducto_colRutaImg = "rutaImagen";
	public static final String tblProducto_colRutaMini = "rutaMiniatura";

	// Tabla Producto Imagen
	public static final String tblProductoImagen = "ProductoImagen";
	public static final String tblProductoImagen_colId = "id"; // Autoincremental
	public static final String tblProductoImagen_colProductoId = "productoid";
	public static final String tblProductoImagen_colTipo = "tipo";
	public static final String tblProductoImagen_colPath = "path";

	// Tabla Pedido
	public static final String tblPedido = "Pedido";
	public static final String tblPedido_colId = "id"; // Autoincremental
	public static final String tblPedido_colClienteId = "clienteid";
	public static final String tblPedido_colEstado = "estado";

	// Tabla Pedido Item
	public static final String tblPedidoItem = "PedidoItem";
	public static final String tblPedidoItem_colId = "id"; // Autoincremental
	public static final String tblPedidoItem_colPedidoId = "pedidoid";
	public static final String tblPedidoItem_colProductoId = "productoid";
	public static final String tblPedidoItem_colCantidad = "cantidad";

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
		createTable += ", " + tblCliente_colNombreCompleto + " TEXT";
		createTable += ", " + tblCliente_colNombre + " TEXT";
		createTable += ", " + tblCliente_colApellido + " TEXT";
		createTable += ", " + tblCliente_colRazonSocial + " TEXT";
		createTable += ", " + tblCliente_colDireccion + " TEXT";
		createTable += ", " + tblCliente_colTelefono + " TEXT";
		createTable += ", " + tblCliente_colEmail + " TEXT";
		createTable += ", " + tblCliente_colLat + " REAL";
		createTable += ", " + tblCliente_colLng + " REAL";
		createTable += ", " + tblCliente_colEstado + " TEXT";
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
		createTable += ", " + tblProducto_colEstado + " TEXT";
		createTable += ", " + tblProducto_colRutaImg + " TEXT";
		createTable += ", " + tblProducto_colRutaMini + " TEXT";
		createTable += ")";
		try {
			db.execSQL(createTable);
		} catch (SQLException sql) {

		}

		// Producto
		createTable = "CREATE TABLE " + tblProductoImagen + " (";
		createTable += tblProductoImagen_colId + " INTEGER PRIMARY KEY";
		createTable += ", " + tblProductoImagen_colProductoId + " INTEGER";
		createTable += ", " + tblProductoImagen_colTipo + " TEXT";
		createTable += ", " + tblProductoImagen_colPath + " TEXT";
		createTable += ")";
		try {
			db.execSQL(createTable);
		} catch (SQLException sql) {

		}

		// Pedido
		createTable = "CREATE TABLE " + tblPedido + " (";
		createTable += tblPedido_colId + " INTEGER PRIMARY KEY";
		createTable += ", " + tblPedido_colClienteId + " INTEGER";
		createTable += ", " + tblPedido_colEstado + " INTEGER";
		createTable += ")";
		try {
			db.execSQL(createTable);
		} catch (SQLException sql) {

		}

		// Pedido Item
		createTable = "CREATE TABLE " + tblPedidoItem + " (";
		createTable += tblPedidoItem_colId + " INTEGER PRIMARY KEY";
		createTable += ", " + tblPedidoItem_colPedidoId + " INTEGER";
		createTable += ", " + tblPedidoItem_colProductoId + " INTEGER";
		createTable += ", " + tblPedidoItem_colCantidad + " INTEGER";
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

			dropTable = "DROP TABLE IF EXISTS " + tblProductoImagen;
			db.execSQL(dropTable);

			dropTable = "DROP TABLE IF EXISTS " + tblPedido;
			db.execSQL(dropTable);

			dropTable = "DROP TABLE IF EXISTS " + tblPedidoItem;
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
