package com.noqapp.android.merchant.views.fragments;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStat;
import com.noqapp.android.merchant.presenter.beans.stats.YearlyData;
import com.noqapp.android.merchant.utils.DayAxisValueFormatter;
import com.noqapp.android.merchant.utils.MyValueFormatter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ChartFragment extends Fragment {


    private PieChart pieChart;
    private BarChart bar_chart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        bar_chart = view.findViewById(R.id.bar_chart);
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setEntryLabelColor(Color.BLACK);
        // pieChart.setEntryLabelTextSize(16f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        //  pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawHoleEnabled(false);
        //pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
       // pieChart.setOnChartValueSelectedListener(this);
        bar_chart.getDescription().setEnabled(false);
        bar_chart.setDrawGridBackground(false);
        bar_chart.setDrawBarShadow(false);
        bar_chart.setDoubleTapToZoomEnabled(false);
        bar_chart.setPinchZoom(false);
        bar_chart.setFitBars(true);
        // to disable the pnch & zoom vertical/horizontal
        // bar_chart.setScaleEnabled(false);
        //bar_chart.setHighlightPerTapEnabled(false);
        bar_chart.setTouchEnabled(false);
        XAxis xAxis = bar_chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new DayAxisValueFormatter());

        YAxis leftAxis = bar_chart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(20f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = bar_chart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(20f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // set data
        //  bar_chart.setData((BarData) generateDataBar(12));
        bar_chart.setFitBars(true);

        // do not forget to refresh the chart
//        bar_chart.invalidate();
        bar_chart.animateY(700);
        Bundle bundle = getArguments();
        if (null != bundle) {
            HealthCareStat healthCareStat = (HealthCareStat) bundle.getSerializable("healthCareStat");
            updateChart(healthCareStat);
        }
        return view;
    }


    private void setData(ArrayList<PieEntry> entries1) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        entries.addAll(entries1);

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(12f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }


    private void generateDataBar(List<YearlyData> yearlyData) {
        int cnt = yearlyData.size();
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < cnt; i++) {
            entries.add(new BarEntry(yearlyData.get(i).getYearMonth(), yearlyData.get(i).getValue()));
        }
        BarDataSet d = new BarDataSet(entries, "");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);
        BarData cd = new BarData(d);
        d.setValueFormatter(new MyValueFormatter());
        cd.setBarWidth(0.9f);
        bar_chart.setData(cd);
        bar_chart.invalidate();
    }

    public void updateChart(HealthCareStat healthCareStat) {
        if (null != healthCareStat) {
            int new_count = healthCareStat.getRepeatCustomers().getCustomerNew();
            int old_count = healthCareStat.getRepeatCustomers().getCustomerRepeat();
            String[] mParties = new String[]{
                    "New patient", "Repeat patient"};
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            entries.add(new PieEntry((float) new_count, mParties[0]));
            entries.add(new PieEntry((float) old_count, mParties[1]));
            setData(entries);
            //set the bar data
            generateDataBar(healthCareStat.getTwelveMonths());
        }
    }
}
