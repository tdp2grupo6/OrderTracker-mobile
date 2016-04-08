package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
 * {@link ClienteFueraRutaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClienteFueraRutaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClienteFueraRutaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private TextView mEmptyView;
    private ClienteAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private ClientesBuscarTask mClientesBuscarTask;

    private OnFragmentInteractionListener mListener;

    public ClienteFueraRutaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClienteFueraRutaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClienteFueraRutaFragment newInstance(String param1, String param2) {
        ClienteFueraRutaFragment fragment = new ClienteFueraRutaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        View view = inflater.inflate(R.layout.fragment_cliente_fuera_ruta, container, false);

        //Set the swipe for refresh
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_cliente);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }

        });

        //Set the list of productos
        mListView = (ListView) view.findViewById(R.id.clientes_list);
        mEmptyView = (TextView) view.findViewById(R.id.clientes_list_empty);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

        /*
        final EditText searchField = (EditText) menu.findItem(R.id.action_search).getActionView();
        */

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
        public ClientesBuscarTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Cliente> doInBackground(Void... params) {
            ArrayList<Cliente> resultado = new ArrayList<Cliente>();

            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                ClienteBZ clienteBz = new ClienteBZ(this.mContext);
                clienteBz.sincronizar();
                resultado = clienteBz.listar();
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(ArrayList<Cliente> clientes) {
            if (mListView != null && clientes!=null)
            {
                mSwipeLayout.setRefreshing(false);
                actualizarLista(clientes);
            }
        }

    }
}
