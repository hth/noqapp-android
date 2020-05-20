package com.noqapp.android.merchant.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.stats.HealthCareStat;
import com.noqapp.android.merchant.presenter.beans.stats.YearlyData;
import com.noqapp.android.merchant.utils.MyAxisValueFormatter;
import com.noqapp.android.merchant.utils.MyValueFormatter;
import com.noqapp.android.merchant.views.activities.LaunchActivity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChartFragment extends Fragment {
    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private LineChart lineChart;
    private ArrayList<String> mMonths = new ArrayList<>();
    private ArrayList<String> mMonths1 = new ArrayList<>();
    private String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private YAxis leftAxis, rightAxis;
    private CardView cv_earning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        horizontalBarChart = view.findViewById(R.id.bar_chart);
        lineChart = view.findViewById(R.id.line_chart);

        TextView tv1 = view.findViewById(R.id.tv1);
        tv1.setText(Html.fromHtml(tv1.getText().toString().replace("**", "<b>*</b>")));
        TextView tv2 = view.findViewById(R.id.tv2);
        tv2.setText(Html.fromHtml(tv2.getText().toString().replace("**", "<b>*</b>")));
        cv_earning = view.findViewById(R.id.cv_earning);
        pieChart = view.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setEntryLabelColor(Color.BLACK);
        // pieChart.setEntryLabelTextSize(16f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.getDescription().setEnabled(false);
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
        pieChart.getDescription().setTextSize(18f);

        horizontalBarChart.setDrawBarShadow(false);
        horizontalBarChart.setDrawValueAboveBar(true);
        horizontalBarChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        horizontalBarChart.setMaxVisibleValueCount(60);
        horizontalBarChart.setPinchZoom(false);
        horizontalBarChart.setScaleXEnabled(false);
        horizontalBarChart.setScaleYEnabled(false);
        horizontalBarChart.setDoubleTapToZoomEnabled(false);
        horizontalBarChart.setDrawGridBackground(false);
        // horizontalBarChart.setDrawYLabels(false);
        // To hide the small color cubes at bottom of screen
        horizontalBarChart.getLegend().setEnabled(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter();
        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(xAxisFormatter);
        ValueFormatter custom = new MyAxisValueFormatter();
        leftAxis = horizontalBarChart.getAxisLeft();
        // leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        rightAxis = horizontalBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        //rightAxis.setLabelCount(5, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = horizontalBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        horizontalBarChart.animateY(700);
        Bundle bundle = getArguments();
        if (null != bundle) {
            HealthCareStat healthCareStat = (HealthCareStat) bundle.getSerializable("healthCareStat");
            updateChart(healthCareStat);
        }
        return view;
    }

    private void setData(ArrayList<PieEntry> entries1, int month) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.addAll(entries1);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(12f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

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
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        mMonths.clear();
        // Sorted in reverse order
        Collections.sort(yearlyData, (Comparator) (o1, o2) -> {
            Integer x1 = ((YearlyData) o1).getYear();
            Integer x2 = ((YearlyData) o2).getYear();
            int sComp = x2.compareTo(x1);

            if (sComp != 0) {
                return sComp;
            }

            Integer x11 = ((YearlyData) o1).getYearMonth();
            Integer x22 = ((YearlyData) o2).getYearMonth();
            return x22.compareTo(x11);
        });

        for (int i = 0; i < cnt; i++) {
            yVals1.add(new BarEntry(1 + i, yearlyData.get(i).getValue()));
            mMonths.add(months[yearlyData.get(i).getYearMonth() - 1] + " - " + yearlyData.get(i).getYear());
        }

        rightAxis.setLabelCount(mMonths.size(), false);
        leftAxis.setLabelCount(mMonths.size(), false);

        BarDataSet set1;
        if (horizontalBarChart.getData() != null && horizontalBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) horizontalBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            horizontalBarChart.getData().notifyDataChanged();
            horizontalBarChart.notifyDataSetChanged();
            horizontalBarChart.invalidate();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setHighlightEnabled(false); // to hide  the click text on bar
            data.setValueTextSize(10f);
            data.setBarWidth(0.5f);
            data.setValueFormatter(new MyValueFormatter());
            horizontalBarChart.setData(data);
            horizontalBarChart.invalidate();
        }
    }

    public void updateChart(HealthCareStat healthCareStat) {
        if (null != healthCareStat) {
            int new_count = healthCareStat.getRepeatCustomers().getCustomerNew();
            int old_count = healthCareStat.getRepeatCustomers().getCustomerRepeat();
            String[] mParties = new String[]{"New", "Repeat"};
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            entries.add(new PieEntry((float) new_count, mParties[0]));
            entries.add(new PieEntry((float) old_count, mParties[1]));
            setData(entries, healthCareStat.getRepeatCustomers().getMonthOfYear());
            //set the bar data
            generateDataBar(healthCareStat.getTwelveMonths());
            switch (LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
                case DO:
                    updateLineChart(healthCareStat.getTwelveMonths(), healthCareStat.getProductPrice());
                    cv_earning.setVisibility(View.VISIBLE);
                    break;
                default:
                    cv_earning.setVisibility(View.GONE);
            }
        }
    }

    private void updateLineChart(List<YearlyData> yearlyData, int productPrice) {
        int cnt = yearlyData.size();
        ArrayList<Entry> yVals1 = new ArrayList<>();
        mMonths1.clear();
        // Sorted in reverse order
        Collections.sort(yearlyData, (Comparator) (o1, o2) -> {
            Integer x1 = ((YearlyData) o1).getYear();
            Integer x2 = ((YearlyData) o2).getYear();
            int sComp = x1.compareTo(x2);

            if (sComp != 0) {
                return sComp;
            }

            Integer x11 = ((YearlyData) o1).getYearMonth();
            Integer x22 = ((YearlyData) o2).getYearMonth();
            return x11.compareTo(x22);
        });

        for (int i = 0; i < cnt; i++) {
            BigDecimal price = new BigDecimal(productPrice).divide(new BigDecimal(100), MathContext.DECIMAL64);
            yVals1.add(new Entry(1 + i, yearlyData.get(i).getValue() * price.intValue()));
            mMonths1.add(months[yearlyData.get(i).getYearMonth() - 1] + " - " + yearlyData.get(i).getYear());
        }

        LineDataSet dataSet = new LineDataSet(yVals1, "");
        dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        dataSet.setDrawFilled(true);

        // Controlling X axis
        XAxis xAxis = lineChart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(mMonths1.size());
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                try {
                    int index = (int) value - 1;
                    return mMonths1.get(index); // minus 1 because array start with 0
                } catch (Exception e) {
                    return "";
                }
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        // Controlling right side of y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Controlling left side of y axis
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setData(data);
        lineChart.animateX(0);
        lineChart.invalidate();
    }

    public class DayAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            try {
                int index = (int) value - 1;
                return mMonths.get(index); // minus 1 because array start with 0
            } catch (Exception e) {
                return "";
            }
        }
    }
}
