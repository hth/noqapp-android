package com.noqapp.android.common.model.types.medical;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/18/18 2:34 PM
 */
public enum MedicineTypeEnum {
    CA("CA", "Capsule"),
    CR("CR", "Cream"),
    IH("IH", "Inhaler"),
    IJ("IJ", "Injection"),
    PW("PW", "Powder"),
    SY("SY", "Syrup"),
    TA("TA", "Tablet");

    private final String description;
    private final String name;

    MedicineTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<MedicineTypeEnum> asList() {
        MedicineTypeEnum[] all = MedicineTypeEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for(MedicineTypeEnum medicineType : MedicineTypeEnum.values()) {
            a.add(medicineType.description);
        }

        return a;
    }

    @Override
    public String toString() {
        return description;
    }

    public static String getValue(String input) {
        try {
            switch (input) {
                case "Capsule":
                    return "CA";
                case "Cream":
                    return "CR";
                case "Inhaler":
                    return "IH";
                case "Injection":
                    return "IJ";
                case "Powder":
                    return "PW";
                case "Syrup":
                    return "SY";
                case "Tablet":
                    return "TA";
                default:
                    return input;
            }
        } catch (Exception e) {
            return input;
        }
    }
}
