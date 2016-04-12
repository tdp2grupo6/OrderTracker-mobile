package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;

public class ProductoDetailActivity extends AppCompatActivity {
    Context mContext;
    Bundle mExtras;
    Long id;
    String categoria, nombre, marca, precio, descripcion, codigo, stock, campo, rutaImagen;

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
            categoria = mExtras.getString("productoCategoria");

            collapsingToolbar.setTitle(categoria);

			poblarVista(nombre, marca, precio, descripcion, stock, codigo, rutaImagen);
        }
    }

    private void poblarVista(String nombre, String marca, String precio, String descripcion, String stock, String codigo, String rutaImagen) {
        TextView nombreTV, marcaTV, precioTV, descripcionTV, codigoTV, stockTV, campoTV;
        ImageView imagenIV;

        nombreTV = (TextView) findViewById(R.id.detalle_producto_nombre);
        marcaTV = (TextView) findViewById(R.id.detalle_producto_marca);
        precioTV = (TextView) findViewById(R.id.detalle_producto_precio);
        descripcionTV = (TextView) findViewById(R.id.detalle_producto_descripcion);
        codigoTV = (TextView) findViewById(R.id.detalle_producto_codigo);
        stockTV = (TextView) findViewById(R.id.detalle_producto_stock);
        //categoriaTV = (TextView) findViewById(R.id.detalle_producto_categoria);
        //campoTV = (TextView) findViewById(R.id.detalle_producto_pedido_campo);

        imagenIV = (ImageView) findViewById(R.id.detalle_producto_imagen);

        if (nombreTV != null) {
            nombreTV.setText(nombre);
        }
        if (marcaTV != null) {
            marcaTV.setText(marca);
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

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagen = imagenBZ.leer(rutaImagen);
        if (imagen != null && imagenIV != null) {
            imagenIV.setImageBitmap(imagen);
        }
    }
}
