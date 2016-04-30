package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ClienteAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClienteFueraRutaListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClienteFueraRutaListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClienteFueraRutaListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private boolean mTwoPane;
    private ListView mListView;
    private TextView mEmptyView;
    private ClienteAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private ClientesBuscarTask mClientesBuscarTask;

    private OnFragmentInteractionListener mListener;

    public ClienteFueraRutaListFragment() {
        // Required empty public constructor
    }

    public static ClienteFueraRutaListFragment newInstance() { //String param1, String param2) {
        ClienteFueraRutaListFragment fragment = new ClienteFueraRutaListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        View view = inflater.inflate(R.layout.fragment_list_cliente_fuera_ruta, container, false);

        //Set the swipe for refresh
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_cliente);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }

        });

        //Set the list of items
        mEmptyView = (TextView) view.findViewById(R.id.clientes_list_empty);
        mListView = (ListView) view.findViewById(R.id.clientes_list);
        mListView.setOnItemClickListener(this);

        if (view.findViewById(R.id.cliente_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        inflater.inflate(R.menu.fragment_menu_cliente_fuera_ruta, menu);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Cliente cliente = (Cliente)parent.getItemAtPosition(position);
            if (cliente != null ) {
                if (mTwoPane) {
                    ClienteDetailFragment fragment = ClienteDetailFragment.newInstance(cliente.id, "");
                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.cliente_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(this.getContext(), ClienteDetailActivity.class);
                    intent.putExtra(ClienteDetailActivity.ARG_CLIENTE_ID, cliente.id);

                    this.getContext().startActivity(intent);
                }
            }
        } catch (Exception e) {
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void refrescarLista() {
        mClientesBuscarTask = new ClientesBuscarTask(getContext());
        mClientesBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Cliente> clientes) {
        if (mListView != null) {
            mListAdapter = new ClienteAdapter(getContext(), clientes);
            mListView.setAdapter(mListAdapter);
            mListView.setEmptyView(mEmptyView);
        }
    }

    public class ClientesBuscarTask extends AsyncTask<Void, String, ArrayList<Cliente>> {
        private Context mContext;
        //private ProgressDialog mPd;

        public ClientesBuscarTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            /*
            mPd = new ProgressDialog(getActivity());
            mPd.setMessage(mContext.getResources().getString(R.string.msg_procesando));
            mPd.setCancelable(false);
            mPd.getWindow().setGravity(Gravity.CENTER);
            mPd.show();
            */
        }

        @Override
        protected ArrayList<Cliente> doInBackground(Void... params) {
            ArrayList<Cliente> resultado = new ArrayList<Cliente>();

            ClienteBZ clienteBz = new ClienteBZ(this.mContext);
            try {
                //Si puede sincroniza los clientes primero
                clienteBz.sincronizar();
            } catch (Exception e) {
            }

            try {
                //y luego busca el listado
                resultado = clienteBz.listar();
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(ArrayList<Cliente> clientes) {

            //mPd.dismiss();
            if (mListView != null && clientes!=null)
            {
                mSwipeLayout.setRefreshing(false);
                actualizarLista(clientes);
            }
        }

    }
}
