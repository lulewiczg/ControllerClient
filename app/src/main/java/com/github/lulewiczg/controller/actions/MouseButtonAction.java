package com.github.lulewiczg.controller.actions;

/**
 * Abstract action for mouse button event.
 *
 * @author Grzegurz
 */
public abstract class MouseButtonAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;
    public static final int LMB = 1 << 4;
    public static final int RMB = 1 << 2;
    public static final int MMB = 1 << 3;

}
