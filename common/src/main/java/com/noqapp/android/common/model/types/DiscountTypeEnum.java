package com.noqapp.android.common.model.types;

/**
 * User: hitender
 * Date: 2019-06-10 11:18
 */
public enum DiscountTypeEnum {
    P("P", "Percent"),
    F("F", "Fixed");

    private String name;
    private String description;

    DiscountTypeEnum(String name, String description) {
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
