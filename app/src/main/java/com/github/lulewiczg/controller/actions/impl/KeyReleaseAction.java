package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.KeyAction;

/**
 * Action for key release event.
 *
 * @author Grzegurz
 */
public class KeyReleaseAction extends KeyAction {

    private static final long serialVersionUID = 1L;

    protected int key;

    public KeyReleaseAction() {
    }

    public KeyReleaseAction(int key) {
        this.key = key;
    }

    @Override
    public String getDescription() {
        return String.format("Key Release: [%s]", key);
    }
}
