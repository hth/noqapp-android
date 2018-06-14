package com.noqapp.common.model.types;

import java.util.Arrays;
import java.util.List;

/**
 * hitender
 * 6/14/18 1:49 PM
 */
public enum MedicationTypeEnum {
    TA("TA", "Tablet"),
    IN("IN", "Injection"),
    CA("CA", "Capsule"),
    SY("SY", "Syrup");

    private final String name;
    private final String description;

    MedicationTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<MedicationTypeEnum> asList() {
        MedicationTypeEnum[] all = MedicationTypeEnum.values();
        return Arrays.asList(all);
    }

    @Override
    public String toString() {
        return description;
    }
}
