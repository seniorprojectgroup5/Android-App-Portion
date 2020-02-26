package com.example.buzzinbees;

import android.bluetooth.BluetoothDevice;

public interface OnFragmentInteractionListener {
    void openBluetoothFragment();
    void setUpBluetooth(BluetoothDevice bleD, String uuid);
}
