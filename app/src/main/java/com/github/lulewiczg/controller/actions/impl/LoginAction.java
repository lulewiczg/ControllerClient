package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.Action;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for login.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class LoginAction extends Action {

    private static final long serialVersionUID = 1L;
    private String password;
    private String info;
    private String ip;

    @Override
    public String getDescription() {
        return "Login";
    }
}
