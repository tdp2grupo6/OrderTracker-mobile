package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * A fragment representing a single Cliente detail screen.
 * This fragment is either contained in a {@link ClienteFueraRutaListFragment}
 * in two-pane mode (on tablets) or a {@link ClienteDetailActivity}
 * on handsets.
 */
public class ClienteFueraRutaMapFragment extends Fragment { //implements OnMapReadyCallback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    private CollapsingToolbarLayout mAppBarLayout;
    private View mRootView;

    private ClientesBuscarTask mClientesBuscarTask;
    private MapView mMapView;
    private GoogleMap mMap;


    private OnClienteFueraRutaMapFragment mListener;

    public interface OnClienteFueraRutaMapFragment {
    }

    public static ClienteFueraRutaMapFragment newInstance() {
        ClienteFueraRutaMapFragment fragment = new ClienteFueraRutaMapFragment();
        //Bundle args = new Bundle();
        //args.putLong(ARG_CLIENTE_ID, clienteId);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        //if (getArguments().containsKey(ARG_CLIENTE_ID)) {
        //    this.mClienteId = getArguments().getLong(ARG_CLIENTE_ID);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(R.layout.fragment_map_cliente_fuera_ruta, container, false);

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
        try {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location myLocation) {
                    LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

                    //desactiva el localizador
                    mMap.setOnMyLocationChangeListener(null);
                    mCenterOnPosition = true;
                }
            });

        } catch (SecurityException se) {
            mCenterOnPosition = false;
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (mCenterOnPosition)
                Toast.makeText(getContext(), "Se centrara el mapa en la posicion actual, la aplicacion esta intentando de obtener su ubicacion!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getContext(), "No se podra centrar el mapa en la posicion actual, la aplicacion tiene permisos para obtener su ubicacion!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClienteFueraRutaMapFragment) {
            mListener = (OnClienteFueraRutaMapFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClienteFueraRutaMapFragment");
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
        mClientesBuscarTask = new ClientesBuscarTask(getContext());
        mClientesBuscarTask.execute((Void) null);
    }

    private void actualizar(ArrayList<Cliente> clientes) {

        if (clientes != null) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Cliente cliente: clientes) {
                // Add a marker in client location
                LatLng position = new LatLng(cliente.lat, cliente.lng);

                //Segun el estado, se cambia el color del pin
                float color = BitmapDescriptorFactory.HUE_RED;

                MarkerOptions marker = new MarkerOptions()
                        .position(position)
                        .title(cliente.nombreCompleto)
                        .snippet(cliente.direccion)
                        .icon(BitmapDescriptorFactory.defaultMarker(color));
                mMap.addMarker(marker);

                //agrega para luego calcular los limites
                builder.include(marker.getPosition());
            }


            // Centra el mapa en la posicion actual
            if (!mCenterOnPosition) {
                //Calcula los limites para hacer zoom
                LatLngBounds bounds = builder.build();
                int padding = 8; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                //Hace el zoom en el mapa
                //mMap.animateCamera(cu);
                mMap.moveCamera(cu);
            }
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
            actualizar(clientes);
        }

    }
}
