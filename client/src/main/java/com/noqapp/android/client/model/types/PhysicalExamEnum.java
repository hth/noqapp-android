package com.noqapp.android.client.model.types;

public enum PhysicalExamEnum {
    P("P", "Pluse"),
    B("B", "B.P"),
    W("W", "Weight");

    private final String name;
    private final String description;

    private PhysicalExamEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }
}
