package com.shorbgy.muzica.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.adapters.SongsAdapter;
import com.shorbgy.muzica.viewmodels.MusicViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class SongFragment extends Fragment {

    private SongsAdapter adapter;

    private ArrayList<Song> mySongs = new ArrayList<>();

    @BindView(R.id.songs_rv) RecyclerView songsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));
        adapter = new SongsAdapter(requireContext(), mySongs);
        songsRecyclerView.setAdapter(adapter);

        MusicViewModel musicViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        musicViewModel.getAllSongs(requireContext());

        musicViewModel.songMutableLiveData.observe(requireActivity(), songs -> {
            mySongs = songs;
            adapter.setSongs(mySongs);
            adapter.notifyDataSetChanged();
        });
    }
}