package com.shorbgy.muzica.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.ui.adapters.MyFragmentStateAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 101;

    private ViewPager2 mainViewPager2;
    private TabLayout tabLayout;


    private MyFragmentStateAdapter myFragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        mainViewPager2 = findViewById(R.id.view_pager);



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


}