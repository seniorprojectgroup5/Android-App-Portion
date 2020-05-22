package com.example.buzzinbees;

import android.bluetooth.BluetoothDevice;

public interface OnFragmentInteractionListener {
    void openBluetoothFragment();
    void setUpBluetooth(BluetoothDevice bleD, String uuid);

    //send effects
    void stopEffects();
    void sendEffect1();
    void sendEffect2();
    void sendEffect3();
    void sendEffect4();
    void sendEffect5();

    void waitToSendInfo();
}
