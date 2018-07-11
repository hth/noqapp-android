package com.noqapp.android.common.model.types;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MedicationWithFoodEnum {
    BF("BF", "Before Food"),
    AF("AF", "After Food");

    private final String name;
    private final String description;

    MedicationWithFoodEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static List<String> asList() {
        MedicationWithFoodEnum[] all = MedicationWithFoodEnum.values();
        ArrayList<String> dataList = new ArrayList<>();
        for (MedicationWithFoodEnum num : all) {
            dataList.add(num.description);
        }
        return dataList;
    }

    public static MedicationWithFoodEnum get(String name) {
        Map<String, MedicationWithFoodEnum> map = new ConcurrentHashMap<String, MedicationWithFoodEnum>();
        for (MedicationWithFoodEnum instance : MedicationWithFoodEnum.values()) {
            map.put(instance.getDescription(), instance);
        }
        Map<String, MedicationWithFoodEnum> ENUM_MAP = Collections.unmodifiableMap(map);
        Log.v("MedicationWithFoodEnum", ENUM_MAP.get(name).toString());
        return ENUM_MAP.get(name);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }
}
