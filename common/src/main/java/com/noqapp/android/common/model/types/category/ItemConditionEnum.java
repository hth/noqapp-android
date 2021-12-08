package com.noqapp.android.common.model.types.category;

import android.os.Build;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * hitender
 * 2/24/21 4:37 PM
 */
public enum ItemConditionEnum {
    U("U", "Used"),
    B("B", "Brand New, Unused");

    private final String description;
    private final String name;

    ItemConditionEnum(String name, String description) {
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
            List<ItemConditionEnum> itemConditionEnums = Stream.of(ItemConditionEnum.values())
               .sorted(Comparator.comparing(ItemConditionEnum::getDescription))
               .collect(Collectors.toList());

            for (ItemConditionEnum itemConditionEnum : itemConditionEnums) {
                a.add(itemConditionEnum.description);
            }
        } else {
            for (ItemConditionEnum itemConditionEnum : ItemConditionEnum.values()) {
                a.add(itemConditionEnum.description);
            }
        }
        return a;
    }

    public static ItemConditionEnum getNameByDescription(String description) {
        for (ItemConditionEnum itemConditionEnum : ItemConditionEnum.values()) {
            if (description.equals(itemConditionEnum.description)) {
                return itemConditionEnum;
            }
        }

        return ItemConditionEnum.U;
    }

    public static String findByName(String name) {
        try {
            return ItemConditionEnum.valueOf(name).description;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
