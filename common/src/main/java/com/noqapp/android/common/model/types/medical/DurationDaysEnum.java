package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

public enum DurationDaysEnum {
    D1D("D1D", 1, "1 day"),
    D2D("D2S", 2, "2 days"),
    D3D("D3S", 3, "3 days"),
    D4D("D4D", 4, "4 days"),
    D5D("D5D", 5, "5 days"),
    D6D("D6D", 6, "6 days"),
    D7D("D7D", 7, "7 days"),
    D10D("D10D", 10, "10 days"),
    D15D("D15D", 15, "15 days"),
    D45D("D45D", 45, "45 days"),
    D1M("D1M", 30, "1 month"),
    D2M("D2M", D1M.value * 2, "2 months"),
    D3M("D3M", D1M.value * 3, "3 months"),
    D4M("D4M", D1M.value * 4, "4 months"),
    D5M("D5M", D1M.value * 5, "5 months"),
    D6M("D6M", D1M.value * 6, "6 months"),
    D7M("D7M", D1M.value * 7, "7 months"),
    D8M("D8M", D1M.value * 8, "8 months"),
    D9M("D9M", D1M.value * 9, "9 months"),
    D10M("D10M", D1M.value * 10, "10 months"),
    D11M("D11M", D1M.value * 11, "11 months"),
    D1Y("D1Y", D1M.value * 12, "1 year");

    private final String name;
    private final int value;
    private final String description;

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
