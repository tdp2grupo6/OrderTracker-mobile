package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

public class AgendaViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public TextView mNombreView;
    public TextView mDireccionView;
    public Cliente mCliente;

    public AgendaViewHolder(View view) {
        super(view);
        this.mView = view;
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
