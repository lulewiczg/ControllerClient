package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

/**
 * Action for mouse scroll event.
 *
 * @author Grzegurz
 */
public class MouseScrollAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    private int lines;

    public MouseScrollAction(int lines) {
        this.lines = lines;
    }

    public MouseScrollAction() {
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append(", ").append(lines);
        return str.toString();
    }

    @Override
    public String getDescription() {
        return String.format("Scroll: [%s]", lines);
    }
}
