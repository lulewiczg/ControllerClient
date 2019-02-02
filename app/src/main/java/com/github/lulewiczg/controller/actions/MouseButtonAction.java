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
    protected int key;

    public MouseButtonAction() {
    }

    public MouseButtonAction(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append(", ").append(key);
        return str.toString();
    }

}
