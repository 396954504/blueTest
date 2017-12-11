package com.example.lihon.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.lihon.myapplication.MESSAGE";
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final boolean D = true;
    private static final String TAG = "BlueTest";
    private int REQUEST_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (D)
            Log.d(TAG, "+++ onCreate +++");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备无蓝牙功能", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
        }
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        //获取本机蓝牙地址
        String address = mBluetoothAdapter.getAddress();
        Log.d(TAG,"+++ bluetooth name ="+name+" address ="+address +" +++");
        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "+++ bonded device size ="+devices.size() +" +++");
        for(BluetoothDevice bonddevice:devices){
            Log.d(TAG, "   bonded device name ="+bonddevice.getName()+" address="+bonddevice.getAddress());
        }




        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBluetoothReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBluetoothReceiver, filter);

    }
    private BroadcastReceiver  mBluetoothReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"mBluetoothReceiver action ="+action);
            if(BluetoothDevice.ACTION_FOUND.equals(action)){//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(scanDevice == null || scanDevice.getName() == null) return;
                Log.d(TAG, "name="+scanDevice.getName()+"address="+scanDevice.getAddress());
                //蓝牙设备名称
                String name = scanDevice.getName();
                // if(name != null && name.equals(BLUETOOTH_NAME)){
                //     mBluetoothAdapter.cancelDiscovery();
                //取消扫描
                // mProgressDialog.setTitle(getResources().getString(R.string.progress_connecting));                   //连接到设备。
                // mBlthChatUtil.connect(scanDevice);
                //}
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

            }
        }

    };
    public void scan(View view){
        mBluetoothAdapter.startDiscovery();
        //mBluetoothAdapter.cancelDiscovery();

    }

    public void sendMessage(View view){
        //Do nothing in response to the button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_status);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,message);
        startActivity(intent);
    }
}

//public class MainActivity extends ActionBarActivity {
//}
