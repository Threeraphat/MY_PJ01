package com.example.my_pj01;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;

import static com.example.my_pj01.App.channel;

public class NotificationService {

    public static NotificationManagerCompat notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addNotification(View v, String title, String message) {
        Notification notification = new NotificationCompat.Builder(v.getContext(),channel.getId())
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .setGroup(title)
                .setColor(Color.GREEN)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message))
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1,notification);
    }
}