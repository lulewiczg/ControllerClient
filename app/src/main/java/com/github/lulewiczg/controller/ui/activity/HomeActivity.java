package com.github.lulewiczg.controller.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Response;
import com.github.lulewiczg.controller.common.Status;
import com.github.lulewiczg.controller.ui.listener.ValueChangeListener;

import java.io.IOException;

/**
 * Starting activity, allows to login to server.
 */
public class HomeActivity extends AppCompatActivity {

    private WifiManager wm;
    private int timeout;
    private int serverTimeout;

    /**
     * Returns phone address, no other method available for IPv4 formatting.
     *
     * @return IP address
     */
    @SuppressWarnings("deprecation")
    private String getIP() {
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    /**
     * Handles login action.
     *
     * @param v view
     */
    public void click(View v) {
        final Intent intent = new Intent(getBaseContext(), MainActivity.class);
        EditText addressInput = findViewById(R.id.address_input_address);
        final String address = addressInput.getText().toString();
        if (address.equals("")) {
            Toast.makeText(getBaseContext(), R.string.connect_address_empty, Toast.LENGTH_LONG).show();
            return;
        }

        EditText portInput = findViewById(R.id.address_input_port);
        String portStr = portInput.getText().toString();
        if (portStr.equals("")) {
            Toast.makeText(getBaseContext(), R.string.connect_port_empty, Toast.LENGTH_LONG).show();
            return;
        }
        final int port = Integer.parseInt(portStr);

        EditText passwordInput = findViewById(R.id.password_input);
        final String password = passwordInput.getText().toString();

        final ProgressDialog progress = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.connect_loading_title), getResources().getString(R.string.connect_loading));
        Thread t = new Thread() {

            @Override
            public void run() {
                Client client;
                try {
                    client = Client.create(address, port, timeout, serverTimeout);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(Helper.displayToast(getBaseContext(), R.string.connect_error));
                    return;
                } finally {
                    runOnUiThread(Helper.closeProgress(progress));
                }
                Response response;
                response = client.login(password, Build.MANUFACTURER + ", " + Build.MODEL, getIP(), HomeActivity.this);
                if (response.getStatus() != Status.OK) {
                    runOnUiThread(Helper.displayToast(getBaseContext(), R.string.connect_invalid_password));
                } else {
                    startActivity(intent);
                }
            }
        };
        t.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs();
    }

    /**
     * Sets up input fields on start.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        setContentView(R.layout.activity_home);
        loadPrefs();
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        EditText address = findViewById(R.id.address_input_address);
        address.addTextChangedListener(new ValueChangeListener(Consts.ADDRESS, edit));
        address.setText(prefs.getString(Consts.ADDRESS, "192.168.1.1"));

        EditText port = findViewById(R.id.address_input_port);
        port.setText(prefs.getString(Consts.PORT, "5555"));
        port.addTextChangedListener(new ValueChangeListener(Consts.PORT, edit));

        EditText password = findViewById(R.id.password_input);
        password.setText(prefs.getString(Consts.PASSWORD, "0"));
        password.addTextChangedListener(new ValueChangeListener(Consts.PASSWORD, edit));
    }

    /**
     * Loads connection settings.
     */
    private void loadPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        timeout = Integer.parseInt(settings.getString(Consts.CONNECT_TIMEOUT, "2000"));
        serverTimeout = Integer.parseInt(settings.getString(Consts.SERVER_TIMEOUT, "10000"));
    }
}
