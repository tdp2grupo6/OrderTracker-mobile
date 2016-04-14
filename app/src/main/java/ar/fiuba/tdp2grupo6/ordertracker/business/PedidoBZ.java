package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
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

    public Pedido actualizarProducto(Pedido pedido, Producto producto, int nuevacantidad) throws ServiceException, BusinessException {
        try {

            //Actualiza el pedido
            PedidoItem item = null;
            if (!pedido.productos.containsKey(String.valueOf(producto.id))) {
                item = new PedidoItem();
                item.producto = producto;
                item.cantidad = nuevacantidad;

                pedido.productos.put(String.valueOf(producto.id), item);
            } else {
                item = pedido.productos.get(String.valueOf(producto.id));
            }

            //Actualiza el pedido en la bd
            if (pedido.id == 0) {
                pedido = mSql.pedidoGuardar(pedido);
            } else {
                mSql.pedidoActualizar(pedido);
            }

            //Actualiza el importe
            pedido.importe = 0;
            for(Map.Entry<String, PedidoItem> entry : pedido.productos.entrySet()) {
                String key = entry.getKey();
                PedidoItem pedidoItem = entry.getValue();

                if (pedidoItem.id == 0) {
                    pedidoItem = mSql.pedidoItemGuardar(pedido.id, pedidoItem);
                } else {
                    mSql.pedidoItemActualizar(pedido.id, pedidoItem);
                }

                //Actualiza el importe
                pedido.importe += pedidoItem.producto.precio * pedidoItem.cantidad;
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedido;
    }
}
