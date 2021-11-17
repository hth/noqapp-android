package com.noqapp.android.common.model.types.category;

import com.noqapp.android.common.model.types.InventoryStateEnum;

import java.util.LinkedList;
import java.util.List;

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
        for (ItemConditionEnum itemConditionEnum : ItemConditionEnum.values()) {
            a.add(itemConditionEnum.description);
        }
        return a;
    }

    public static ItemConditionEnum getNameByDescription(String description) {
        switch (description) {
            case "Used":
                return ItemConditionEnum.U;
            case "Brand New, Unused":
                return ItemConditionEnum.B;
            default:
                return ItemConditionEnum.U;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
