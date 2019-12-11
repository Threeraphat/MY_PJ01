package com.example.my_pj01;

public class BeaconModel{
    private int UUID, RSSI;
    private String instance, namespace;

    public BeaconModel(int UUID, int RSSI, String instance, String namespace) {
        this.UUID = UUID;
        this.RSSI = RSSI;
        this.instance = instance;
        this.namespace = namespace;
    }

    public BeaconModel(){
    }

    public void blescan(){

    }
    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}