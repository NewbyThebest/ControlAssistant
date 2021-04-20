package com.geekb.controlassistant;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.mmkv.MMKV;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends BaseActivity {
    private final static int REQUEST_ENABLE_BT = 1001;
    private final static int MY_PERMISSION_REQUEST_CONSTANT = 1002;
    private BluetoothAdapter mBluetoothAdapter;
    private SwitchCompat switchCompat;
    private RecyclerView mRvMatched;
    private RecyclerView mRvCanMatch;
    private TextView mBack;
    private RvAdapter mMatchedAdapter;
    private RvAdapter mCanMatchAdapter;
    private ConnectedThread connectedThread;
    private List<BluetoothDevice> mMatchedList = new ArrayList<>();
    private List<BluetoothDevice> mCanMatchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        MMKV.defaultMMKV().encode("isFirstLaunch", false);
        switchCompat = findViewById(R.id.sw_bluetooth_switch);
        mRvMatched = findViewById(R.id.rv_matched);
        mRvCanMatch = findViewById(R.id.rv_can_match);
        mBack = findViewById(R.id.back_title);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    mCanMatchList.clear();
                    mMatchedList.clear();
                    mCanMatchAdapter.notifyDataSetChanged();
                    mMatchedAdapter.notifyDataSetChanged();
                    mBluetoothAdapter.disable();
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        //垂直方向
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        //垂直方向
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        //给RecyclerView设置布局管理器
        mRvMatched.setLayoutManager(layoutManager1);
        mRvCanMatch.setLayoutManager(layoutManager2);

        //创建适配器，并且设置
        mMatchedAdapter = new RvAdapter(mMatchedList);
        mRvMatched.setAdapter(mMatchedAdapter);
        mMatchedAdapter.setOnItemClickListener(new RvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice data) {
                mBluetoothAdapter.cancelDiscovery();
                ConnectThread bluetoothThread = new ConnectThread(data, new ConnectThread.BluetoothConnectCallback() {
                    @Override
                    public void connectSuccess(BluetoothSocket socket) {
                        connectedThread = new ConnectedThread(socket);
                        connectedThread.start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BluetoothActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                    @Override
                    public void connectFailed(String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BluetoothActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
                bluetoothThread.start();
            }

            @Override
            public void onItemLongClick(BluetoothDevice data) {
                Method method = null;
                try {
                    method = BluetoothDevice.class.getMethod("removeBond");
                    Toast.makeText(BluetoothActivity.this, "移除配对！", Toast.LENGTH_SHORT).show();
                    method.invoke(data);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                mMatchedList.remove(data);
            }
        });

        //创建适配器，并且设置
        mCanMatchAdapter = new RvAdapter(mCanMatchList);
        mRvCanMatch.setAdapter(mCanMatchAdapter);
        mCanMatchAdapter.setOnItemClickListener(new RvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice data) {
                mBluetoothAdapter.cancelDiscovery();
                Method method = null;
                try {
                    method = BluetoothDevice.class.getMethod("createBond");
                    Toast.makeText(BluetoothActivity.this, "开始配对！", Toast.LENGTH_SHORT).show();
                    method.invoke(data);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                mMatchedList.add(data);
            }

            @Override
            public void onItemLongClick(BluetoothDevice data) {

            }
        });

        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CONSTANT);
        }

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intent);

        initBluetooth();

    }

    void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                switchCompat.setChecked(true);
                checkAlreadyConnect();
                mBluetoothAdapter.startDiscovery();

            } else {
                switchCompat.setChecked(false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "没有权限，无法搜索蓝牙！", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkAlreadyConnect() {
        mMatchedList.clear();
        //获取已经配对的集合
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mMatchedList.add(device);
            }
            mMatchedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "开启蓝牙成功！", Toast.LENGTH_SHORT).show();
                checkAlreadyConnect();
                mBluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(this, "开启蓝牙失败！", Toast.LENGTH_SHORT).show();
                switchCompat.setChecked(false);
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获得 BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //向mArrayAdapter中添加设备信息
                if (!TextUtils.isEmpty(device.getName()) && !TextUtils.isEmpty(device.getAddress())) {
                    mCanMatchList.add(device);
                    mCanMatchAdapter.notifyDataSetChanged();
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(getBaseContext(), "正在扫描", Toast.LENGTH_SHORT).show();
            } else if (action
                    .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(getBaseContext(), "扫描完成，点击列表中的设备来尝试连接", Toast.LENGTH_SHORT).show();
            } else if (action
                    .equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        mMatchedAdapter.notifyDataSetChanged();

                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(getBaseContext(), "正在配对", Toast.LENGTH_SHORT).show();

                        break;
                    case BluetoothDevice.BOND_BONDED:
                        mMatchedAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "配对成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

    }
}
