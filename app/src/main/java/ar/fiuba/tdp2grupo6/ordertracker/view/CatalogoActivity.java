package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ProductoAdapter;

public class CatalogoActivity extends AppBaseActivity {

    private final int MENU_INDEX = 1;

    private Context mContext;
    private ListView mListView;
    private TextView mEmptyView;
    private SwipeRefreshLayout mSwipeLayout;
    private ProductosBuscarTask mProductosBuscarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the swipe for refresh
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_producto);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }
        });

        //Set the list of productos
        mListView = (ListView) findViewById(R.id.productos_list);
        mEmptyView = (TextView) findViewById(R.id.productos_list_empty);

        //Set the item selected
        mDrawerMenu.getItem(MENU_INDEX).setChecked(true);
        mContext = (Context)this;

        // dgacitua: Soporte para detalle producto
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductoDetailActivity.class);
                Producto prod = (Producto) parent.getItemAtPosition(position);
                //Toast.makeText(mContext, "Seleccionando el producto (" + prod.id + ") " + prod.nombre, Toast.LENGTH_SHORT).show();
                intent.putExtra("productoId", prod.id);
                intent.putExtra("productoNombre", prod.nombre);
                intent.putExtra("productoMarca", prod.marca);
                intent.putExtra("productoPrecio", prod.mostrarPrecio());
                intent.putExtra("productoDescripcion", prod.caracteristicas);
                intent.putExtra("productoCodigo", prod.mostrarCodigo());
                intent.putExtra("productoStock", prod.mostrarStock());
                intent.putExtra("productoRutaImagen", prod.getNombreImagenMiniatura());
                intent.putExtra("productoCategoria", prod.categoria);
                intent.putExtra("productoEstado", prod.mostrarEstado());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // por las dudas corre el servicio, si ya esta corriendo es ignorada
        // este inicio
        this.refrescarLista();
    }

    private void refrescarLista() {
        mProductosBuscarTask = new ProductosBuscarTask(this);
        mProductosBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Producto> productos) {
        if (mListView != null) {
            ProductoAdapter adapter = new ProductoAdapter(this, productos);
            mListView.setAdapter(adapter);
            mListView.setEmptyView(mEmptyView);
        }
    }

    public class ProductosBuscarTask extends AsyncTask<Void, String, ArrayList<Producto>> {
        private Context mContext;
        public ProductosBuscarTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Producto> doInBackground(Void... params) {
            ArrayList<Producto> resultado = new ArrayList<Producto>();

            ProductoBZ productoBz = new ProductoBZ(this.mContext);
            try {
                //Si puede sincroniza los productos primero
                productoBz.sincronizar();
            }
            catch (Exception e) {
            }

            try {
                //y luego busca el listado
                resultado = productoBz.listar();
            }
            catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(ArrayList<Producto> productos) {
            if (mListView != null && productos!=null) {
                mSwipeLayout.setRefreshing(false);
                actualizarLista(productos);
            }
        }
    }
}
