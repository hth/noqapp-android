package com.noqapp.android.common.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 17:03
 */
public enum AppointmentStateEnum {
    O("O", "Off"),
    A("A", "Appointment"),
    S("S", "Slot");

    private final String description;
    private final String name;

    AppointmentStateEnum(String name, String description) {
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
        for (AppointmentStateEnum appointmentStateEnum : AppointmentStateEnum.values()) {
            a.add(appointmentStateEnum.description);
        }
        return a;
    }

    @Override
    public String toString() {
        return description;
    }
}
