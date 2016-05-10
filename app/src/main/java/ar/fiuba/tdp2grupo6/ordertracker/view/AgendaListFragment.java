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
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.AgendaAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.AgendaViewHolder;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ClienteViewHolder;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoAdapter;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.PedidoProductoViewHolder;

public class AgendaListFragment extends Fragment implements AgendaAdapter.OnItemClickListener {

    private static final String ARG_DIA_ID = "dia_id";

    public int mDiaId;

    private ClienteViewHolder mHolder;
    private AgendaAdapter mReciclerAdapter;
    private RecyclerView mReciclerView;

    private boolean mTwoPane;
    private TextView mEmptyView;
    private LinearLayout mListFooter;

    private AgendaActivity mAgendaActivity;
    private OnAgendaListFragmentListener mListener;
    public interface OnAgendaListFragmentListener {
        void onClienteClick(Cliente cliente);
    }

    public static AgendaListFragment newInstance(int diaId) {
        AgendaListFragment fragment = new AgendaListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DIA_ID, diaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDiaId = getArguments().getInt(ARG_DIA_ID);
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
        View view = inflater.inflate(R.layout.fragment_list_agenda, container, false);

        //Set the list of items
        mEmptyView = (TextView) view.findViewById(R.id.agenda_list_empty);
        mReciclerView = (RecyclerView) view.findViewById(R.id.agenda_list);

        /*
        if (view.findViewById(R.id.agenda_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        */

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAgendaListFragmentListener) {
            mListener = (OnAgendaListFragmentListener) context;
            if (context instanceof AgendaActivity)
                mAgendaActivity = (AgendaActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAgendaListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mAgendaActivity = null;
    }

    @Override
    public void onItemClick(AgendaViewHolder holder, int position) {
        mListener.onClienteClick(holder.mCliente);
    }

    private void actualizarLista() {
        if (mReciclerView != null) {
            ArrayList<AgendaItem> list =  mAgendaActivity.mAgenda.getAgendaItem(mDiaId);
            if (list != null && list.size() != 0) {
                mReciclerAdapter = new AgendaAdapter(this, list);
                mReciclerView.setAdapter(mReciclerAdapter);
                mReciclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            } else {
                mReciclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void invalidateRecicler() {
        this.onResume();
    }

}
