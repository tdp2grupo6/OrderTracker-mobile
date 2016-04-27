package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoConfirmaAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoConfirmaTouchHelper;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoConfirmaViewHolder;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoViewHolder;

public class PedidoConfirmaListFragment extends Fragment implements PedidoProductoConfirmaAdapter.OnItemListener {

    private static final String ARG_PEDIDO_ID = "pedido_id";

    private long mPedidoId;

    private RecyclerView mReciclerView;
    private PedidoProductoConfirmaAdapter mReciclerAdapter;
    private TextView mEmptyView;
    private LinearLayout mListFooter;

    private PedidoConfirmaActivity mPedidoConfirmaActivity;
    private OnPedidoConfirmaListFragmentListener mListener;

    public interface OnPedidoConfirmaListFragmentListener {
        void onPedidoItemClick(Producto producto);
        void onPedidoItemActualizar(PedidoItem pedidoItem);
    }

    public static PedidoConfirmaListFragment newInstance(long pedidoId) {
        PedidoConfirmaListFragment fragment = new PedidoConfirmaListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PEDIDO_ID, pedidoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPedidoId = getArguments().getLong(ARG_PEDIDO_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresca la lista de clientes
        //this.actualizarLista();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_pedido, container, false);

        //Set the list of items
        //mEmptyView = (TextView) view.findViewById(R.id.productos_pedido_list_empty);
        mReciclerView = (RecyclerView) view.findViewById(R.id.productos_pedido_list);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoConfirmaListFragmentListener) {
            mListener = (OnPedidoConfirmaListFragmentListener) context;
            if (context instanceof PedidoConfirmaActivity)
                mPedidoConfirmaActivity = (PedidoConfirmaActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPedidoConfirmaListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mPedidoConfirmaActivity = null;
    }

    @Override
    public void onItemClick(PedidoProductoConfirmaViewHolder holder, int position) {
        mListener.onPedidoItemClick(holder.mPedidoItem.producto);
    }

    @Override
    public void onItemDelete(PedidoItem pedidoItem, int position) {
        mListener.onPedidoItemActualizar(pedidoItem);
    }

    public void actualizarLista() {
        if (mReciclerView != null) {
            ArrayList<PedidoItem> list =  mPedidoConfirmaActivity.mPedido.getItems(0, "");
            mReciclerAdapter = new PedidoProductoConfirmaAdapter(this, list, false);
            mReciclerView.setAdapter(mReciclerAdapter);

            ItemTouchHelper.Callback callback = new PedidoProductoConfirmaTouchHelper(mReciclerAdapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(mReciclerView);
        }
    }

}
