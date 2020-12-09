package com.shorbgy.muzica.ui.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class PlayerActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";


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

    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;

    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);

        editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        SharedPreferences preferences = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        isRepeatOn = preferences.getBoolean(REPEAT, false);
        isShuffleOn = preferences.getBoolean(SHUFFLE, false);

        checkShuffle();
        checkRepeat();

        songs = getIntent().getParcelableArrayListExtra("songs");
        pos = getIntent().getIntExtra("pos", -1);
        currentSong = songs.get(pos);

        setupCoverImage();
        setupSongsInfo();
        setupSeekBar();

        playPauseFab.setOnClickListener(v -> playPause());
        nextSongImageView.setOnClickListener(v -> playNextSong());
        previousSongImageView.setOnClickListener(v -> playPreviousSong());
        shuffleImageView.setOnClickListener(v -> {
            isShuffleOn = !isShuffleOn;
            editor.putBoolean(SHUFFLE, isShuffleOn);
            checkShuffle();
            editor.apply();
        });
        repeatImageView.setOnClickListener(v -> {
            isRepeatOn = !isRepeatOn;
            editor.putBoolean(REPEAT, isRepeatOn);
            checkRepeat();
            editor.apply();
        });
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

            Bitmap bitmap;

            if (songCover != null){
                bitmap = BitmapFactory.decodeByteArray(songCover, 0, songCover.length);
            }else {
                bitmap = null;
            }

            runOnUiThread(() -> {
                if (bitmap != null){

                    animateImage(songCoverImageView, bitmap);

                    Palette.from(bitmap).generate(palette -> {
                        assert palette != null;
                        Palette.Swatch swatch = palette.getDominantSwatch();
                        ImageView gradient = findViewById(R.id.gradient_img);
                        RelativeLayout containerLayout = findViewById(R.id.player_container);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        containerLayout.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable;
                        if(swatch != null){

                            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{swatch.getRgb(), 0x00000000});
                            gradient.setBackground(gradientDrawable);

                            GradientDrawable gradientDrawableBg =
                                    new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{swatch.getRgb(), swatch.getRgb()});
                            containerLayout.setBackground(gradientDrawableBg);

                        }else {

                            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                    new int[]{0xff000000, 0x00000000});
                            gradient.setBackground(gradientDrawable);

                            GradientDrawable gradientDrawableBg =
                                    new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                            new int[]{0xff000000, 0xff000000});
                            containerLayout.setBackground(gradientDrawableBg);

                        }
                        songTitleTextView.setTextColor(Color.WHITE);
                        artistTextView.setTextColor(Color.WHITE);
                    });
                }else {
                    animateImage(songCoverImageView, null);
                    ImageView gradient = findViewById(R.id.gradient_img);
                    RelativeLayout containerLayout = findViewById(R.id.player_container);
                    gradient.setBackgroundResource(R.drawable.gradient_bg);
                    containerLayout.setBackgroundResource(R.drawable.main_bg);
                    songTitleTextView.setTextColor(Color.WHITE);
                    artistTextView.setTextColor(Color.WHITE);
                }
            });

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
        if (!isRepeatOn) {
            if (!isShuffleOn) {
                if (pos == songs.size() - 1) {
                    pos = 0;
                } else {
                    pos++;
                }
            }else {
                pos = new Random().nextInt(songs.size()-1);
            }
        }
        currentSong = songs.get(pos);
        setupCoverImage();
        setupSongsInfo();
        setupSongsInfo();
    }

    private void playPreviousSong(){
        if (!isRepeatOn) {
            if (!isShuffleOn) {
                if (pos == 0) {
                    pos = songs.size()-1;
                } else {
                    pos--;
                }
            }else {
                pos = new Random().nextInt(songs.size()-1);
            }
        }
        currentSong = songs.get(pos);
        setupCoverImage();
        setupSongsInfo();
        setupSongsInfo();
    }

    private void animateImage(ImageView imageView, Bitmap bitmap){

        Animation animOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (bitmap != null) {
                    Glide.with(PlayerActivity.this)
                            .load(bitmap)
                            .into(imageView);
                }else {
                    Glide.with(PlayerActivity.this)
                            .load(R.mipmap.place_holder)
                            .into(imageView);
                }

                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animOut);
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

    private void checkShuffle(){
        if (isShuffleOn) {
            shuffleImageView.setImageResource(R.drawable.ic_shuffle);
        }else {
            shuffleImageView.setImageResource(R.drawable.ic_shuffle_off);
        }
    }

    private void checkRepeat(){
        if (isRepeatOn){
            repeatImageView.setImageResource(R.drawable.ic_repeat);
        }else {
            repeatImageView.setImageResource(R.drawable.ic_repeat_off);
        }
    }
}
