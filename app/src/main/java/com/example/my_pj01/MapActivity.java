package com.example.my_pj01;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.example.my_pj01.Models.BTLE_Device;

import java.util.HashMap;

public class MapActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    public static HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 456);
        }

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(MapActivity.this, 1, -100);
        mBTDevicesHashMap = new HashMap<>();

        startScan();
        CustomView customView = new CustomView((this));
        Log.d("Scanner",mBTLeScanner.toString());
        customView.setNumColumns(20);
        customView.setNumRows(40);
        setContentView(customView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 456: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.
                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.toast(this, "onStart BLE scan...");
        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        Utils.toast(this, "Resume BLE scan...");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.toast(this, "onPause BLE scan...");
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.toast(this, "onStop BLE scan...");
        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    public void onDestroy() {
        Utils.toast(this, "onDestroy BLE scan...");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                //Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            } else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }

    /**
     * Adds a device to the ArrayList and Hashmap that the ListAdapter is keeping track of.
     *
     * @param device the BluetoothDevice to be added
     * @param rssi   the rssi of the BluetoothDevice
     */
    public void addDevice(BluetoothDevice device, int rssi) {

        String name = device.getName();
        if (name == null) return;
        if (!name.equals("Beacon_1") && !name.equals("Beacon_2") && !name.equals("Beacon_3")) return;
        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(name, btleDevice);
            mBTDevicesHashMap.put(address, btleDevice);
            Log.d("TEST_BLE", "--------->" + btleDevice.getName() + "  RSSI-->" + btleDevice.getRSSI());
        } else {
            mBTDevicesHashMap.get(name).setName(name);
            mBTDevicesHashMap.get(address).setRSSI(rssi);
        }
    }

    /**
     * Clears the ArrayList and Hashmap the ListAdapter is keeping track of.
     * Starts Scanner_BTLE.
     * Changes the scan button text.
     */
    public void startScan() {
        Utils.toast(this, "startScan");
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

    /**
     * Stops Scanner_BTLE
     * Changes the scan button text.
     */
    public void stopScan() {
        Utils.toast(this, "Stopscan");
        mBTLeScanner.stop();
    }
}