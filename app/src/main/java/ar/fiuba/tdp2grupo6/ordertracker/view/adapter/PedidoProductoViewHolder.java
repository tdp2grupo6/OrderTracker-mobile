package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;

public class PedidoProductoViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public ImageView mImageView;
    public TextView mNombreView;
    public TextView mStockView;
    public TextView mPrecioView;
    public TextView mPlusView;
    public TextView mMinusView;
    public TextView mQuantityView;
    public PedidoItem mPedidoItem;

    public PedidoProductoViewHolder(View view)  {
        super(view);
        mView = view;
        mImageView = (ImageView) view.findViewById(R.id.list_producto_imagen);
        mNombreView = (TextView) view.findViewById(R.id.list_producto_nombre);
        mStockView = (TextView) view.findViewById(R.id.list_producto_stock);
        mPrecioView = (TextView) view.findViewById(R.id.list_producto_precio);
        mMinusView = (TextView) view.findViewById(R.id.list_producto_minus);
        mPlusView = (TextView) view.findViewById(R.id.list_producto_plus);
        mQuantityView = (TextView) view.findViewById(R.id.list_producto_pedido_quantity);
    }

    public ImageView getImagenView() {
        if (this.mImageView == null) {
            mImageView = (ImageView) mView.findViewById(R.id.list_producto_imagen);
        }
        return this.mImageView;
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

    public TextView getMinusView() {
        if (this.mMinusView == null) {
            mMinusView = (TextView) mView.findViewById(R.id.list_producto_minus);
        }
        return this.mMinusView;
    }

    public TextView getPlusView() {
        if (this.mPlusView == null) {
            mPlusView = (TextView) mView.findViewById(R.id.list_producto_plus);
        }
        return this.mPlusView;
    }

    public TextView getQuantityText() {
        if (this.mQuantityView == null) {
            mQuantityView = (TextView) mView.findViewById(R.id.list_producto_pedido_quantity);
        }
        return this.mQuantityView;
    }

}

