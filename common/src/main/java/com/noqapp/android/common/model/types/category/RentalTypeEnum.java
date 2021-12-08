package com.noqapp.android.common.model.types.category;

import android.os.Build;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<RentalTypeEnum> rentalTypeEnums = Stream.of(RentalTypeEnum.values())
               .sorted(Comparator.comparing(RentalTypeEnum::getDescription))
               .collect(Collectors.toList());

            for (RentalTypeEnum rentalTypeEnum : rentalTypeEnums) {
                a.add(rentalTypeEnum.description);
            }
        } else {
            for (RentalTypeEnum rentalTypeEnum : RentalTypeEnum.values()) {
                a.add(rentalTypeEnum.description);
            }
        }
        return a;
    }

    public static RentalTypeEnum getNameByDescription(String description) {
        for (RentalTypeEnum rentalTypeEnum : RentalTypeEnum.values()) {
            if (description.equals(rentalTypeEnum.description)) {
                return rentalTypeEnum;
            }
        }

        return RentalTypeEnum.A;
    }

    @Override
    public String toString() {
        return description;
    }
}