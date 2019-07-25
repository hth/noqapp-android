package com.noqapp.android.common.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-22 11:07
 */
public enum BooleanReplacementEnum {
    S("S", "Skip", "#CD334E"),
    Y("Y", "Yes", "#19769f"),
    N("N", "No", "#aaaaaa");

    private final String description;
    private final String name;
    private final String color;

    BooleanReplacementEnum(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (BooleanReplacementEnum booleanReplacementEnum : BooleanReplacementEnum.values()) {
            a.add(booleanReplacementEnum.description);
        }
        return a;
    }

    public static BooleanReplacementEnum getValue(String input) {
        try {
            switch (input) {
                case "Skip":
                    return BooleanReplacementEnum.S;
                case "Yes":
                    return BooleanReplacementEnum.Y;
                case "No":
                    return BooleanReplacementEnum.Y;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDisplayDescription(BooleanReplacementEnum booleanReplacementEnum) {
        switch (booleanReplacementEnum) {
            case S:
                return "Pending";
            case Y:
                return "Completed";
            case N:
                return "";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return description;
    }
}