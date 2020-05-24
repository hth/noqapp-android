package com.noqapp.android.common.model.types.category;

import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;

import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.CG;
import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.CL;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    GA("GA", "Grocery Serving", CG),
    GR("GR", "Grocery Ex-Servicemen", CG),
    GO("GO", "Grocery Officers", CG),
    LA("LA", "Liquor Serving", CL),
    LR("LR", "Liquor Ex-Servicemen", CL),
    LO("LO", "Liquor Officers", CL);

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