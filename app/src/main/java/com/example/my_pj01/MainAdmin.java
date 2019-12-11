package com.example.my_pj01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainAdmin extends AppCompatActivity {

    RecyclerView dataRecyclerView;

    private DataAdapter adapter;
    private static final String TAG = "_project_";
    List<PromotionModel> productModels = new ArrayList<>();
    PromotionModel productModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        init();
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainAdmin.this,ManageProductActivity.class));
                finish();
            }
        });
    }

    private void init(){
        dataRecyclerView = findViewById(R.id.re_list_admin);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        readDataFromFirebase(myRef);
    }

    private void readDataFromFirebase(DatabaseReference myRef) {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    productModel = new PromotionModel();
                    productModel.setColumn(ds.child("column").getValue(String.class));
                    productModel.setDescription(ds.child("description").getValue(String.class));
                    productModel.setName(ds.child("name").getValue(String.class));
                    productModel.setPicture(ds.child("picture").getValue(String.class));
                    productModel.setPrice(ds.child("price").getValue(String.class));
                    productModel.setProductX(ds.child("productX").getValue(String.class));
                    productModel.setProductY(ds.child("productY").getValue(String.class));
                    productModel.setRow(ds.child("row").getValue(String.class));
                    productModel.setShelf(ds.child("shelf").getValue(String.class));
                    productModel.setType(ds.child("Type").getValue(String.class));
                    productModel.setWeight(ds.child("weight").getValue(String.class));
                    productModel.setPromotion(ds.child("promotion").getValue(String.class));
                    productModels.add(productModel);
                }
                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                dataRecyclerView.setLayoutManager(manager);
                adapter = new DataAdapter(getApplicationContext(), productModels);
                dataRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}