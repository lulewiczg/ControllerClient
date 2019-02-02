package com.github.lulewiczg.controller.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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


/**
 * Allows to control mouse on server.
 */
public class MouseFragment extends ActionFragment implements View.OnTouchListener {

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
    private int interval;
    private int maxQueue;
    private boolean queueLimiter;

    /**
     * Loads required preferences
     */
    private void loadPreferences() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        speed = Integer.valueOf(settings.getString(Consts.MOUSE_SPEED, "50"));
        scrollSpeed = Integer.valueOf(settings.getString(Consts.MOUSE_SCROLL_SPEED, "20"));
        interval = Integer.valueOf(settings.getString(Consts.MOUSE_INTERVAL, "50"));
        maxQueue = Integer.valueOf(settings.getString(Consts.MOUSE_QUEUE, "3"));
        queueLimiter = settings.getBoolean(Consts.MOUSE_LIMITER_TYPE, true);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        v.performClick();
        if (v.equals(mouseButton1)) {
            processButtonClick(MouseButtonAction.LMB, event);
        } else if (v.equals(mouseButton2)) {
            processButtonClick(MouseButtonAction.RMB, event);
        } else if (v.equals(mouseButton3)) {
            processButtonClick(MouseButtonAction.MMB, event);
        } else if (v.equals(touchpad)) {
            processTouchpad(event);
        } else if (v.equals(scroll)) {
            processScroll(event);
        }

        return false;
    }

    /**
     * Handles scroll event.
     *
     * @param event event
     */
    private void processScroll(MotionEvent event) {
        if (checkIfDo()) {
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);
            vt.addMovement(event);
            vt.computeCurrentVelocity(scrollSpeed);
            double y = vt.getYVelocity(pointerId);
            if (y != 0) {
                MouseScrollAction action = new MouseScrollAction((int) y);
                Client.get().doActionFast(action, getActivity());
            }
        }
    }

    /**
     * Handles mouse button runBind.
     *
     * @param key   key
     * @param event event
     */
    private void processButtonClick(int key, MotionEvent event) {
        MouseButtonAction action;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            action = new MouseButtonPressAction(key);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            action = new MouseButtonReleaseAction(key);
        } else {
            return;
        }
        Client.get().doAction(action, getActivity());
    }

    /**
     * Checks if mouse move action can be sent.
     *
     * @return true if can
     */
    private boolean checkIfDo() {
        if (queueLimiter) {
            return Client.get().getAwaitingActions() <= maxQueue;
        } else {
            time = System.currentTimeMillis();
            return time - prevTime > interval;
        }
    }

    /**
     * Handles mouse move action
     *
     * @param event event
     */
    private void processTouchpad(MotionEvent event) {
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        vt.clear();
        vt.addMovement(event);
        vt.computeCurrentVelocity(speed);
        dx += vt.getXVelocity(pointerId);
        dy += vt.getYVelocity(pointerId);
        if (dx != 0 && dy != 0 && checkIfDo()) {
            MouseMoveAction action = new MouseMoveAction(dx, dy);
            Client.get().doActionFast(action, getActivity());
            prevTime = time;
            dx = 0;
            dy = 0;
        }
    }

}
