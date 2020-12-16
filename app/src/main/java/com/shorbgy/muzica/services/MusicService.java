package com.shorbgy.muzica.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.shorbgy.muzica.MyApp;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.notification.NotificationReceiver;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.activities.MainActivity;
import com.shorbgy.muzica.ui.activities.PlayerActivity;
import com.shorbgy.muzica.ui.fragments.AlbumFragment;
import com.shorbgy.muzica.ui.fragments.SongFragment;

import java.util.ArrayList;

import static com.shorbgy.muzica.MyApp.ACTION_NEXT;
import static com.shorbgy.muzica.MyApp.ACTION_PLAY;
import static com.shorbgy.muzica.MyApp.ACTION_PREVIOUS;
import static com.shorbgy.muzica.MyApp.CHANNEL_2_ID;


public class MusicService extends Service{

    public class MyBinder extends Binder{
        public MusicService getMusicService(){
            return MusicService.this;
        }
    }

    IBinder myBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<Song> songs = new ArrayList<>();
    int pos;

    private MediaSessionCompat mediaSession;

    private ServiceCallbacks serviceCallbacks;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "My Music");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Bind", "onBind: HollaZz");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("UnBind", "onUnbind: Bye");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = "";
        if (intent != null && intent.hasExtra("action")) {
            action = intent.getStringExtra("action");
        }

        int deleteInt = 0;
        if (intent != null) {
            deleteInt = intent.getIntExtra("delete", 0);
        }
        if (deleteInt == 101){
            stopSelf();
            MyApp.close(getApplicationContext());
        }
        //Toast.makeText(this, action, Toast.LENGTH_SHORT).show();

        if (intent!=null) {
            songs = intent.getParcelableArrayListExtra("songs");
            pos = intent.getIntExtra("pos", -1);
        }
        if (pos != -1){
            playMedia(pos);
        }

        if (serviceCallbacks!=null){
            serviceCallbacks.takeAction(action);
        }
        return START_STICKY;
    }

    public void playMedia(int pos) {
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (songs != null){
                createMediaPlayer(pos);
            }
        }
        else {
            createMediaPlayer(pos);
        }
    }

    public void createMediaPlayer(int pos){
        if (songs.size()!=0) {
            Uri uri = Uri.parse(songs.get(pos).getPath());
            mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
            mediaPlayer.start();
        }
    }

    public void start(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void seekTo(int pos){
        mediaPlayer.seekTo(pos);
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }


    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void showNotification(int playPauseBtn){

        Intent intent = new Intent(this, SongFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  intent, 0);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPending = PendingIntent.getBroadcast(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, 0);

        byte[] picture = getSongCover(songs.get(pos).getPath());
        Bitmap thumb;

        if(picture!=null){
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }else {
            thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.place_holder);
        }

        Intent deleteIntent = new Intent(this, MusicService.class);
        deleteIntent.putExtra("delete", 101);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                101,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(songs.get(pos).getTitle())
                .setContentText(songs.get(pos).getArtist())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_skip_previous, "previous", prevPending)
                .addAction(playPauseBtn, "play", playPending)
                .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPending)
                .addAction(R.drawable.ic_close, "Delete", deletePendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        startForeground(2, notification);
    }

    private byte[] getSongCover(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] songCover = retriever.getEmbeddedPicture();
        retriever.release();
        return songCover;
    }

}
