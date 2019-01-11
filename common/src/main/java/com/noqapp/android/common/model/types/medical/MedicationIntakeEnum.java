package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-01-03 13:29
 */
public enum MedicationIntakeEnum {
    AT("AT", "Any Time"),
    BF("BF", "Before Food"),
    AF("AF", "After Food"),
    ES("ES", "Empty Stomach");

    private final String description;
    private final String name;

    MedicationIntakeEnum(String name, String description) {
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

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (MedicationIntakeEnum medicationIntakeEnum : MedicationIntakeEnum.values()) {
            a.add(medicationIntakeEnum.description);
        }
        return a;
    }

    public static String getValue(String input) {
        try {
            switch (input) {
                case "Any Time":
                    return "AT";
                case "Before Food":
                    return "BF";
                case "After Food":
                    return "AF";
                case "Empty Stomach":
                    return "ES";
                default:
                    return input;
            }
        } catch (Exception e) {
            return input;
        }
    }

}
