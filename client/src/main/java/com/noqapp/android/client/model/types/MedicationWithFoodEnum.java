package com.noqapp.android.client.model.types;

public enum MedicationWithFoodEnum {
    BF("BF", "Before Food"),
    AF("AF", "After Food");

    private final String name;
    private final String description;

    MedicationWithFoodEnum(String name, String description) {
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
