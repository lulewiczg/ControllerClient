package com.github.lulewiczg.controller.actions;

import java.io.Serializable;

/**
 * Abstract action to runBind on server.
 *
 * @author Grzegurz
 */
public abstract class Action implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Action: ").append(this.getClass().getSimpleName());
        return str.toString();
    }

    public abstract String getDescription();
}