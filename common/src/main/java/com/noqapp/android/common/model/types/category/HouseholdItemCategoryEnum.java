package com.noqapp.android.common.model.types.category;

import android.os.Build;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<HouseholdItemCategoryEnum> householdItemCategoryEnums = Stream.of(HouseholdItemCategoryEnum.values())
               .sorted(Comparator.comparing(HouseholdItemCategoryEnum::getDescription))
               .collect(Collectors.toList());

            for (HouseholdItemCategoryEnum householdItemCategoryEnum : householdItemCategoryEnums) {
                a.add(householdItemCategoryEnum.description);
            }
        } else {
            for (HouseholdItemCategoryEnum householdItemCategoryEnum : HouseholdItemCategoryEnum.values()) {
                a.add(householdItemCategoryEnum.description);
            }
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
