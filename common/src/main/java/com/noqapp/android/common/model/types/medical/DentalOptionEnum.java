package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-08-17 09:17
 */
public enum DentalOptionEnum {
    NOR("NOR", "Normal"),
    CAO("CAO", "Caries O"),
    CAD("CAD", "Caries D"),
    REO("REO", "Rest O"),
    RED("RED", "Rest D"),
    EXP("EXP", "Exposed"),
    RCT("RCT", "RCT"),
    RRC("RRC", "Root RC"),
    PTC("PTC", "Post & TC"),
    TCR("TCR", "Temp CR"),
    CRW("CRW", "Crown"),
    VEN("VEN", "Veneer"),
    IMP("IMP", "Implant"),
    RCC("RCC", "RC & Crown"),
    PON("PON", "Pontic"),
    ICR("ICR", "Imp CR"),
    MIS("MIS", "Missing"),
    CAR("CAR", "Cariesm"),
    CAM("CAM", "Caries MOD"),
    REM("REM", "Rest M"),
    RMO("RMO", "Rest Mod"),
    FRA("FRA", "Fracture"),
    RPC("RPC", "Root PC"),
    POS("POS", "Posts"),
    PCR("PCR", "Post CR"),
    RTC("RTC", "RC & TC");

    private final String description;
    private final String name;

    DentalOptionEnum(String name, String description) {
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
        for (DentalOptionEnum dentalOptionEnum : DentalOptionEnum.values()) {
            a.add(dentalOptionEnum.description);
        }
        return a;
    }


    @Override
    public String toString() {
        return description;
    }
}

