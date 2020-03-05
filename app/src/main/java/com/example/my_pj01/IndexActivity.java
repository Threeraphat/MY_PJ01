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
import android.os.Parcel;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.my_pj01.Models.BTLE_Device;
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
    public static HashMap<String, BTLE_Device> mBTDevicesHashMap;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("products");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Utils.FullScreen(this);
        Utils.CheckOpenLocation(this);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
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
        //Utils.toast(this, "onStart BLE scan...");
        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        //Utils.toast(this, "Resume BLE scan...");
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Utils.toast(this, "onPause BLE scan...");
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Utils.toast(this, "onStop BLE scan...");
        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    public void onDestroy() {
        //Utils.toast(this, "onDestroy BLE scan...");
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

    private void checkDuplicationPromotion(List<PromotionModel> promoList, PromotionModel promotionModel) {
        for (Iterator<PromotionModel> iterator = promoList.iterator(); iterator.hasNext(); ) {
            if (iterator.next().getPromotion().equalsIgnoreCase(promotionModel.getPromotion())) {
                iterator.remove();
            }
        }
    }

    /**
     * Adds a device to the ArrayList and Hashmap that the ListAdapter is keeping track of.
     *
     * @param device the BluetoothDevice to be added
     * @param rssi   the rssi of the BluetoothDevice
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDevice(BluetoothDevice device, int rssi) {

        String name = device.getName();
        String address = device.getAddress();
        if (address == null) return;
        if (!address.equals(addr_b1) && !address.equals(addr_b2) && !address.equals(addr_b3))
            return;

        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(name, btleDevice);
            mBTDevicesHashMap.put(address, btleDevice);
            Log.d("TEST_BLE", "--------->" + btleDevice.getName() + "  RSSI-->" + btleDevice.getRSSI());
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
                                promotionModel.setPromotion(ds.child("promotion").getValue().toString());
                                if (promotionModels.size() > 0) {
                                    checkDuplicationPromotion(promotionModels, promotionModel);
                                }
                                if (!promotionModel.getPromotion().equals("No promotion")) {
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
                        //NotificationService.addNotification(this,"test","test");
                    });
                }
                isState = true;
            } else {
                isState = true;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, channel.getId())
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setColor(Color.GREEN)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(title)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1, notification);
    }

    /**
     * Clears the ArrayList and Hashmap the ListAdapter is keeping track of.
     * Starts Scanner_BTLE.
     * Changes the scan button text.
     */
    public void startScan() {
        //Utils.toast(this, "startScan");
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

    /**
     * Stops Scanner_BTLE
     * Changes the scan button text.
     */
    public void stopScan() {
        //Utils.toast(this, "Stopscan");
        mBTLeScanner.stop();
    }
}