package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 5/30/18 12:04 AM
 */
public enum BloodTypeEnum {
    AP("AP", "A+"),
    AN("AN", "A-"),
    BP("BP", "B+"),
    BN("BN", "B-"),
    OP("OP", "O+"),
    ON("ON", "O-"),
    ABP("ABP", "AB+"),
    ABN("ABN", "AB-");

    private final String description;
    private final String name;

    BloodTypeEnum(String name, String description) {
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
        for (BloodTypeEnum bloodTypeEnum : BloodTypeEnum.values()) {
            a.add(bloodTypeEnum.description);
        }
        return a;
    }

    public static BloodTypeEnum getEnum(String description) {
        switch (description) {
            case "AB-":
                return ABN;
            case "AB+":
                return ABP;
            case "A-":
                return AN;
            case "A+":
                return AP;
            case "B-":
                return BN;
            case "B+":
                return BP;
            case "O-":
                return ON;
            case "O+":
                return OP;
            default:
                return null;
        }
    }
}
