package com.shorbgy.muzica.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.back_img) ImageView backImageView;
    @BindView(R.id.menu_img) ImageView menuImageView;
    @BindView(R.id.song_cover_img_playing) ImageView songCoverImageView;
    @BindView(R.id.shuffle_img) ImageView shuffleImageView;
    @BindView(R.id.prev_img) ImageView previousSongImageView;
    @BindView(R.id.next_img) ImageView nextSongImageView;
    @BindView(R.id.repeat_img) ImageView repeatImageView;
    @BindView(R.id.song_title_tv) TextView songTitleTextView;
    @BindView(R.id.artist_name_tv) TextView artistTextView;
    @BindView(R.id.current_seek_tv) TextView currentDurationTextView;
    @BindView(R.id.duration_seek_tv) TextView endDurationTextView;
    @BindView(R.id.song_seek_bar) SeekBar songSeekBar;
    @BindView(R.id.play_pause_fab) FloatingActionButton playPauseFab;

    private Song currentSong;
    private static MediaPlayer mediaPlayer;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ArrayList<Song> songs = new ArrayList<>();
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        songs = getIntent().getParcelableArrayListExtra("songs");
        pos = getIntent().getIntExtra("pos", -1);
        currentSong = songs.get(pos);

        setupCoverImage();
        setupSongsInfo();
        setupSeekBar();

        playPauseFab.setOnClickListener(v -> playPause());
        nextSongImageView.setOnClickListener(v -> playNextSong());
        previousSongImageView.setOnClickListener(v -> playPreviousSong());
    }

    private void setupSongsInfo(){
        songTitleTextView.setText(currentSong.getTitle());
        if (currentSong.getArtist().equals("<unknown>")){
            currentSong.setArtist("Unknown");
        }
        artistTextView.setText(currentSong.getArtist());

        Uri uri = Uri.parse(currentSong.getPath());

        long millis = Long.parseLong(currentSong.getDuration());

        String duration = formattedDuration(millis/1000);
        endDurationTextView.setText(duration);
        playPauseFab.setImageResource(R.drawable.ic_pause);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

        songSeekBar.setMax(mediaPlayer.getDuration()/1000);

        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
    }

    private void setupCoverImage(){
        new Thread(() -> {
            byte[] songCover = getSongCover(currentSong.getPath());
            runOnUiThread(() -> Glide.with(PlayerActivity.this).asBitmap()
                    .load(songCover)
                    .placeholder(R.mipmap.place_holder)
                    .into(songCoverImageView));
        }).start();
    }

    private void setupSeekBar(){

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition()/1000;
                    songSeekBar.setProgress(currentPosition);
                    currentDurationTextView.setText(formattedDuration(currentPosition));
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void playPause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseFab.setImageResource(R.drawable.ic_play_arrow);
        }else {
            mediaPlayer.start();
            playPauseFab.setImageResource(R.drawable.ic_pause);
        }
    }

    private void playNextSong(){
        if (pos==songs.size()-1){
            pos = 0;
        }else {
            pos++;
        }
        currentSong = songs.get(pos);
        setupCoverImage();
        setupSongsInfo();
        setupSongsInfo();
    }

    private void playPreviousSong(){
        if (pos==0){
            pos = songs.size()-1;
        }else {
            pos--;
        }
        currentSong = songs.get(pos);
        setupCoverImage();
        setupSongsInfo();
        setupSongsInfo();
    }

    private byte[] getSongCover(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] songCover = retriever.getEmbeddedPicture();
        retriever.release();
        return songCover;
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
