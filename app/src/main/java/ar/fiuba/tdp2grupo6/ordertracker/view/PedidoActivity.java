package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
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

    public static final String ARG_VISITA_ID = "visita_id";
    public static final String ARG_CLIENTE_ID = "cliente_id";
    public static final int ACTIVITY_PEDIDO = 1000;

    public Pedido mPedido;
    //public String mMarcaFiltro;
    public boolean mShowStockMensaje = true;
    public boolean mAgregaItemSinStock = false;

    private long mClienteId;
    private long mVisitaId;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView mClienteView;
    private TextView mTotalView;
    private Spinner mSpinnerBrand;

    private CategoryPagerAdapter mCategoryPagerAdapter;
    private PedidoProductosBuscarTask mPedidoProductosBuscarTask;
    private PedidoActualizarTask mPedidoActualizarTask;

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
                onPedidoConfirma();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.mClienteId = getIntent().getLongExtra(PedidoActivity.ARG_CLIENTE_ID, 0);
            this.mVisitaId = getIntent().getLongExtra(PedidoActivity.ARG_VISITA_ID, 0);

            //mListFooter = (LinearLayout) view.findViewById(R.id.producto_pedido_list_footer);
            mTotalView = (TextView) findViewById(R.id.productos_pedido_list_money);
            mClienteView = (TextView) findViewById(R.id.productos_pedido_list_cliente);

            // Setea el viewpager
            mViewPager = (ViewPager) findViewById(R.id.container);
            mTabLayout = (TabLayout) findViewById(R.id.tabs);

            cargarPedido();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu_pedido, menu);
        MenuItem item = menu.findItem(R.id.spinner_brand);

        mSpinnerBrand = (Spinner) MenuItemCompat.getActionView(item);
        //actualizarHeader();
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PedidoConfirmaActivity.ACTIVITY_PEDIDO_CONFIRMA) : {
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK);
                    finish();
                    //Toast.makeText(this, "Se ha confirmado el Pedido!", Toast.LENGTH_LONG).show();
                } else {
                    mCategoryPagerAdapter.setAllCategoryDirty(true);
                    mCategoryPagerAdapter.notifyDataSetChanged();

                    cargarPedido();
                }
                break;
            }
        }
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
        startActivity(intent);
    }

    @Override
    public void onPedidoItemActualizar(PedidoListFragment fragment, PedidoItem pedidoItem) {
        if (pedidoItem != null) {
            mPedido.updateItem(pedidoItem.productoId, pedidoItem.cantidad);

            mPedidoActualizarTask = new PedidoActualizarTask(this, this.mPedido);
            mPedidoActualizarTask.execute((Void) null);

            //Actualizo las pantallas que correspondo
            if (fragment.mCategoriaId != 0)
                mCategoryPagerAdapter.setCategoryDirty(0, true);
            else
                mCategoryPagerAdapter.setCategoryDirty(pedidoItem.producto.categoria.id, true);
            mCategoryPagerAdapter.notifyDataSetChanged();

        }
    }

    public void onPedidoConfirma() {
        Intent intent = new Intent(this, PedidoConfirmaActivity.class);
        intent.putExtra(PedidoConfirmaActivity.ARG_PEDIDO_ID, mPedido.id);
        startActivityForResult(intent, PedidoConfirmaActivity.ACTIVITY_PEDIDO_CONFIRMA);
        //startActivity(intent);

        //PedidoConfirmaTask mPedidoConfirmarTask = new PedidoConfirmaTask(this, this.mPedido);
        //mPedidoConfirmarTask.execute((Void) null);
    }

    private void actualizarVista() {
        mCategoryPagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mCategoryPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        actualizarFooter();
    }


    /*
    private void actualizarHeader() {

        if (mPedido != null && mSpinnerBrand != null) {
            ArrayList<String> marcas = new ArrayList<String>();
            marcas.addAll(mPedido.marcas);
            marcas.add(0, "Todas");

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, marcas);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Setea el Spinner
            mSpinnerBrand.setAdapter(dataAdapter);
        }

    }
    */

    private void actualizarFooter() {
        if (mPedido != null) {
            mClienteView.setText(mPedido.cliente.nombreCompleto);
            mTotalView.setText(String.format("%.2f", mPedido.getImporte(false)));
        }
    }

    private void cargarPedido() {
        mPedidoProductosBuscarTask = new PedidoProductosBuscarTask(this, mClienteId);
        mPedidoProductosBuscarTask.execute((Void) null);
    }

    /*
    public class PedidoConfirmaTask extends AsyncTask<Void, String, Boolean> {
        private Context mContext;
        private ProgressDialog mPd;

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
                pedidoBZ.confirmar(mPedido.id);
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            mPd.dismiss();

            setResult(Activity.RESULT_OK);
            finish();
        }

    }
    */

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
                resultado = pedidoBZ.obtenerParaCliente(mVisitaId, mClienteId);
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
        //private PedidoItem mPedidoItem;
        //private int mCantidad;

        public PedidoActualizarTask(Context context, Pedido pedido) {
            //this.mPedidoItem = pedidoItem;
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
            actualizarFooter();
        }

    }


    public class CategoryPagerAdapter extends FragmentStatePagerAdapter {

        private Map<Long,Boolean> mCategoryDirtyMap;

        public CategoryPagerAdapter(FragmentManager fm) {
            super(fm);
            mCategoryDirtyMap = new HashMap<Long, Boolean>();
        }

        @Override
        public Fragment getItem(int i) {
            //Setea la categoria como limpia
            long catedoriaId = getItemCategory(i);
            setCategoryDirty(catedoriaId, false);

            PedidoListFragment fragment = PedidoListFragment.newInstance(catedoriaId, mClienteId);
            return fragment;
        }

        @Override
        public int getCount() {
            return mPedido.categorias.size() + 1;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof PedidoListFragment) {
                long categoriaId = ((PedidoListFragment)object).mCategoriaId;

                if (getCategoryDirty(categoriaId)) {
                    setCategoryDirty(categoriaId, false);
                    return POSITION_NONE;
                }
            }
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String categoriaNombre = "TODAS";
            if (position > 0)
                categoriaNombre = mPedido.categorias.get(position-1).nombre;
            return categoriaNombre.toUpperCase();
        }

        public long getItemCategory(int i) {
            long categoriaId = 0;
            if (i > 0)
                categoriaId = mPedido.categorias.get(i-1).id;
            return categoriaId;
        }

        public void setAllCategoryDirty(boolean isDirty) {
            for (Map.Entry<Long, Boolean> entry : mCategoryDirtyMap.entrySet()) {
                entry.setValue(isDirty);
            }
        }

        public void setCategoryDirty(long categoriaId, boolean isDirty) {
            mCategoryDirtyMap.put(categoriaId, isDirty);
        }

        public boolean getCategoryDirty(long categoriaId) {
            boolean response = true;
            if (mCategoryDirtyMap.containsKey(categoriaId)) {
                response = mCategoryDirtyMap.get(categoriaId);
            } else {
                mCategoryDirtyMap.put(categoriaId, response);
            }
            return response;
        }

    }


}
