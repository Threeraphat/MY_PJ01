package com.example.my_pj01;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.support.v7.widget.SearchView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_pj01.Models.ProductModel;
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

public class MainActivity extends AppCompatActivity {

    RecyclerView dataRecyclerView;
    private DataAdapter adapter;
    private static final String TAG = "_project_";
    List<ProductModel> productModels = new ArrayList<>();
    ProductModel productModel;
    private ImageView de_img;
    private TextView de_type, de_promo, de_name, de_price, de_weight, de_detail, de_shelf, de_row, de_column;
    private Button de_find, de_cancel;
    private StorageReference c_pic;
    private File file;
    String getType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.FullScreen(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        getType = intent.getStringExtra("type");
        //System.out.println("getType from TypeActivity---> " + getType);
        dataRecyclerView = findViewById(R.id.re_list);
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
                        de_type.setText(productModels.get(position).getType());
                        de_promo.setText(productModels.get(position).getPromotion());
                        de_name.setText(productModels.get(position).getName());
                        de_price.setText(productModels.get(position).getPrice());
                        de_weight.setText(productModels.get(position).getWeight());
                        de_detail.setText(productModels.get(position).getDescription());
                        de_shelf.setText(productModels.get(position).getShelf());
                        de_row.setText(productModels.get(position).getRow());
                        de_column.setText(productModels.get(position).getColumn());

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
                                            .into(de_img);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!dialog.isShowing()) {
                            dialog.show();
                        }

                        de_find.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!MainActivity.this.isFinishing()) {
                                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                    intent.putExtra("shelf", productModels.get(position).getShelf());
                                    startActivity(intent);
                                }
                                dialog.dismiss();
                            }
                        });

                        de_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()) {
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
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view_detail, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        de_img = dialog.findViewById(R.id.dialog_de_img);
        de_type = dialog.findViewById(R.id.dialog_de_type);
        de_promo = dialog.findViewById(R.id.dialog_de_promo);
        de_name = dialog.findViewById(R.id.dialog_de_name);
        de_price = dialog.findViewById(R.id.dialog_de_price);
        de_weight = dialog.findViewById(R.id.dialog_de_weight);
        de_detail = dialog.findViewById(R.id.dialog_de_detail);
        de_shelf = dialog.findViewById(R.id.dialog_de_shelf);
        de_row = dialog.findViewById(R.id.dialog_de_row);
        de_column = dialog.findViewById(R.id.dialog_de_column);
        de_find = dialog.findViewById(R.id.dialog_de_find);
        de_cancel = dialog.findViewById(R.id.dialog_de_cancel);
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
        inflater.inflate(R.menu.guest_bar, menu);

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