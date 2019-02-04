package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.MouseButtonAction;

/**
 * Action for mouse button press event.
 *
 * @author Grzegurz
 */
public class MouseButtonPressAction extends MouseButtonAction {

    private static final long serialVersionUID = 1L;

    public MouseButtonPressAction(int key) {
        super(key);
    }

    public MouseButtonPressAction() {
    }

    @Override
    public String getDescription() {
        return String.format("Mouse Press: [%s]", key);
    }
}
