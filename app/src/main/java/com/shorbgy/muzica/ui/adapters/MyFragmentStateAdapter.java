package com.shorbgy.muzica.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.shorbgy.muzica.ui.fragments.AlbumFragment;
import com.shorbgy.muzica.ui.fragments.SongFragment;

public class MyFragmentStateAdapter extends FragmentStateAdapter{

    public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new SongFragment();
        }
        return new AlbumFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public String getPageTitle(int pos){
        if (pos == 1){
            return "Songs";
        }
        return "Albums";
    }
}
