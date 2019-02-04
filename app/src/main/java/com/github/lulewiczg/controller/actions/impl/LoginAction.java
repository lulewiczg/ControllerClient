package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.Action;

/**
 * Aciton for login.
 *
 * @author Grzegurz
 */
public class LoginAction extends Action {

    private static final long serialVersionUID = 1L;
    private String password;
    private String info;
    private String ip;

    public LoginAction() {
    }

    public LoginAction(String password, String info, String ip) {
        this.password = password;
        this.info = info;
        this.ip = ip;
    }

    @Override
    public String getDescription() {
        return "Login";
    }
}
