package com.noqapp.android.common.model.types.order;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 4/1/18.
 */
public enum ProductTypeEnum {
    GE("GE", "General"),
    OR("OR", "Organic Produce (Fruits/Vegetables)"),
    FR("FR", "Fresh Produce (Fruits/Vegetables)"),
    GM("GM", "GMO Produce (Fruits/Vegetables)"),
    VE("VE", "Vegetarian Food"),
    NV("NV", "Non-Vegetarian Food"),
    EL("EL", "Electronic"),
    PH("PH", "Pharmacy");

    private final String name;
    private final String description;

    ProductTypeEnum(String name, String description) {
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
        for(ProductTypeEnum productTypeEnum : ProductTypeEnum.values()) {
            a.add(productTypeEnum.description);
        }
        return a;
    }

    public static ProductTypeEnum getEnum(String description){
        for(ProductTypeEnum productTypeEnum : ProductTypeEnum.values()) {
            if(description.equals(productTypeEnum.description)) {
                return productTypeEnum;
            }
        }
        return ProductTypeEnum.GE;
    }
}
