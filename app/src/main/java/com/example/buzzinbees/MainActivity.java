package com.example.buzzinbees;

//import android.arch.lifecycle.ViewModelProvider;
//import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;
//import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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


import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity {

    int currentFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("AppCrash","Oncreate called");
        super.onCreate(savedInstanceState);
        Log.d("AppCrash","SaveInstance");
        setContentView(R.layout.activity_main);

        // to load a specified fragment
        loadFragment(new ML_List_SongsFragment());
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
