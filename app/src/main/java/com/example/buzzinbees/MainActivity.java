package com.example.buzzinbees;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int currentFragment = 0;

    private DrawerLayout drawer;

    FrameLayout audioContainer;

    AudioManagerFragment audioManagerFragment;

    NavigationView navigationView;


    public Playlist allSongs;
    //main list of songs loaded into the app
    public Playlist playingQ;
    //clone of arraylist set to play from
    public ArrayList<Playlist> arrayPlaylists = new ArrayList<>();
    //list of all user and default playlists

    public boolean songsLoaded;


    //check permissions
    List<String> permissions = new ArrayList<String>();
    private boolean askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO );
            if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), Constant.AUDIO_PERMS);
            } else
                return false;
        } else
            return false;
        return true;
    }
    private boolean askDataPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int ACCESS_DATA = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE );
            if (ACCESS_DATA != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), Constant.DATA_PERMS);
            } else
                return false;
        } else
            return false;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.AUDIO_PERMS) {
            boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if (!result) {
                Toast.makeText(this, "..", Toast.LENGTH_LONG).show();
            } else {
            }
        }
        else if (requestCode == Constant.DATA_PERMS) {
            boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_SongsFragment()).commit();
                audioContainer.setVisibility(View.GONE);
                currentFragment = Constant.FRAGVAL_SONGS;
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
        //called at app load

        Log.d("AppCrash","Oncreate called");
        super.onCreate(savedInstanceState);
        Log.d("AppCrash","SaveInstance");
        setContentView(R.layout.activity_main);
        Log.d("AppCrash","contentView set");


        audioContainer = findViewById(R.id.fragmentContainer_audioManager);
        //reference to entire audio manager fragment container


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //tool bar reference for nav button

        drawer = findViewById(R.id.mainContainer);
        //ref to navigation drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //sets up drawer open/close listener

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //loads nav menu

        if (savedInstanceState == null) {

            //load audio manager fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_audioManager,
                    new AudioManagerFragment()).commit();

            askPermission();//ask permission for the audio manager stuff to work

            FragmentManager fragmentManager = getSupportFragmentManager();
            audioManagerFragment = (AudioManagerFragment)fragmentManager.findFragmentById(R.id.fragmentContainer_audioManager);

            //load home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);
            currentFragment = 0;
            songsLoaded = false;

        }


        allSongs = new Playlist();
        playingQ = new Playlist();
        Log.d("SONGLIST",allSongs.songsArray.toString());

        arrayPlaylists.add(new Playlist(getString(R.string.name_defaultPlaylist),Constant.PLAYLIST_FAVOURITES_ID,new ArrayList<Song>()));



    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        //nav menu click lsiteners
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new HomeFragment()).commit();
                //update main fragment with chosen page
                currentFragment = Constant.FRAGVAL_HOME;
                // set fragment tracker to corresponding fragment value
                audioContainer.setVisibility(View.VISIBLE);
                //toggle visibility of entire audio manager fragment
                break;
            case R.id.navigation_songs:
                //check permissions
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.DATA_PERMS);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.DATA_PERMS);
                    }
                } else {
                    //if permissions are granted, do the things
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                            new ML_List_SongsFragment()).commit();
                    audioContainer.setVisibility(View.GONE);
                    currentFragment = Constant.FRAGVAL_SONGS;
                }
                break;
            case R.id.navigation_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_PlaylistsFragment()).commit();
                audioContainer.setVisibility(View.GONE);
                currentFragment = Constant.FRAGVAL_PLAYLISTS;
                break;
            case R.id.navigation_albums:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_AlbumsFragment()).commit();
                audioContainer.setVisibility(View.GONE);
                currentFragment = Constant.FRAGVAL_ALBUMS;
                break;
            case R.id.navigation_artists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_ArtstisFragment()).commit();
                audioContainer.setVisibility(View.GONE);
                currentFragment = Constant.FRAGVAL_ARTISTS;
                break;
            case R.id.navigation_visualizer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new VisualizerFragment()).commit();
                audioContainer.setVisibility(View.VISIBLE);
                currentFragment = Constant.FRAGVAL_VISUALIZER;
                break;
            case R.id.navigation_options:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new OptionsFragment()).commit();
                audioContainer.setVisibility(View.GONE);
                currentFragment = Constant.FRAGVAL_OPTIONS;
                break;
        }

        toggleVisualizer();//toggle visibility of visualizer
        drawer.closeDrawer(GravityCompat.START);//close nav drawer
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
            //on backpress, if menu open, close menu
        }else{
            switch(currentFragment){
                case Constant.FRAGVAL_PLAYLISTPAGE:{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                            new ML_List_PlaylistsFragment()).commit();
                    audioContainer.setVisibility(View.GONE);
                    currentFragment = Constant.FRAGVAL_PLAYLISTS;
                    break;
                }
                default:{
                    super.onBackPressed();
                    break;
                }
            }

            //else usual back btn function, closes app currently
        }

    }

    public void toggleVisualizer(){
        //creates reference to audio manager fragment to call method for visualizer visibility

        FragmentManager fragmentManager = getSupportFragmentManager();
        audioManagerFragment = (AudioManagerFragment)fragmentManager.findFragmentById(R.id.fragmentContainer_audioManager);
        //create instance of audio manager fragment that is currently running
        audioManagerFragment.toggleVisualizer(currentFragment);
        //calls method from fragment's java class
    }


    private boolean loadFragment(Fragment frag) {
        //loadsfragment - not used
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
