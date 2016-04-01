package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ProductoAdapter;

public class CatalogoActivity extends AppBaseActivity {

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

        mContext = (Context)this;
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

            try {
                ProductoBZ productoBz = new ProductoBZ(this.mContext);
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
