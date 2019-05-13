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

    public static OccupationEnum getEnum(String description) {
        switch (description) {
            case "Computer Professional":
                return CMP;
            case "Doctor":
                return DOC;
            case "Engineer":
                return ENG;
            case "Student":
                return STU;
            case "Teacher":
                return TEC;
            case "Police":
                return POL;
            case "House Wife":
                return HOW;
            case "Retired":
                return RET;
            case "Banker":
                return BAN;
            case "Service Industry":
                return SER;
            case "Lawyer":
                return LAW;
            case "Government Official":
                return GOV;
            default:
                return null;
        }
    }


}
