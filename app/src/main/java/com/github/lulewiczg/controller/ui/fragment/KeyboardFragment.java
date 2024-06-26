package com.github.lulewiczg.controller.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.KeyPressAction;
import com.github.lulewiczg.controller.actions.impl.KeyReleaseAction;
import com.github.lulewiczg.controller.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;

/**
 * Fragment for keyboard control.
 */
public class KeyboardFragment extends Fragment implements View.OnTouchListener {

    private final List<String> pressedKeys = new ArrayList<>(5);
    private EditText label;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        label = view.findViewById(R.id.keyLabel);
        List<View> buttons = view.getTouchables();
        for (View button : buttons) {
            if (button instanceof AppCompatButton) {
                button.setOnTouchListener(this);
            }
        }
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Object tag = view.getTag();
        int key = Integer.decode(tag.toString());
        Action a;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            a = new KeyPressAction(key);
            addPressedKey(((Button) view).getText().toString());
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            a = new KeyReleaseAction(key);
            removePressedKey(((Button) view).getText().toString());
        } else {
            return false;
        }
        Client.get().doActionFast(a, this.getActivity());
        return false;
    }

    /**
     * Removes label with pressed key.
     *
     * @param text key
     */
    private void removePressedKey(String text) {
        pressedKeys.remove(text);
        updateLabel();
    }

    /**
     * Adds label with pressed key.
     *
     * @param text key
     */
    private void addPressedKey(String text) {
        pressedKeys.add(text);
        updateLabel();
    }

    /**
     * Updates label.
     */
    private void updateLabel() {
        if (pressedKeys.isEmpty()) {
            Optional.ofNullable(getContext()).map(i -> i.getString(R.string.keyboard_pressed)).ifPresent(label::setText);
        } else {
            String txt = TextUtils.join(" + ", pressedKeys);
            label.setText(txt.replace('\n', ' '));
        }
    }
}
