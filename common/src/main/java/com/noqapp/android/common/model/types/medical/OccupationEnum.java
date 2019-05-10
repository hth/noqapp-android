package com.noqapp.android.common.model.types.medical;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 7/30/18 3:37 PM
 */
public enum OccupationEnum {
    CMP("CMP", "Computer Professional"),
    DOC("DOC", "Doctor"),
    ENG("ENG", "Engineer"),
    STU("STU", "Student"),
    TEC("TEC", "Teacher"),
    POL("POL", "Police"), 
    HOW("HOW", "House Wife"),
    RET("RET", "Retired"),
    BAN("BAN", "Banker"),
    SER("SER", "Service Industry"),
    LAW("LAW", "Lawyer"),
    GOV("GOV", "Government Official");

    private final String name;
    private final String description;

    OccupationEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (OccupationEnum occupationEnum : OccupationEnum.values()) {
            a.add(occupationEnum.description);
        }
        return a;
    }
}
