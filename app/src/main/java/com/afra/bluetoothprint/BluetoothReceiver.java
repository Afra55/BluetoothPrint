package com.afra.bluetoothprint;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction(); //得到action

        switch (action) {
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, "发现设备:" + bluetoothDevice.getName() + "|" + bluetoothDevice.getAddress() , Toast.LENGTH_LONG).show();
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST:

                break;
        }
    }
}
