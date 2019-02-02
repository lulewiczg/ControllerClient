package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.KeyAction;

/**
 * Action for key release event.
 *
 * @author Grzegurz
 *
 */
public class KeyReleaseAction extends KeyAction {

    private static final long serialVersionUID = 1L;

    public KeyReleaseAction(int key) {
        super(key);
    }

    public KeyReleaseAction() {
    }
}
