package com.shorbgy.muzica.viewmodels;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shorbgy.muzica.pojo.Song;

import java.util.ArrayList;

public class MusicViewModel extends ViewModel{

    private static final String TAG = "MusicViewModel";

    public static final String DATA = "_data";
    public static final String DURATION = "duration";

    public MutableLiveData<ArrayList<Song>> songMutableLiveData = new MutableLiveData<>();

    ArrayList<Song> songs = new ArrayList<>();

    public void getAllSongs(Context context, String sortOrder){
        new Thread(() -> {
            songs = getSongsFromStorage(context, sortOrder);
            songMutableLiveData.postValue(songs);
        }).start();
    }

    private ArrayList<Song> getSongsFromStorage(Context context, String sortOrder){

        ArrayList<Song> songs = new ArrayList<>();

        String sort = null;

        if (sortOrder!=null) {
            switch (sortOrder) {
                case "byName":
                    sort = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                    break;
                case "bySize":
                    sort = MediaStore.MediaColumns.SIZE + " DESC";
                    break;
                case "byDate":
                    sort = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                    break;
            }
        }

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            projection = new String[]{
                    DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media._ID
            };
        }else {
            projection = new String[]{
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media._ID

            };
        }

        Cursor cursor = context.getContentResolver().query(mediaUri, projection,
                null, null, sort);

        if (cursor!=null){
            while (cursor.moveToNext()){
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String duration = cursor.getString(3);
                String album = cursor.getString(4);
                String id = cursor.getString(5);

                Log.d(TAG, "getAllSongs: "+title+", Thread: "+Thread.currentThread().getName());
                songs.add(new Song(id, path, title, artist, album, duration));
            }
            cursor.close();
        }
        return songs;
    }

}
