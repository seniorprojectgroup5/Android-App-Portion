package com.example.buzzinbees;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Controlling extends AppCompatActivity {

    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;


    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;

    final static String effct1 = "1";
    final static String effct4 = "4";
    final static String effct7 = "7";
    final static String effct24 = "24";
    final static String effct47 = "47";

    final static String off = "0";


    private ProgressDialog progressDialog;
    Button btn1,btn4,btn7,btn24,btn47,btnOff,btnMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);

        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);

        btn1 = findViewById(R.id.Ebtn_1);
        btn4 = findViewById(R.id.Ebtn_4);
        btn7 = findViewById(R.id.Ebtn_7);
        btn24 = findViewById(R.id.Ebtn_24);
        btn47 = findViewById(R.id.Ebtn_47);

        btnOff = findViewById(R.id.effects_off);
        btnMA = findViewById(R.id.startMainActivity);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(BLE_Manager.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(BLE_Manager.DEVICE_UUID));
        //mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");

        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(effct1.getBytes());
                    Log.d(TAG, effct1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(effct4.getBytes());
                    Log.d(TAG, effct7);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btn7.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(effct7.getBytes());
                    Log.d(TAG, effct7);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn24.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(effct24.getBytes());
                    Log.d(TAG, effct24);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btn47.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(effct47.getBytes());
                    Log.d(TAG, effct47);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(off.getBytes());
                    Log.d(TAG, effct47);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnMA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }




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
                inputStream = mBTSocket.getInputStream();
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
                mBTSocket.close();
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
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

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

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
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
                mReadThread = new ReadInput(); // Kick off input reader
            }
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
