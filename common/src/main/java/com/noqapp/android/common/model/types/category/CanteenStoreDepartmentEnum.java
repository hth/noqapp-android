package com.noqapp.android.common.model.types.category;

import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;

import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.GY;
import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.LQ;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    GA("GA", "Grocery Serving", GY),
    GR("GR", "Grocery Ex-Servicemen", GY),
    GO("GO", "Grocery Officers", GY),
    LA("LA", "Liquor Serving", LQ),
    LR("LR", "Liquor Ex-Servicemen", LQ),
    LO("LO", "Liquor Officers", LQ);

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