package com.example.my_pj01;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_pj01.Models.ProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.my_pj01.ManageProductActivity.PICK_IMAGE_REQUEST;

public class MainAdmin extends AppCompatActivity {

    RecyclerView dataRecyclerView;
    private Uri path;
    private Bitmap bitmap;
    private DataAdapter adapter;
    private TextView clickHere;
    private static final String TAG = "_project_";
    List<ProductModel> productModels = new ArrayList<>();
    ProductModel productModel;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
    private ImageView m_img, add_p;
    private EditText m_type, m_name, m_price, m_weight, m_detail, m_shelf, m_row, m_column;
    private Button m_update, m_remove, m_back;
    private StorageReference storageReference;
    private File file;
    String getType = "", m_promo;
    Spinner aSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        System.out.println("BAS MainAdmin");
        Utils.FullScreen(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        getType = intent.getStringExtra("type");
        //System.out.println("getType from TypeActivity---> " + getType);
        dataRecyclerView = findViewById(R.id.re_list_admin);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("products");
        readDataFromFirebase(myRef);
    }

    private void readDataFromFirebase(final DatabaseReference myRef) {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    productModel = new ProductModel();
                    productModel.setKey(ds.getKey());
                    productModel.setColumn(ds.child("column").getValue(String.class));
                    productModel.setDescription(ds.child("description").getValue(String.class));
                    productModel.setId(ds.child("id").getValue(Integer.class));
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
                        m_type.setText(productModels.get(position).getType());
                        m_name.setText(productModels.get(position).getName());
                        m_price.setText(productModels.get(position).getPrice());
                        m_weight.setText(productModels.get(position).getWeight());
                        m_detail.setText(productModels.get(position).getDescription());
                        m_shelf.setText(productModels.get(position).getShelf());
                        m_row.setText(productModels.get(position).getRow());
                        m_column.setText(productModels.get(position).getColumn());

                        clickHere.setVisibility(View.GONE);

                        storageReference = FirebaseStorage.getInstance().getReference(productModels.get(position).getPicture());
                        try {
                            file = File.createTempFile("images", "jpeg");
                            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

                        if (!dialog.isShowing()) {
                            dialog.show();
                        }

                        m_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = new ProgressDialog(MainAdmin.this);
                                progressDialog.setTitle("Uploading...");
                                progressDialog.show();
                                if (path != null) {
                                    uploadImageToFirebase(progressDialog, position, dialog);
                                } else {
                                    productModel.setKey(productModels.get(position).getKey());
                                    productModel.setColumn(m_column.getText().toString());
                                    productModel.setDescription(m_detail.getText().toString());
                                    productModel.setId(productModels.get(position).getId());
                                    productModel.setName(m_name.getText().toString());
                                    productModel.setPicture(productModels.get(position).getPicture());
                                    productModel.setPrice(m_price.getText().toString());
                                    productModel.setRow(m_row.getText().toString());
                                    productModel.setShelf(m_shelf.getText().toString());
                                    productModel.setType(m_type.getText().toString());
                                    productModel.setWeight(m_weight.getText().toString());
                                    productModel.setPromotion(aSpinner.getSelectedItem().toString());
//                                    productModels.set(position,productModel);
                                    ref.child(productModels.get(position).getKey()).setValue(productModel);
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    progressDialog.dismiss();
                                    if (!MainAdmin.this.isFinishing()) {
                                        startActivity(new Intent(getApplicationContext(), MainAdmin.class));
                                        finish();
                                    }
                                }
                            }
                        });

                        dialog.findViewById(R.id.dialog_m_relative).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
                            }
                        });

                        dialog.findViewById(R.id.dialog_m_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ref = FirebaseDatabase.getInstance().getReference("products");
                                ref.child(productModels.get(position).getKey()).removeValue();
                                dialog.dismiss();
                                if (!MainAdmin.this.isFinishing()) {
                                    startActivity(new Intent(getApplicationContext(), TypeAdminActivity.class));
                                    finish();
                                }
                            }
                        });

                        m_back.setOnClickListener(new View.OnClickListener() {
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

    private void uploadImageToFirebase(final ProgressDialog progressDialog, final int position, final Dialog dialog) {
        storageReference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                productModel.setKey(productModels.get(position).getKey());
                productModel.setColumn(m_column.getText().toString());
                productModel.setDescription(m_detail.getText().toString());
                productModel.setId(productModels.get(position).getId());
                productModel.setName(m_name.getText().toString());
                productModel.setPicture(storageReference.getPath());
                productModel.setPrice(m_price.getText().toString());
                productModel.setRow(m_row.getText().toString());
                productModel.setShelf(m_shelf.getText().toString());
                productModel.setType(m_type.getText().toString());
                productModel.setWeight(m_weight.getText().toString());
                productModel.setPromotion(aSpinner.getSelectedItem().toString());
                ref.child(productModels.get(position).getKey()).setValue(productModel);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                progressDialog.dismiss();

                if (!MainAdmin.this.isFinishing()) {
                    startActivity(new Intent(getApplicationContext(), MainAdmin.class));
                    finish();
                }
                Toast.makeText(MainAdmin.this, "Update Success.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(MainAdmin.this, "Update Fail!", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setMessage("Updating "
                        + (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) + "%");
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
        aSpinner = dialog.findViewById(R.id.dialog_m_promo);
        m_img = dialog.findViewById(R.id.dialog_m_img);
        m_type = dialog.findViewById(R.id.dialog_m_type);
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
        clickHere = dialog.findViewById(R.id.clickHere);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {
            path = data.getData();
            try {
                bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), path);
                m_img.setImageBitmap(bitmap);
                clickHere.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_bar, menu);

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

        MenuItem addProduct = menu.findItem(R.id.action_add);
        addProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(!MainAdmin.this.isFinishing()) {
                    startActivity(new Intent(getApplicationContext(), ManageProductActivity.class));
                    finish();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}