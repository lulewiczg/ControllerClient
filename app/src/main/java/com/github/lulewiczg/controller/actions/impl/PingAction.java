package com.github.lulewiczg.controller.actions.impl;


import com.github.lulewiczg.controller.actions.Action;

/**
 * Ping action to keep connection alive.
 *
 * @author Grzegorz
 */
public class PingAction extends Action {

    private static final long serialVersionUID = 1L;

    @Override
    public String getDescription() {
        return "Ping";
    }
}
