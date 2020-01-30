package com.noqapp.android.common.model.types;

public enum WalkInStateEnum {
    E("E", "Enable"),
    D("D", "Disable");

    private final String name;
    private final String description;

    WalkInStateEnum(String name, String description) {
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