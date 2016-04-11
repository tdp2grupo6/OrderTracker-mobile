package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.view.dummy.DummyContent;

/**
 * Created by pablo on 28/03/16.
 */


public class ClienteViewHolder_ extends RecyclerView.ViewHolder {
    public final View mView;
    public TextView mNombreView;
    public TextView mDireccionView;
    public Cliente mCliente;

    public ClienteViewHolder_(View view) {
        super(view);
        mView = view;
        mNombreView = (TextView) view.findViewById(R.id.id);
        mDireccionView = (TextView) view.findViewById(R.id.content);
    }

    public TextView getNombreView() {
        if (this.mNombreView == null) {
            this.mNombreView = (TextView) mView.findViewById(R.id.list_cliente_nombre);
        }
        return this.mNombreView;
    }

    public TextView getDireccionView() {
        if (this.mDireccionView == null) {
            this.mDireccionView = (TextView) mView.findViewById(R.id.list_cliente_direccion);
        }
        return this.mDireccionView;
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mDireccionView.getText() + "'";
    }
}

