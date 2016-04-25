package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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

    public long mCategoriaId;
    private long mClienteId;

    //Propiedades para actualizar Item
    private boolean mShowStockMensaje = true;
    private boolean mAgregaItemSinStock = false;
    private PedidoProductoViewHolder mHolder;
    private int mPosition;
    private int mViejaCantidad;
    private int mNuevaCantidad;

    private boolean mTwoPane;
    private RecyclerView mReciclerView;
    private TextView mEmptyView;
    private LinearLayout mListFooter;
    private PedidoProductoAdapter mReciclerAdapter;

    private PedidoActivity mPedidoActivity;
    private OnPedidoListFragmentListener mListener;
    public interface OnPedidoListFragmentListener {
        void onPedidoItemClick(Producto producto);
        void onPedidoItemActualizar(PedidoListFragment fragment, PedidoItem pedidoItem);
        //void onPedidoActualizar(Pedido pedido);
        //void onPedidoConfirma(Pedido pedido);
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
        this.actualizarLista();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Tiene options menu
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_pedido, container, false);

        //Set the list of items
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoListFragmentListener) {
            mListener = (OnPedidoListFragmentListener) context;
            //if (context instanceof PedidoActivity)
            //    mPedidoActivity = (PedidoActivity) context;

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

    public void actualizarCantidadItem() {
        mHolder.getQuantityText().setText(String.valueOf(mNuevaCantidad));

        PedidoItem pedidoItem = mHolder.mPedidoItem;
        pedidoItem.cantidad = mNuevaCantidad;

        mListener.onPedidoItemActualizar(this, pedidoItem);
    }

    @Override
    public void onItemClick(PedidoProductoViewHolder holder, int position) {
        mListener.onPedidoItemClick(holder.mPedidoItem.producto);
    }

    @Override
    public void onItemPlusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {
        mHolder = holder;
        mPosition = position;
        mViejaCantidad = viejaCantidad;
        mNuevaCantidad = nuevaCantidad;

        if (nuevaCantidad <= mHolder.mPedidoItem.producto.stock || mAgregaItemSinStock) {
            actualizarCantidadItem();
        } else {
            if (mShowStockMensaje) {

                AlertDialog.Builder dataDialogBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                dataDialogBuilder.setTitle(getContext().getResources().getString(R.string.title_popup_agregar_sin_stock));
                dataDialogBuilder.setMessage(getContext().getResources().getString(R.string.error_agregar_sin_stock));
                dataDialogBuilder.setCancelable(false);
                dataDialogBuilder.setPositiveButton(getContext().getResources().getString(R.string.btn_si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mShowStockMensaje = false;
                        mAgregaItemSinStock = true;

                        actualizarCantidadItem();
                        dialog.cancel();
                    }
                }).setNegativeButton(getContext().getResources().getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mShowStockMensaje = false;
                        mAgregaItemSinStock = false;

                        dialog.cancel();
                    }
                }).setNeutralButton(getContext().getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // create an alert dialog
                dataDialogBuilder.create().show();
            } else {
                mHolder.getQuantityText().setText(String.valueOf(mViejaCantidad));
                //Toast.makeText(getContext(), "No se puede agregar mas items, alcanzo la disponibilidad maxima", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onItemMinusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {
        mHolder = holder;
        mPosition = position;
        mViejaCantidad = viejaCantidad;
        mNuevaCantidad = nuevaCantidad;

        if (nuevaCantidad >= 0 ) {
            actualizarCantidadItem();
        } else {
            mHolder.getQuantityText().setText(String.valueOf(mViejaCantidad));
            //Toast.makeText(getContext(), "No se puede quitar mas items", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemQuantityClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad) {
        mHolder = holder;
        mPosition = position;
        mViejaCantidad = viejaCantidad;
        mNuevaCantidad = nuevaCantidad;
        if (nuevaCantidad > 0 && (nuevaCantidad <= mHolder.mPedidoItem.producto.stock || mAgregaItemSinStock)) {
            actualizarCantidadItem();
        } else {
            mHolder.getQuantityText().setText(String.valueOf(mViejaCantidad));
            //Toast.makeText(getContext(), "No se puede procesar esa cantidad de items", Toast.LENGTH_SHORT).show();
        }

    }

    private void actualizarLista() {
        if (mReciclerView != null) {
            mPedidoActivity = (PedidoActivity) getActivity();

            ArrayList<PedidoItem> list = null;
            if (mCategoriaId == 0)
                list = new ArrayList<PedidoItem>(mPedidoActivity.mPedido.items.values());
            else
                list = (ArrayList<PedidoItem>) mPedidoActivity.mPedido.itemsByCategory.get(String.valueOf(mCategoriaId));

            mReciclerAdapter = new PedidoProductoAdapter(this, list, mTwoPane);
            mReciclerView.setAdapter(mReciclerAdapter);
        }
    }

    public void invalidateRecicler() {
        this.onResume();
    }

}
