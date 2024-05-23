package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for mouse scroll event.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class MouseScrollAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    private int lines;

    @Override
    public String toString() {
        return super.toString() + ", " + lines;
    }

    @Override
    public String getDescription() {
        return String.format("Scroll: [%s]", lines);
    }
}
