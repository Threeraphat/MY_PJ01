package com.example.my_pj01;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import static com.example.my_pj01.App.channel;

public class IndexActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    LinearLayout layoutTop,layoutBottom;
    Animation upToDown,downToUp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        notificationManager = NotificationManagerCompat.from(this);

        layoutTop = findViewById(R.id.layoutTop);
        layoutBottom = findViewById(R.id.layoutBottom);
        upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        layoutTop.setAnimation(upToDown);
        layoutBottom.setAnimation(downToUp);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
        } else {
            // Bluetooth is enabled
            addNotification(IndexActivity.this,"Indoor Preference Application","Promotions");
        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNotification(IndexActivity v, String title, String message) {
        Notification notification = new NotificationCompat.Builder(this,channel.getId())
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setGroup(title)
                .setColor(Color.GREEN)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setStyle(new NotificationCompat.InboxStyle()
                    .addLine(message))
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1,notification);
    }
}