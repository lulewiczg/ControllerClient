package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for mouse move event.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class MouseMoveAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    private long dx;
    private long dy;

    @Override
    public String toString() {
        return super.toString() + ", dx:" + dx + ", dy:" + dy;
    }

    @Override
    public String getDescription() {
        return String.format("Cursor: [%s, %s]", dx, dy);
    }
}
