package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.MouseButtonAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for mouse button press event.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class MouseButtonPressAction extends MouseButtonAction {

    private static final long serialVersionUID = 1L;
    protected int key;

    @Override
    public String getDescription() {
        return String.format("Mouse Press: [%s]", key);
    }
}
