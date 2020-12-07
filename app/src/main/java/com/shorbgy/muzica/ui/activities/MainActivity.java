package com.shorbgy.muzica.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.shorbgy.muzica.R;
import com.shorbgy.muzica.ui.adapters.MyFragmentStateAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mainViewPager2;

    private MyFragmentStateAdapter myFragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        mainViewPager2 = findViewById(R.id.view_pager);

        myFragmentStateAdapter = new MyFragmentStateAdapter(this);
        mainViewPager2.setAdapter(myFragmentStateAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, mainViewPager2,
                (tab, position) -> {
                    tab.setText(myFragmentStateAdapter.getPageTitle(position));
                    mainViewPager2.setCurrentItem(0);
                });

        tabLayoutMediator.attach();
    }
}