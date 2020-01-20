package com.example.buzzinbees;

import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Bluetooth_Communication extends MainActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void sendInfo(BluetoothSocket BlueSocket){
        try{
            B_input = new DataInputStream(BlueSocket.getInputStream());
            B_output = new DataOutputStream(BlueSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int msgTmp = 1;
        try{
            B_output.write(msgTmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(BluetoothSocket BS, OutputStream OS, InputStream IS){
        try {
            if(BS != null && OS != null && IS != null){
                BS.close();
                OS.close();
                IS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
