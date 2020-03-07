package com.example.my_pj01.Models;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Kelvin on 5/8/16.
 */
public class BTLE_Device {

    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private String text_rssi;
    private String namespace, uuid, addres;

    public BTLE_Device(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BTLE_Device(){
    }
    public String getAddress() {
        return addres;
    }

    public void setAddres(String addr){
        this.addres = addr;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return namespace;
    }

    public void setText_rssi(String text_rssi) {
        this.text_rssi = text_rssi;
    }

    public void setName(String name) {
        this.namespace = name;
    }

    public String getUUID() {
        return bluetoothDevice.getUuids().toString();
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }
}