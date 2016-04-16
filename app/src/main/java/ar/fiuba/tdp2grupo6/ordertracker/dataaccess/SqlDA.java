package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ProductoImagen;
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
			cv.put(DbHelper.tblCliente_colNombreCompleto, cliente.nombreCompleto);
			cv.put(DbHelper.tblCliente_colNombre, cliente.nombre);
			cv.put(DbHelper.tblCliente_colApellido, cliente.apellido);
			cv.put(DbHelper.tblCliente_colRazonSocial, cliente.razonSocial);
			cv.put(DbHelper.tblCliente_colDireccion, cliente.direccion);
			cv.put(DbHelper.tblCliente_colTelefono, cliente.telefono);
			cv.put(DbHelper.tblCliente_colEmail, cliente.email);
			cv.put(DbHelper.tblCliente_colLat, cliente.lat);
			cv.put(DbHelper.tblCliente_colLng, cliente.lng);
			cv.put(DbHelper.tblCliente_colEstado, cliente.lng);

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
			cv.put(DbHelper.tblCliente_colEstado, cliente.lng);

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
					cliente.nombreCompleto = c.getString(c.getColumnIndex(DbHelper.tblCliente_colNombreCompleto));
					cliente.nombre = c.getString(c.getColumnIndex(DbHelper.tblCliente_colNombre));
					cliente.apellido = c.getString(c.getColumnIndex(DbHelper.tblCliente_colApellido));
					cliente.razonSocial = c.getString(c.getColumnIndex(DbHelper.tblCliente_colRazonSocial));
					cliente.direccion = c.getString(c.getColumnIndex(DbHelper.tblCliente_colDireccion));
					cliente.telefono = c.getString(c.getColumnIndex(DbHelper.tblCliente_colTelefono));
					cliente.email = c.getString(c.getColumnIndex(DbHelper.tblCliente_colEmail));
					cliente.lat = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLat));
					cliente.lng = c.getDouble(c.getColumnIndex(DbHelper.tblCliente_colLng));
					cliente.estado = c.getString(c.getColumnIndex(DbHelper.tblCliente_colEstado));
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
			cv.put(DbHelper.tblProducto_colCategoria, producto.categoria);
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
			cv.put(DbHelper.tblProducto_colCategoria, producto.categoria);
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
					producto.categoria = c.getString(c.getColumnIndex(DbHelper.tblProducto_colCategoria));
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
	// Pedido

	public Pedido pedidoGuardar(Pedido pedido) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			//cv.put(DbHelper.tblPedido_colId, pedido.id);
			cv.put(DbHelper.tblPedido_colClienteId, pedido.clienteId);
			cv.put(DbHelper.tblPedido_colEstado, pedido.estado);
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
			cv.put(DbHelper.tblPedido_colClienteId, pedido.clienteId);
			cv.put(DbHelper.tblPedido_colEstado, pedido.estado);

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

	public ArrayList<Pedido> pedidoBuscar(long id, long clienteId, int estado) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<Pedido> listPedido = new ArrayList<Pedido>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblPedido;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblPedido_colId + "=" + String.valueOf(id);
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
					pedido.clienteId = c.getLong(c.getColumnIndex(DbHelper.tblPedido_colClienteId));
					pedido.estado = c.getShort(c.getColumnIndex(DbHelper.tblPedido_colEstado));
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

    public long pedidoItemEliminar(long id, long pedidoId) throws LocalException {
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
				where = UtilsDA.AddWhereCondition(where, condition, "and");
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
	// ProductoImagen

    /*
	public ProductoImagen productoImagenGuardar(ProductoImagen productoImagen) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblProductoImagen_colId, productoImagen.id);
			cv.put(DbHelper.tblProductoImagen_colProductoId, productoImagen.productoId);
			cv.put(DbHelper.tblProductoImagen_colTipo, productoImagen.tipo);
			cv.put(DbHelper.tblProductoImagen_colPath, productoImagen.path);
			db.insert(DbHelper.tblProductoImagen, null, cv);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return productoImagen;
	}

	public long productoImagenActualizar(ProductoImagen productoImagen) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbHelper.tblProductoImagen_colProductoId, productoImagen.productoId);
			cv.put(DbHelper.tblProductoImagen_colTipo, productoImagen.tipo);
			cv.put(DbHelper.tblProductoImagen_colPath, productoImagen.path);

			String where = "";
			if (productoImagen != null) {
				String condition = DbHelper.tblProductoImagen_colId + "=" + String.valueOf(productoImagen.id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.update(DbHelper.tblProductoImagen, cv, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public ArrayList<ProductoImagen> productoImagenBuscar(long id, long productoId) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		ArrayList<ProductoImagen> listProductoImagen = new ArrayList<ProductoImagen>();
		try {

			String select = "SELECT * FROM " + DbHelper.tblProductoImagen;

			String where = "";
			if (id > 0) {
				String condition = DbHelper.tblProductoImagen_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}
			if (productoId > 0) {
				String condition = DbHelper.tblProductoImagen_colProductoId + "=" + String.valueOf(productoId);
				where = UtilsDA.AddWhereCondition(where, condition, "and");
			}
			Cursor c = db.rawQuery(select + where, null);
			if (c.moveToFirst()) {
				do {
					ProductoImagen productoImagen = new ProductoImagen();
					productoImagen.id = c.getLong(c.getColumnIndex(DbHelper.tblProductoImagen_colId));
					productoImagen.productoId = c.getLong(c.getColumnIndex(DbHelper.tblProductoImagen_colProductoId);
					productoImagen.tipo = c.getString(c.getColumnIndex(DbHelper.tblProductoImagen_colTipo));
					productoImagen.path = c.getString(c.getColumnIndex(DbHelper.tblProductoImagen_colPath));
					listProductoImagen.add(productoImagen);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();

		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return listProductoImagen;
	}

	public long productoImagenEliminar(long id, long productoId) throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		long cant = 0;
		try {

			String where = "";

			if (id > 0) {
				String condition = DbHelper.tblProductoImagen_colId + "=" + String.valueOf(id);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			if (productoId > 0) {
				String condition = DbHelper.tblProductoImagen_colProductoId + "=" + String.valueOf(productoId);
				where = UtilsDA.AddCondition(where, condition, "and");
			}

			cant = db.delete(DbHelper.tblProductoImagen, where, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}

		return cant;
	}

	public void productoImagenVaciar() throws LocalException {
		SQLiteDatabase db = this.mDb.getWritableDatabase();

		try {
			db.delete(DbHelper.tblProductoImagen, null, null);
		} catch (Exception e) {
			throw new LocalException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
		}
	}
    */
}
