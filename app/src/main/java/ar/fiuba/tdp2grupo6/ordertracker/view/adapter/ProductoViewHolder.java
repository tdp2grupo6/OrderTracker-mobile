package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;

/**
 * Created by dgacitua on 30-03-16.
 */
public class ProductoViewHolder {
    private View row;
    private TextView upperText = null, lowerText1 = null, lowerText2 = null, sideText = null;
    private ImageView image;

    public ProductoViewHolder(View row) {
        this.row = row;
    }

    public TextView getUpperText() {
        if (this.upperText == null) {
            this.upperText = (TextView) row.findViewById(R.id.list_producto_nombre);
        }
        return this.upperText;
    }

    public TextView getLowerText1() {
        if (this.lowerText1 == null) {
            this.lowerText1 = (TextView) row.findViewById(R.id.list_producto_caracteristicas);
        }
        return this.lowerText1;
    }

    public TextView getLowerText2() {
        if (this.lowerText2 == null) {
            this.lowerText2 = (TextView) row.findViewById(R.id.list_producto_precio);
        }
        return this.lowerText2;
    }

    public TextView getSideText() {
        if (this.sideText == null) {
            this.sideText = (TextView) row.findViewById(R.id.list_producto_stock);
        }
        return this.sideText;
    }

    public ImageView getImage() {
        if (this.image == null) {
            this.image = (ImageView) row.findViewById(R.id.list_producto_imagen);
        }
        return this.image;
    }


}
