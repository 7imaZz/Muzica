package com.shorbgy.muzica.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.shorbgy.muzica.ui.activities.MainActivity;
import com.shorbgy.muzica.ui.activities.PlayerActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder>{

    @SuppressLint("NonConstantResourceId")
    public static class SongsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.song_cover_img) ImageView songCoverImageView;
        @BindView(R.id.title_tv) TextView titleTextView;
        @BindView(R.id.artist_tv) TextView artistTextView;
        @BindView(R.id.duration_tv) TextView durationTextView;
        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ArrayList<Song> songs;
    private final Context context;

    public SongsAdapter(Context context, ArrayList<Song> songs) {
        this.songs = songs;
        this.context = context;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        holder.titleTextView.setText(songs.get(position).getTitle());
        if (songs.get(position).getArtist().equals("<unknown>")){
            songs.get(position).setArtist("Unknown");
        }
        holder.artistTextView.setText(songs.get(position).getArtist());
        holder.durationTextView.setText(formattedDuration(
                Integer.parseInt(songs.get(position).getDuration())/1000));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("songs", songs);
            intent.putExtra("pos", position);
            context.startActivity(intent);
        });

        new Thread(() -> {

            Bitmap songCover = getSongCover(songs.get(position).getPath());

            if (songCover != null){
                ((Activity)context).runOnUiThread(() ->
                        Glide.with(context)
                                .load(songCover)
                                .placeholder(R.mipmap.place_holder)
                                .into(holder.songCoverImageView));
            }else {
                ((Activity)context).runOnUiThread(() ->
                        Glide.with(context)
                                .load(R.mipmap.place_holder)
                                .into(holder.songCoverImageView));
            }

        }).start();
    }

    @Override
    public int getItemCount() {
        return songs.size();
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

    private String formattedDuration(long millis){
        String totalOut;
        String totalNew;

        String seconds = String.valueOf(millis%60);
        String minutes = String.valueOf(millis/60);

        totalOut = minutes+":"+seconds;
        totalNew = minutes+":"+"0"+seconds;

        if (seconds.length()==1){
            return totalNew;
        }else {
            return totalOut;
        }
    }
}
