package com.example.buzzinbees;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private static final String TAG = "Main";

    // Nav
    int currentFragment = 0;
    NavigationView navigationView;
    private DrawerLayout drawer;
    FrameLayout audioContainer;

    // Audio
    AudioManagerFragment audioManagerFragment;
    public ArrayList<Song> arraySongList = new ArrayList<>();
    //main list of songs loaded into the app
    public Playlist allSongs;
    public Playlist playingQ;
    //clone of arraylist set to play from
    public ArrayList<Playlist> arrayPlaylists = new ArrayList<>();
    //list of all user and default playlists
    public boolean songsLoaded;


    //BLUETOOTH
    private BluetoothAdapter bleAdapter;
    private BluetoothDevice bleDevice;
    private UUID bleDeviceUUID;
    public BluetoothSocket bleSocket;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;

    // Threading
    private Runnable sendData;
    private Handler mHandler;
    public boolean canSendData;




    //check permissions
    List<String> permissions = new ArrayList<String>();



    //** ONCREATE **//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //called at app load

//        Log.d("AppCrash", "Oncreate called");
        super.onCreate(savedInstanceState);
//        Log.d("AppCrash", "SaveInstance");
        setContentView(R.layout.activity_main);
//        Log.d("AppCrash", "contentView set");


        audioContainer = findViewById(R.id.fragmentContainer_audioManager);
        //reference to entire audio manager fragment container

        // bluetooth adapter
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler();
        canSendData = true;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //tool bar reference for nav button

        drawer = findViewById(R.id.mainContainer);
        //ref to navigation drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
            audioManagerFragment = (AudioManagerFragment) fragmentManager.findFragmentById(R.id.fragmentContainer_audioManager);

            //load home fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);
            currentFragment = 0;
            songsLoaded = false;

        }

        arraySongList = new ArrayList<Song>();
//        Log.d("SONGLIST",arraySongList.toString());

        allSongs = new Playlist();
        playingQ = new Playlist();
//        Log.d("SONGLIST",allSongs.songsArray.toString());

        arrayPlaylists.add(new Playlist(getString(R.string.name_defaultPlaylist),Constant.PLAYLIST_FAVOURITES_ID,new ArrayList<Song>()));
    }



    //** NAV **//
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
            case R.id.navigation_bluetooth:
                openBluetoothFragment();
        }

        toggleVisualizer();//toggle visibility of visualizer
        drawer.closeDrawer(GravityCompat.START);//close nav drawer
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
                case Constant.FRAGVAL_NEWPLAYLIST:{
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



    //** VISUALIZER **//
    public void toggleVisualizer() {
        //creates reference to audio manager fragment to call method for visualizer visibility

        FragmentManager fragmentManager = getSupportFragmentManager();
        audioManagerFragment = (AudioManagerFragment) fragmentManager.findFragmentById(R.id.fragmentContainer_audioManager);
        //create instance of audio manager fragment that is currently running
        audioManagerFragment.toggleVisualizer(currentFragment);
        //calls method from fragment's java class
    }



    //** BLUETOOTH **//
    public void openBluetoothFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                new BLE_Manager()).commit();
        audioContainer.setVisibility(View.GONE);
        currentFragment = Constant.FRAGVAL_BLUETOOTH;
    }

    public void setUpBluetooth(BluetoothDevice bleD, String uuid) {
        bleDeviceUUID = UUID.fromString(uuid);
        bleDevice = bleD;

        if (bleSocket == null || !mIsBluetoothConnected) {
            msg("connecting");
            //progressDialog = ProgressDialog.show(getApplicationContext(), "Hold on", "Connecting");
            new ConnectBT().execute();
        } else {
            msg("no blue");
        }
    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                bleSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (bleSocket == null || !mIsBluetoothConnected) {
                    bleSocket = bleDevice.createInsecureRfcommSocketToServiceRecord(bleDeviceUUID);

                    bleAdapter.getDefaultAdapter().cancelDiscovery();

                    bleSocket.connect();

                    if (bleSocket == null) {
                        Log.d(TAG, "bad");
                    } else {
                        Log.d(TAG, "good");
                    }
                }
            } catch (IOException e) {
                // Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
            }
        }
    }
    // send effects
    public void waitToSendInfo() {
        sendData = new Runnable() {
            @Override
            public void run() {
                canSendData = true;
            }
        };
        mHandler.postDelayed(sendData, 0);
    }

    public void sendEffect1() {
        canSendData = false;
//        Log.d(TAG, "trying to send effect 1");
        if (bleSocket != null) {
            try {
                bleSocket.getOutputStream().write(Constant.VIBRATION_EFFECT_1.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d(TAG, "no bluetooth");
        }
    }

    public void sendEffect4() {
        canSendData = false;
//        Log.d(TAG, "trying to send effect 4");
        if (bleSocket != null) {
            try {
                bleSocket.getOutputStream().write(Constant.VIBRATION_EFFECT_4.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d(TAG, "no bluetooth");
        }
    }

    public void sendEffect7() {
        canSendData = false;
//        Log.d(TAG, "trying to send effect 7");
        if (bleSocket != null) {
            try {
                bleSocket.getOutputStream().write(Constant.VIBRATION_EFFECT_7.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d(TAG, "no bluetooth");
        }
    }

    public void sendEffect24() {
        canSendData = false;
//        Log.d(TAG, "trying to send effect 24");
        if (bleSocket != null) {
//            Log.d(TAG, "yes bluetooth");
            try {
                bleSocket.getOutputStream().write(Constant.VIBRATION_EFFECT_24.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d(TAG, "no bluetooth");
        }
    }

    public void sendEffect47() {
        canSendData = false;
//        Log.d(TAG, "trying to send effect 47");
        if (bleSocket != null) {
//            Log.d(TAG, "yes bluetooth");
            try {
                bleSocket.getOutputStream().write(Constant.VIBRATION_EFFECT_47.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
//            Log.d(TAG, "no bluetooth");
        }
    }



    //** PERMISSIONS **//
    private boolean askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
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
        } else if (requestCode == Constant.DATA_PERMS) {
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


    //** GENERAL **//
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (bleSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

//    public void closeKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    public void addSongToFavs(Song song){
        if(song.isFav){
            Boolean b = arrayPlaylists.get(Constant.PLAYLIST_FAVOURITES_ID).addSong(song);
            if (b){
                //add was successful
                Toast.makeText(this,"Song added to Favourites",Toast.LENGTH_SHORT).show();
            }
            else{
                //add was unsuccesful
                Toast.makeText(this,"[ERROR: FAILED TO ADD SONG]",Toast.LENGTH_SHORT).show();
                song.isFav = false;
            }
        }
        else{
            Boolean b = arrayPlaylists.get(Constant.PLAYLIST_FAVOURITES_ID).removeSong(song);
            if (b) {
                //removal was successful
                Toast.makeText(this, "Song removed from Favourites", Toast.LENGTH_SHORT).show();

            }
            else{
                //removal was unsuccesful
                Toast.makeText(this,"[ERROR: FAILED TO REMOVE SONG]",Toast.LENGTH_SHORT).show();
                song.isFav = true;
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
