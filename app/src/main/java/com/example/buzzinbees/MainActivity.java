package com.example.buzzinbees;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    int currentFragment = 0;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        Log.d("AppCrash","Oncreate called");
        super.onCreate(savedInstanceState);
        Log.d("AppCrash","SaveInstance");
        setContentView(R.layout.activity_main);
        Log.d("AppCrash","contentView set");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.mainContainer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);
        }

        //loadFragment(new HomeFragment());
        //Log.d("AppCrash","Home Fragment Loaded ");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.navigation_songs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ML_List_SongsFragment()).commit();
                break;
            case R.id.navigation_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ML_List_PlaylistsFragment()).commit();
                break;
            case R.id.navigation_albums:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ML_List_AlbumsFragment()).commit();
                break;
            case R.id.navigation_artists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ML_List_ArtstisFragment()).commit();
                break;
            case R.id.navigation_visualizer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new VisualizerFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
            //on backpress, if menu open, close menu
        }else{
            super.onBackPressed();
            //else usual back btn function, closes app currently
        }

    }


    private boolean loadFragment(Fragment frag) {
        if(frag != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();

            /*
            if(currentFragment == 0) {
                currentCount = ViewModelProviders.of(this).get(DataContainer.class).homeCounter;
            } else if (currentFragment == 1) {
                currentCount = ViewModelProviders.of(this).get(DataContainer.class).dashCounter;
            } else if (currentFragment == 2) {
                currentCount = ViewModelProviders.of(this).get(DataContainer.class).notifCounter;
            } else if (currentFragment == 3) {
                currentCount = ViewModelProviders.of(this).get(DataContainer.class).settingCounter;
            }
             */
            return true;
        }
        else {
            return false;
        }
    }

}
