package com.example.buzzinbees;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    int currentFragment = 0;

    private DrawerLayout drawer;

    //check permissions
    List<String> permissions = new ArrayList<String>();
    private boolean askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO );
            if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            } else
                return false;
        } else
            return false;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if (!result) {
                Toast.makeText(this, "..", Toast.LENGTH_LONG).show();
            } else {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        askPermission();//ask permission for the audiomanager stuff to work

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
            //load home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);


            //load audiomanager frament
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_audioManager,
                    new AudioManagerFragment()).commit();

        }

    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        //nav menu click lsiteners
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new HomeFragment()).commit();
                currentFragment = 0;

                break;
            case R.id.navigation_songs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_SongsFragment()).commit();
                currentFragment = 1;
                break;
            case R.id.navigation_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_PlaylistsFragment()).commit();
                currentFragment = 2;
                break;
            case R.id.navigation_albums:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_AlbumsFragment()).commit();
                currentFragment = 3;
                break;
            case R.id.navigation_artists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_ArtstisFragment()).commit();
                currentFragment = 4;
                break;
            case R.id.navigation_visualizer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new VisualizerFragment()).commit();
                currentFragment = 5;
                break;
            case R.id.navigation_options:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new OptionsFragment()).commit();
                currentFragment = 6;
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main, frag).commit();

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
