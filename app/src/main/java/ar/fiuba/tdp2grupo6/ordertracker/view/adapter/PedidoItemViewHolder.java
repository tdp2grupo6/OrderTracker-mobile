package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoItemViewHolder {
    private View row;
    private TextView codigoText = null, nombreText = null, cantidadText = null;

    public PedidoItemViewHolder(View row) {
        this.row = row;
    }

    public TextView getCodigoText() {
        if (this.codigoText == null) {
            this.codigoText = (TextView) row.findViewById(R.id.list_pedido_item_codigo);
        }
        return this.codigoText;
    }

    public TextView getNombreText() {
        if (this.nombreText == null) {
            this.nombreText = (TextView) row.findViewById(R.id.list_pedido_item_nombre);
        }
        return this.nombreText;
    }

    public TextView getCantidadText() {
        if (this.cantidadText == null) {
            this.cantidadText = (TextView) row.findViewById(R.id.list_pedido_item_cantidad);
        }
        return this.cantidadText;
    }

}
