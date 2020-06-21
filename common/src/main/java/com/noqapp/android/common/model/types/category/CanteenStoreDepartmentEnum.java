package com.noqapp.android.common.model.types.category;

import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;

import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.GR;
import static com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum.LQ;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    EG("EG", "Ex-Servicemen (Grocery)", GR),
    XG("XG", "Officer Retired (Grocery)", GR),
    SG("SG", "Serving (Grocery)", GR),
    OG("OG", "Officer Serving (Grocery)", GR),

    EL("EL", "Ex-Servicemen (Liquor)", LQ),
    XL("XL", "Officer Retired (Liquor)", LQ),
    SL("SL", "Serving (Liquor)", LQ),
    OL("OL", "Officer Serving (Liquor)", LQ);

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

    public static BusinessCustomerAttributeEnum getBusinessCustomerAttribute(String name) {
        switch (name) {
            case "EG":
            case "XG":
            case "SG":
            case "OG":
                return GR;
            case "EL":
            case "XL":
            case "SL":
            case "OL":
                return LQ;
        }
        return GR;
    }

    @Override
    public String toString() {
        return description;
    }
}