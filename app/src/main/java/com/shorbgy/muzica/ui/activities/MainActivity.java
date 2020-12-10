package com.shorbgy.muzica.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.pojo.Song;
import com.shorbgy.muzica.ui.adapters.MyFragmentStateAdapter;
import com.shorbgy.muzica.ui.fragments.SongFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    public static final String SORT_PREFS = "sortPreference";

    public static final String SORT = "sort";
    public static final String BY_NAME = "byName";
    public static final String BY_SIZE = "bySize";
    public static final String BY_DATE = "byDate";

    public static final int REQUEST_CODE = 101;

    private ViewPager2 mainViewPager2;
    private TabLayout tabLayout;

    private String sortOrder;

    private MyFragmentStateAdapter myFragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        mainViewPager2 = findViewById(R.id.view_pager);


        SharedPreferences sharedPreferences = getSharedPreferences(SORT_PREFS, Context.MODE_PRIVATE);
        sortOrder = sharedPreferences.getString(SORT, BY_NAME);

        askForPermission();

    }

    public void initializingTabLayoutWithViewPager2(){
        myFragmentStateAdapter = new MyFragmentStateAdapter(this);
        mainViewPager2.setAdapter(myFragmentStateAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, mainViewPager2,
                (tab, position) -> {
                    tab.setText(myFragmentStateAdapter.getPageTitle(position));
                    mainViewPager2.setCurrentItem(0);
                });

        tabLayoutMediator.attach();
    }

    public void askForPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }else {
            initializingTabLayoutWithViewPager2();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initializingTabLayoutWithViewPager2();
            }else {
                askForPermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor = getSharedPreferences(SORT_PREFS, Context.MODE_PRIVATE).edit();

        switch (item.getItemId()){
            case R.id.by_name_menu:
                editor.putString(SORT, BY_NAME);
                editor.apply();
                SongFragment.mySongs.clear();
                this.recreate();
                break;
            case R.id.by_date_menu:
                editor.putString(SORT, BY_DATE);
                editor.apply();
                SongFragment.mySongs.clear();
                this.recreate();
                break;
            case R.id.by_size_menu:
                editor.putString(SORT, BY_SIZE);
                editor.apply();
                SongFragment.mySongs.clear();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<Song> filteredSongs = new ArrayList<>();
        for (Song song: SongFragment.mySongs){
            if (song.getTitle().toLowerCase().contains(newText.toLowerCase())){
                filteredSongs.add(song);
            }
        }
        SongFragment.adapter.setSongs(filteredSongs);
        SongFragment.adapter.notifyDataSetChanged();
        return false;
    }

    public String getSortOrder() {
        return sortOrder;
    }
}