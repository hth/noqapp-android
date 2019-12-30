package com.noqapp.android.common.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 17:03
 */
public enum AppointmentStateEnum {
    O("O", "Off", "No Appointment"),
    A("A", "Traditional Appointments", "Appointment"),
    S("S", "Walk-in Appointments", "Slots"),

    /* Mixture of Walk-ins and traditional appointments. To be implemented. */
    F("F", "Flex Appointments", "Flex");

    private final String description;
    private final String name;
    private final String additionalDescription;

    AppointmentStateEnum(String name, String description, String additionalDescription) {
        this.name = name;
        this.description = description;
        this.additionalDescription = additionalDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
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
