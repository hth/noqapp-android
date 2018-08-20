package com.noqapp.android.common.model.types.medical;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/17/18 5:31 PM
 */
public enum DailyFrequencyEnum {
    OD("OD", "One time", 1),
    TD("TD", "Two times", 2),
    HD("HD", "Three times", 3),
    FD("FD", "Four times", 4),
    VD("VD", "Five times", 5);

    private final String description;
    private final String name;
    private int times;

    DailyFrequencyEnum(String name, String description, int times) {
        this.name = name;
        this.description = description;
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<DailyFrequencyEnum> asList() {
        DailyFrequencyEnum[] all = DailyFrequencyEnum.values();
        return Arrays.asList(all);
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for(DailyFrequencyEnum dailyFrequencyEnum : DailyFrequencyEnum.values()) {
            a.add(dailyFrequencyEnum.description);
        }
        return a;
    }

    public static String getValue(String input){
        try {
            switch (input) {
                case "One time":
                    return "OD";
                case "Two times":
                    return "TD";
                case "Three times":
                    return "HD";
                case "Four times":
                    return "FD";
                case "Five times":
                    return "VD";
                default:
                    return input;
            }
        }catch (Exception e){
            return input;
        }
    }

    public static String getValueOfField(String input) {
        try {
            return DailyFrequencyEnum.valueOf(input).description;
        } catch (Exception e) {
            return input;
        }
    }
}
