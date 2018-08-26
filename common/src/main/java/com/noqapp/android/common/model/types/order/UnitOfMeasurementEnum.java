package com.noqapp.android.common.model.types.order;

/**
 * Created by hitender on 4/1/18.
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count"),
//    DZ("DZ", "dozen"),
    MG("MG", "mg"),
    GM("GM", "gm"),
    KG("KG", "kg"),
    ML("ML", "ml"),
    LT("LT", "lt");

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
