package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ProductoAdapter;

public class PedidosActivity extends AppBaseActivity {

    private final int MENU_INDEX = 3;

    private Context mContext;
    private ListView mListView;
    private TextView mEmptyView;
    private SwipeRefreshLayout mSwipeLayout;
    private PedidosBuscarTask mPedidosBuscarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the swipe for refresh
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_pedidos);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }
        });

        //Set the list of items
        mListView = (ListView) findViewById(R.id.pedidos_list);
        mEmptyView = (TextView) findViewById(R.id.pedidos_list_empty);

        //Set the item selected
        mDrawerMenu.getItem(MENU_INDEX).setChecked(true);
        mContext = (Context)this;

        // dgacitua: Soporte para detalle producto
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
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
                intent.putExtra("productoCategoria", prod.categoria.toString());
                intent.putExtra("productoEstado", prod.mostrarEstado());
                startActivity(intent);
                */
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
        mPedidosBuscarTask = new PedidosBuscarTask(this);
        mPedidosBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Pedido> pedidos) {
        if (mListView != null) {
            PedidoAdapter adapter = new PedidoAdapter(this, pedidos);
            mListView.setAdapter(adapter);
            mListView.setEmptyView(mEmptyView);
        }
    }

    public class PedidosBuscarTask extends AsyncTask<Void, String, ArrayList<Pedido>> {
        private Context mContext;
        private ProgressDialog mPd;
        private boolean mSessionInvalid = false;

        public PedidosBuscarTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mPd = new ProgressDialog(mContext);
            mPd.setMessage(mContext.getResources().getString(R.string.msg_procesando));
            mPd.setCancelable(false);
            mPd.getWindow().setGravity(Gravity.CENTER);
            mPd.show();
        }

        @Override
        protected ArrayList<Pedido> doInBackground(Void... params) {
            ArrayList<Pedido> resultado = new ArrayList<Pedido>();

            PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
            try {
                //Si puede sincroniza los items primero
                pedidoBZ.sincronizarDown();
            } catch (AutorizationException ae) {
                mSessionInvalid = true;
            } catch (Exception e) {

            }

            try {
                //y luego busca el listado
                resultado = pedidoBZ.buscar(0, 0, -1, false);
            }
            catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(ArrayList<Pedido> pedidos) {
            if (mSessionInvalid == false) {
                if (mListView != null && pedidos != null) {
                    mSwipeLayout.setRefreshing(false);
                    actualizarLista(pedidos);
                }
            } else {
                logoutApplication(true);
            }

            mPd.dismiss();
        }
    }
}
