package com.noqapp.android.common.model.types;

/**
 * hitender
 * 2019-03-13 11:45
 */
public enum ServicePaymentEnum {
    N("N", "Not Required"),
    R("R", "Required"),
    B("B", "Both");

    private final String name;
    private final String description;

    ServicePaymentEnum(String name, String description) {
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