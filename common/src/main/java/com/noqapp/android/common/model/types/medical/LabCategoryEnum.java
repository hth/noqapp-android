package com.noqapp.android.common.model.types.medical;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 11/10/18 6:39 PM
 */
public enum LabCategoryEnum {
    MRI("MRI", "MRI"),
    SCAN("SCAN", "CT Scan"),
    SONO("SONO", "Sonography"),
    XRAY("XRAY", "X-ray"),
    PATH("PATH", "Pathology"),
    SPEC("SPEC", "Special Diagnostic");

    private final String description;
    private final String name;

    LabCategoryEnum(String name, String description) {
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
        for (LabCategoryEnum radiologyCategory : LabCategoryEnum.values()) {
            a.add(radiologyCategory.description);
        }

        return a;
    }

    @Override
    public String toString() {
        return description;
    }
}