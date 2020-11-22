package com.noqapp.android.common.model.types.order;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 4/1/18.
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count (nos)", ""),
    DZ("DZ", "dozen", "dz"),
    HD("HD", "1/2 dozen", "1/2 dz"),
    MG("MG", "mg", "mg"),
    GM("GM", "gm", "gm"),
    KG("KG", "kg", "kg"),
    ML("ML", "ml", "ml"),
    LT("LT", "lt", "lt"),

    CM("CM", "cm", "cm"),
    LA("LA", "Large", "L"),
    XL("XL", "Extra Large", "XL"),
    ME("ME", "Medium", "M"),
    SM("SM", "Small", "S"),
    KT("KT", "Kit", "kit");

    private final String name;
    private final String description;
    private final String friendlyDescription;

    UnitOfMeasurementEnum(String name, String description, String friendlyDescription) {
        this.name = name;
        this.description = description;
        this.friendlyDescription = friendlyDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    @Override
    public String toString() {
        return description;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (UnitOfMeasurementEnum unitOfMeasurementEnum : UnitOfMeasurementEnum.values()) {
            a.add(unitOfMeasurementEnum.description);
        }
        return a;
    }

    public static UnitOfMeasurementEnum getEnum(String description) {
        for (UnitOfMeasurementEnum unitOfMeasurementEnum : UnitOfMeasurementEnum.values()) {
            if (description.equals(unitOfMeasurementEnum.description)) {
                return unitOfMeasurementEnum;
            }
        }
        return UnitOfMeasurementEnum.CN;
    }
}
