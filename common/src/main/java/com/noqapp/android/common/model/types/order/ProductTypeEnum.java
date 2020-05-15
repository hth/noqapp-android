package com.noqapp.android.common.model.types.order;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 4/1/18.
 */
public enum ProductTypeEnum {
    GE("GE", "General"),
    OR("OR", "Organic Produce"),
    FR("FR", "Fresh Produce"),
    GM("GM", "GMO Produce"),
    VE("VE", "Vegetarian Food"),
    NV("NV", "Non-Vegetarian Food"),
    EL("EL", "Electronic"),
    PH("PH", "Pharmacy"),
    HS("HS", "Health Care Services");
    private static final String TAG = ProductTypeEnum.class.getSimpleName();

    public static EnumSet<ProductTypeEnum> PHARMACY = EnumSet.of(PH);
    public static ProductTypeEnum[] PHARMACY_VALUES = {PH};
    public static EnumSet<ProductTypeEnum> HEALTH_CARE = EnumSet.of(HS);
    public static ProductTypeEnum[] HEALTH_CARE_VALUES = {HS};
    public static EnumSet<ProductTypeEnum> GROCERY = EnumSet.of(GE, OR, FR);
    public static ProductTypeEnum[] GROCERY_VALUES = {GE, OR, FR};
    public static EnumSet<ProductTypeEnum> RESTAURANT = EnumSet.of(VE, NV);
    public static ProductTypeEnum[] RESTAURANT_VALUES = {VE, NV};

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

    public static List<String> asListOfDescription(ProductTypeEnum[] productTypeEnums) {
        List<String> a = new LinkedList<>();
        for (ProductTypeEnum productTypeEnum : productTypeEnums) {
            a.add(productTypeEnum.description);
        }
        return a;
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (ProductTypeEnum productTypeEnum : ProductTypeEnum.values()) {
            a.add(productTypeEnum.description);
        }
        return a;
    }

    public static ProductTypeEnum getEnum(String description) {
        for (ProductTypeEnum productTypeEnum : ProductTypeEnum.values()) {
            if (description.equals(productTypeEnum.description)) {
                return productTypeEnum;
            }
        }
        return ProductTypeEnum.GE;
    }


    public static List<String> populateWithProductType(BusinessTypeEnum businessType) {
        switch (businessType) {
            case GS:
                return ProductTypeEnum.asListOfDescription(ProductTypeEnum.GROCERY_VALUES);
            case RS:
                return ProductTypeEnum.asListOfDescription(ProductTypeEnum.RESTAURANT_VALUES);
            case PH:
                return ProductTypeEnum.asListOfDescription(ProductTypeEnum.PHARMACY_VALUES);
            case HS:
                return ProductTypeEnum.asListOfDescription(ProductTypeEnum.HEALTH_CARE_VALUES);
            default:
                FirebaseCrashlytics.getInstance().log("Failed get productTypeEnum for businessType " + businessType);
                Log.e(TAG, "Reached un-supported condition" + businessType);
                return ProductTypeEnum.asListOfDescription();
        }
    }
}
