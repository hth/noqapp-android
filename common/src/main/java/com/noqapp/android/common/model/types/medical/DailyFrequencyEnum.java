package com.noqapp.android.common.model.types.medical;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 8/17/18 5:31 PM
 */
public enum DailyFrequencyEnum {
    OD("OD", "1 time a day", 1),
    TD("TD", "2 times a day", 2),
    HD("HD", "3 times a day", 3),
    FD("FD", "4 times a day", 4),
    VD("VD", "5 times a day", 5);

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

    public static String getValueFromTimes(String input){
        try {
            switch (input) {
                case "1":
                    return "OD";
                case "2":
                    return "TD";
                case "3":
                    return "HD";
                case "4":
                    return "FD";
                case "5":
                    return "VD";
                default:
                    return input;
            }
        }catch (Exception e){
            return input;
        }
    }

    public static String getValue(String input){
        try {
            switch (input) {
                case "1 time a day":
                    return "OD";
                case "2 times a day":
                    return "TD";
                case "3 times a day":
                    return "HD";
                case "4 times a day":
                    return "FD";
                case "5 times a day":
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
