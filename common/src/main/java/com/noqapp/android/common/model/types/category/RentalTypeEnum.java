package com.noqapp.android.common.model.types.category;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 1/10/21 11:17 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public enum RentalTypeEnum {

    A("A", "Apartment"),
    H("H", "Bungalow/House"),
    R("R", "Sublet Room"),
    T("T", "Townhouse");

    private final String name;
    private final String description;

    RentalTypeEnum(String name, String description) {
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
        for (RentalTypeEnum rentalTypeEnum : RentalTypeEnum.values()) {
            a.add(rentalTypeEnum.description);
        }
        return a;
    }

    public static RentalTypeEnum getNameByDescription(String description) {
        switch (description) {
            case "Apartment":
                return RentalTypeEnum.A;
            case "Bungalow/House":
                return RentalTypeEnum.H;
            case "Sublet Room":
                return RentalTypeEnum.R;
            case "Townhouse":
                return RentalTypeEnum.T;
            default:
                return RentalTypeEnum.A;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}