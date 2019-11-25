package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button b1,b3,b4;
    private BluetoothAdapter BA;
    TextView connectedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        temporary buttons needed
//      turn on bluetooth
        b1 = (Button) findViewById(R.id.button);
//      list bluetooth devices
        b3 =(Button)findViewById(R.id.button3);
//      turn off bluetooth
        b4 =(Button)findViewById(R.id.button4);

        connectedDevices = (TextView) findViewById(R.id.listOfDevices);

//      bluetooth adapter needed for bluetooth to work
        BA = (BluetoothAdapter)BluetoothAdapter.getDefaultAdapter();

        if(BA == null){
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth", Toast.LENGTH_LONG).show();
        }
    }

//    turn bluetooth on
    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

//    turn bluetooth off
    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


//  look for all paired devices
    public void list(View v){
        Set<BluetoothDevice>pairedDevices;
        pairedDevices = BA.getBondedDevices();

        String deviceName = "";
        connectedDevices.setText(deviceName);

//        looks for blutooth devices, using .getName() we get the name of the device
        for(BluetoothDevice bt : pairedDevices) deviceName = deviceName + bt.getName() + "~~~~";

//        the toast for debugging
//        Toast.makeText(getApplicationContext(), deviceName,Toast.LENGTH_SHORT).show();
        connectedDevices.setText(deviceName);
    }
}
