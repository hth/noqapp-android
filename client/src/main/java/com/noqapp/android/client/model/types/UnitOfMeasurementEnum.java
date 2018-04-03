package com.noqapp.android.client.model.types;

/**
 * Created by hitender on 4/1/18.
 */
public enum UnitOfMeasurementEnum {
    CNT("CNT", "Count"),
    HDO("HDO", "1/2 Dozen"),
    DOZ("DOZ", "Dozen"),
    GM2("GM2", "250 gm/mlt"),
    GM5("GM5", "500 gm/mlt"),
    KG1("KG1", "1 Kg/Lt");

    private final String name;
    private final String description;

    UnitOfMeasurementEnum(String name, String description) {
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
