package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * Created by pablo on 28/03/16.
 */
public class ClienteViewHolder {
    public View mView;
    public TextView mNombreView;
    public TextView mDireccionView;
    public TextView mEstadoView;
    public Cliente mCliente;

    public ClienteViewHolder(View view) {
        this.mView = view;
    }


    public TextView getEstadoView() {
        if (this.mEstadoView == null) {
            this.mEstadoView = (TextView) mView.findViewById(R.id.list_cliente_estado);
        }
        return this.mEstadoView;
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
}
