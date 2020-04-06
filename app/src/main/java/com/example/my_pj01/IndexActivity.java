package com.example.my_pj01;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.my_pj01.Models.BeaconModel;
import com.example.my_pj01.Models.PromotionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.my_pj01.App.channel;

public class IndexActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_BT = 1;
    public static HashMap<String, BeaconModel> mBTDevicesHashMap;
    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;
    private NotificationManagerCompat notificationManager;
    boolean isState = false;
    final private String addr_b1 = "30:AE:A4:F4:87:32";
    final private String addr_b2 = "24:6F:28:9D:6E:AE";
    final private String addr_b3 = "A4:CF:12:75:0A:6A";

    LinearLayout layoutTop, layoutBottom;
    Animation upToDown, downToUp;
    List<PromotionModel> promotionModels = new ArrayList<>();
    PromotionModel promotionModel;
    BeaconModel btle_device;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("products");
    DatabaseReference beacons = FirebaseDatabase.getInstance().getReference("beacons");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Utils.FullScreen(this);
        Utils.CheckOpenLocation(this);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(IndexActivity.this, 1, -100);
        mBTDevicesHashMap = new HashMap<>();

        startScan();

        notificationManager = NotificationManagerCompat.from(this);
        layoutTop = findViewById(R.id.layoutTop);
        layoutBottom = findViewById(R.id.layoutBottom);
        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        layoutTop.setAnimation(upToDown);
        layoutBottom.setAnimation(downToUp);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
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
        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //check bluetooth enable
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
            } else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }

    //check promotion duplicate
    private void checkDuplicationPromotion(List<PromotionModel> promoList, PromotionModel promotionModel) {
        for (Iterator<PromotionModel> iterator = promoList.iterator(); iterator.hasNext(); ) {
            if (iterator.next().getPromotion().equalsIgnoreCase(promotionModel.getPromotion())) {
                iterator.remove();
            }
        }
    }

     //Adds a device to the ArrayList and Hashmap that the ListAdapter is keeping track of
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDevice(BluetoothDevice device, int rssi) {

        String name = device.getName();
        String address = device.getAddress();

        if (address == null) return;
        if (!address.equals(addr_b1) && !address.equals(addr_b2) && !address.equals(addr_b3))
            return;

        if (!mBTDevicesHashMap.containsKey(address)) {
            btle_device = new BeaconModel(device);
            btle_device.setRSSI(rssi);

            mBTDevicesHashMap.put(name, btle_device);
            mBTDevicesHashMap.put(address, btle_device);

            if (address.equals(addr_b1)) {
                DatabaseReference child = beacons.child("beacon1");
                child.child("uuid").setValue(address).toString();
                child.child("namespace").setValue(name).toString();
                child.child("instance").setValue(name + address).toString();
                child.child("rssi").setValue(rssi).toString();
            } else if (address.equals(addr_b2)) {
                DatabaseReference child = beacons.child("beacon2");
                child.child("uuid").setValue(address).toString();
                child.child("namespace").setValue(name).toString();
                child.child("instance").setValue(name + address).toString();
                child.child("rssi").setValue(rssi).toString();
            } else if (address.equals(addr_b3)) {
                DatabaseReference child = beacons.child("beacon3");
                child.child("uuid").setValue(address).toString();
                child.child("namespace").setValue(name).toString();
                child.child("instance").setValue(name + address).toString();
                child.child("rssi").setValue(rssi).toString();
            }
        } else {
            mBTDevicesHashMap.get(name).setName(name);
            mBTDevicesHashMap.get(address).setRSSI(rssi);

            if (isState == false) {
                if (name.equals("Beacon_1") || name.equals("Beacon_2") || name.equals("Beacon_3")) {
                    myRef.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                promotionModel = new PromotionModel();

                                promotionModel.setPromotion(ds.child("promotion").getValue().toString().toLowerCase());
                                if (promotionModels.size() > 0) {
                                    checkDuplicationPromotion(promotionModels, promotionModel);
                                }
                                if (!promotionModel.getPromotion().equals("no promotion")) {
                                    promotionModels.add(promotionModel);
                                }
                            }

                            String sum_text = "";
                            for (int i = 0; i < promotionModels.size(); i++) {
                                String msg = promotionModels.get(i).getPromotion();
                                sum_text = sum_text.concat("- " + msg + System.getProperty("line.separator"));
                                if (i == promotionModels.size() - 1) {
                                    addNotification("Indoor Preference Application", sum_text);
                                }
                            }
                            promotionModels.clear();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                        }
                    });
                }
                isState = true;
            } else {
                isState = true;
            }
        }
        //Read beacon data from firebasae
        beacons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    btle_device = new BeaconModel();
                    btle_device.setAddres(ds.child("uuid").getValue().toString());
                    btle_device.setName(ds.child("namespace").getValue().toString());
                    btle_device.setAddres(ds.child("instance").getValue().toString());
                    btle_device.setText_rssi(ds.child("rssi").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //set notification and notification to mobile
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, channel.getId())
                .setSmallIcon(R.drawable.earphones)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setColor(Color.YELLOW)
                .setGroup(title)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .build();

        notificationManager.notify(1, notification);
    }


     // Clears the ArrayList and Hashmap the ListAdapter is keeping track of
     // Starts Scanner_BTLE
    public void startScan() {
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

     // Stops Scanner_BTLE
    public void stopScan() {
        mBTLeScanner.stop();
    }
}