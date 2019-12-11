package com.example.my_pj01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginAdmin extends AppCompatActivity {
    EditText username,password;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin_Account");
    String user = "none",pass = "none";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("username").getValue(String.class);
                pass = dataSnapshot.child("password").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.equals("none") && !pass.equals("none")) {
                    if (username.getText().toString().equals(user) && password.getText().toString().equals(pass)) {
                        startActivity(new Intent(LoginAdmin.this, MainAdmin.class));
                        finish();
                        Login.login.finish();
                    } else {
                        Toast.makeText(LoginAdmin.this, "กรุณากรอกบัญชีผู้ใช้งานและรหัสผ่านอีกครั้ง", Toast.LENGTH_SHORT).show();
                        username.setText("");
                        password.setText("");
                    }
                }else{
                    Toast.makeText(LoginAdmin.this, "กรุญาตรวจสอบการเชื่อมต่อระบบอินเทอร์เน็ตของคุณ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
