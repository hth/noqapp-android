package com.noqapp.android.common.model.types;

/**
 * User: hitender
 * Date: 4/16/17 5:39 PM
 */
public enum QueueUserStateEnum {
    Q("Q", "Queued"),
    N("N", "No Show"),
    A("A", "Abort"),
    S("S", "Serviced");

    private final String name;
    private final String description;

    QueueUserStateEnum(String name, String description) {
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