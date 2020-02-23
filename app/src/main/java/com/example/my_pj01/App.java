package com.example.my_pj01;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class App extends Application {

    public static final String CHANNEL_ID = "Promotion Notification";
    public static final String CHANNEL_NAME = "Promotion";
    public static final String GROUP_ID = "Promotion_Group";
    public static final String GROUP_NAME = "Promotion";
    public static int importance_Level = NotificationManager.IMPORTANCE_DEFAULT;
    public static NotificationChannelGroup group;
    public static NotificationChannel channel;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void  createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,importance_Level);
            group = new NotificationChannelGroup(GROUP_ID,GROUP_NAME);
            channel.setGroup(group.getId());

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannelGroup(group);
            manager.createNotificationChannel(channel);
        }
    }
}