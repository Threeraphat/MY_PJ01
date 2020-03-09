package com.example.my_pj01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_pj01.Models.BTLE_Device;
import com.example.my_pj01.Models.ProductModel;
import com.example.my_pj01.Models.PromotionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManageProductActivity extends AppCompatActivity {
    final static int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference;
    Uri path;
    ImageView imageView;
    Bitmap bitmap;
    RelativeLayout relativeLayout;
    TextView clickHere;
    String nopromo = "no promotion";
    String name, price, type, description, row, column, shelf, weight, promo;
    int id;
    EditText edtname, edtprice, edttype, edtweight, edtdescription, edtrow, edtcolumn, edtshelf, edtpromo;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
    DatabaseReference run_no = FirebaseDatabase.getInstance().getReference("running");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);
        Utils.FullScreen(this);

        relativeLayout = findViewById(R.id.Relative);
        clickHere = findViewById(R.id.clickHere);
        imageView = findViewById(R.id.in_Img1);
        edtname = findViewById(R.id.in_name);
        edtprice = findViewById(R.id.in_price);
        edttype = findViewById(R.id.in_type);
        edtdescription = findViewById(R.id.in_detail);
        edtweight = findViewById(R.id.in_weight);
        edtrow = findViewById(R.id.in_row);
        edtcolumn = findViewById(R.id.in_column);
        edtshelf = findViewById(R.id.in_shelf);
        edtpromo = findViewById(R.id.in_promo);

        storageReference = FirebaseStorage.getInstance().getReference("Product_Images/" + UUID.randomUUID().toString());
        run_no.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.getValue(Integer.class);
                } else {
                    id = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ManageProductActivity.this.isFinishing()) {
                    startActivity(new Intent(getApplicationContext(), TypeAdminActivity.class));
                    finish();
                }
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edtname.getText().toString();
                price = edtprice.getText().toString();
                type = edttype.getText().toString().toLowerCase();
                description = edtdescription.getText().toString();
                weight = edtweight.getText().toString();
                row = edtrow.getText().toString();
                column = edtcolumn.getText().toString();
                shelf = edtshelf.getText().toString();
                promo = edtpromo.getText().toString();

                if (imageView.getDrawable() == null) {
                    Toast.makeText(ManageProductActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                } if(promo.equals("")) {
                    promo = nopromo;
                } if (name.equals("") || price.equals("") || type.equals("") || description.equals("") || weight.equals("") || row.equals("") || column.equals("") || shelf.equals("")) {
                    Toast.makeText(ManageProductActivity.this, "Please enter data all field", Toast.LENGTH_SHORT).show();
                } else {
                    if (path != null) {
                        final ProgressDialog dialog = new ProgressDialog(ManageProductActivity.this);
                        dialog.setTitle("Uploading...");
                        dialog.show();
                        storageReference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                DatabaseReference child = ref.child("Product" + String.format("%010d", id));
                                child.child("id").setValue(id);
                                child.child("name").setValue(name);
                                child.child("price").setValue(price);
                                child.child("type").setValue(type);
                                child.child("description").setValue(description);
                                child.child("weight").setValue(weight);
                                child.child("row").setValue(row);
                                child.child("column").setValue(column);
                                child.child("picture").setValue(storageReference.getPath());
                                child.child("shelf").setValue(shelf);
                                child.child("promotion").setValue(promo);
                                run_no.setValue(id + 1);
                                Toast.makeText(ManageProductActivity.this, "Upload Success.", Toast.LENGTH_SHORT).show();

                                if (!ManageProductActivity.this.isFinishing()) {
                                    startActivity(new Intent(getApplicationContext(), TypeAdminActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(ManageProductActivity.this, "Upload Fail!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.setMessage("Uploading "
                                        + (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) + "%");
                            }
                        });
                    }
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
            }
        });
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
                imageView.setImageBitmap(bitmap);
                clickHere.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}