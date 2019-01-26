package com.github.lulewiczg.controller.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.lulewiczg.controller.MainActivity;
import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.common.CommunicationHelper;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Status;
import com.github.lulewiczg.controller.exception.ConnectException;
import com.github.lulewiczg.controller.ui.listener.ValueChangeListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private WifiManager wm;


    private String getIP() {
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    public void click(View v) throws InterruptedException {
        final Intent intent = new Intent(getBaseContext(), MainActivity.class);
        AsyncTask<Void, Integer, Socket> connectTask;

        EditText addressInput = (EditText) findViewById(R.id.address_input_address);
        final String address = addressInput.getText().toString();
        if (address.equals("")) {
            Toast.makeText(getBaseContext(), R.string.connect_address_empty, Toast.LENGTH_LONG).show();
            return;
        }

        EditText portInput = (EditText) findViewById(R.id.address_input_port);
        String portStr = portInput.getText().toString();
        if (portStr.equals("")) {
            Toast.makeText(getBaseContext(), R.string.connect_port_empty, Toast.LENGTH_LONG).show();
            return;
        }
        final int port = Integer.parseInt(portStr);

        EditText passwordInput = (EditText) findViewById(R.id.password_input);
        final String password = passwordInput.getText().toString();

        final ProgressDialog progress = ProgressDialog.show(HomeActivity.this, getResources().getString(R.string.connect_loading_title), getResources().getString(R.string.connect_loading));
        Thread t = new Thread() {

            @Override
            public void run() {
                Socket socket;
                try {
                    socket = getSocket(address, port);
                    socket.setSoTimeout(Consts.TIMEOUT);
                    if (socket == null || !socket.isConnected()) {
                        throw new TimeoutException();
                    }
                } catch (TimeoutException | ExecutionException | InterruptedException | SocketException e) {
                    e.printStackTrace();
                    runOnUiThread(Helper.displayToast(getBaseContext(), R.string.connect_error));
                    return;
                } finally {
                    runOnUiThread(Helper.closeProgress(progress));
                }
                CommunicationHelper.setIp(getIP());
                CommunicationHelper.setGlobalSocket(socket);
                CommunicationHelper.setPassword(password);
                CommunicationHelper helper = null;
                try {
                    helper = CommunicationHelper.getInstance();
                } catch (ConnectException e) {
                    if (e.getResponse().getStatus() == Status.INVALID_PASSWORD) {
                        runOnUiThread(Helper.displayToast(getBaseContext(), R.string.connect_invalid_password));
                    } else {
                        runOnUiThread(Helper.displayToast(getBaseContext(), R.string.connect_error));
                    }
                }
                if (helper != null) {
                    startActivity(intent);
                }
            }
        };
        t.start();
    }

    private Socket getSocket(final String address, final int port) throws InterruptedException, ExecutionException, TimeoutException {
        AsyncTask<Void, Integer, Socket> connectTask;
        Socket socket;
        connectTask = new AsyncTask<Void, Integer, Socket>() {
            @Override
            protected Socket doInBackground(Void... params) {

                Socket s = null;
                try {
                    s = new Socket();
                    s.setKeepAlive(true);
                    s.connect(new InetSocketAddress(address, port), 5000);
                    while (!s.isConnected() && !isCancelled()) {
                        Thread.sleep(Consts.SLEEP);
                    }
                    return s;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return s;
            }

        };
        connectTask.execute();
        socket = connectTask.get(5, TimeUnit.SECONDS);
        connectTask.cancel(true);
        return socket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        setContentView(R.layout.activity_home);

        prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        EditText address = (EditText) findViewById(R.id.address_input_address);
        address.addTextChangedListener(new ValueChangeListener(Consts.ADDRESS, edit));
        address.setText(prefs.getString(Consts.ADDRESS, "192.168.1.1"));

        EditText port = (EditText) findViewById(R.id.address_input_port);
        port.setText(prefs.getString(Consts.PORT, "5555"));
        port.addTextChangedListener(new ValueChangeListener(Consts.PORT, edit));

        EditText password = (EditText) findViewById(R.id.password_input);
        password.setText(prefs.getString(Consts.PASSWORD, "0"));
        password.addTextChangedListener(new ValueChangeListener(Consts.PASSWORD, edit));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
