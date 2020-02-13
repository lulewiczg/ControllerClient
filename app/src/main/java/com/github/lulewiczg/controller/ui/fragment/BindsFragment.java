package com.github.lulewiczg.controller.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fragment for creating binds.
 */
public class BindsFragment extends Fragment {

    private static final Type TYPE = new TypeToken<List<Bind>>() {
    }.getType();

    private FloatingActionButton addButton;
    private FloatingActionButton cancelButton;
    private FloatingActionButton saveButton;

    private RecyclerView listView;
    private LinearLayoutManager layoutManger;
    private BindsDataAdapter dataAdapter;

    private AtomicBoolean bindRunning = new AtomicBoolean(false);


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

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String name = input.getText().toString();
                Bind bind = new Bind(name, actions);
                saveBind(bind);
                dataAdapter.add(bind);
                dataAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
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
        } else {
            addButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
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
        Gson gson = getGson();
        if (oldValue == null) {
            edit.putString(Consts.BINDS, gson.toJson(Arrays.asList(bind), TYPE));
        } else {
            List<Bind> list = gson.fromJson(oldValue, TYPE);
            list.add(bind);
            edit.putString(Consts.BINDS, gson.toJson(list));
        }
        edit.apply();
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
        Gson gson = getGson();
        if (oldValue != null) {
            List<Bind> list = gson.fromJson(oldValue, TYPE);
            list.remove(pos);
            edit.putString(Consts.BINDS, gson.toJson(list));
        }
        edit.apply();
    }

    /**
     * Builds Gson with custom serializers.
     *
     * @return gson
     */
    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Action.class, new GsonMapper());
        return gsonBuilder.create();
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
        Gson gson = getGson();
        List<Bind> savedBinds = gson.fromJson(oldValue, TYPE);
        return savedBinds;
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

        layoutManger = new LinearLayoutManager(getContext());
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

        addButton.setOnClickListener(v -> bind());
        saveButton.setOnClickListener(v -> save());
        cancelButton.setOnClickListener(v -> cancel());
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
                new Thread() {
                    @Override
                    public void run() {
                        bindRunning.set(true);
                        dataAdapter.runBind(position);
                        Helper.displayToast(getActivity(), R.string.bind_exec_end);
                        bindRunning.set(false);
                    }
                }.start();
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
            if (dataAdapter.getLongPressPos() > -1) {
                Helper.displayToast(getActivity(), R.string.not_implemented);//TODO
            }
            dataAdapter.setLongPressPos(-1);
            return false;
        }, menuItem -> {
            final long pos = dataAdapter.getLongPressPos();
            if (pos > -1) {
                Helper.displayAlert(getActivity(), R.string.bind_menu_delete, R.string.bind_delete_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete((int) pos);
                        dataAdapter.remove((int) pos);
                        dataAdapter.notifyDataSetChanged();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

            }
            dataAdapter.setLongPressPos(-1);
            return false;
        });
    }
}
