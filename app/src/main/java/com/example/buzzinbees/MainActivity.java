package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button B_on,B_list,B_off, snd_effct, get_nme;
    public BluetoothAdapter BA;
    public Bluetooth_Connection BC;
    public BluetoothDevice BD;

    private static String deviceName = "DSD_TECH";
    private static String mac_adress;
    public static final UUID B_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public static Context context;

    TextView connectedDevices;


    public int effect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        temporary buttons needed
//      turn on bluetooth
        B_on = findViewById(R.id.Bluetooth_onBtn);
//      list bluetooth devices
        B_list = findViewById(R.id.Bluetooth_list);
//      turn off bluetooth
        B_off = findViewById(R.id.Bluetooth_offBtn);

        snd_effct = findViewById(R.id.sendEffect);

        get_nme = findViewById(R.id.getNameofDevice);

        connectedDevices = (TextView) findViewById(R.id.listOfDevices);

        effect = 1;

//      bluetooth adapter needed for bluetooth to work
        BA = (BluetoothAdapter)BluetoothAdapter.getDefaultAdapter();
        if(BA == null){
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth", Toast.LENGTH_LONG).show();
        }

        context = getApplicationContext();

        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        if(pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if(checkForChip(deviceName)){
                    BD = device;
                }
            }
        }

        BC = new Bluetooth_Connection(BD);

        int REQUEST_ENABLE_BT = 1;
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }


    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if(checkForChip(deviceName)){
                    BD = device;
                }
            }
        }
    };


    public boolean checkForChip(String name){
        if(name == deviceName){
            return true;
        }else{
            return false;
        }
    }

    //    turn bluetooth off
    public void Bluetooth_off(View v){
        BC.cancel();
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

    public void sendEffect(View v){
        Toast.makeText(getApplicationContext(), "sent effect", Toast.LENGTH_LONG).show();
        BC.sendInfo(effect);
    }

    public void getName(View v){
        String BName = BA.getName();
        Toast.makeText(getApplicationContext(), BName, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

}
