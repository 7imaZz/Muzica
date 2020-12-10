package com.shorbgy.muzica.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.activities.MainActivity;
import com.shorbgy.muzica.ui.adapters.AlbumAdapter;
import com.shorbgy.muzica.viewmodels.MusicViewModel;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class AlbumFragment extends Fragment {

    private AlbumAdapter adapter;

    @BindView(R.id.album_rv)
    RecyclerView albumRecyclerView;

    ArrayList<String> existAlbums = new ArrayList<>();
    ArrayList<Song> albums = new ArrayList<>();

    String sortOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sortOrder = ((MainActivity) requireActivity()).getSortOrder();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        albumRecyclerView.setHasFixedSize(true);
        adapter = new AlbumAdapter(albums, requireContext());
        albumRecyclerView.setAdapter(adapter);

        MusicViewModel musicViewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        musicViewModel.getAllSongs(requireContext(), sortOrder);


        musicViewModel.songMutableLiveData.observe(requireActivity(), songs -> {


            for(int i=0; i<songs.size()-1; i++){
                if (!existAlbums.contains(songs.get(i).getAlbum())){
                    existAlbums.add(songs.get(i).getAlbum());
                    albums.add(songs.get(i));
                }
            }

            adapter.setAllSongs(songs);
            adapter.setSongs(albums);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity)
                Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }
}