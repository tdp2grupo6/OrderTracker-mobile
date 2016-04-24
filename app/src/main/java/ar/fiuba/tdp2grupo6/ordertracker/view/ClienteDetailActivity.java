package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ComentarioBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;

/**
 * An activity representing a single Cliente detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClienteFueraRutaListFragment}.
 */
public class ClienteDetailActivity extends AppCompatActivity implements ClienteDetailFragment.OnFragmentClienteDetailListener {

    public static final String ARG_CLIENTE_ID = "cliente_id";
    public static final String ARG_CLIENTE_NOMBRE_COMPLETO = "cliente_nombreCompleto";

    private long mClienteId;
    private String mClienteNombreCompleto;
    private EnviarComentarioTask mEnviarComentarioTask;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = (Context)this;

        setContentView(R.layout.activity_cliente_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEnviarComentarioTask = new EnviarComentarioTask(mContext);
                mEnviarComentarioTask.execute((Void) null);

                Snackbar.make(view, "Enviando consulta POST", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            this.mClienteId = getIntent().getLongExtra(ClienteDetailActivity.ARG_CLIENTE_ID, 0);
            this.mClienteNombreCompleto = getIntent().getStringExtra(ClienteDetailActivity.ARG_CLIENTE_NOMBRE_COMPLETO);

            // Create the detail fragment and add it to the activity using a fragment transaction.
            ClienteDetailFragment fragment = ClienteDetailFragment.newInstance(this.mClienteId, mClienteNombreCompleto);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.cliente_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ClienteActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClienteAgregarPedido() {
        Intent intent = new Intent(this, PedidoActivity.class);
        intent.putExtra(ClienteDetailActivity.ARG_CLIENTE_ID, this.mClienteId);

        this.startActivity(intent);
    }

    // dgacitua: Async Task para manejar la subida de comentarios
    public class EnviarComentarioTask extends AsyncTask<Void, String, Comentario> {
        private Context mContext;

        public EnviarComentarioTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Comentario doInBackground(Void... params) {
            ComentarioBZ cbz = new ComentarioBZ(this.mContext);
            Comentario comm = new Comentario();
            comm.clienteId = mClienteId;
            comm.fechaComentario = new Date();
            comm.razonComun = "Otro";
            comm.comentario = "Buen cliente";

            try {
                //cbz.guardarComentario(comm);
                cbz.enviarComentario(comm);
            } catch (BusinessException e) {
                e.printStackTrace();
            }

            return comm;
        }
    }
}
