package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

public enum DentalWorkDoneEnum {
    I("I", "Incomplete"),
    C("C", "Complete"),
    D("D", "Delayed");

    private final String description;
    private final String name;

    DentalWorkDoneEnum(String name, String description) {
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
        for (DentalWorkDoneEnum dentalWorkDoneEnum : DentalWorkDoneEnum.values()) {
            a.add(dentalWorkDoneEnum.description);
        }
        return a;
    }


    @Override
    public String toString() {
        return description;
    }
}
