package com.github.lulewiczg.controller.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.impl.ServerStopAction;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.ui.component.NonSwipeableViewPager;
import com.github.lulewiczg.controller.ui.component.SectionsPagerAdapter;

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
        Client.registerActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        NonSwipeableViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setActivity(this);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * Adds listener for back button press, display confirm before exit. Disconnects from server after confirm.
     */
    @Override
    public void onBackPressed() {
        displayExitAlert();
    }


    /**
     * Displays tri-button alert message when trying to exit.
     */
    public void displayExitAlert() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.alert_exit_message);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(R.string.alert_exit_message);
            builder.setNegativeButton(R.string.exit_disconnect, (dialog, which) -> {
                client.logout(MainActivity.this);
                Helper.close(client);
                MainActivity.super.onBackPressed();
            });
            builder.setNeutralButton(R.string.exit_shutdown, (dialog, which) -> {
                client.doAction(new ServerStopAction(), MainActivity.this);
                Helper.close(client);
                MainActivity.super.onBackPressed();
            });
            builder.setPositiveButton(R.string.cancel, (dialog, which) -> {
            });
            builder.create().show();

        });
    }
}