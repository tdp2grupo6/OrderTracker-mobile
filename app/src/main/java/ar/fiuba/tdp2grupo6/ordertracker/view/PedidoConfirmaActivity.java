package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;

/**
 * An activity representing a single Cliente detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClienteFueraRutaListFragment}.
 */
public class PedidoConfirmaActivity extends AppBaseActivity
        implements PedidoConfirmaListFragment.OnPedidoConfirmaListFragmentListener {

    public static final String ARG_PEDIDO_ID = "pedido_id";
    public static final int ACTIVITY_PEDIDO_CONFIRMA = 2000;

    private long mPedidoId;
    public Pedido mPedido;

    private TextView mClienteView;
    private TextView mDescuentoView;
    private TextView mTotalView;
    private TextView mSubTotalView;
    private FloatingActionButton mFbEnviar;

    private PedidoConfirmaListFragment mFragment;
    private PedidoConfirmaTask mPedidoConfirmarTask;
    private PedidoProductosBuscarTask mPedidoProductosBuscarTask;
    private PedidoActualizarTask mPedidoActualizarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_confirma);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFbEnviar = (FloatingActionButton) findViewById(R.id.fb_enviar);
        mFbEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPedidoConfirma();
            }
        });

        FloatingActionButton fbBack = (FloatingActionButton) findViewById(R.id.fb_back);
        fbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if (savedInstanceState == null) {
            this.mPedidoId = getIntent().getLongExtra(PedidoConfirmaActivity.ARG_PEDIDO_ID, 0);

            //mListFooter = (LinearLayout) view.findViewById(R.id.producto_pedido_list_footer);
            mSubTotalView = (TextView) findViewById(R.id.productos_pedido_list_subtotal);
            mDescuentoView = (TextView) findViewById(R.id.productos_pedido_list_descuento);
            mTotalView = (TextView) findViewById(R.id.productos_pedido_list_total);
            mClienteView = (TextView) findViewById(R.id.productos_pedido_list_cliente);

            mFragment = PedidoConfirmaListFragment.newInstance(mPedidoId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pedido_container, mFragment)
                    .commit();

            cargarPedido();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPedidoItemClick(Producto producto) {
        Intent intent = new Intent(this, ProductoDetailActivity.class);
        intent.putExtra("productoId", producto.id);
        intent.putExtra("productoNombre", producto.nombre);
        intent.putExtra("productoMarca", producto.marca);
        intent.putExtra("productoPrecio", producto.mostrarPrecio());
        intent.putExtra("productoDescripcion", producto.caracteristicas);
        intent.putExtra("productoCodigo", producto.mostrarCodigo());
        intent.putExtra("productoStock", producto.mostrarStock());
        intent.putExtra("productoRutaImagen", producto.getNombreImagenMiniatura());
        intent.putExtra("productoCategoria", producto.categoria.toString());
        intent.putExtra("productoEstado", producto.mostrarEstado());
        intent.putExtra("productoDescuentos", producto.descuentosJson.toString());
        startActivity(intent);
    }

    @Override
    public void onPedidoItemActualizar(PedidoItem pedidoItem) {
        if (pedidoItem != null) {
            mPedido.updateItem(pedidoItem.productoId, 0);

            mPedidoActualizarTask = new PedidoActualizarTask(this, this.mPedido, pedidoItem);
            mPedidoActualizarTask.execute((Void) null);
        }
    }

    @Override
    public void onPedidoItemVacio(boolean vacio) {
        mFbEnviar.setEnabled(!vacio);
        if (vacio) {
            mFbEnviar.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            mFbEnviar.getBackground().setColorFilter(null);
        }
    }

    public void onPedidoConfirma() {
        if (mPedido.items.size() > 0) {
            mPedidoConfirmarTask = new PedidoConfirmaTask(this, this.mPedido);
            mPedidoConfirmarTask.execute((Void) null);
        }
    }

    private void actualizarVista() {
        mFragment.actualizarLista();
        actualizarFooter();
    }

    private void actualizarFooter() {
        if (mPedido != null) {
            mClienteView.setText(mPedido.cliente.nombreCompleto + ": ");
            mDescuentoView.setText("Desc. $ " + String.format("%.2f", mPedido.getDescuento(false)));
            mTotalView.setText("Total. $ " + String.format("%.2f", mPedido.getImporte(false)));
            mSubTotalView.setText("SubTotal. $ " + String.format("%.2f", mPedido.getSubtotal(false)));
        }
    }

    private void cargarPedido() {
        mPedidoProductosBuscarTask = new PedidoProductosBuscarTask(this, mPedidoId);
        mPedidoProductosBuscarTask.execute((Void) null);
    }

    public class PedidoConfirmaTask extends AsyncTask<Void, String, Boolean> {
        private Context mContext;
        private ProgressDialog mPd;
        private boolean mSessionInvalid = false;

        private Pedido mPedido;

        public PedidoConfirmaTask(Context context, Pedido pedido) {
            this.mPedido = pedido;
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
        protected Boolean doInBackground(Void... params) {
            Boolean resultado =  true;
            try {
                //Procesa el cambio
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                pedidoBZ.confirmar(mPedido, true);
            } catch (AutorizationException ae) {
                //TODO: Hacer el deslogueo de la app
                mSessionInvalid = true;
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (mSessionInvalid == false) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                logoutApplication(true);
            }

            mPd.dismiss();
        }

    }

    public class PedidoProductosBuscarTask extends AsyncTask<Void, String, Pedido> {
        private Context mContext;
        private ProgressDialog mPd;

        private long mCliente;

        public PedidoProductosBuscarTask(Context context, long clienteId) {
            this.mContext = context;
            this.mCliente = clienteId;
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
        protected Pedido doInBackground(Void... params) {
            Pedido resultado = null;

            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.obtenerParaConfirmar(mPedidoId);
            } catch (Exception e) {
                String err = e.getLocalizedMessage();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            if (pedido!=null)
            {
                mPedido = pedido;
                actualizarVista();
            }

            mPd.dismiss();
        }

    }

    public class PedidoActualizarTask extends AsyncTask<Void, String, Pedido> {
        private Context mContext;
        private Pedido mPedido;
        private PedidoItem mPedidoItem;
        private ProgressDialog mPd;
        //private int mCantidad;

        public PedidoActualizarTask(Context context, Pedido pedido, PedidoItem pedidoItem) {
            this.mPedidoItem = pedidoItem;
            this.mPedido = pedido;
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
        protected Pedido doInBackground(Void... params) {
            Pedido resultado = null;

            try {
                //Actualiza la cantida en el item
                //mHolder.mPedidoItem.cantidad = mCantidad;

                //Procesa el cambio
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.actualizarNuevo(mPedido);
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            mPedido.deleteItem(this.mPedidoItem.productoId);
            actualizarFooter();

            mPd.dismiss();
        }

    }


}
