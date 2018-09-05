package com.tj.carpool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.tj.carpool.datastructure.Passenger;
import com.tj.carpool.fragment.ActiveFragment;
import com.tj.carpool.fragment.CreateCarpoolFragment;
import com.tj.carpool.fragment.NearbyFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CreateCarpoolFragment.OnFragmentInteractionListener,
        NearbyFragment.OnFragmentInteractionListener,
        ActiveFragment.OnFragmentInteractionListener{

    private CreateCarpoolFragment createCarpoolFragment = null;
    private NearbyFragment nearbyFragment = null;
    private ActiveFragment activeFragment = null;

    private Passenger passenger;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setSupportActionBar(toolbar);
        SDKInitializer.initialize(getApplicationContext());
        Bundle bundle = this.getIntent().getExtras();
        userType = bundle.getString("userType");
        passenger = (Passenger) bundle.getSerializable("userInfo");

        TextView startTab,listTab,userTab;
        FrameLayout frameLayout;

        startTab = (TextView) findViewById(R.id.tab_start);
        listTab = (TextView) findViewById(R.id.tab_list);
        userTab = (TextView) findViewById(R.id.tab_user);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        createCarpoolFragment = new CreateCarpoolFragment();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt("ID",passenger.getID());
        createCarpoolFragment.setArguments(fragmentBundle);

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container,createCarpoolFragment);

        nearbyFragment = NearbyFragment.newInstance();
        transaction.add(R.id.fragment_container,nearbyFragment);

        activeFragment = ActiveFragment.newInstance();
        transaction.add(R.id.fragment_container,activeFragment);

        transaction.hide(nearbyFragment);
        transaction.hide(activeFragment);
        transaction.commit();

        startTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction transaction = manager.beginTransaction();
                transaction.hide(nearbyFragment)
                        .hide(activeFragment)
                        .show(createCarpoolFragment)
                        .commit();
            }
        });

        listTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction transaction = manager.beginTransaction();
                transaction.hide(createCarpoolFragment)
                        .hide(activeFragment)
                        .show(nearbyFragment)
                        .commit();
            }
        });

        userTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction transaction = manager.beginTransaction();
                transaction.hide(createCarpoolFragment)
                        .hide(nearbyFragment)
                        .show(activeFragment)
                        .commit();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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

        if (id == R.id.nav_userinfo) {
            if(userType.equals("passenger"))
            {
                Intent intent = new Intent(MainActivity.this,PassengerInfoActivity.class);
                intent.putExtra("userinfo",passenger);
                startActivity(intent);
            }
            else
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo",passenger);
                Intent intent = new Intent(MainActivity.this,DriverInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }else if(id == R.id.nav_signout){
            Intent intent = new Intent(MainActivity.this,ChooseUserTypeActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"test",Toast.LENGTH_LONG).show();
    }
}
