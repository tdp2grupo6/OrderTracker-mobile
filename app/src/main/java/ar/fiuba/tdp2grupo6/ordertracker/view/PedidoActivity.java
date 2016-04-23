package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoViewHolder;

/**
 * An activity representing a single Cliente detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClienteFueraRutaListFragment}.
 */
public class PedidoActivity extends AppBaseActivity
        implements PedidoListFragment.OnPedidoListFragmentListener {

    public static final String ARG_CLIENTE_ID = "cliente_id";

    private long mClienteId;
    public Pedido mPedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */

                onPedidoConfirma(mPedido);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.mClienteId = getIntent().getLongExtra(PedidoActivity.ARG_CLIENTE_ID, 0);

            PedidoListFragment fragment = PedidoListFragment.newInstance(0, mClienteId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pedido_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onPedidoItemClick(Producto producto) {
        /*
        // TODO: hay que cambiar el detalle de producto para que sea un fragment
        if (producto != null ) {
            //ProductoDetailFragment fragment = ProductoDetailFragment.newInstance();
            if (mTwoPane) {
                this.getFragmentManager().beginTransaction()
                        .replace(R.id.pedido_detail_container, fragment)
                        .commit();
            } else {
            }
        }
        */

        Intent intent = new Intent(this, ProductoDetailActivity.class);
        intent.putExtra("productoId", producto.id);
        intent.putExtra("productoNombre", producto.nombre);
        intent.putExtra("productoMarca", producto.marca);
        intent.putExtra("productoPrecio", producto.mostrarPrecio());
        intent.putExtra("productoDescripcion", producto.caracteristicas);
        intent.putExtra("productoCodigo", producto.mostrarCodigo());
        intent.putExtra("productoStock", producto.mostrarStock());
        intent.putExtra("productoRutaImagen", producto.getNombreImagenMiniatura());
        intent.putExtra("productoCategoria", producto.categoria);
        intent.putExtra("productoEstado", producto.mostrarEstado());
        startActivity(intent);

    }

    @Override
    public void onPedidoActualizar(Pedido pedido) {
        this.mPedido = pedido;
    }

    @Override
    public void onPedidoConfirma(Pedido pedido) {
        PedidoConfirmaTask mPedidoConfirmarTask = new PedidoConfirmaTask(this, pedido);
        mPedidoConfirmarTask.execute((Void) null);
    }

    public class PedidoConfirmaTask extends AsyncTask<Void, String, Pedido> {
        private Context mContext;
        private Pedido mPedido;

        public PedidoConfirmaTask(Context context, Pedido pedido) {
            this.mPedido = pedido;
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Pedido doInBackground(Void... params) {
            Pedido resultado = null;

            try {
                //Procesa el cambio
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.confirmar(mPedido);
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            setResult(Activity.RESULT_OK);
            finish();
        }

    }

}
