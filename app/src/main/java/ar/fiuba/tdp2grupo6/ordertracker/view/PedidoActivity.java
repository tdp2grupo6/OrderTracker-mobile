package ar.fiuba.tdp2grupo6.ordertracker.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

/**
 * An activity representing a single Cliente detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ClienteFueraRutaListFragment}.
 */
public class PedidoActivity extends AppBaseActivity
        implements PedidoListFragment.OnPedidoListFragmentListener {

    public static final String ARG_CLIENTE_ID = "cliente_id";

    private long mClienteId;
    public Pedido mPedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            this.mClienteId = getIntent().getLongExtra(ClienteDetailActivity.ARG_CLIENTE_ID, 0);

            PedidoListFragment fragment = PedidoListFragment.newInstance(0, mClienteId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pedido_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onPedidoItemClick(Producto producto) {
        //Visualiza el detalle del producto
        /*
        ProductoDetailFragment fragment = ProductoDetailFragment.newInstance();

        this.getFragmentManager().beginTransaction()
                .replace(R.id.pedido_container, fragment)
                .commit();
        */
    }

    @Override
    public void onPedidoActualizar(Pedido pedido) {
        this.mPedido = pedido;
    }

    private void actualizarCarrito(Pedido pedido) {
        /*
        if (mReciclerView != null) {
            mReciclerAdapter = new PedidoProductoAdapter(this, pedidoProductos, mTwoPane);
            mReciclerView.setAdapter(mReciclerAdapter);
        }
        */
    }


}
