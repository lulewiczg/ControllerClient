package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.MouseButtonAction;

/**
 * Action for mouse button press event.
 *
 * @author Grzegurz
 */
public class MouseButtonPressAction extends MouseButtonAction {

    private static final long serialVersionUID = 1L;

    protected MouseButtonPressAction() {
        super();
    }

    public MouseButtonPressAction(int key) {
        super(key);
    }

}
