package com.example.buzzinbees;

import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Bluetooth_Communication extends MainActivity {
    BluetoothSocket BS;
    InputStream B_input;
    OutputStream B_output;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void sendInfo(BluetoothSocket BlueSocket){
        BS = BlueSocket;
        try{
            B_input = BS.getInputStream();
            B_output = BS.getOutputStream();
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
}
