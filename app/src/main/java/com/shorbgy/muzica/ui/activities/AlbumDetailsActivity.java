package com.shorbgy.muzica.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.adapters.SongsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class AlbumDetailsActivity extends AppCompatActivity {


    @BindView(R.id.songs_det_rv) RecyclerView songsRecyclerView;
    @BindView(R.id.song_cover_img_det) ImageView albumCoverImageView;

    ArrayList<Song> albumSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        ButterKnife.bind(this);

        Song currentSong = getIntent().getParcelableExtra("current");
        ArrayList<Song> songs = getIntent().getParcelableArrayListExtra("songs");

        for (int i=0; i<songs.size()-1; i++){
            if (songs.get(i).getAlbum().equals(currentSong.getAlbum())){
                albumSongs.add(songs.get(i));
            }
        }

        Glide.with(this)
                .load(getAlbumbCover(currentSong.getPath()))
                .placeholder(R.mipmap.place_holder)
                .into(albumCoverImageView);

        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SongsAdapter adapter = new SongsAdapter(this, albumSongs);
        songsRecyclerView.setAdapter(adapter);
    }

    private Bitmap getAlbumbCover(String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] songCover = retriever.getEmbeddedPicture();
        retriever.release();
        if(songCover != null){
            return BitmapFactory.decodeByteArray(songCover, 0, songCover.length);
        }else {
            return null;
        }
    }

}