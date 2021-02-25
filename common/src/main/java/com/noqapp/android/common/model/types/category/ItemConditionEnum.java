package com.noqapp.android.common.model.types.category;

/**
 * hitender
 * 2/24/21 4:37 PM
 */
public enum ItemConditionEnum {
    G("G", "Good"),
    P("P", "Poor"),
    V("V", "Very Good");

    private final String description;
    private final String name;

    ItemConditionEnum(String name, String description) {
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