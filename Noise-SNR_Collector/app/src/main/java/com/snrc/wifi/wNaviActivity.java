package com.snrc.wifi;

/**
 * Created by Cem on 26.10.2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.snrc.lib.loginActivity;
import com.snrc.R;

public class wNaviActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        login();
        DrawerLayout drawer = findViewById(R.id.drawer_layout_navi);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // BANNER ad
        loadAdView(R.id.adView_navi);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_navi);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_refresh, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map_view) {
            gotoMapsActivity();
        } else if (id == R.id.refresh) {
            refresh();
        } else if (id == R.id.graph_view) {
            gotoGraphActivity();
        } else if (id == R.id.exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_navi);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void gotoMapsActivity() {
        Intent i = new Intent(this, wMapsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void gotoGraphActivity() {
        Intent i = new Intent(this, wGraphActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void refresh() {
        Intent i = new Intent(wNaviActivity.this, wNaviActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
    private void login() {
        TextView statusField = findViewById(R.id.textViewStatus);
        new loginActivity(statusField).execute("WSNR");
    }
    private void loadAdView(int adInt) {
        AdView adView = this.findViewById(adInt);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}