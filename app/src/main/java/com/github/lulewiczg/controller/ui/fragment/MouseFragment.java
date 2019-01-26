package com.github.lulewiczg.controller.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.MouseButtonAction;
import com.github.lulewiczg.controller.actions.impl.MouseButtonPressAction;
import com.github.lulewiczg.controller.actions.impl.MouseButtonReleaseAction;
import com.github.lulewiczg.controller.actions.impl.MouseMoveAction;
import com.github.lulewiczg.controller.actions.impl.MouseScrollAction;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.Consts;


public class MouseFragment extends ActionFragment implements View.OnTouchListener {

    private static final double SCROLLBAR_POS = 0.95;
    private Button mouseButton1;
    private Button mouseButton2;
    private Button mouseButton3;
    private View touchpad;
    private View scroll;
    private VelocityTracker vt;
    private double dx;
    private double dy;
    private long prevTime = System.currentTimeMillis();
    private long time;
    private int speed;
    private int scrollSpeed;
    private int frequency;
    private boolean autoFrequency;

    private void loadPreferences() {
        SharedPreferences settings;
        settings = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        speed = Integer.valueOf(settings.getString(Consts.MOUSE_SPEED, "50"));
        scrollSpeed = Integer.valueOf(settings.getString(Consts.MOUSE_SCROLL_SPEED, "20"));
        frequency = Integer.valueOf(settings.getString(Consts.MOUSE_FREQUENCY, "200"));
        autoFrequency = settings.getBoolean(Consts.MOUSE_AUTO_FREQUENCY, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mouse, container, false);
        mouseButton1 = view.findViewById(R.id.mouse_button1);
        mouseButton2 = view.findViewById(R.id.mouse_button2);
        mouseButton3 = view.findViewById(R.id.mouse_button3);
        touchpad = view.findViewById(R.id.mouse_touchpad);
        touchpad.setOnTouchListener(this);
        scroll = view.findViewById(R.id.mouse_scroll);
        scroll.setOnTouchListener(this);
        mouseButton1.setOnTouchListener(this);
        mouseButton2.setOnTouchListener(this);
        mouseButton3.setOnTouchListener(this);
        vt = VelocityTracker.obtain();
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int key = 0;
        if (v.equals(mouseButton1)) {
            key = MouseButtonAction.LMB;
            mouseButton(key, event);
        } else if (v.equals(mouseButton2)) {
            key = MouseButtonAction.RMB;
            mouseButton(key, event);
        } else if (v.equals(mouseButton3)) {
            key = MouseButtonAction.MMB;
            mouseButton(key, event);
        } else if (v.equals(touchpad)) {
            touchpad(event);
        } else if (v.equals(scroll)) {
            scroll(event);
        }

        return false;
    }

    private void scroll(MotionEvent event) {
        if (checkIfDo()) {
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);
            vt.addMovement(event);
            vt.computeCurrentVelocity(scrollSpeed);
            double y = VelocityTrackerCompat.getYVelocity(vt, pointerId);
            MouseScrollAction action = new MouseScrollAction((int) y / 2);
            Client.get().doActionFast(action, getActivity());
        }
    }

    private void mouseButton(int key, MotionEvent event) {
        MouseButtonAction action = null;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            action = new MouseButtonPressAction(key);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            action = new MouseButtonReleaseAction(key);
        } else {
            return;
        }
        Client.get().doAction(action, getActivity());
    }

    private boolean checkIfDo() {
        if (autoFrequency) {
            return Client.get().getAwaitingActions() <= 1;
        } else {
            time = System.currentTimeMillis();
            return time - prevTime > frequency;
        }
    }

    private void touchpad(MotionEvent event) {
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        vt.clear();
        vt.addMovement(event);
        vt.computeCurrentVelocity(speed);
        dx += VelocityTrackerCompat.getXVelocity(vt, pointerId);
        dy += VelocityTrackerCompat.getYVelocity(vt, pointerId);
        if (checkIfDo()) {
            MouseMoveAction action = new MouseMoveAction(dx, dy);
            Client.get().doActionFast(action, getActivity());
            prevTime = time;
            dx = 0;
            dy = 0;
        }
    }

}
