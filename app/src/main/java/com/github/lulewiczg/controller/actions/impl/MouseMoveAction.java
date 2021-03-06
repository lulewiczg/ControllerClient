package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

/**
 * Action for mouse move event.
 *
 * @author Grzegurz
 */
public class MouseMoveAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    private long dx;
    private long dy;

    public MouseMoveAction(long dx, long dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public MouseMoveAction() {
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append(", dx:").append(dx).append(", dy:").append(dy);
        return str.toString();
    }

    @Override
    public String getDescription() {
        return String.format("Cursor: [%s, %s]", dx, dy);
    }
}
