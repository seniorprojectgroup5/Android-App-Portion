package com.example.buzzinbees;

import android.bluetooth.BluetoothDevice;

public interface OnFragmentInteractionListener {
    void openBluetoothFragment();
    void setUpBluetooth(BluetoothDevice bleD, String uuid);

    //send effects
    void sendEffect1();
    void sendEffect4();
    void sendEffect7();
    void sendEffect24();
}
