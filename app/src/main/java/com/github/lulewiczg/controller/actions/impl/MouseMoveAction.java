package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

/**
 * Action for mouse move event.
 *
 * @author Grzegurz
 */
public class MouseMoveAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    private double dx;
    private double dy;

    @Override
    public boolean isValid() {
        return Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01;

    }

    public MouseMoveAction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

}
