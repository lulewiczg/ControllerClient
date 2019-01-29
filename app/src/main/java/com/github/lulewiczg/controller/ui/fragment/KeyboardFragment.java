package com.github.lulewiczg.controller.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.actions.impl.KeyPressAction;
import com.github.lulewiczg.controller.actions.impl.KeyReleaseAction;
import com.github.lulewiczg.controller.client.Client;

import java.util.List;

/**
 * Fragment for keyboard control.
 */
public class KeyboardFragment extends Fragment implements View.OnTouchListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
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
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            a = new KeyReleaseAction(key);
        } else {
            return false;
        }
        Client.get().doActionFast(a, this.getActivity());
        return false;
    }
}
