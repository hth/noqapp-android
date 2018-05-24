package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantStatsModel;
import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;
import com.noqapp.android.merchant.presenter.beans.stats.YearlyData;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.DayAxisValueFormatter;
import com.noqapp.android.merchant.utils.MyValueFormatter;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;

import java.util.ArrayList;
import java.util.List;


public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener, ChartPresenter {

    private FrameLayout fl_notification;
    private TextView tv_toolbar_title;
    private ImageView actionbarBack;
    private PieChart pieChart;
    private BarChart bar_chart;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initProgress();
        bar_chart = findViewById(R.id.bar_chart);
        fl_notification = (FrameLayout) findViewById(R.id.fl_notification);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Charts");
        pieChart = findViewById(R.id.pieChart);
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
        pieChart.setOnChartValueSelectedListener(this);


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

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            MerchantStatsModel.chartPresenter = this;
            MerchantStatsModel.doctor(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), "SN_5ad2022db85cb75a156d65b2");
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }


    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }


    private void setData(ArrayList<PieEntry> entries1) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        entries.addAll(entries1);

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

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
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(16f);
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

    @Override
    public void chartError() {
        dismissProgress();
    }

    @Override
    public void chartResponse(DoctorStats doctorStats) {
        Log.v("Chart data :", doctorStats.toString());
        if (null != doctorStats) {
            int new_count = doctorStats.getRepeatCustomers().getCustomerNew();
            int old_count = doctorStats.getRepeatCustomers().getCustomerRepeat();
            String[] mParties = new String[]{
                    "New patient", "Repeat patient"};
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            entries.add(new PieEntry((float) new_count, mParties[0]));
            entries.add(new PieEntry((float) old_count, mParties[1]));
            setData(entries);
            //set the bar data
            generateDataBar(doctorStats.getTwelveMonths());
        }
        dismissProgress();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
