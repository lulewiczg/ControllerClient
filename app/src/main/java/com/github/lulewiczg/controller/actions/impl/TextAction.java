package com.github.lulewiczg.controller.actions.impl;

import com.github.lulewiczg.controller.actions.LoginRequiredAction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Action for sending text.
 *
 * @author Grzegurz
 */
@NoArgsConstructor
@AllArgsConstructor
public class TextAction extends LoginRequiredAction {

    private static final long serialVersionUID = 1L;

    private String text;

    @Override
    public String toString() {
        return super.toString() + ", " + text;
    }

    @Override
    public String getDescription() {
        return String.format("Text: [%s]", text);
    }
}
