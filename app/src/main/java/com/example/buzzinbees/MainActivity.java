package com.example.buzzinbees;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private static final String TAG = "ble - ";

    int currentFragment = 0;

    private DrawerLayout drawer;

    FrameLayout audioContainer;

    AudioManagerFragment audioManagerFragment;

    NavigationView navigationView;

    public ArrayList<Song> arraySongList = new ArrayList<>();


    //BLUETOOTH
    private BluetoothAdapter bleAdapter;
    private BluetoothDevice bleDevice;
    private UUID bleDeviceUUID;
    private BluetoothSocket bleSocket;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private ReadInput mReadThread = null;
    private ProgressDialog progressDialog;


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

        // bluetooth adapter
        bleAdapter = BluetoothAdapter.getDefaultAdapter();

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

        }

        arraySongList = new ArrayList<Song>();
        Log.d("SONGLIST",arraySongList.toString());
        //arraySongList.add(new Song());
        // init song list array

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
            case R.id.navigation_bluetooth:
                openBluetoothFragment();
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
            super.onBackPressed();
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



    //** BLUETOOTH **//
    public void openBluetoothFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                new BLE_Manager()).commit();
        audioContainer.setVisibility(View.GONE);
        currentFragment = Constant.FRAGVAL_BLUETOOTH;
    }

    public void setUpBluetooth(BluetoothDevice bleD, String uuid){
        bleDeviceUUID = UUID.fromString(uuid);
        bleDevice = bleD;

        if (bleSocket == null || !mIsBluetoothConnected) {
            msg("connected");
            //progressDialog = ProgressDialog.show(getApplicationContext(), "Hold on", "Connecting");
            new ConnectBT().execute();
        }else {
            msg("no");
        }
    }


    // headaches below
    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = bleSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */
                    }
                    Thread.sleep(500);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void stop() {
            bStop = true;
        }
    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;
            }

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

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (bleSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }
//
//    @Override
//    protected void onResume() {
//        if (bleSocket == null || !mIsBluetoothConnected) {
//            new ConnectBT().execute();
//        }
//        Log.d(TAG, "Resumed");
//        super.onResume();
//    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }



    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(getApplicationContext(), "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
//        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (bleSocket == null || !mIsBluetoothConnected) {
                    bleSocket = bleDevice.createInsecureRfcommSocketToServiceRecord(bleDeviceUUID);

                    bleAdapter.getDefaultAdapter().cancelDiscovery();

                    bleSocket.connect();

                    if(bleSocket == null){
                        Log.d(TAG, "bad");
                    }else{
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
                //mReadThread = new ReadInput(); // Kick off input reader
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
