package com.github.lulewiczg.controller.ui.listener;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;

import lombok.AllArgsConstructor;

/**
 * Created by Grzegurz on 2016-04-02.
 */
@AllArgsConstructor
public class ValueChangeListener implements TextWatcher {

    private final String key;
    private final SharedPreferences.Editor editor;

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
