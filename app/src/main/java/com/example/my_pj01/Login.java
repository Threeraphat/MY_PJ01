package com.example.my_pj01;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.my_pj01.Models.BeaconModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    public static Activity login;
    BeaconModel btle_device;
    final private String addr_b1 = "30:AE:A4:F4:87:32";
    final private String addr_b2 = "24:6F:28:9D:6E:AE";
    final private String addr_b3 = "A4:CF:12:75:0A:6A";
    List<BeaconModel> btle_devices = new ArrayList<>();
    DatabaseReference beacons = FirebaseDatabase.getInstance().getReference("beacons");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.FullScreen(this);

        findViewById(R.id.guest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to activity for users
                startActivity(new Intent(Login.this, TypeActivity.class));
            }
        });

        beacons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    btle_device = new BeaconModel();
                    btle_device.setAddres(ds.child("uuid").getValue().toString());
                    btle_devices.add(btle_device);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i < btle_devices.size();i++){
                    if (btle_devices.get(i).getAddress().equals(addr_b1)) {
                        startActivity(new Intent(Login.this, LoginAdmin.class));
                        break;
                    } else if(btle_devices.get(i).getAddress().equals(addr_b2)) {
                        startActivity(new Intent(Login.this, LoginAdmin.class));
                        break;
                    } else if(btle_devices.get(i).getAddress().equals(addr_b3)){
                        startActivity(new Intent(Login.this, LoginAdmin.class));
                        break;
                    } else {
                        Utils.toast(getApplicationContext(), "uuid not match please try again.");
                    }
                }

            }
        });
    }
}