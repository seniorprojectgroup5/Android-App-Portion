package com.example.buzzinbees;

//import android.arch.lifecycle.ViewModelProvider;
//import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity {

    int currentFragment = 0;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        toolBar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolBar);
//
//        drawerLayout = findViewById(R.id.mainContainer);
//        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
//
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//
//
//        navigationView = findViewById(R.id.navView);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                switch(id){
//                    case R.id.navigation_home:
//
//                    case R.id.navigation_songs:
//
//                    case R.id.navigation_playlists:
//
//                    case R.id.navigation_albums:
//
//                    case R.id.navigation_artists:
//
//                    case R.id.navigation_visualizer:
//
//                    default:
//                        return true;
//                }
//            }
//        });






        // to load a specified fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new HomeFragment());
        ft.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return true;
//    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionItemSelected(MenuItem item){
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment frag) {
        if(frag != null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();

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
