package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;

/**
 * Created by pablo on 28/03/16.
 */
public class ClienteViewHolder {
    private View row;
    private TextView upperText = null, lowerText = null;

    public ClienteViewHolder(View row) {
        this.row = row;
    }

    public TextView getUpperText() {
        if (this.upperText == null) {
            this.upperText = (TextView) row.findViewById(R.id.list_cliente_nombre);
        }
        return this.upperText;
    }

    public TextView getLowerText() {
        if (this.lowerText == null) {
            this.lowerText = (TextView) row.findViewById(R.id.list_cliente_direccion);
        }
        return this.lowerText;
    }
}
