package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.MouseButtonAction;

/**
 * Action for mouse button release event.
 *
 * @author Grzegurz
 */
public class MouseButtonReleaseAction extends MouseButtonAction {

    private static final long serialVersionUID = 1L;
    protected int key;

    public MouseButtonReleaseAction(int key) {
        this.key = key;
    }

    public MouseButtonReleaseAction() {
    }

    @Override
    public String getDescription() {
        return String.format("Mouse Release: [%s]", key);
    }
}
