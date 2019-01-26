package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

/**
 * Action for sending text.
 *
 * @author Grzegurz
 */
public class TextAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;

    private String text;

    public TextAction(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid() {
        return !text.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append(", ").append(text);
        return str.toString();
    }
}
