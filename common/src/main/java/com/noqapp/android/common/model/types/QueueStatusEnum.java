package com.noqapp.android.common.model.types;

/**
 * User: hitender
 * Date: 3/27/17 1:50 PM
 */
public enum QueueStatusEnum {
    S("S", "Start"),
    R("R", "Re-Start"),
    N("N", "Next"),
    P("P", "Pause"),
    D("D", "Done"),
    C("C", "Closed");

    private final String name;
    private final String description;

    QueueStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
