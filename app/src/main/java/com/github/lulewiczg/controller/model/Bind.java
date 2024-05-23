package com.github.lulewiczg.controller.model;

import com.github.lulewiczg.controller.actions.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Model for bind.
 */
@Getter
@Setter
@AllArgsConstructor
public class Bind implements Serializable {

    private String name;
    private final List<Action> actions;

    public Bind() {
        actions = new ArrayList<>();
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
