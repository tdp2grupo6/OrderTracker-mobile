package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * A fragment representing a single Cliente detail screen.
 * This fragment is either contained in a {@link ClienteFueraRutaListFragment}
 * in two-pane mode (on tablets) or a {@link ClienteDetailActivity}
 * on handsets.
 */
public class ClienteDetailFragment extends Fragment { //implements OnMapReadyCallback {
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
    private MapView mMapView;
    private GoogleMap mMap;


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

        /*
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */
        // Gets the MapView from the XML layout and creates it
        mMapView = (MapView) this.mRootView.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        if (mMapView != null)
            mMapView.onResume();
        super.onResume();

        // Refresca la lista de clientes
        this.refrescar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null)
            mMapView.onLowMemory();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentClienteDetailListener) {
            mListener = (OnFragmentClienteDetailListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentClienteDetailListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    */

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
            String razonSocial = mCliente.razonSocial.trim().isEmpty()? "NO DISPONIBLE":mCliente.razonSocial.trim();
            String telefono = mCliente.telefono.trim().isEmpty()? "NO DISPONIBLE":mCliente.telefono.trim();
            String mail = mCliente.email.trim().isEmpty()? "NO DISPONIBLE":mCliente.email.trim();
            String direccion = mCliente.direccion.trim().isEmpty()? "NO DISPONIBLE":mCliente.direccion.trim();

            ((TextView) this.mRootView.findViewById(R.id.cliente_razon_social)).setText(razonSocial);
            ((TextView) this.mRootView.findViewById(R.id.cliente_telefono)).setText(telefono);
            ((TextView) this.mRootView.findViewById(R.id.cliente_direccion)).setText(direccion);
            ((TextView) this.mRootView.findViewById(R.id.cliente_mail)).setText(mail);
            ((Button) this.mRootView.findViewById(R.id.agregar_pedido)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClienteAgregarPedido();
                    //Intent c = new Intent(getContext(), PedidoActivity.class);
                    //c.putExtra(PedidoActivity.ARG_CLIENTE_ID, mClienteId);
                    //startActivity(c);
                }
            });

            // Add a marker in client location
            LatLng position = new LatLng(mCliente.lat, mCliente.lng);
            mMap.addMarker(new MarkerOptions().position(position).title(mCliente.nombreCompleto));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
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
