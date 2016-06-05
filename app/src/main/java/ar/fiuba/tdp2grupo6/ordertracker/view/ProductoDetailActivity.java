package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Categoria;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Descuento;

public class ProductoDetailActivity extends AppBaseAuthActivity {
    Context mContext;
    Bundle mExtras;
    Long id;
    Categoria categoria;
    String nombre, marca, precio, descripcion, codigo, stock, estado, campo, rutaImagen;
    JSONArray descuento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.detalle_producto_toolbar_layout);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        mContext = (Context)this;

        ImageView imgView = (ImageView)findViewById(R.id.bar_producto_detail);
        Drawable draw = ContextCompat.getDrawable(mContext, R.drawable.image_producto_detail);

        if (imgView != null && draw != null) {
            imgView.setImageDrawable(draw);
        }


        // dgacitua: Obtener datos de CatalogoActivity
        mExtras = getIntent().getExtras();
        if (mExtras != null) {
            id = mExtras.getLong("productoId");
            nombre = mExtras.getString("productoNombre");
            marca = mExtras.getString("productoMarca");
            precio = mExtras.getString("productoPrecio");
            descripcion = mExtras.getString("productoDescripcion");
            stock = mExtras.getString("productoStock");
            codigo = mExtras.getString("productoCodigo");
            rutaImagen = mExtras.getString("productoRutaImagen");
            categoria = new Categoria(mExtras.getString("productoCategoria"));
            estado = mExtras.getString("productoEstado");
            if (mExtras.containsKey("productoDescuentos")) {
                try {
                    descuento = new JSONArray(mExtras.getString("productoDescuentos"));
                } catch (Exception e) {
                    descuento = null;
                }
            }

            collapsingToolbar.setTitle(categoria.nombre);

			poblarVista(nombre, marca, precio, descripcion, stock, codigo, rutaImagen, estado, descuento);
        }
    }

    private void poblarVista(String nombre, String marca, String precio, String descripcion, String stock, String codigo, String rutaImagen, String estado, JSONArray descuento) {
        TextView nombreTV, marcaTV, precioTV, descripcionTV, codigoTV, stockTV, estadoTV, campoTV;
        ImageView imagenIV;

        nombreTV = (TextView) findViewById(R.id.detalle_producto_nombre);
        precioTV = (TextView) findViewById(R.id.detalle_producto_precio);
        descripcionTV = (TextView) findViewById(R.id.detalle_producto_descripcion);
        codigoTV = (TextView) findViewById(R.id.detalle_producto_codigo);
        stockTV = (TextView) findViewById(R.id.detalle_producto_stock);
        //marcaTV = (TextView) findViewById(R.id.detalle_producto_marca);
        //estadoTV = (TextView) findViewById(R.id.detalle_producto_estado);
        //campoTV = (TextView) findViewById(R.id.detalle_producto_pedido_campo);

        imagenIV = (ImageView) findViewById(R.id.detalle_producto_imagen);

        if (nombreTV != null) {
            nombreTV.setText(nombre);
        }
        if (precioTV != null) {
            precioTV.setText(precio);
        }
        if (descripcionTV != null) {
            descripcionTV.setText(descripcion);
        }
        if (codigoTV != null) {
            codigoTV.setText(codigo);
        }
        if (stockTV != null) {
            stockTV.setText(stock);
        }
        /*
        if (marcaTV != null) {
            marcaTV.setText(marca);
        }
        */
        /*
        if (estadoTV != null) {
            estadoTV.setText(estado);
        }
        */

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagen = imagenBZ.leer(rutaImagen);
        if (imagen != null && imagenIV != null) {
            imagenIV.setImageBitmap(imagen);
        }

        //Configura los descuentos
        LinearLayout tablaDescuentos = (LinearLayout) findViewById(R.id.detalle_producto_descuento);
        if (descuento != null && descuento.length() > 0) {
            tablaDescuentos.setVisibility(View.VISIBLE);

            LinearLayout tablaDescuentosCol0 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col0);
            LinearLayout tablaDescuentosCol1 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col1);
            LinearLayout tablaDescuentosCol2 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col2);
            LinearLayout tablaDescuentosCol3 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col3);
            LinearLayout tablaDescuentosCol4 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col4);
            LinearLayout tablaDescuentosCol5 = (LinearLayout) findViewById(R.id.detalle_producto_descuento_col5);
            TextView tablaDescuentosTextViewCol0Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col0_row1);
            TextView tablaDescuentosTextViewCol0Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col0_row2);
            TextView tablaDescuentosTextViewCol1Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col1_row1);
            TextView tablaDescuentosTextViewCol1Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col1_row2);
            TextView tablaDescuentosTextViewCol2Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col2_row1);
            TextView tablaDescuentosTextViewCol2Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col2_row2);
            TextView tablaDescuentosTextViewCol3Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col3_row1);
            TextView tablaDescuentosTextViewCol3Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col3_row2);
            TextView tablaDescuentosTextViewCol4Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col4_row1);
            TextView tablaDescuentosTextViewCol4Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col4_row2);
            TextView tablaDescuentosTextViewCol5Row1 = (TextView) findViewById(R.id.detalle_producto_descuento_col5_row1);
            TextView tablaDescuentosTextViewCol5Row2 = (TextView) findViewById(R.id.detalle_producto_descuento_col5_row2);

            //Hace visible los Titulos
            tablaDescuentosCol0.setVisibility(View.VISIBLE);
            //tablaDescuentosTextViewCol0Row1.setVisibility(View.VISIBLE);
            //tablaDescuentosTextViewCol0Row2.setVisibility(View.VISIBLE);

            for (int i = 0; i < 5; i++) {
                Descuento descuentoItem = null;
                try {
                    descuentoItem = new Descuento(descuento.getJSONObject(i));
                } catch (Exception e) {
                }

                //Actualiza los controles
                int index = i + 1;
                switch (index){
                    case 1:
                        setCeldaDescuento(descuentoItem, tablaDescuentosCol1, tablaDescuentosTextViewCol1Row1, tablaDescuentosTextViewCol1Row2);
                        break;
                    case 2:
                        setCeldaDescuento(descuentoItem, tablaDescuentosCol2, tablaDescuentosTextViewCol2Row1, tablaDescuentosTextViewCol2Row2);
                        break;
                    case 3:
                        setCeldaDescuento(descuentoItem, tablaDescuentosCol3, tablaDescuentosTextViewCol3Row1, tablaDescuentosTextViewCol3Row2);
                        break;
                    case 4:
                        setCeldaDescuento(descuentoItem, tablaDescuentosCol4, tablaDescuentosTextViewCol4Row1, tablaDescuentosTextViewCol4Row2);
                        break;
                    case 5:
                        setCeldaDescuento(descuentoItem, tablaDescuentosCol5, tablaDescuentosTextViewCol5Row1, tablaDescuentosTextViewCol5Row2);
                        break;
                }

            }
        } else {
            tablaDescuentos.setVisibility(View.GONE);
        }
    }

    private void setCeldaDescuento(Descuento descuentoItem, LinearLayout tablaDescuentosColX, TextView tablaDescuentosTextViewColXRow1, TextView tablaDescuentosTextViewColXRow2) {
        if (descuentoItem != null) {
            tablaDescuentosColX.setVisibility(View.VISIBLE);
            tablaDescuentosTextViewColXRow1.setText(descuentoItem.minimoProductos + " a " + descuentoItem.maximoProductos);
            tablaDescuentosTextViewColXRow2.setText("%" + descuentoItem.descuento);
        } else {
            tablaDescuentosColX.setVisibility(View.GONE);
        }
    }
}
