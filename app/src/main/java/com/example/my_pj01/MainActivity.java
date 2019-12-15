package com.example.my_pj01;

import android.app.Dialog;
import android.graphics.BitmapFactory;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.support.v7.widget.SearchView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

   RecyclerView dataRecyclerView;
   private DataAdapter adapter;
   private static final String TAG = "_project_";
   List<ProductModel> productModels = new ArrayList<>();
   ProductModel productModel;
   private ImageView Img1;
   private TextView type,promotionTEXT,name,price,weight,detail,shelf,row,column;
   private Button find,cancel;
   private StorageReference c_pic;
   private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
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
                adapter = new DataAdapter(getApplicationContext(), productModels);
                dataRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(new DataAdapter.onRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        final Dialog dialog = getDialog();
                        type.setText(productModels.get(position).getType());
                        promotionTEXT.setText(productModels.get(position).getPromotion());
                        name.setText(productModels.get(position).getName());
                        price.setText(productModels.get(position).getPrice());
                        weight.setText(productModels.get(position).getWeight());
                        detail.setText(productModels.get(position).getDescription());
                        shelf.setText(productModels.get(position).getShelf());
                        row.setText(productModels.get(position).getRow());
                        column.setText(productModels.get(position).getColumn());

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
                                            .into(Img1);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(!dialog.isShowing()){
                            dialog.show();
                        }

                        cancel.setOnClickListener(new View.OnClickListener() {
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
        final Dialog dialog = new Dialog(MainActivity.this);
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view_detail, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        Img1 = dialog.findViewById(R.id.Img1);
        type = dialog.findViewById(R.id.type);
        promotionTEXT = dialog.findViewById(R.id.promotionTEXT);
        name = dialog.findViewById(R.id.name);
        price = dialog.findViewById(R.id.price);
        weight = dialog.findViewById(R.id.weight);
        detail = dialog.findViewById(R.id.detail);
        shelf = dialog.findViewById(R.id.shelf);
        row = dialog.findViewById(R.id.row);
        column = dialog.findViewById(R.id.column);
        find = dialog.findViewById(R.id.find);
        cancel = dialog.findViewById(R.id.cancel);
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guest_bar,menu);

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