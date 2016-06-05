package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;

public class PedidoProductoConfirmaViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public ImageView mImageView;
    public ImageView mDescuentoView;
    public TextView mNombreView;
    public TextView mStockView;
    public TextView mPrecioView;
    public TextView mQuantityView;
    public PedidoItem mPedidoItem;

    public PedidoProductoConfirmaViewHolder(View view)  {
        super(view);
        mView = view;
        mImageView = (ImageView) view.findViewById(R.id.list_producto_imagen);
        mDescuentoView = (ImageView) view.findViewById(R.id.list_producto_pedido_descuento);
        mNombreView = (TextView) view.findViewById(R.id.list_producto_nombre);
        mStockView = (TextView) view.findViewById(R.id.list_producto_stock);
        mPrecioView = (TextView) view.findViewById(R.id.list_producto_precio);
        mQuantityView = (TextView) view.findViewById(R.id.list_producto_pedido_quantity);
    }

    public ImageView getImagenView() {
        if (this.mImageView == null) {
            mImageView = (ImageView) mView.findViewById(R.id.list_producto_imagen);
        }
        return this.mImageView;
    }

    public ImageView getDescuentoView() {
        if (this.mDescuentoView == null) {
            mDescuentoView = (ImageView) mView.findViewById(R.id.list_producto_pedido_descuento);
        }
        return this.mDescuentoView;
    }

    public TextView getNombreView() {
        if (this.mNombreView == null) {
            this.mNombreView = (TextView) mView.findViewById(R.id.list_cliente_nombre);
        }
        return this.mNombreView;
    }


    public TextView getStockView() {
        if (this.mStockView == null) {
            this.mStockView = (TextView) mView.findViewById(R.id.list_producto_stock);
        }
        return this.mStockView;
    }


    public TextView getPrecioView() {
        if (this.mPrecioView == null) {
            mPrecioView = (TextView) mView.findViewById(R.id.list_producto_precio);
        }
        return this.mPrecioView;
    }

    public TextView getQuantityText() {
        if (this.mQuantityView == null) {
            mQuantityView = (TextView) mView.findViewById(R.id.list_producto_pedido_quantity);
        }
        return this.mQuantityView;
    }

}

