package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cliente_fuera_ruta, container, false);

        //Set the swipe for refresh
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }

        });

        //Set the list of productos
        mListView = (ListView) view.findViewById(R.id.clientes_list);

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
            ClienteAdapter adapter = new ClienteAdapter(getContext(), clientes);
            mListView.setAdapter(adapter);
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
                ClienteBZ clienteBz = new ClienteBZ(this.mContext);
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
