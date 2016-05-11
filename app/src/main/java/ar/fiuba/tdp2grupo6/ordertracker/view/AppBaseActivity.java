package ar.fiuba.tdp2grupo6.ordertracker.view;

/*
* This is a simple and easy approach to reuse the same
* navigation drawer on your other activities. Just create
* a base layout that conains a DrawerLayout, the
* navigation drawer and a FrameLayout to hold your
* content view. All you have to do is to extend your
* activities from this class to set that navigation
* drawer. Happy hacking :)
* P.S: You don't need to declare this Activity in the
* AndroidManifest.xml. This is just a base class.
*
* https://gist.github.com/anandbose/7d6efb35c900eaba3b26
*/
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ar.fiuba.tdp2grupo6.ordertracker.R;

public abstract class AppBaseActivity extends AppBaseAuthActivity implements MenuItem.OnMenuItemClickListener {
    private FrameLayout view_stub; //This is the framelayout to keep your content view
    private NavigationView navigation_view; // The new navigation view from Android Design Library. Can inflate menu resources. Easy
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    public Menu mDrawerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);// The base layout that contains your navigation drawer.
        view_stub = (FrameLayout) findViewById(R.id.view_stub);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerMenu = navigation_view.getMenu();
        for(int i = 0; i < mDrawerMenu.size(); i++) {
            mDrawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }
        // and so on...
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Override all setContentView methods to put the content view to the FrameLayout view_stub
     * so that, we can make other activity implementations looks like normal activity subclasses.
     */
    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view_stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_clientes:
                Intent a = new Intent(this, ClienteActivity.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
                break;
            case R.id.nav_agenda:
                Intent c = new Intent(this, AgendaActivity.class);
                c.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(c);
                break;
            case R.id.nav_catalogo:
                Intent b = new Intent(this, CatalogoActivity.class);
                b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(b);
                break;
            case R.id.nav_logout:
                super.logoutApplication(true);
                break;
            /*
            case R.id.nav_pedido:
                Intent c = new Intent(this, PedidoActivity.class);
                c.putExtra(PedidoActivity.ARG_CLIENTE_ID, 1);
                startActivity(c);
                break;            // and so on...
            */
        }
        return false;
    }
}
