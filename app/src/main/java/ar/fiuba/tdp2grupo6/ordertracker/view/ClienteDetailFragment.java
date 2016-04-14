package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * A fragment representing a single Cliente detail screen.
 * This fragment is either contained in a {@link ClienteFueraRutaFragment}
 * in two-pane mode (on tablets) or a {@link ClienteDetailActivity}
 * on handsets.
 */
public class ClienteDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_CLIENTE_ID = "cliente_id";

    private CollapsingToolbarLayout mAppBarLayout;
    private View mRootView;

    private long mClienteId;
    private Cliente mCliente;
    private ClienteObtenerTask mClienteObtenerTask;
    private OnFragmentClienteDetailListener mListener;

    public interface OnFragmentClienteDetailListener {
        void onClienteAgregarPedido();
    }

    public static ClienteDetailFragment newInstance(long clienteId) {
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLIENTE_ID, clienteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (getArguments().containsKey(ARG_CLIENTE_ID)) {
            this.mClienteId = getArguments().getLong(ARG_CLIENTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_cliente_detail, container, false);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresca la lista de clientes
        this.refrescar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentClienteDetailListener) {
            mListener = (OnFragmentClienteDetailListener) context;
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

    private void refrescar() {
        mClienteObtenerTask = new ClienteObtenerTask(getContext(), this.mClienteId);
        mClienteObtenerTask.execute((Void) null);
    }

    private void actualizar(Cliente cliente) {
        if (cliente != null) {
            this.mCliente = cliente;

            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mCliente.nombreCompleto);
            }

            ((TextView) this.mRootView.findViewById(R.id.cliente_detail)).setText(mCliente.direccion);

        }
    }

    public class ClienteObtenerTask extends AsyncTask<Void, String, Cliente> {
        private Context mContext;
        private long mId;

        public ClienteObtenerTask(Context context, long id) {
            this.mContext = context;
            this.mId = id;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cliente doInBackground(Void... params) {
            Cliente resultado = null;

            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                ClienteBZ clienteBz = new ClienteBZ(this.mContext);
                resultado = clienteBz.obtener(this.mId);
            } catch (Exception e) {
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Cliente cliente) {
            actualizar(cliente);
        }

    }
}
