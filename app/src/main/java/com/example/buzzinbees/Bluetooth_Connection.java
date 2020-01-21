package com.example.buzzinbees;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.buzzinbees.MainActivity.B_UUID;
import static com.example.buzzinbees.MainActivity.context;

class Bluetooth_Connection extends Thread {
    private static final String TAG = "debugger";
    private final BluetoothSocket B_Socket;
    private final BluetoothDevice B_Device;
    Bluetooth_Service.ConnectedThread CT;
    private final MainActivity main;

    public Bluetooth_Connection(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        B_Device = device;

        main = new MainActivity();

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(B_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        B_Socket = tmp;
    }


    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        main.BA.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            B_Socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                B_Socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }


        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        CT = new Bluetooth_Service.ConnectedThread(B_Socket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            B_Socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public void sendInfo(int i){
        CT.write(i);
    }

}
