package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoViewHolder;

public class PedidoListFragment extends Fragment implements PedidoProductoAdapter.OnItemClickListener {

    private static final String ARG_CATEGORIA_ID = "categoria_id";

    private long mCategoriaId;

    private boolean mTwoPane;
    private RecyclerView mReciclerView;
    private TextView mEmptyView;
    private PedidoProductoAdapter mReciclerAdapter;
    private PedidoProductosBuscarTask mPedidoProductosBuscarTask;
    private PedidoActualizarTask mPedidoActualizarTask;

    private OnPedidoListFragmentListener mListener;
    public interface OnPedidoListFragmentListener {
        void onPedidoItemClick(Producto producto);
        void onPedidoActualizar();
    }

    public static PedidoListFragment newInstance(long categoriaId) {
        PedidoListFragment fragment = new PedidoListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORIA_ID, categoriaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategoriaId = getArguments().getLong(ARG_CATEGORIA_ID);
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

        //Set the list of productos
        mEmptyView = (TextView) view.findViewById(R.id.productos_pedido_list_empty);
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
            if (holder.mProducto != null ) {
                //ProductoDetailFragment fragment = ProductoDetailFragment.newInstance();
                if (mTwoPane) {
                    /*
                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.pedido_detail_container, fragment)
                            .commit();
                    */
                } else {
                    mListener.onPedidoItemClick(holder.mProducto);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onItemPlusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {

        if (nuevaCantidad <= holder.mProducto.stock) {
            holder.getQuantityText().setText(String.valueOf(nuevaCantidad));
            Pedido pedido = ((PedidoActivity)getActivity()).mPedido;

            mPedidoActualizarTask = new PedidoActualizarTask(getContext(), pedido, holder, nuevaCantidad);
            mPedidoActualizarTask.execute((Void) null);
        } else {
            holder.getQuantityText().setText(String.valueOf(viejaCantidad));
            Toast.makeText(getContext(), "No se puede agregar mas items", Toast.LENGTH_SHORT).show();
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

        if (nuevaCantidad > 0 && nuevaCantidad <= holder.mProducto.stock ) {
            //holder.getQuantityText().setText(String.valueOf(nuevaCantidad));
            Pedido pedido = ((PedidoActivity)getActivity()).mPedido;

            mPedidoActualizarTask = new PedidoActualizarTask(getContext(), pedido, holder, nuevaCantidad);
            mPedidoActualizarTask.execute((Void) null);
        } else {
            holder.getQuantityText().setText(String.valueOf(viejaCantidad));
            Toast.makeText(getContext(), "No se puede procesar esa cantidad de items", Toast.LENGTH_SHORT).show();
        }

    }

    private void refrescarLista() {
        mPedidoProductosBuscarTask = new PedidoProductosBuscarTask(getContext());
        mPedidoProductosBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Producto> pedidoProductos) {
        if (mReciclerView != null) {
            mReciclerAdapter = new PedidoProductoAdapter(this, pedidoProductos, mTwoPane);
            mReciclerView.setAdapter(mReciclerAdapter);
            //mReciclerView.setEmptyView(mEmptyView);
        }
    }

    public class PedidoProductosBuscarTask extends AsyncTask<Void, String, ArrayList<Producto>> {
        private Context mContext;

        public PedidoProductosBuscarTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Producto> doInBackground(Void... params) {
            ArrayList<Producto> resultado = new ArrayList<Producto>();

            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                ProductoBZ productoBZ = new ProductoBZ(this.mContext);
                resultado = productoBZ.listar();
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(ArrayList<Producto> pedidoProductos) {
            if (mReciclerView != null && pedidoProductos!=null)
            {
                actualizarLista(pedidoProductos);
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
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                resultado = pedidoBZ.actualizarProducto(mPedido, mHolder.mProducto, mCantidad);
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Pedido pedido) {
            mListener.onPedidoActualizar();
        }

    }
}
