package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ComentarioBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;

public class ClienteDetailActivity extends AppBaseAuthActivity implements ClienteDetailFragment.OnFragmentClienteDetailListener {

    public static final int REQUEST_CODE = 10001;

    public static final String ARG_CLIENTE_ID = "cliente_id";
    public static final String ARG_CLIENTE_NOMBRE_COMPLETO = "cliente_nombreCompleto";

    private long mClienteId;
    private String mClienteNombreCompleto;
    private long mVisitaId;

    private Context mContext;
    private ClienteDetailFragment mFragment;
    private FloatingActionButton mEnviaComentario;
    private EnviarComentarioTask mEnviarComentarioTask;
    private boolean mensajeEnviado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = (Context)this;

        setContentView(R.layout.activity_cliente_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEnviaComentario = (FloatingActionButton) this.findViewById(R.id.fab);
        mEnviaComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClienteEnviarComentario();
            }
        });

        if (savedInstanceState == null) {
            this.mClienteId = getIntent().getLongExtra(ClienteDetailActivity.ARG_CLIENTE_ID, 0);
            this.mClienteNombreCompleto = getIntent().getStringExtra(ClienteDetailActivity.ARG_CLIENTE_NOMBRE_COMPLETO);

            // Create the detail fragment and add it to the activity using a fragment transaction.
            mFragment = ClienteDetailFragment.newInstance(this.mClienteId, mClienteNombreCompleto);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.cliente_detail_container, mFragment)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PedidoActivity.ACTIVITY_PEDIDO) : {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Se ha confirmado el Pedido!", Toast.LENGTH_LONG).show();
                    mFragment.onResume();
                }
                break;
            }
        }
    }

    /*
    @Override
    public void onClienteAgregarPedido() {
        Intent intent = new Intent(this, PedidoActivity.class);
        intent.putExtra(PedidoActivity.ARG_CLIENTE_ID, mClienteId);
        startActivityForResult(intent, PedidoActivity.ACTIVITY_PEDIDO);
    }
    */

    @Override
    public void onFloatButtonPropertysChange(int visibility, boolean enable){
        mEnviaComentario.setVisibility(visibility);
        mEnviaComentario.setEnabled(enable);
        if (!enable) {
            mEnviaComentario.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            mEnviaComentario.getBackground().setColorFilter(Color.parseColor("#7ff781"), PorterDuff.Mode.MULTIPLY);
        }

    }

    @Override
    public void onVisitaCreated(long visitaId) {
        this.mVisitaId = visitaId;
    }

    private void onClienteEnviarComentario() {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_comentario, null);
        final TextView tv = (TextView) view.findViewById(R.id.comentarioTexto);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle(mContext.getResources().getString(R.string.title_popup_comentario));
        //builder.setMessage(mContext.getResources().getString(R.string.msg_popup_comentario));
        builder.setSingleChoiceItems(R.array.comentarioRazonesComunes, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_enviarComentario, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ListView lw = ((AlertDialog)dialog).getListView();
                Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                //Muestra mensaje
                Snackbar.make(view, "Enviando Comentario", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                //Graba mensaje
                Comentario comentario = new Comentario();
                comentario.clienteId = mClienteId;
                comentario.fechaComentario = new Date();
                comentario.razonComun = checkedItem.toString();//(sp != null)? sp.getSelectedItem().toString() : "Otro";
                comentario.comentario = (tv != null)? tv.getText().toString() : "";
                comentario.visitaId = mVisitaId;

                mEnviarComentarioTask = new EnviarComentarioTask(mContext, comentario);
                mEnviarComentarioTask.execute((Void) null);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.btn_cancelarComentario, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create();
        builder.show();
    }

    public class EnviarComentarioTask extends AsyncTask<Void, String, Comentario> {
        private Context mContext;
        private Comentario mComentario;
        private ProgressDialog mPd;
        private boolean mSessionInvalid = false;

        public EnviarComentarioTask(Context context, Comentario comentario) {
            this.mContext = context;
            this.mComentario = comentario;
        }

        @Override
        protected void onPreExecute() {
            mPd = new ProgressDialog(mContext);
            mPd.setMessage(mContext.getResources().getString(R.string.msg_procesando));
            mPd.setCancelable(false);
            mPd.getWindow().setGravity(Gravity.CENTER);
            mPd.show();
        }

        @Override
        protected Comentario doInBackground(Void... params) {
            ComentarioBZ cbz = new ComentarioBZ(this.mContext);

            Comentario comm = mComentario;
            comm.enviado = false;

            try {
                comm = cbz.guardarComentario(comm);
                cbz.enviarComentario(comm);
            } catch (AutorizationException ae) {
                //TODO: Hacer el deslogueo de la app
                mSessionInvalid = true;
            } catch (BusinessException e) {
                e.printStackTrace();
            }

            return comm;
        }

        @Override
        protected void onPostExecute(Comentario comentario) {
            if (mSessionInvalid == false) {
                Toast.makeText(mContext, "Mensaje enviado!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                logoutApplication(true);
            }

            mPd.dismiss();
        }
    }
}
