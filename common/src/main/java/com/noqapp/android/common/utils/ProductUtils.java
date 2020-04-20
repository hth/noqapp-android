package com.noqapp.android.common.utils;

import java.math.BigDecimal;

public class ProductUtils {

    public static BigDecimal calculateDiscountPrice(String displayPrice, String discountAmount) {
        BigDecimal price = new BigDecimal(displayPrice);
        BigDecimal discountAmountValue = new BigDecimal(discountAmount);
        return price.subtract(discountAmountValue);
    }
}
