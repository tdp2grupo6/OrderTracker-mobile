package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
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

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ComentarioBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;

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
    public static final String ARG_CLIENTE_NOMBRE_COMPLETO = "cliente_nombreCompleto";

    private long mClienteId;
    private String mClienteNombreCompleto;

    private CollapsingToolbarLayout mAppBarLayout;
    private View mRootView;

    private Cliente mCliente;
    private Pedido mPedidoPendiente;
    private Cliente mClientePedidoPendiente;


    private ClienteObtenerTask mClienteObtenerTask;
    private PedidoPendienteProcesarTask mPedidoPendienteProcesarTask;
    private MapView mMapView;
    private GoogleMap mMap;


    private OnFragmentClienteDetailListener mListener;

    public interface OnFragmentClienteDetailListener {
        void onClienteAgregarPedido();
    }

    public static ClienteDetailFragment newInstance(long clienteId, String clienteNombreCompleto) {
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CLIENTE_ID, clienteId);
        args.putString(ARG_CLIENTE_NOMBRE_COMPLETO, clienteNombreCompleto);
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
            this.mClienteNombreCompleto = getArguments().getString(ARG_CLIENTE_NOMBRE_COMPLETO);
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void refrescar() {
        mClienteObtenerTask = new ClienteObtenerTask(getContext(), this.mClienteId, this.mClienteNombreCompleto);
        mClienteObtenerTask.execute((Void) null);
    }

    private void actualizarVista() {
        if (mCliente != null) {
            //Actualiza
            this.mClienteId = mCliente.id;
            this.mClienteNombreCompleto = mCliente.nombreCompleto;

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
                    setupNuevoPedido();
                }
            });

            // Add a marker in client location
            LatLng position = new LatLng(mCliente.lat, mCliente.lng);
            mMap.addMarker(new MarkerOptions().position(position).title(mCliente.nombreCompleto));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        }
    }

    private void setupNuevoPedido() {
        if (mPedidoPendiente != null && mCliente.id != mPedidoPendiente.clienteId) {

            AlertDialog.Builder dataDialogBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
            dataDialogBuilder.setTitle(getContext().getResources().getString(R.string.title_popup_pedido_pendiente));
            dataDialogBuilder.setMessage(String.format(getContext().getResources().getString(R.string.error_pedido_pendiente), mClientePedidoPendiente.nombreCompleto));
            dataDialogBuilder.setCancelable(false);
            dataDialogBuilder.setPositiveButton(getContext().getResources().getString(R.string.btn_confirmarlo), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    procesarPedidoPendiente(mPedidoPendiente.id, 0);
                    dialog.cancel();
                }
            }).setNegativeButton(getContext().getResources().getString(R.string.btn_descartarlo), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    procesarPedidoPendiente(0, mPedidoPendiente.id);
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
            abrirNuevoPedido();
        }
    }

    private void procesarPedidoPendiente(long confirmarPendienteId, long descartarPendienteId) {
        mPedidoPendienteProcesarTask = new PedidoPendienteProcesarTask(getContext(), confirmarPendienteId, descartarPendienteId);
        mPedidoPendienteProcesarTask.execute((Void) null);
    }

    private void abrirNuevoPedido() {
        Intent c = new Intent(getContext(), PedidoActivity.class);
        c.putExtra(PedidoActivity.ARG_CLIENTE_ID, mClienteId);
        startActivity(c);
    }


    public class ClienteObtenerTask extends AsyncTask<Void, String, ArrayList<Object>> {
        private Context mContext;
        private ProgressDialog mPd;
        private long mId;
        private String mNombreCompleto;

        public ClienteObtenerTask(Context context, long id, String nombreCompleto) {
            this.mContext = context;
            this.mId = id;
            this.mNombreCompleto = nombreCompleto;
        }

        @Override
        protected void onPreExecute() {
            mPd = new ProgressDialog(getActivity());
            mPd.setMessage(getContext().getResources().getString(R.string.msg_procesando));
            mPd.setCancelable(false);
            mPd.getWindow().setGravity(Gravity.CENTER);
            mPd.show();
        }

        @Override
        protected ArrayList<Object> doInBackground(Void... params) {
            ArrayList<Object> resultados = new ArrayList<Object>();
            Cliente resultado1 = null;
            Pedido resultado2 = null;
            Cliente resultado3 = null;

            try {
                //Obtiene el cliente buscado
                ClienteBZ clienteBz = new ClienteBZ(this.mContext);
                resultado1 = clienteBz.obtener(mId, mNombreCompleto);
                resultados.add(resultado1);
            } catch (Exception e) {
            }

            try {
                //Obtiene el listado de pendientes
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                ArrayList<Pedido> pendientes = pedidoBZ.buscar(0, 0, Pedido.ESTADO_NUEVO, false);

                if (pendientes != null && pendientes.size() > 0){
                    resultado2 = pendientes.get(0);
                    resultados.add(resultado2);

                    ClienteBZ clienteBz = new ClienteBZ(this.mContext);
                    resultado3 = clienteBz.obtener(resultado2.clienteId, "");
                    resultados.add(resultado3);
                }
            } catch (Exception e) {
            }

            return resultados;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> resultados) {

            mCliente = (Cliente) resultados.get(0);
            if (resultados.size() > 1) {
                mPedidoPendiente = (Pedido) resultados.get(1);
                mClientePedidoPendiente = (Cliente) resultados.get(2);
            }
            actualizarVista();

            mPd.dismiss();
        }

    }


    public class PedidoPendienteProcesarTask extends AsyncTask<Void, String, Boolean> {
        private Context mContext;
        private long mConfirmarPendienteId;
        private long mDescartarPendienteId;


        public PedidoPendienteProcesarTask(Context context, long confirmarPendienteId, long descartarPendienteId) {
            this.mContext = context;
            this.mConfirmarPendienteId = confirmarPendienteId;
            this.mDescartarPendienteId = descartarPendienteId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean response = true;
            try {
                //Si puede sincroniza los clientes primero
                //y luego busca el listado
                PedidoBZ pedidoBZ = new PedidoBZ(this.mContext);
                if (mConfirmarPendienteId > 0)
                    pedidoBZ.confirmar(mConfirmarPendienteId);
                else if (mDescartarPendienteId > 0)
                    pedidoBZ.borrar(mDescartarPendienteId);
            } catch (Exception e) {
                response = false;
            }

            return response;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            abrirNuevoPedido();
        }

    }

}
