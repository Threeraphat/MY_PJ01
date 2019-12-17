package com.example.my_pj01;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainAdmin extends AppCompatActivity {

    RecyclerView dataRecyclerView;
    private DataAdapter adapter;
    private static final String TAG = "_project_";
    List<ProductModel> productModels = new ArrayList<>();
    ProductModel productModel;
    private ImageView m_img;
    private EditText m_type,m_promotionTEXT,m_name,m_price,m_weight,m_detail,m_shelf,m_row,m_column;
    private Button m_update,m_remove,m_back;
    private StorageReference c_pic;
    private File file;
    String getType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        getType = intent.getStringExtra("type");
        //System.out.println("getType from TypeActivity---> " + getType);
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
                    productModel = new ProductModel();
                    productModel.setColumn(ds.child("column").getValue(String.class));
                    productModel.setDescription(ds.child("description").getValue(String.class));
                    productModel.setName(ds.child("name").getValue(String.class));
                    productModel.setPicture(ds.child("picture").getValue(String.class));
                    productModel.setPrice(ds.child("price").getValue(String.class));
                    productModel.setProductX(ds.child("productX").getValue(String.class));
                    productModel.setProductY(ds.child("productY").getValue(String.class));
                    productModel.setRow(ds.child("row").getValue(String.class));
                    productModel.setShelf(ds.child("shelf").getValue(String.class));
                    productModel.setType(ds.child("type").getValue(String.class));
                    productModel.setWeight(ds.child("weight").getValue(String.class));
                    productModel.setPromotion(ds.child("promotion").getValue(String.class));
                    productModels.add(productModel);
                }
                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                dataRecyclerView.setLayoutManager(manager);
                adapter = new DataAdapter(getApplicationContext(), checkDuplicationType(productModels));
                dataRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(new DataAdapter.onRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, final int position) {
                        final Dialog dialog = getDialog();
                        m_type.setText(productModels.get(position).getType());
                        m_promotionTEXT.setText(productModels.get(position).getPromotion());
                        m_name.setText(productModels.get(position).getName());
                        m_price.setText(productModels.get(position).getPrice().concat(" THB."));
                        m_weight.setText(productModels.get(position).getWeight().concat(" Kg."));
                        m_detail.setText(productModels.get(position).getDescription());
                        m_shelf.setText(productModels.get(position).getShelf());
                        m_row.setText(productModels.get(position).getRow());
                        m_column.setText(productModels.get(position).getColumn());

                        c_pic = FirebaseStorage.getInstance().getReference(productModels.get(position).getPicture());
                        try {
                            file = File.createTempFile("images", "jpeg");
                            c_pic.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getApplicationContext())
                                            .load(file)
                                            .fit()
                                            .centerCrop()
                                            .into(m_img);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(!dialog.isShowing()){
                            dialog.show();
                        }

                        m_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        dialog.findViewById(R.id.dialog_m_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");
                                databaseReference.child(productModels.get(position).getName()).removeValue();
                                dialog.dismiss();
                            }
                        });

                        m_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private Dialog getDialog() {
        final Dialog dialog = new Dialog(MainAdmin.this, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view_manage, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        m_img = dialog.findViewById(R.id.dialog_m_img);
        m_type = dialog.findViewById(R.id.dialog_m_type);
        m_promotionTEXT = dialog.findViewById(R.id.dialog_m_promo);
        m_name = dialog.findViewById(R.id.dialog_m_name);
        m_price = dialog.findViewById(R.id.dialog_m_price);
        m_weight = dialog.findViewById(R.id.dialog_m_weight);
        m_detail = dialog.findViewById(R.id.dialog_m_detail);
        m_shelf = dialog.findViewById(R.id.dialog_m_shelf);
        m_row = dialog.findViewById(R.id.dialog_m_row);
        m_column = dialog.findViewById(R.id.dialog_m_column);
        m_update = dialog.findViewById(R.id.dialog_m_update);
        m_remove = dialog.findViewById(R.id.dialog_m_delete);
        m_back = dialog.findViewById(R.id.dialog_m_cancel);
        return dialog;
    }

    private List<ProductModel> checkDuplicationType(List<ProductModel> productList) {
        for (Iterator<ProductModel> iterator = productList.iterator(); iterator.hasNext(); ) {
            if (!iterator.next().getType().equalsIgnoreCase(getType)) {
                iterator.remove();
            }
        }
        return productList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_bar,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("input something...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}