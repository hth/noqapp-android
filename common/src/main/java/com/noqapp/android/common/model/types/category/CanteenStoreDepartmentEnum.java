package com.noqapp.android.common.model.types.category;

import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;

import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.GR;
import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.LQ;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    SG("SG", "Serving Grocery", GR),
    EG("EG", "Ex-Servicemen Grocery", GR),
    OG("OG", "Officers Grocery", GR),
    SL("SL", "Serving Liquor", LQ),
    EL("EL", "Ex-Servicemen Liquor", LQ),
    OL("OL", "Officers Liquor", LQ);

    private final String description;
    private final String name;
    private final BusinessCustomerAttributeEnum businessCustomerAttribute;

    CanteenStoreDepartmentEnum(String name, String description, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        this.name = name;
        this.description = description;
        this.businessCustomerAttribute = businessCustomerAttribute;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public BusinessCustomerAttributeEnum getBusinessCustomerAttribute() {
        return businessCustomerAttribute;
    }

    @Override
    public String toString() {
        return description;
    }
}