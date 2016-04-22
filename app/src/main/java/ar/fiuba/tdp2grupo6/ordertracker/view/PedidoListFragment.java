package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoViewHolder;

public class PedidoListFragment extends Fragment implements PedidoProductoAdapter.OnItemClickListener {

    private static final String ARG_CATEGORIA_ID = "categoria_id";
    private static final String ARG_CLIENTE_ID = "cliente_id";

    private long mCategoriaId;
    private long mClienteId;


    private boolean mTwoPane;
    private RecyclerView mReciclerView;
    private TextView mEmptyView;
    private TextView mTotalView;
    private LinearLayout mListFooter;
    private PedidoProductoAdapter mReciclerAdapter;
    private PedidoProductosBuscarTask mPedidoProductosBuscarTask;
    private PedidoActualizarTask mPedidoActualizarTask;

    private OnPedidoListFragmentListener mListener;
    public interface OnPedidoListFragmentListener {
        void onPedidoItemClick(Producto producto);
        void onPedidoActualizar(Pedido pedido);
    }

    public static PedidoListFragment newInstance(long categoriaId, long clienteId) {
        PedidoListFragment fragment = new PedidoListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORIA_ID, categoriaId);
        args.putLong(ARG_CLIENTE_ID, clienteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategoriaId = getArguments().getLong(ARG_CATEGORIA_ID);
            mClienteId = getArguments().getLong(ARG_CLIENTE_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresca la lista de clientes
        this.refrescarLista();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Tiene options menu
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_pedido, container, false);

        //Set the list of items
        //mListFooter = (LinearLayout) view.findViewById(R.id.producto_pedido_list_footer);
        mTotalView = (TextView) view.findViewById(R.id.productos_pedido_list_money);
        //mEmptyView = (TextView) view.findViewById(R.id.productos_pedido_list_empty);
        mReciclerView = (RecyclerView) view.findViewById(R.id.productos_pedido_list);

        if (view.findViewById(R.id.producto_pedido_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        return view;
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoListFragmentListener) {
            mListener = (OnPedidoListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_list_pedido, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        EditText searchField = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (mReciclerAdapter != null) {
                    mReciclerAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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

    @Override
    public void onItemClick(PedidoProductoViewHolder holder, int position) {
        try {
            /*
            // TODO: hay que cambiar el detalle de producto para que sea un fragment

            if (holder.mPedidoItem != null ) {
                //ProductoDetailFragment fragment = ProductoDetailFragment.newInstance();
                if (mTwoPane) {
                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.pedido_detail_container, fragment)
                            .commit();

                } else {
                    mListener.onPedidoItemClick(holder.mPedidoItem);
                }
            }
            */

            Intent intent = new Intent(getContext(), ProductoDetailActivity.class);
            Producto prod = holder.mPedidoItem.producto;
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
        } catch (Exception e) {
        }
    }

    @Override
    public void onItemPlusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {

        if (nuevaCantidad <= holder.mPedidoItem.producto.stock) {
            holder.getQuantityText().setText(String.valueOf(nuevaCantidad));
            Pedido pedido = ((PedidoActivity)getActivity()).mPedido;

            mPedidoActualizarTask = new PedidoActualizarTask(getContext(), pedido, holder, nuevaCantidad);
            mPedidoActualizarTask.execute((Void) null);
        } else {
            holder.getQuantityText().setText(String.valueOf(viejaCantidad));
            Toast.makeText(getContext(), "No se puede agregar mas items, alcanzo la disponibilidad maxima", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemMinusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {

        if (nuevaCantidad >= 0 ) {
            holder.getQuantityText().setText(String.valueOf(nuevaCantidad));
            Pedido pedido = ((PedidoActivity)getActivity()).mPedido;

            mPedidoActualizarTask = new PedidoActualizarTask(getContext(), pedido, holder, nuevaCantidad);
            mPedidoActualizarTask.execute((Void) null);
        } else {
            holder.getQuantityText().setText(String.valueOf(viejaCantidad));
            Toast.makeText(getContext(), "No se puede quitar mas items", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemQuantityClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {

        if (nuevaCantidad > 0 && nuevaCantidad <= holder.mPedidoItem.producto.stock ) {
            holder.getQuantityText().setText(String.valueOf(nuevaCantidad));
            Pedido pedido = ((PedidoActivity)getActivity()).mPedido;

            mPedidoActualizarTask = new PedidoActualizarTask(getContext(), pedido, holder, nuevaCantidad);
            mPedidoActualizarTask.execute((Void) null);
        } else {
            holder.getQuantityText().setText(String.valueOf(viejaCantidad));
            Toast.makeText(getContext(), "No se puede procesar esa cantidad de items", Toast.LENGTH_SHORT).show();
        }

    }

    private void refrescarLista() {
        mPedidoProductosBuscarTask = new PedidoProductosBuscarTask(getContext(), mClienteId);
        mPedidoProductosBuscarTask.execute((Void) null);
    }

    private void actualizarLista(Pedido pedido) {
        if (mReciclerView != null) {
            ArrayList<PedidoItem> list = new ArrayList<PedidoItem>(pedido.items.values());
            mReciclerAdapter = new PedidoProductoAdapter(this, list, mTwoPane);
            mReciclerView.setAdapter(mReciclerAdapter);

            actualizarFooter(pedido);

            mListener.onPedidoActualizar(pedido);
        }
    }

    private void actualizarFooter(Pedido pedido) {
        if (pedido != null) {
            mTotalView.setText("Total: $" + String.format("%.2f", pedido.importe));
        }
    }

    public class PedidoProductosBuscarTask extends AsyncTask<Void, String, Pedido> {
        private Context mContext;
        private long mCliente;

        public PedidoProductosBuscarTask(Context context, long clienteId) {
            this.mContext = context;
            this.mCliente = clienteId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Pedido doInBackground(Void... params) {
            Pedido resultado = null;

            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.obtenerParaCliente(mClienteId);
            } catch (Exception e) {
                String err = e.getLocalizedMessage();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            if (mReciclerView != null && pedido!=null)
            {
                actualizarLista(pedido);
            }
        }

    }

    public class PedidoActualizarTask extends AsyncTask<Void, String, Pedido> {
        private Context mContext;
        private Pedido mPedido;
        private PedidoProductoViewHolder mHolder;
        private int mCantidad;

        public PedidoActualizarTask(Context context, Pedido pedido, PedidoProductoViewHolder holder, int cantidad) {
            this.mHolder = holder;
            this.mPedido = pedido;
            this.mCantidad = cantidad;
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
                mHolder.mPedidoItem.cantidad = mCantidad;

                //Procesa el cambio
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.actualizarPendiente(mPedido, mHolder.mPedidoItem);
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            actualizarFooter(pedido);
            mListener.onPedidoActualizar(pedido);
        }

    }
}
