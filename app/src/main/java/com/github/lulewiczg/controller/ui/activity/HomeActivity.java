package com.github.lulewiczg.controller.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.ui.component.HomeSectionsPagerAdapter;

/**
 * Home app view.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HomeSectionsPagerAdapter sectionsPagerAdapter = new HomeSectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager_home);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.home_tabs);
        tabs.setupWithViewPager(viewPager);
    }
}
