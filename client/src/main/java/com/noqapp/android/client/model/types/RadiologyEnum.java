package com.noqapp.android.client.model.types;

public enum RadiologyEnum {
    XRay_RGU("XRay_RGU", "XRay RGU"),
    Sono_Pelvis("Sono_Pelvis", "Sono Pelvis");

    private final String name;
    private final String description;

    private RadiologyEnum(String name, String description) {
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