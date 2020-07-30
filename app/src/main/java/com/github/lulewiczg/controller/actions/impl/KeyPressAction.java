package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.KeyAction;

/**
 * Action for key press event.
 *
 * @author Grzegurz
 */
public class KeyPressAction extends KeyAction {

    private static final long serialVersionUID = 1L;

    protected int key;

    public KeyPressAction() {
    }

    public KeyPressAction(int key) {
        this.key = key;
    }

    @Override
    public String getDescription() {
        return String.format("Key Press: [%s]", key);
    }
}
