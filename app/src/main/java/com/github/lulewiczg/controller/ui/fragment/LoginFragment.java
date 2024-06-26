package com.github.lulewiczg.controller.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.common.Response;
import com.github.lulewiczg.controller.common.Status;
import com.github.lulewiczg.controller.ui.activity.MainActivity;
import com.github.lulewiczg.controller.ui.listener.ValueChangeListener;

import java.io.IOException;

import static android.content.Context.WIFI_SERVICE;

/**
 * Fragment for connecting to server.
 */
public class LoginFragment extends Fragment {

    private WifiManager wm;
    private int timeout;
    private int serverTimeout;
    private EditText addressInput;
    private EditText portInput;
    private EditText passwordInput;
    private int ping;

    /**
     * Returns phone addressInput, no other method available for IPv4 formatting.
     *
     * @return IP addressInput
     */
    @SuppressWarnings("deprecation")
    private String getIP() {
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    /**
     * Handles login action.
     */
    public void click() {
        final Context context = getContext();
        final Intent intent = new Intent(context, MainActivity.class);
        final String address = addressInput.getText().toString();
        if (address.isEmpty()) {
            Toast.makeText(context, R.string.connect_address_empty, Toast.LENGTH_LONG).show();
            return;
        }

        String portStr = portInput.getText().toString();
        if (portStr.isEmpty()) {
            Toast.makeText(context, R.string.connect_port_empty, Toast.LENGTH_LONG).show();
            return;
        }
        final int port = Integer.parseInt(portStr);

        final String password = passwordInput.getText().toString();

        final ProgressDialog progress = ProgressDialog.show(context, getResources().getString(R.string.connect_loading_title), getResources().getString(R.string.connect_loading));
        Thread t = new Thread() {

            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                Client client;
                try {
                    client = Client.create(address, port, timeout, serverTimeout, ping);
                } catch (IOException e) {
                    e.printStackTrace();
                    Helper.displayToast(activity, R.string.connect_error);
                    return;
                } finally {
                    Helper.closeProgress(progress, activity);
                }
                Response response;
                response = client.login(password, Build.MANUFACTURER + ", " + Build.MODEL, getIP(), activity);
                if (response.getStatus() == Status.INVALID_PASSWORD) {
                    Helper.displayToast(activity, R.string.connect_invalid_password);
                } else if (response.getStatus() == Status.NOT_OK) {
                    Helper.displayToast(activity, getResources().getString(R.string.connect_unknown_error) + response.getExceptionStr());
                } else {
                    startActivity(intent);
                }
            }
        };
        t.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPrefs();
    }


    /**
     * Sets up input fields on start.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        FragmentActivity activity = getActivity();
        wm = (WifiManager) activity.getApplicationContext().getSystemService(WIFI_SERVICE);
        loadPrefs();
        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        addressInput = view.findViewById(R.id.address_input_address);
        portInput = view.findViewById(R.id.address_input_port);
        passwordInput = view.findViewById(R.id.password_input);

        addressInput.addTextChangedListener(new ValueChangeListener(Consts.ADDRESS, edit));
        addressInput.setText(prefs.getString(Consts.ADDRESS, "192.168.1.1"));

        portInput.setText(prefs.getString(Consts.PORT, "5555"));
        portInput.addTextChangedListener(new ValueChangeListener(Consts.PORT, edit));

        passwordInput.setText(prefs.getString(Consts.PASSWORD, "0"));
        passwordInput.addTextChangedListener(new ValueChangeListener(Consts.PASSWORD, edit));

        Button button = view.findViewById(R.id.connect_button);
        button.setOnClickListener(v -> click());
        return view;
    }

    /**
     * Loads connection settings.
     */
    private void loadPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        timeout = Integer.parseInt(settings.getString(Consts.CONNECT_TIMEOUT, "2000"));
        serverTimeout = Integer.parseInt(settings.getString(Consts.SERVER_TIMEOUT, "10000"));
        ping = Integer.parseInt(settings.getString(Consts.SERVER_PING, "60"));
    }
}
