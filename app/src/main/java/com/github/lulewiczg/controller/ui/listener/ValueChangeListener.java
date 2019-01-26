package com.github.lulewiczg.controller.ui.listener;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Grzegurz on 2016-04-02.
 */
public class ValueChangeListener implements TextWatcher {

    private String key;
    private SharedPreferences.Editor editor;

    public ValueChangeListener(String key, SharedPreferences.Editor editor) {
        this.key = key;
        this.editor = editor;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        editor.putString(key, s.toString());
        editor.commit();
    }
}
