package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Utils;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoItemAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ProductoAdapter;

public class PedidosActivity extends AppBaseActivity {

    private final int MENU_INDEX = 3;

    private Context mContext;
    private ListView mListView;
    private TextView mEmptyView;
    private SwipeRefreshLayout mSwipeLayout;
    private PedidosBuscarTask mPedidosBuscarTask;
    private PedidoAdapter mListAdapter;

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
                Pedido pedido = (Pedido) parent.getItemAtPosition(position);

                //Configura el total
                final View viewHeaderDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_pedido_detalle_title, null);
                final TextView pedidoNumeroView = (TextView) viewHeaderDialog.findViewById(R.id.pedido_title_numero);
                final TextView pedidoClienteView = (TextView) viewHeaderDialog.findViewById(R.id.pedido_title_cliente);
                final TextView pedidoFechaView = (TextView) viewHeaderDialog.findViewById(R.id.pedido_title_fecha);
                pedidoClienteView.setText(pedido.cliente.nombreCompleto);
                pedidoFechaView.setText(Utils.date2human(pedido.fechaRealizado,true));
                pedidoNumeroView.setText("# " + Utils.long2human(pedido.idServer, (short)5));

                //Configura el total
                final View viewFooterDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_pedido_detalle, null);
                final TextView pedidoMontoView = (TextView) viewFooterDialog.findViewById(R.id.pedido_monto);
                pedidoMontoView.setText("Importe Total $ " + Utils.double2human(pedido.getImporte(false), (short)2));

                //Configura la lista
                ArrayList<PedidoItem> pedidoItems = new ArrayList<PedidoItem>(pedido.items.values());
                PedidoItemAdapter adapter = new PedidoItemAdapter(mContext, pedidoItems);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//, R.style.AlertDialogCustom);
                //builder.setTitle(mContext.getResources().getString(R.string.title_popup_comentario));
                builder.setCustomTitle(viewHeaderDialog);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setView(viewFooterDialog);
                builder.setCancelable(false);
                builder.setNegativeButton(R.string.btn_cancelarComentario, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create();
                AlertDialog alertDialog = builder.create();
                ListView listView = alertDialog.getListView();
                listView.setDivider(new ColorDrawable(Color.BLACK));
                listView.setDividerHeight(1);
                alertDialog.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu_cliente_fuera_ruta, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        EditText searchField = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (mListAdapter != null) {
                    mListAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // do stuff
                return true;
        }
        return false;
    }

    private void refrescarLista() {
        mPedidosBuscarTask = new PedidosBuscarTask(this);
        mPedidosBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Pedido> pedidos) {
        if (mListView != null) {
            mListAdapter = new PedidoAdapter(this, pedidos);
            mListView.setAdapter(mListAdapter);
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
