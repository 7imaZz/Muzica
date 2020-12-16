package com.shorbgy.muzica.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shorbgy.muzica.services.MusicService;
import com.shorbgy.muzica.ui.activities.PlayerActivity;

import static com.shorbgy.muzica.MyApp.ACTION_NEXT;
import static com.shorbgy.muzica.MyApp.ACTION_PLAY;
import static com.shorbgy.muzica.MyApp.ACTION_PREVIOUS;

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);


        switch (action){
            case ACTION_PLAY:
                serviceIntent.putExtra("action", ACTION_PLAY);
                context.startService(serviceIntent);
                break;
            case ACTION_NEXT:
                serviceIntent.putExtra("action", ACTION_NEXT);
                context.startService(serviceIntent);
                break;
            case ACTION_PREVIOUS:
                serviceIntent.putExtra("action", ACTION_PREVIOUS);
                context.startService(serviceIntent);
                break;
        }
    }
}
