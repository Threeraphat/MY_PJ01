package com.example.my_pj01;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by Kelvin on 4/20/16.
 */
public class Scanner_BTLE {

    private IndexActivity ma;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private long scanPeriod;
    private int signalStrength;

    public Scanner_BTLE(IndexActivity indexActivity, long scanPeriod, int signalStrength) {
        ma = indexActivity;

        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager;

        bluetoothManager = (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        if (!Utils.checkBluetooth(mBluetoothAdapter)) {
            Utils.requestUserBluetooth(ma);
            ma.stopScan();
        } else {
            scanLeDevice(true);
        }
    }

    public void stop() {
        scanLeDevice(false);
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.

    private void scanLeDevice(final boolean enable) {
        Log.d("Scanner", String.valueOf(mScanning));
//        if (enable && !mScanning) {
        //Utils.toast(ma.getApplicationContext(), "Starting BLE scan...");

        // Stops scanning after a pre-defined scan period.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Utils.toast(ma.getApplicationContext(), "Stopping BLE scan...");
//
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//
//                    ma.stopScan();
//                }
//            }, scanPeriod);

        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        //mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
//        } else {
//            mScanning = false;
//
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            try {
                String decodedRecord = new String(scanRecord,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("DEBUG-ARM","decoded String : " + ByteArrayToString(scanRecord));
            final int new_rssi = rssi;
            if (rssi > signalStrength) {
                mHandler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        ma.addDevice(device, new_rssi);
                    }
                });
            }
        }
    };

    public static String ByteArrayToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");
        return hex.toString();
    }
}