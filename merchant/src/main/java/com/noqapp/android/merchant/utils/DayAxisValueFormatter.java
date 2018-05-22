package com.noqapp.android.merchant.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class DayAxisValueFormatter implements IAxisValueFormatter {

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    public DayAxisValueFormatter() {

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        //Insert code here to return value from your custom array or based on some processing

        try {
            int index = (int) value;
            return mMonths[(int) value-1]; // minus 1 because array start with 0
        } catch (Exception e) {
            return "";
        }
    }


}
