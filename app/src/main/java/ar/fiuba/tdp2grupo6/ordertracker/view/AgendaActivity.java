package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.AgendaBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Agenda;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

/**
 * An activity representing a single Cliente detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClienteFueraRutaListFragment}.
 */
public class AgendaActivity extends AppBaseActivity
        implements AgendaListFragment.OnAgendaListFragmentListener {

    private final int MENU_INDEX = 1;
    public static final int AGENDA_PEDIDO = 1000;

    public Agenda mAgenda;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private DiaPagerAdapter mDiaPagerAdapter;
    private AgendaBuscarTask mAgendaBuscarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPedidoConfirma();
            }
        });
        */
        //Set the item selected
        mDrawerMenu.getItem(MENU_INDEX).setChecked(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {

            // Setea el viewpager
            mViewPager = (ViewPager) findViewById(R.id.container);
            mTabLayout = (TabLayout) findViewById(R.id.tabs);

            cargarAgenda();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ClienteDetailActivity.REQUEST_CODE) {
            cargarAgenda();
        }
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        try {
            if (cliente != null ) {
                if (false) {//mTwoPane) {
                    //ClienteDetailFragment fragment = ClienteDetailFragment.newInstance(cliente.id, "");
                    //this.getFragmentManager().beginTransaction()
                    //        .replace(R.id.cliente_detail_container, fragment)
                    //        .commit();
                } else {
                    Intent intent = new Intent(this, ClienteDetailActivity.class);
                    intent.putExtra(ClienteDetailActivity.ARG_CLIENTE_ID, cliente.id);
                    this.startActivityForResult(intent, ClienteDetailActivity.REQUEST_CODE);

                }
            }
        } catch (Exception e) {
        };
    }



    private void actualizarVista() {
        mDiaPagerAdapter = new DiaPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mDiaPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //actualizarHeader();
        actualizarFooter();
    }

    private void actualizarFooter() {
        if (mAgenda != null) {
            //mClienteView.setText(mPedido.cliente.nombreCompleto);
            //mTotalView.setText(String.format("%.2f", mPedido.getImporte(false)));
        }
    }

    private void cargarAgenda() {
        mAgendaBuscarTask = new AgendaBuscarTask(this);
        mAgendaBuscarTask.execute((Void) null);
    }


    public class AgendaBuscarTask extends AsyncTask<Void, String, Agenda> {
        private Context mContext;
        private ProgressDialog mPd;

        public AgendaBuscarTask(Context context) {
            this.mContext = context;
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
        protected Agenda doInBackground(Void... params) {
            Agenda resultado = null;

            try {

                AgendaBZ agendaBZ = new AgendaBZ(this.mContext);
                try {
                    //Si puede sincroniza los items primero
                    agendaBZ.sincronizar(false);
                }
                catch (Exception e) {
                }

                try {
                    //y luego busca el listado
                    resultado = agendaBZ.obtener(true);
                }
                catch (Exception e) {
                }

            } catch (Exception e) {
                String err = e.getLocalizedMessage();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Agenda agenda) {
            if (agenda != null)
            {
                mAgenda = agenda;
                actualizarVista();
            }

            mPd.dismiss();
        }
    }

    public class DiaPagerAdapter extends FragmentStatePagerAdapter {

        //private Map<Long,Boolean> mCategoryDirtyMap;

        public DiaPagerAdapter(FragmentManager fm) {
            super(fm);
            //mCategoryDirtyMap = new HashMap<Long, Boolean>();
        }

        @Override
        public Fragment getItem(int i) {
            int dayId = getDayWeek(i);
            AgendaListFragment fragment = AgendaListFragment.newInstance(dayId);
            return fragment;
        }

        @Override
        public int getCount() {
            return 7; //Dias de la Semana
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int i) {
            int dayWeekId = getDayWeek(i);
            switch(dayWeekId){
                case 0:
                    return "DOMINGO";
                case 1:
                    return "LUNES";
                case 2:
                    return "MARTES";
                case 3:
                    return "MIERCOLES";
                case 4:
                    return "JUEVES";
                case 5:
                    return "VIERNES";
                case 6:
                    return "SABADO";
            }
            return "ERROR";
        }

        public int getDayWeek(int i) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int day = currentDay + i > 6? currentDay + i - 7 : currentDay + i;
            return day;
        }

    }


}
