package com.noqapp.android.common.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 17:03
 */
public enum AppointmentStateEnum {
    O("O", "Off", "No Appointment", "Off"),
    A("A", "Traditional Appointment", "Appointment", "Traditional"),
    S("S", "Walk-in Appointment", "Slots", "Walk-in"),

    /* Mixture of Walk-ins and traditional appointments. To be implemented. */
    F("F", "Flex Appointment", "Flex", "Flex");

    private final String description;
    private final String name;
    private final String additionalDescription;
    private final String shortForm;

    AppointmentStateEnum(String name, String description, String additionalDescription, String shortForm) {
        this.name = name;
        this.description = description;
        this.additionalDescription = additionalDescription;
        this.shortForm = shortForm;
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

    public String getShortForm() {
        return shortForm;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (AppointmentStateEnum appointmentStateEnum : AppointmentStateEnum.values()) {
            a.add(appointmentStateEnum.description);
        }
        return a;
    }

    public static List<String> asListOfDescriptionShortForm() {
        List<String> a = new LinkedList<>();
        for (AppointmentStateEnum appointmentStateEnum : AppointmentStateEnum.values()) {
            a.add(appointmentStateEnum.shortForm);
        }
        return a;
    }

    @Override
    public String toString() {
        return description;
    }
}
