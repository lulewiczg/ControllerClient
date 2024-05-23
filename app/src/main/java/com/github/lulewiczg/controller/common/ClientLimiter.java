package com.github.lulewiczg.controller.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.lulewiczg.controller.client.Client;

/**
 * Limiter for sending actions.
 */
public class ClientLimiter {

    private long prevTime = System.currentTimeMillis();
    private long time = System.currentTimeMillis();
    private final int interval;
    private final int maxQueue;
    private final boolean queueLimiter;
    private final int bindInterval;

    public ClientLimiter(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        interval = Integer.parseInt(settings.getString(Consts.MOUSE_INTERVAL, "50"));
        bindInterval = Integer.parseInt(settings.getString(Consts.BIND_INTERVAL, "100"));
        maxQueue = Integer.parseInt(settings.getString(Consts.MOUSE_QUEUE, "3"));
        queueLimiter = settings.getBoolean(Consts.MOUSE_LIMITER_TYPE, true);
    }

    /**
     * Checks if mouse move action can be sent.
     *
     * @return true if can
     */
    public boolean checkIfDo() {
        if (queueLimiter) {
            return Client.get().getAwaitingActions() <= maxQueue;
        } else {
            time = System.currentTimeMillis();
            boolean result = time - prevTime > interval;
            if (result) {
                prevTime = time;
            }
            return result;
        }
    }


    /**
     * Checks if next bind action can be sent.
     *
     * @return true if can
     */
    public boolean checkIfDoBind() {
        time = System.currentTimeMillis();
        boolean result = time - prevTime > bindInterval;
        if (result) {
            prevTime = time;
        }
        return result;
    }

    /**
     * Waits to send next action.
     */
    public void waitForBind() {
        try {
            Thread.sleep(bindInterval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
