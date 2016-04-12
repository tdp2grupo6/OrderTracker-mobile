package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

public class ProductoDetailActivity extends AppCompatActivity {
    Context mContext;
    Bundle mExtras;
    Long id;
    String categoria, nombre, marca, precio, descripcion, codigo, stock, campo, rutaImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            //categoria = mExtras.getString("productoCategoria");

            poblarVista(nombre, marca, precio, descripcion, stock, codigo, rutaImagen);
        }
    }

    private void poblarVista(String nombre, String marca, String precio, String descripcion, String stock, String codigo, String rutaImagen) {
        TextView categoriaTV, nombreTV, marcaTV, precioTV, descripcionTV, codigoTV, stockTV, campoTV;
        ImageView imagenIV;

        //categoriaTV = (TextView) findViewById(R.id.detalle_producto_categoria);
        nombreTV = (TextView) findViewById(R.id.detalle_producto_nombre);
        marcaTV = (TextView) findViewById(R.id.detalle_producto_marca);
        precioTV = (TextView) findViewById(R.id.detalle_producto_precio);
        descripcionTV = (TextView) findViewById(R.id.detalle_producto_descripcion);
        codigoTV = (TextView) findViewById(R.id.detalle_producto_codigo);
        stockTV = (TextView) findViewById(R.id.detalle_producto_stock);
        //campoTV = (TextView) findViewById(R.id.detalle_producto_pedido_campo);

        imagenIV = (ImageView) findViewById(R.id.detalle_producto_imagen);

        nombreTV.setText(nombre);
        marcaTV.setText(marca);
        precioTV.setText(precio);
        descripcionTV.setText(descripcion);
        codigoTV.setText(codigo);
        stockTV.setText(stock);

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagen = imagenBZ.leer(rutaImagen);
        if (imagen != null)
            imagenIV.setImageBitmap(imagen);
    }
}
