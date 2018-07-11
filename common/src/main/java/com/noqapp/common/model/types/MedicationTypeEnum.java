package com.noqapp.common.model.types;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * hitender
 * 6/14/18 1:49 PM
 */
public enum MedicationTypeEnum {
    TA("TA", "Tablet"),
    IN("IN", "Injection"),
    CA("CA", "Capsule"),
    SY("SY", "Syrup");

    private final String name;
    private final String description;

    MedicationTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> asList() {
        MedicationTypeEnum[] all = MedicationTypeEnum.values();
        ArrayList<String> dataList = new ArrayList<>();
        for (MedicationTypeEnum num : all) {
            dataList.add(num.description);
        }
        return dataList;
    }

    public static MedicationTypeEnum get(String name) {
        Map<String, MedicationTypeEnum> map = new ConcurrentHashMap<String, MedicationTypeEnum>();
        for (MedicationTypeEnum instance : MedicationTypeEnum.values()) {
            map.put(instance.getDescription(), instance);
        }
        Map<String, MedicationTypeEnum> ENUM_MAP = Collections.unmodifiableMap(map);
        Log.v("MedicationTypeEnum",ENUM_MAP.get(name).toString());
        return ENUM_MAP.get(name);
    }


    @Override
    public String toString() {
        return description;
    }
}
