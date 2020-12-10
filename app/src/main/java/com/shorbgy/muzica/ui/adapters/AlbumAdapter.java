package com.shorbgy.muzica.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.activities.AlbumDetailsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    @SuppressLint("NonConstantResourceId")
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_img) ImageView albumCoverImageView;
        @BindView(R.id.album_title_tv) TextView albumTitle;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ArrayList<Song> albums;
    private final Context context;
    private ArrayList<Song> allSongs = new ArrayList<>();

    public AlbumAdapter(ArrayList<Song> songs, Context context) {
        this.albums = songs;
        this.context = context;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.albums = songs;
    }

    public void setAllSongs(ArrayList<Song> allSongs) {
        this.allSongs = allSongs;
    }

    @NonNull
    @Override
    public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, int position) {



        holder.albumTitle.setText(albums.get(position).getAlbum());

        Glide.with(context)
                .load(getSongCover(albums.get(position).getPath()))
                .placeholder(R.mipmap.place_holder)
                .into(holder.albumCoverImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumDetailsActivity.class);
            intent.putExtra("songs", allSongs);
            intent.putExtra("current", albums.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    private Bitmap getSongCover(String uri){

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
