package com.shorbgy.muzica.ui.adapters;

import android.content.Context;
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

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder>{

    public static class SongsViewHolder extends RecyclerView.ViewHolder{
        ImageView songCoverImageView;
        TextView titleTextView, artistTextView, durationTextView;
        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            songCoverImageView = itemView.findViewById(R.id.song_cover_img);
            titleTextView = itemView.findViewById(R.id.title_tv);
            artistTextView = itemView.findViewById(R.id.artist_tv);
            durationTextView = itemView.findViewById(R.id.duration_tv);
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
        holder.artistTextView.setText(songs.get(position).getArtist());
        holder.durationTextView.setText(songs.get(position).getDuration());

        new Thread(() -> {

            byte[] songCover = getSongCover(songs.get(position).getPath());

            ((MainActivity)context).runOnUiThread(() -> Glide.with(context).asBitmap()
                    .load(songCover)
                    .placeholder(R.drawable.place_holder)
                    .into(holder.songCoverImageView));
        }).start();

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private byte[] getSongCover(String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] songCover = retriever.getEmbeddedPicture();
        retriever.release();
        return songCover;
    }
}
