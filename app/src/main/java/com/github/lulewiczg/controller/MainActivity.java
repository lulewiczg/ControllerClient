package com.github.lulewiczg.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.ui.main.SectionsPagerAdapter;

/**
 * Main activity for app.
 */
public class MainActivity extends AppCompatActivity {

    private Client client;

    /**
     * Builds UI for controlling fragments.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = Client.get();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * Adds listener for back button press, display confirm before exit. Disconnects from server after confirm.
     */
    @Override
    public void onBackPressed() {
        Helper.displayAlert(this, R.string.alert_exit_title, R.string.alert_exit_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.logout();
                Client.destroy();
                MainActivity.super.onBackPressed();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }
}