package com.noqapp.android.common.model.types;

/**
 * Created by hitender on 1/20/18.
 */
public enum GenderEnum {
    M("M", "Male"),
    F("F", "Female"),
    T("T", "Transgender");

    private final String description;
    private final String name;

    GenderEnum(String name, String description) {
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
