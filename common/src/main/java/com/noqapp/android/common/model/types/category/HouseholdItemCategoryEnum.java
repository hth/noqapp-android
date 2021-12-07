package com.noqapp.android.common.model.types.category;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 11/7/21 7:34 AM
 */
public enum HouseholdItemCategoryEnum {
    AUTO("AUTO", "Automobile"),
    FURN("FURN", "Furniture"),
    ELAP("ELAP", "Electrical Appliance"),
    FREQ("FREQ", "Farm Equipment"),
    BOOK("BOOK", "Books"),
    FASH("FASH", "Fashion");

    private final String name;
    private final String description;

    HouseholdItemCategoryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (HouseholdItemCategoryEnum householdItemCategoryEnum : HouseholdItemCategoryEnum.values()) {
            a.add(householdItemCategoryEnum.description);
        }
        return a;
    }

    public static HouseholdItemCategoryEnum getNameByDescription(String description) {
        for (HouseholdItemCategoryEnum householdItemCategoryEnum : HouseholdItemCategoryEnum.values()) {
            if (description.equals(householdItemCategoryEnum.description)) {
                return householdItemCategoryEnum;
            }
        }

        return HouseholdItemCategoryEnum.AUTO;
    }

    public static String findByName(String name) {
        try {
            return HouseholdItemCategoryEnum.valueOf(name).description;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
