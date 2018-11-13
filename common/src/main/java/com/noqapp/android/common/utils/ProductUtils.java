package com.noqapp.android.common.utils;

public class ProductUtils {

    public double calculateDiscountPrice(String displayPrice, String discountAmount) {
        double price = Double.valueOf(displayPrice);
        double discountAmountValue = Double.valueOf(discountAmount);
        return (price - discountAmountValue);
    }
}
