package com.shorbgy.muzica;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

public class MyApp extends Application{

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String ACTION_PREVIOUS = "action_prev";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PLAY = "action_play";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,
                    "Channel (1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc...");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID,
                    "Channel (2)", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 Desc...");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    public static void close(Context context){
        Toast.makeText(context, "Kill", Toast.LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
