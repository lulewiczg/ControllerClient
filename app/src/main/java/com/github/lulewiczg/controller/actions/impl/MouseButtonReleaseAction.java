package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.MouseButtonAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for mouse button release event.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class MouseButtonReleaseAction extends MouseButtonAction {

    private static final long serialVersionUID = 1L;
    protected int key;

    @Override
    public String getDescription() {
        return String.format("Mouse Release: [%s]", key);
    }
}
