package com.noqapp.android.common.model.types.category;

/**
 * hitender
 * 2/24/21 4:37 PM
 */
public enum ItemConditionEnum {
    U("U", "Used"),
    B("B", "Brand New, Unused");

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
