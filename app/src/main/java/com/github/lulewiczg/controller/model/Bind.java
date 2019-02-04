package com.github.lulewiczg.controller.model;

import com.github.lulewiczg.controller.actions.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for bind.
 */
public class Bind implements Serializable {

    private String name;
    private List<Action> actions;

    public Bind() {
        actions = new ArrayList<>();
    }

    public Bind(String name, List<Action> actions) {
        this.name = name;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nActions: %s", name, actions);
    }

    /**
     * Builds String with action details.
     *
     * @return details
     */
    public String buildDetails() {
        StringBuilder builder = new StringBuilder();
        for (Action a : actions) {
            builder.append(a.getDescription()).append(", ");
        }
        builder.replace(builder.length() - 2, builder.length() - 1, "");
        return builder.toString();
    }
}
