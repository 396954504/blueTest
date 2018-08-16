package com.example.lihon.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.bluetooth.BluetoothAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.lihon.myapplication.MESSAGE";
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final boolean D = true;
    private static final String TAG = "BlueTest";
    private int REQUEST_ENABLE = 1;
    private Spinner spinner=null;
    private Button button_scan = null;
    private boolean spinner_selected = false;
    private Context mContext;
    private EditText editText_bt_name;
    //真正的字符串数据将保存在这个list中
    private List<String> device_list;
    private ArrayAdapter spin_adapter;
    //private com.example.android.BluetoothChat.BluetoothChatService mChatService = null;

    private static final String[] device_default =
            {
                    "MH_TEST","MH_ranzhao","MH_zhaojunwei"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (D)
            Log.w(TAG, "+++ onCreate +++");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (D)
            Log.w(TAG, "getDefaultAdapter ok");
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "No Bluetooth!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
        }
        if (D)
            Log.w(TAG, "before get name");
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        //获取本机蓝牙地址
        String address = mBluetoothAdapter.getAddress();
        if (D)
            Log.w(TAG,"+++ bluetooth name ="+name+" address ="+address +" +++");
        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (D)
            Log.w(TAG, "+++ bonded device size ="+devices.size() +" +++");
        for(BluetoothDevice bonddevice:devices){
            if (D)
                Log.w(TAG, "   bonded device name ="+bonddevice.getName()+" address="+bonddevice.getAddress());
        }

        editText_bt_name= (EditText) findViewById(R.id.editText_bt_name);
        button_scan = (Button) findViewById(R.id.button_scan);

        //spinner处理
        device_list = new ArrayList<String>();
        for (int i = 0; i < device_default.length; i++)
        {
            device_list.add(device_default[i]);
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        mContext = MainActivity.this;
        spin_adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,device_list);
        //这个是设置选项卡的显示格式的
        //adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spin_adapter.setDropDownViewResource(R.layout.my_spinner_item);
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.bt_name_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(spin_adapter);
        spinner.setOnItemSelectedListener(new spinnerOnItemSelectedListener());


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
            if (D)
                Log.w(TAG,"+++ mBluetoothReceiver action +++="+action);
            if(BluetoothDevice.ACTION_FOUND.equals(action)){//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(scanDevice == null || scanDevice.getName() == null) return;
                if (D)
                    Log.w(TAG, "name="+scanDevice.getName()+"address="+scanDevice.getAddress());
                //蓝牙设备名称
                String name = scanDevice.getName();
                String addr = scanDevice.getAddress();
                spin_adapter.add(name + "[" + addr + "]");
                // if(name != null && name.equals(BLUETOOTH_NAME)){
                //     mBluetoothAdapter.cancelDiscovery();
                //取消扫描
                // mProgressDialog.setTitle(getResources().getString(R.string.progress_connecting));                   //连接到设备。
                // mBlthChatUtil.connect(scanDevice);
                //}
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                button_scan.setEnabled(true);
                if (D)
                    Log.w(TAG, "+++ scan finished +++");
                Toast.makeText(mContext, "scan finished!", Toast.LENGTH_LONG).show();

            }
        }

    };
    public void scan(View view){
        spin_adapter.clear();
        spinner_selected = false;
        button_scan.setEnabled(false);
        mBluetoothAdapter.startDiscovery();
        if (D)
            Log.w(TAG, "+++ scan +++");
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

    //OnItemSelected监听器
    private class  spinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.spinner:
                        if (spinner_selected) {
                            Toast.makeText(mContext, "选择设备:" + parent.getItemAtPosition(position).toString(),
                                    Toast.LENGTH_SHORT).show();
                            String[] temp=null;
                            String str1 = parent.getItemAtPosition(position).toString();
                            temp = str1.split("\\[");
                            editText_bt_name.setText(temp[0]);

                        } else spinner_selected = true;
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


    }

}

//public class MainActivity extends ActionBarActivity {
//}
