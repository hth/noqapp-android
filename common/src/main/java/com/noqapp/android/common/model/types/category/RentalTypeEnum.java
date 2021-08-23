package com.noqapp.android.common.model.types.category;

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
}