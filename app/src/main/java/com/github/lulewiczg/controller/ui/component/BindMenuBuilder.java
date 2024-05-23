package com.github.lulewiczg.controller.ui.component;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.github.lulewiczg.controller.R;

import lombok.AllArgsConstructor;

/**
 * Menu builder for binds.
 */
@AllArgsConstructor
public class BindMenuBuilder implements View.OnCreateContextMenuListener {

    private final MenuItem.OnMenuItemClickListener editListener;
    private final MenuItem.OnMenuItemClickListener deleteListener;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        menu.add(0, view.getId(), 0, view.getResources().getString(R.string.bind_menu_edit)).setOnMenuItemClickListener(editListener);
        menu.add(0, view.getId(), 0, view.getResources().getString(R.string.bind_menu_delete)).setOnMenuItemClickListener(deleteListener);
    }
}