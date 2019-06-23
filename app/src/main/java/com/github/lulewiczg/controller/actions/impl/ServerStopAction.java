package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

/**
 * Action that forces server to stop.
 *
 * @author Grzegurz
 */
public class ServerStopAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;

    @Override
    public String getDescription() {
        return "Stop server";
    }

}
