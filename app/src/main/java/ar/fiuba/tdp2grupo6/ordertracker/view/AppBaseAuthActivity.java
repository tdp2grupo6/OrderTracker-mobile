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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.view.app.OrderTrackerApplication;

public abstract class AppBaseAuthActivity extends AppCompatActivity {
    private OrderTrackerApplication mApplication = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (OrderTrackerApplication)this.getApplication();
    }

    public void logoutActivity() {
        mApplication.clearAutentication();

        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
    }
}
