package com.noqapp.common.model.types;

import java.util.Arrays;
import java.util.List;

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

    public static List<MedicationWithFoodEnum> asList() {
        MedicationWithFoodEnum[] all = MedicationWithFoodEnum.values();
        return Arrays.asList(all);
    }

    public String toString() {
        return this.description;
    }
}
