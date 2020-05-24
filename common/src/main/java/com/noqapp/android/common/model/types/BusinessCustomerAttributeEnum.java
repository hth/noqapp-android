package com.noqapp.android.common.model.types;

/**
 * hitender
 * 5/24/20 3:16 AM
 */
public enum BusinessCustomerAttributeEnum {
    CA("CA", "CSD Approved"),
    CR("CR", "CSD Reject"),
    CG("CG", "CSD Grocery"),
    CL("CL", "CSD Liquor");

    private final String description;
    private final String name;

    BusinessCustomerAttributeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return description;
    }
}
