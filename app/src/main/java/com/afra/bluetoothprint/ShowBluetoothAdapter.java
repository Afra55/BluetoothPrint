package com.afra.bluetoothprint;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangshuai on 2017/9/6.
 * {link http://afra55.github.io}
 */

public class ShowBluetoothAdapter extends RecyclerView.Adapter {

    private final Context context;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private OnDeviceChooseListener onDeviceChooseListener;


    public OnDeviceChooseListener getOnDeviceChooseListener() {
        return onDeviceChooseListener;
    }

    public void setOnDeviceChooseListener(OnDeviceChooseListener onDeviceChooseListener) {
        this.onDeviceChooseListener = onDeviceChooseListener;
    }

    public ShowBluetoothAdapter(Context context) {
        this.context = context;
    }

    public void init(Set<BluetoothDevice> deviceSet) {
        deviceList.clear();
        deviceList.addAll(deviceSet);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.bluetooth_show_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bind(deviceList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.show_tv)
        TextView textView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(BluetoothDevice device) {
            String msg = "";

            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDED:
                    msg = "BOND_BONDED";
                    break;
                case BluetoothDevice.BOND_BONDING:
                    msg = "BOND_BONDING";
                    break;
                case BluetoothDevice.BOND_NONE:
                    msg = "BOND_NONE";
                    break;
            }
            msg += "\n";
            msg += device.getName();
            msg += "\n";
            msg += device.getUuids();
            msg += "\n";
            msg += device.getAddress();
            textView.setText(msg);
        }

        @OnClick(R.id.show_tv)
        public void onClicked() {
            if (onDeviceChooseListener != null) {
                onDeviceChooseListener.onChoose(deviceList.get(getLayoutPosition()));
            }
        }
    }
}
