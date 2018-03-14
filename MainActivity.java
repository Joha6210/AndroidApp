package com.example.grillapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.grillapp.MainActivity.TAG;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;

    private UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private Set<BluetoothDevice>pairedDevices;

    public DeviceListAdapter mDeviceListAdapter;



    public static final String TAG = "MAINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void discoverDevices(View view){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    }

    public void findDevice(View view) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("BluetoothThermometer")) {
                    mBTDevice = device;
                    Log.d(TAG, mBTDevice.getName());
                    Log.d(TAG, mBTDevice.getAddress());
                    break;
                }
            }
        }
    }

    public void StartBluetooth(View view){
        startConnection();

    }

    public void startConnection(){

        startBluetoothConnection(mBTDevice);
    }

    public void startBluetoothConnection (BluetoothDevice device){
        Log.d(TAG, "startBluetoothConnection: Initializing RFCOM Bluetooth Connection");
        //mBluetoothConnection.startClient(device,uuid);
        //mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        ConnectThread myThread;
        myThread = (ConnectThread) new ConnectThread(device);
    }



    public void beefButton(View view){
        Intent beef = new Intent(this, BeefActivity.class);
        startActivity(beef);
    }

    public void chickenButton(View view){
        Intent chicken = new Intent(this, ChickenActivity.class);
        startActivity(chicken);
    }

    public void porkchopButton(View view){
        Intent porkchop = new Intent(this, PorkchopActivity.class);
        startActivity(porkchop);
    }

    public void lambButton(View view){
        Intent lamb = new Intent(this, LambActivity.class);
        startActivity(lamb);
    }

    public void progressClick(View view){
        ProgressBar progressBar1 = findViewById(R.id.progressBar);
        TextView percentText = findViewById(R.id.textView8);
        progressBar1.incrementProgressBy(1);
        int progress = progressBar1.getProgress();
        String text = Integer.toString(progress);
        percentText.setText(text);
        /*if (progressBar1.getProgress() > 100){
            progressBar1.setProgress(100);
        }*/
    }


}

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    public BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        BluetoothAdapter mBluetoothAdapter;
        UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        BluetoothAdapter mBluetoothAdapter;
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        MyBluetoothService(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}

