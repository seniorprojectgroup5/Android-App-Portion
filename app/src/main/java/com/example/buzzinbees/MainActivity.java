package com.example.buzzinbees;

//import android.arch.lifecycle.ViewModelProvider;
//import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
//import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    //int currentCount = 0;

    int currentFragment = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            /*
            if(currentFragment == 0) {
                ViewModelProviders.of(MainActivity.this).get(DataContainer.class).homeCounter = currentCount;
            } else if (currentFragment == 1) {
                ViewModelProviders.of(MainActivity.this).get(DataContainer.class).dashCounter = currentCount;
            } else if (currentFragment == 2) {
                ViewModelProviders.of(MainActivity.this).get(DataContainer.class).notifCounter = currentCount;
            } else if (currentFragment == 3) {
                ViewModelProviders.of(MainActivity.this).get(DataContainer.class).settingCounter = currentCount;
            }
            */

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    currentFragment = 0;
                    break;
                case R.id.navigation_songs:
                    fragment = new ML_List_SongsFragment();
                    currentFragment = 1;
                    break;
                case R.id.navigation_playlists:
                    fragment = new ML_List_PlaylistsFragment();
                    currentFragment = 2;
                    break;
                case R.id.navigation_albums:
                    fragment = new ML_List_AlbumsFragment();
                    currentFragment = 3;
                    break;
                case R.id.navigation_artists:
                    fragment = new ML_List_ArtstisFragment();
                    currentFragment = 4;
                    break;
                case R.id.navigation_visualizer:
                    fragment = new VisualizerFragment();
                    currentFragment = 5;
                    break;
            }
            return loadFragment(fragment);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        loadFragment(new HomeFragment());
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
