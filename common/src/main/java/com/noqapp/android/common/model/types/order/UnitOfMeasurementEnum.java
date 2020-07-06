package com.noqapp.android.common.model.types.order;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 4/1/18.
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count (nos)"),
    DZ("DZ", "dozen"),
    HD("HD", "1/2 dozen"),
    MG("MG", "mg"),
    GM("GM", "gm"),
    KG("KG", "kg"),
    ML("ML", "ml"),
    LT("LT", "lt"),

    CM("CM", "cm"),
    LA("LA", "Large"),
    XL("XL", "Extra Large"),
    ME("ME", "Medium"),
    SM("SM", "Small"),
    KT("KT", "Kit");

    private final String name;
    private final String description;

    UnitOfMeasurementEnum(String name, String description) {
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
