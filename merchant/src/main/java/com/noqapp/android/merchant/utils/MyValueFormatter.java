package com.noqapp.android.merchant.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;


public class MyValueFormatter extends ValueFormatter {

    public MyValueFormatter() {

    }

    @Override
    public String getFormattedValue(float value) {
        // write your logic here
        return String.valueOf((int)value) ;
    }
}