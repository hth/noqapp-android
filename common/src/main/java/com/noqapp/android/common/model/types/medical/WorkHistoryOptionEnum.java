package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;


public enum WorkHistoryOptionEnum {
    WD("WD", "Work Done"),
    LT("LT", "Lab Test"),
    MD("MD", "Medicine");

    private final String description;
    private final String name;

    WorkHistoryOptionEnum(String name, String description) {
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
        for (WorkHistoryOptionEnum workHistoryOptionEnum : WorkHistoryOptionEnum.values()) {
            a.add(workHistoryOptionEnum.description);
        }
        return a;
    }


    @Override
    public String toString() {
        return description;
    }
}

