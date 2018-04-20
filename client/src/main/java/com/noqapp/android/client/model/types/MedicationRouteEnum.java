package com.noqapp.android.client.model.types;

public enum MedicationRouteEnum {
    OR("OR", "Oral"),
    IM("IM", "After Food"),
    IV("IV", "Intravenous"),
    SC("SC", "After Food");

    private final String name;
    private final String description;

    private MedicationRouteEnum(String name, String description) {
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