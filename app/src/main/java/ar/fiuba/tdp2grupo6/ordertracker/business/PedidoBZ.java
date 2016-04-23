package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoBZ {
    private Context mContext;
    private WebDA mWeb;
    private SqlDA mSql;

    public PedidoBZ(Context context) {
        this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
    }

    public PedidoBZ(Context context, WebDA service) {
        this.mContext = context;
        this.mWeb = service;
    }

    public PedidoBZ(Context context, SqlDA dataBase) {
        this.mContext = context;
        this.mSql = dataBase;
    }

    public PedidoBZ(Context context, WebDA service, SqlDA dataBase) {
        this.mContext = context;
        this.mWeb = service;
        this.mSql = dataBase;
    }

    public Pedido actualizarConfirmado(Pedido pedido, PedidoItem pedidoItem) throws ServiceException, BusinessException {
        pedido.estado = Pedido.ESTADO_CONFIRMADO;
        return  actualizar(pedido, pedidoItem);
    }

    public Pedido actualizarPendiente(Pedido pedido, PedidoItem pedidoItem) throws ServiceException, BusinessException {
        pedido.estado = Pedido.ESTADO_PENDIENTE;
        return  actualizar(pedido, pedidoItem);
    }

    private Pedido actualizar(Pedido pedido, PedidoItem pedidoItem) throws ServiceException, BusinessException {
        try {

            //Actualiza el pedido
            pedido.items.put(String.valueOf(pedidoItem.productoId), pedidoItem);

            //Actualiza el pedido en la bd
            if (pedido.id == 0) {
                pedido = mSql.pedidoGuardar(pedido);
            } else {
                mSql.pedidoActualizar(pedido);
            }

            //Actualiza el pedido item en la bd
            pedido.importe = 0;
            for(Map.Entry<String, PedidoItem> entry : pedido.items.entrySet()) {
                String key = entry.getKey();
                PedidoItem item = entry.getValue();

                if (item.id == 0 && item.cantidad > 0) {
                    item = mSql.pedidoItemGuardar(pedido.id, item);
                } else if (item.id > 0 && item.cantidad > 0) {
                    mSql.pedidoItemActualizar(pedido.id, item);
                } else if (item.id == 0 && item.cantidad == 0) {
                    // no hace nada
                } else if (item.id > 0 && item.cantidad == 0) {
                    mSql.pedidoItemEliminar(pedido.id, 0);
                } else {
                    // no hace nada
                }

                //Actualiza el importe global del pedido
                pedido.importe += item.producto.precio * item.cantidad;
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedido;
    }

    public ArrayList<Pedido> buscar(long clienteId, int estado) throws BusinessException {
        ArrayList<Pedido> pedidos = null;
        try {
            ArrayList<PedidoItem> pedidoitems = null;
            pedidos = mSql.pedidoBuscar(0, clienteId, estado);

            if (pedidos != null && pedidos.size() > 0) {
                for (Pedido pedido: pedidos) {
                    //Obtiene los datos grabados para ese Pedido
                    pedidoitems = mSql.pedidoItemBuscar(0, pedido.id, false);

                    //Arma el catalogo
                    ArrayList<Producto> catalogo = mSql.productoBuscar(0);
                    for (Producto producto: catalogo) {
                        PedidoItem item = new PedidoItem();
                        item.productoId = producto.id;
                        item.producto = producto;
                        item.cantidad = 0;

                        pedido.items.put(String.valueOf(producto.id), item);
                    }

                    //Actualiza el catalogo si ya tenia parte del pedido cargado
                    pedido.importe = 0;
                    for (PedidoItem pedidoItem: pedidoitems) {
                        boolean borrarItem = true;

                        //Busca si el producto esta en el catalogo, sino lo borra
                        if (pedidoItem.cantidad > 0) {
                            PedidoItem pedidoItemExistente = pedido.items.get(String.valueOf(pedidoItem.productoId));
                            if (pedidoItemExistente != null) {
                                borrarItem = false;

                                //Actualiza los datos
                                pedidoItemExistente.id = pedidoItem.id;
                                pedidoItemExistente.cantidad = pedidoItem.cantidad;

                                //Actualiza el importe global del pedido
                                pedido.importe += pedidoItemExistente.producto.precio * pedidoItemExistente.cantidad;
                            }
                        }

                        //Si no esta el item del pedido en el catalogo actual, lo borra
                        if (borrarItem)
                            mSql.pedidoItemEliminar(pedidoItem.id, 0);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedidos;
    }

    public Pedido obtenerParaCliente(long clienteId) throws BusinessException {
        Pedido response = null;
        try {
            ArrayList<Pedido> pedidos = this.buscar(clienteId, Pedido.ESTADO_PENDIENTE);
            if (pedidos != null && pedidos.size() > 0) {
                //Obtiene los datos grabados para ese Pedido
                response = pedidos.get(0);
            } else {
                //Borra todos los pendientes (solo puede existier un pedido pendiente)
                borrarPendientes();

                //Crea uno nuevo
                response = new Pedido();
                response.clienteId = clienteId;
                response.importe = 0;

                //Arma el catalogo
                ArrayList<Producto> catalogo = mSql.productoBuscar(0);
                for (Producto producto: catalogo) {
                    PedidoItem item = new PedidoItem();
                    item.productoId = producto.id;
                    item.producto = producto;
                    item.cantidad = 0;

                    response.items.put(String.valueOf(producto.id), item);
                }
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }

    public void confirmar(long pedidoId) throws BusinessException {
        try {

            ArrayList<Pedido> pedidos = mSql.pedidoBuscar(pedidoId, 0, -1);

            if (pedidos != null && pedidos.size() > 0) {
                Pedido pedido = pedidos.get(0);
                pedido.estado =  Pedido.ESTADO_CONFIRMADO;

                mSql.pedidoActualizar(pedido);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void borrar(long pedidoId) throws BusinessException {
        try {

            mSql.pedidoItemEliminar(0, pedidoId);
            mSql.pedidoEliminar(pedidoId);

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void borrarPendientes() throws BusinessException {
        try {

            ArrayList<Pedido> pedidos = mSql.pedidoBuscar(0, 0, Pedido.ESTADO_PENDIENTE);
            for (Pedido pedido: pedidos) {
                mSql.pedidoItemEliminar(0, pedido.id);
                mSql.pedidoEliminar(pedido.id);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }
}
