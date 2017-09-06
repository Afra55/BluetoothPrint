package com.afra.bluetoothprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fitness_root_rv)
    RecyclerView mBluetoothRv;

    @BindView(R.id.get_bluetooth)
    View get_bluetooth;

    private ShowBluetoothAdapter adapter;
    private boolean isPrinting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        BroadcastReceiver receiver = new BluetoothReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiver, filter);

        adapter = new ShowBluetoothAdapter(this);
        mBluetoothRv.setLayoutManager(new LinearLayoutManager(this));
        mBluetoothRv.setAdapter(adapter);

        adapter.setOnDeviceChooseListener(new OnDeviceChooseListener() {
            @Override
            public void onChoose(final BluetoothDevice bluetoothDevice) {

                if (isPrinting) {
                    Toast.makeText(MainActivity.this, "正在打印", Toast.LENGTH_LONG).show();
                    return;
                }
                isPrinting = true;

                Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<BluetoothDevice> e) throws Exception {
                        try {
                            toPrint(bluetoothDevice);
                            e.onComplete();
                        } catch (Exception e1) {
                            e.onError(e1);
                        }

                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<BluetoothDevice>() {
                            @Override
                            public void accept(BluetoothDevice bluetoothDevice) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                isPrinting = false;
                                throwable.printStackTrace();

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                isPrinting = false;
                            }
                        });
            }
        });
    }

    private void toPrint(BluetoothDevice bluetoothDevice) throws Exception{
        BluetoothSocket bluetoothSocket = null;
        OutputStream outputStream = null;
        try {
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            //获取了对应的输入流
            outputStream = bluetoothSocket.getOutputStream();

            PrintUtils.setOutputStream(outputStream);
            PrintUtils.selectCommand(PrintUtils.RESET);
            PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
            PrintUtils.printText("美食餐厅\n\n");
            PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
            PrintUtils.printText("桌号：1号桌\n\n");
            PrintUtils.selectCommand(PrintUtils.NORMAL);
            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
            PrintUtils.printText(PrintUtils.printTwoData("订单编号", "201507161515\n"));
            PrintUtils.printText(PrintUtils.printTwoData("点菜时间", "2016-02-16 10:46\n"));
            PrintUtils.printText(PrintUtils.printTwoData("上菜时间", "2016-02-16 11:46\n"));
            PrintUtils.printText(PrintUtils.printTwoData("人数：2人", "收银员：张三\n"));

            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD);
            PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
            PrintUtils.printText(PrintUtils.printThreeData("面", "1", "0.00\n"));
            PrintUtils.printText(PrintUtils.printThreeData("米饭", "1", "6.00\n"));
            PrintUtils.printText(PrintUtils.printThreeData("铁板烧", "1", "26.00\n"));
            PrintUtils.printText(PrintUtils.printThreeData("一个测试", "1", "226.00\n"));
            PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊", "1", "2226.00\n"));
            PrintUtils.printText(PrintUtils.printThreeData("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n"));

            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("合计", "53.50\n"));
            PrintUtils.printText(PrintUtils.printTwoData("抹零", "3.50\n"));
            PrintUtils.printText("--------------------------------\n");
            PrintUtils.printText(PrintUtils.printTwoData("应收", "50.00\n"));
            PrintUtils.printText("--------------------------------\n");

            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
            PrintUtils.printText("备注：不要辣、不要香菜");
            PrintUtils.printText("\n\n\n\n\n");
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                    bluetoothSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.get_bluetooth)
    public void handleBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            //判断蓝牙是否可用    不可以用就开启蓝牙  开启表示可用
            if (!bluetoothAdapter.isEnabled()) {
                //通过一个意图启动蓝牙  请求码大于0就可以了
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 4);
            } else {
                Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
                if (bondedDevices != null && !bondedDevices.isEmpty()) {
                    adapter.init(bondedDevices);
                    adapter.notifyDataSetChanged();
                }

            }
        } else {
            Toast.makeText(MainActivity.this, "该设备不存在蓝牙", Toast.LENGTH_LONG).show();
        }
    }
}
