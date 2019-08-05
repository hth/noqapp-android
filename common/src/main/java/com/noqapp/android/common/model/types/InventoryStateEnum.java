package com.noqapp.android.common.model.types;


import java.util.LinkedList;
import java.util.List;

public enum InventoryStateEnum {
    WK("WK", "Working"),
    NW("NW", "Not working"),
    NC("NC", "Not Checked");

    private final String description;
    private final String name;

    InventoryStateEnum(String name, String description) {
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
        for (InventoryStateEnum inventoryStateEnum : InventoryStateEnum.values()) {
            a.add(inventoryStateEnum.description);
        }
        return a;
    }

    public static InventoryStateEnum getNameByDescription(String description) {
        switch (description) {
            case "Working":
                return InventoryStateEnum.WK;
            case "Not working":
                return InventoryStateEnum.NW;
            case "Not Checked":
                return InventoryStateEnum.NC;
            default:
                return InventoryStateEnum.NC;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}

