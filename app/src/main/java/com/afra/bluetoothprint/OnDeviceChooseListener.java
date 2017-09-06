package com.afra.bluetoothprint;

import android.bluetooth.BluetoothDevice;

/**
 * Created by yangshuai on 2017/9/6.
 * {link http://afra55.github.io}
 */

public interface OnDeviceChooseListener {

    void onChoose(BluetoothDevice bluetoothDevice);
}
