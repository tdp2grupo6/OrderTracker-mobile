package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoViewHolder {
    private View row;
    private TextView estadoText = null, clienteText = null, numeroText = null, fechaText = null;
    private TextView importeText;

    public PedidoViewHolder(View row) {
        this.row = row;
    }

    public TextView getEstadoText() {
        if (this.estadoText == null) {
            this.estadoText = (TextView) row.findViewById(R.id.list_pedido_estado);
        }
        return this.estadoText;
    }

    public TextView getClienteText() {
        if (this.clienteText == null) {
            this.clienteText = (TextView) row.findViewById(R.id.list_pedido_cliente);
        }
        return this.clienteText;
    }

    public TextView getNumeroText() {
        if (this.numeroText == null) {
            this.numeroText = (TextView) row.findViewById(R.id.list_pedido_nro);
        }
        return this.numeroText;
    }

    public TextView getFechaText() {
        if (this.fechaText == null) {
            this.fechaText = (TextView) row.findViewById(R.id.list_pedido_fecha);
        }
        return this.fechaText;
    }

    public TextView getImporteText() {
        if (this.importeText == null) {
            this.importeText = (TextView) row.findViewById(R.id.list_pedido_monto);
        }
        return this.importeText;
    }
}
