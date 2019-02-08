package com.github.lulewiczg.controller.ui.component;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.github.lulewiczg.controller.R;

/**
 * Menu builder for binds.
 */
public class BindMenuBuilder implements View.OnCreateContextMenuListener {

    private MenuItem.OnMenuItemClickListener editListener;
    private MenuItem.OnMenuItemClickListener deleteListener;

    public BindMenuBuilder(MenuItem.OnMenuItemClickListener editListener, MenuItem.OnMenuItemClickListener deleteListener) {
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        menu.add(0, view.getId(), 0, view.getResources().getString(R.string.bind_menu_edit)).setOnMenuItemClickListener(editListener);
        menu.add(0, view.getId(), 0, view.getResources().getString(R.string.bind_menu_delete)).setOnMenuItemClickListener(deleteListener);
    }
}