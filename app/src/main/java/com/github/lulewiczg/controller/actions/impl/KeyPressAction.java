package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.KeyAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for key press event.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class KeyPressAction extends KeyAction {

    private static final long serialVersionUID = 1L;

    protected int key;

    @Override
    public String getDescription() {
        return String.format("Key Press: [%s]", key);
    }
}
