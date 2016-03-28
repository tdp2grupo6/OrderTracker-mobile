package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;
import ar.fiuba.tdp2grupo6.ordertracker.view.adapter.ClienteAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeLayout;
    private ClientesBuscarTask mClientesBuscarTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescarLista();
            }

        });

        //mAdapter = new WorkflowDynamicAdapter(mContext, mApplication.getLogin().session, mFiltro, mFilterText, this);
        mListView = (ListView) findViewById(R.id.clientes_list);
        //mListView.setAdapter(mAdapter);

        mContext = (Context)this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // por las dudas corre el servicio, si ya esta corriendo es ignorada
        // este inicio
        this.sendBroadcast(new Intent(this, BootReceiver.class));

        this.refrescarLista();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void refrescarLista() {
        mClientesBuscarTask = new ClientesBuscarTask(this);
        mClientesBuscarTask.execute((Void) null);
    }

    private void actualizarLista(ArrayList<Cliente> clientes) {
        if (mListView != null) {
            ClienteAdapter adapter = new ClienteAdapter(this, clientes);
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

                /*
                Cliente cliente = new Cliente();
                cliente.id = 1;
                cliente.nombre = "hola";
                cliente.direccion = "ahi 1234";
                resultado.add(cliente);

                cliente = new Cliente();
                cliente.id = 2;
                cliente.nombre = "chau";
                cliente.direccion = "alla 234";
                resultado.add(cliente);
                */
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
