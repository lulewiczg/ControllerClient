package com.github.lulewiczg.controller.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Consts;
import com.github.lulewiczg.controller.common.Helper;
import com.github.lulewiczg.controller.model.Bind;
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

/**
 * Fragment for creating binds.
 */
public class BindsFragment extends Fragment {

    private static final Type TYPE = new TypeToken<List<Bind>>() {
    }.getType();

    private FloatingActionButton addButton;
    private FloatingActionButton saveButton;

    private RecyclerView listView;
    private LinearLayoutManager layoutManger;
    private BindsDataAdapter dataAdapter;


    public void bind() {
        Client.get().record();
        updateButtons();
        Helper.displayToast(getContext(), getActivity(), R.string.bind_start);
    }

    private void save() {
        List<Action> actions = Client.get().stopRecord();
        updateButtons();
        if (actions.isEmpty()) {
            Helper.displayToast(getContext(), getActivity(), R.string.bind_empty);
            return;
        }
        Helper.displayToast(getContext(), getActivity(), R.string.bind_stop);
        Bind bind = new Bind("test", actions);
        saveBind(bind);

        dataAdapter.add(bind);
        dataAdapter.notifyDataSetChanged();
    }

    @SuppressLint("RestrictedApi")
    private void updateButtons() {
        if (Client.get().isRecord()) {
            addButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_binds, container, false);
        listView = view.findViewById(R.id.bindsView);
        listView.setHasFixedSize(true);

        layoutManger = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManger);
        dataAdapter = new BindsDataAdapter(getBinds(), getActivity());
        listView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), listView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                dataAdapter.runBind(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        registerForContextMenu(listView);

        listView.setAdapter(dataAdapter);
        addButton = view.findViewById(R.id.addBind);
        saveButton = view.findViewById(R.id.saveBind);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        updateButtons();

        return view;
    }

}
