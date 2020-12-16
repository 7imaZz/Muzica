package com.shorbgy.muzica.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.activities.MainActivity;
import com.shorbgy.muzica.ui.activities.PlayerActivity;
import com.shorbgy.muzica.ui.adapters.SongsAdapter;
import com.shorbgy.muzica.ui.adapters.onItemClickListener;
import com.shorbgy.muzica.ui.utils.SwipeHelper;
import com.shorbgy.muzica.viewmodels.MusicViewModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class SongFragment extends Fragment implements onItemClickListener {

    public static SongsAdapter adapter;

    public static ArrayList<Song> mySongs = new ArrayList<>();

    String sortOrder;

    @BindView(R.id.songs_rv) RecyclerView songsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sortOrder = ((MainActivity) requireActivity()).getSortOrder();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));
        adapter = new SongsAdapter(new WeakReference<>(requireContext()), mySongs, this);
        songsRecyclerView.setAdapter(adapter);

        MusicViewModel musicViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        musicViewModel.getAllSongs(requireContext(), sortOrder);


        musicViewModel.songMutableLiveData.observe(requireActivity(), songs -> {
            mySongs = songs;
            adapter.setSongs(mySongs);
            adapter.notifyDataSetChanged();
        });

        SwipeHelper swipeHelper = new SwipeHelper(requireContext(), songsRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Delete",
                        Color.parseColor("#FF3C30"),
                        pos -> deleteSong(pos)
                ));
            }
        };

    }

    private void deleteSong(int pos){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mySongs.get(pos).getId()));

        File file = new File(mySongs.get(pos).getPath());
        boolean isDeleted = file.delete();

        if (isDeleted){
            requireContext().getContentResolver().delete(contentUri, null, null);
            mySongs.remove(pos);
            adapter.notifyItemRemoved(pos);
            adapter.notifyItemRangeChanged(pos, mySongs.size());
            Snackbar.make(requireView(), "Song Deleted", Snackbar.LENGTH_LONG);
        }else {
            Snackbar.make(requireView(), "Song Can't Be Deleted", Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public void setOnItemClickListener(int pos) {
        Intent intent = new Intent(requireContext(), PlayerActivity.class);
        intent.putExtra("songs", mySongs);
        intent.putExtra("pos", pos);
        requireContext().startActivity(intent);
    }
}