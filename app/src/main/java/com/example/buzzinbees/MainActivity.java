package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
    Button B_on,B_list,B_off;
    private BluetoothAdapter BA;
    private BluetoothSocket BS;
    TextView connectedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        temporary buttons needed
//      turn on bluetooth
        B_on = (Button) findViewById(R.id.Bluetooth_onBtn);
//      list bluetooth devices
        B_list =(Button)findViewById(R.id.Bluetooth_list);
//      turn off bluetooth
        B_off =(Button)findViewById(R.id.Bluetooth_offBtn);

        connectedDevices = (TextView) findViewById(R.id.listOfDevices);

//      bluetooth adapter needed for bluetooth to work
        BA = (BluetoothAdapter)BluetoothAdapter.getDefaultAdapter();
        String tmp = BA.getAddress();
        BluetoothDevice BD = BA.getRemoteDevice(tmp);

        if(BA == null){
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth", Toast.LENGTH_LONG).show();
        }

        Bluetooth_Communication BC = new Bluetooth_Communication();
        BC.sendInfo(BS);

    }

    public void Bluetooth_on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    //    turn bluetooth off
    public void Bluetooth_off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    //  look for all paired devices
    public void Bluetooth_list(View v){
        Set<BluetoothDevice> pairedDevices;
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
