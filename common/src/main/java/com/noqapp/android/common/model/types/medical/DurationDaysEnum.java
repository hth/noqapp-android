package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

public enum DurationDaysEnum {
    D1("D1", 1, "1 day"),
    D2("D2", 2, "2 days"),
    D3("D3", 3, "3 days"),
    D4("D4", 4, "4 days"),
    D5("D5", 5, "5 days"),
    D6("D6", 6, "6 days"),
    D7("D7", 7, "7 days"),
    D10("D10", 10, "10 days"),
    D15("D15", 15, "15 days"),
    D30("D30", 30, "1 month"),
    D45("D45", 45, "45 days"),
    D60("D60", 60, "2 months"),
    D90("D90", 90, "3 months"),
    D180("D180", 180, "6 months"),
    D365("D365", 365, "1 year");

    private final String description;
    private final String name;
    private final int value;

    DurationDaysEnum(String name, int value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (DurationDaysEnum medicationIntakeEnum : DurationDaysEnum.values()) {
            a.add(medicationIntakeEnum.description);
        }
        return a;
    }

    public static int getValueFromDesc(String description) {
        int value = -1;
        for (DurationDaysEnum durationDaysEnum : DurationDaysEnum.values()) {
            if (description.equals(durationDaysEnum.description)) {
                value = durationDaysEnum.value;
                break;
            }
        }
        return value;
    }

}
