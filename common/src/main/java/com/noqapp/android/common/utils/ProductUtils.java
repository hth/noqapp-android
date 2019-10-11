package com.noqapp.android.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductUtils {

    public static double calculateDiscountPrice(String displayPrice, String discountAmount) {
        double price = Double.valueOf(displayPrice);
        double discountAmountValue = Double.valueOf(discountAmount);
        return roundOff(price - discountAmountValue);
    }


    public static double roundOff(double amount){
        return new BigDecimal(String.valueOf(amount)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
