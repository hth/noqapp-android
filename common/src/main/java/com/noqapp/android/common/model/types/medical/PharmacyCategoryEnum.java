package com.noqapp.android.common.model.types.medical;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/18/18 2:34 PM
 */
public enum PharmacyCategoryEnum {
    CA("CA", "Capsule"),
    CR("CR", "Cream"),
    IH("IH", "Inhaler"),
    IJ("IJ", "Injection"),
    LO("LO", "Lotion"),
    PW("PW", "Powder"),
    SY("SY", "Syrup"),
    TA("TA", "Tablet");

    private final String description;
    private final String name;

    PharmacyCategoryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<PharmacyCategoryEnum> asList() {
        PharmacyCategoryEnum[] all = PharmacyCategoryEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (PharmacyCategoryEnum pharmacyCategory : PharmacyCategoryEnum.values()) {
            a.add(pharmacyCategory.description);
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
                case "Lotion":
                    return "LO";
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

    public static String getValueOfField(String input) {
        try {
            return PharmacyCategoryEnum.valueOf(input).description;
        } catch (Exception e) {
            return input;
        }
    }
}
