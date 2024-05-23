package com.github.lulewiczg.controller.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.ClientLimiter;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.model.Bind;
import com.github.lulewiczg.controller.ui.component.BindMenuBuilder;
import com.github.lulewiczg.controller.ui.component.BindsDataAdapter;
import com.github.lulewiczg.controller.ui.component.GsonMapper;
import com.github.lulewiczg.controller.ui.listener.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.SneakyThrows;

/**
 * Fragment for creating binds.
 */
public class BindsFragment extends Fragment {

    private static final Type TYPE = new TypeToken<List<Bind>>() {
    }.getType();

    private FloatingActionButton addButton;
    private FloatingActionButton cancelButton;
    private FloatingActionButton saveButton;
    private FloatingActionButton importButton;
    private FloatingActionButton exportButton;
    private RecyclerView listView;
    private BindsDataAdapter dataAdapter;

    private final Gson gson;

    private final AtomicBoolean bindRunning = new AtomicBoolean(false);

    public BindsFragment() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Action.class, new GsonMapper());
        gson = gsonBuilder.create();
    }

    /**
     * Starts recording binds.
     */
    public void bind() {
        Client.get().record();
        updateButtons();
        Helper.displayToast(getActivity(), R.string.bind_start);
    }

    /**
     * Cancels recording.
     */
    public void cancel() {
        Client.get().stopRecord();
        updateButtons();
        Helper.displayToast(getActivity(), R.string.bind_cancel);
    }

    /**
     * Saves bind.
     */
    private void save() {
        final List<Action> actions = Client.get().stopRecord();
        updateButtons();
        if (actions.isEmpty()) {
            Helper.displayToast(getActivity(), R.string.bind_empty);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        builder.setTitle(getResources().getString(R.string.bind_name));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String name = input.getText().toString();
            Bind bind = new Bind(name, actions);
            saveBind(bind);
            dataAdapter.add(bind);
            dataAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Updates buttons visibility.
     */
    @SuppressLint("RestrictedApi")
    private void updateButtons() {
        if (Client.get().isRecord()) {
            addButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            importButton.setVisibility(View.INVISIBLE);
            exportButton.setVisibility(View.INVISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            importButton.setVisibility(View.VISIBLE);
            exportButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Saves bind.
     *
     * @param bind bind
     */
    private void saveBind(Bind bind) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        String oldValue = prefs.getString(Consts.BINDS, null);
        if (oldValue == null) {
            edit.putString(Consts.BINDS, gson.toJson(Collections.singletonList(bind), TYPE));
            edit.apply();
        } else {
            List<Bind> list = gson.fromJson(oldValue, TYPE);
            boolean duplicate = list.stream().map(Bind::getName).anyMatch(i -> i.equals(bind.getName()));
            if (duplicate) {
                Helper.displayToast(getActivity(), R.string.bind_exec_duplicate);
            } else {
                list.add(bind);
                edit.putString(Consts.BINDS, gson.toJson(list));
                edit.apply();
            }
        }
    }

    /**
     * Saves bind.
     *
     * @param pos bind position
     */
    private void delete(int pos) {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        String oldValue = prefs.getString(Consts.BINDS, null);
        if (oldValue != null) {
            List<Bind> list = gson.fromJson(oldValue, TYPE);
            list.remove(pos);
            edit.putString(Consts.BINDS, gson.toJson(list));
        }
        edit.apply();
    }

    private void exportData() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, 1);
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String json = prefs.getString(Consts.BINDS, null);
        File path = new File(Environment.getExternalStorageDirectory(), "controllerExport.json");
        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(json.getBytes());
            Helper.displayToast(getActivity(), R.string.bind_exec_export_success);
        } catch (IOException e) {
            e.printStackTrace();
            Helper.displayToast(getActivity(), R.string.bind_exec_export_error);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void importData() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, 1);
        Path path = Paths.get(Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "controllerExport.json");
        if (Files.exists(path)) {
            Helper.displayAlert(getActivity(), R.string.bind_menu_delete, R.string.bind_exec_import_confirm, (dialog, which) -> {
                SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                try {
                    String input = new String(Files.readAllBytes(path));
                    edit.putString(Consts.BINDS, input);
                    edit.apply();
                    getActivity().recreate();
                    Helper.displayToast(getActivity(), R.string.bind_exec_import_success);
                } catch (IOException e) {
                    e.printStackTrace();
                    Helper.displayToast(getActivity(), R.string.bind_exec_import_error);
                    throw new RuntimeException(e);
                }
            }, (dialog, which) -> {
            });
        } else {
            Helper.displayToast(getActivity(), R.string.bind_exec_import_not_found);
        }
    }

    /**
     * Reads save binds.
     *
     * @return binds
     */
    private List<Bind> getBinds() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String oldValue = prefs.getString(Consts.BINDS, null);
        if (oldValue == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(oldValue, TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        dataAdapter.setLimiter(new ClientLimiter(getContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_binds, container, false);
        listView = view.findViewById(R.id.bindsView);
        listView.setHasFixedSize(true);

        LinearLayoutManager layoutManger = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManger);
        dataAdapter = new BindsDataAdapter(getBinds(), getActivity(), getMenuBuilder());
        listView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addOnItemTouchListener(getItemClickListener());

        registerForContextMenu(listView);

        listView.setAdapter(dataAdapter);
        addButton = view.findViewById(R.id.addBind);
        saveButton = view.findViewById(R.id.saveBind);
        cancelButton = view.findViewById(R.id.cancelBind);
        exportButton = view.findViewById(R.id.exportBinds);
        importButton = view.findViewById(R.id.importBinds);

        addButton.setOnClickListener(v -> bind());
        saveButton.setOnClickListener(v -> save());
        cancelButton.setOnClickListener(v -> cancel());
        exportButton.setOnClickListener(v -> exportData());
        importButton.setOnClickListener(v -> importData());
        updateButtons();

        return view;
    }

    /**
     * Builds item touch listener.
     *
     * @return touch listener
     */
    private RecyclerTouchListener getItemClickListener() {
        return new RecyclerTouchListener(getContext(), listView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (bindRunning.get()) {
                    return;
                }
                new Thread(() -> {
                    bindRunning.set(true);
                    dataAdapter.runBind(position);
                    Helper.displayToast(getActivity(), R.string.bind_exec_end);
                    bindRunning.set(false);
                }).start();
            }

            @Override
            public void onLongClick(View view, int position) {
                dataAdapter.setLongPressPos(position);
            }
        });
    }

    /**
     * Builds menu builder.
     *
     * @return menu builder
     */
    private BindMenuBuilder getMenuBuilder() {
        return new BindMenuBuilder(menuItem -> {
            int pos = dataAdapter.getLongPressPos();
            if (pos > -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                builder.setTitle(getResources().getString(R.string.bind_name));
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                String oldValue = prefs.getString(Consts.BINDS, null);
                List<Bind> binds = gson.fromJson(oldValue, TYPE);
                Bind bind = binds.get(pos);
                input.setText(bind.getName());
                builder.setView(input);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    String name = input.getText().toString();
                    bind.setName(name);
                    edit.putString(Consts.BINDS, gson.toJson(binds));
                    edit.apply();
                    dataAdapter.notifyDataSetChanged();
                    getActivity().recreate();
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                builder.show();
            }
            dataAdapter.setLongPressPos(-1);
            return false;
        }, menuItem -> {
            int pos = dataAdapter.getLongPressPos();
            if (pos > -1) {
                Helper.displayAlert(getActivity(), R.string.bind_menu_delete, R.string.bind_delete_confirm, (dialog, which) -> {
                    delete(pos);
                    dataAdapter.remove(pos);
                    dataAdapter.notifyDataSetChanged();
                }, (dialog, which) -> {
                });

            }
            dataAdapter.setLongPressPos(-1);
            return false;
        });
    }
}
