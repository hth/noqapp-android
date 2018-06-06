package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.MerchantStatsModel;
import com.noqapp.android.merchant.model.types.QueueStatusEnum;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.presenter.beans.stats.DoctorStats;
import com.noqapp.android.merchant.presenter.beans.stats.YearlyData;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.DayAxisValueFormatter;
import com.noqapp.android.merchant.utils.MyValueFormatter;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.OutOfSequenceActivity;
import com.noqapp.android.merchant.views.activities.OutOfSequenceDialogActivity;
import com.noqapp.android.merchant.views.activities.SettingActivity;
import com.noqapp.android.merchant.views.activities.SettingDialogActivity;
import com.noqapp.android.merchant.views.fragments.MerchantViewPagerFragment;
import com.noqapp.android.merchant.views.interfaces.AdapterCallback;
import com.noqapp.android.merchant.views.interfaces.ChartPresenter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.common.beans.ErrorEncounteredJson;
import com.noqapp.common.utils.Formatter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: chandra
 * Date: 4/16/17 4:02 PM
 */
public class ChartViewPagerAdapter extends PagerAdapter implements OnChartValueSelectedListener, ChartPresenter {

    private Context context;
    private List<JsonTopic> topics;
    private LayoutInflater inflater;
   // private ProgressDialog progressDialog;
    private BarChart bar_chart;
    private PieChart pieChart;

    public ChartViewPagerAdapter(Context context, List<JsonTopic> topics) {
        this.context = context;
        this.topics = topics;
    }


    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final JsonTopic lq = topics.get(position);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_chart, container, false);
        TextView tv_queue_name = itemView.findViewById(R.id.tv_queue_name);
        tv_queue_name.setText(lq.getDisplayName());
       // initProgress();

        //PieChart pieChart;
       // BarChart bar_chart;
        String qrCode = lq.getCodeQR();
        bar_chart = itemView.findViewById(R.id.bar_chart);

        pieChart = itemView.findViewById(R.id.pieChart);
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
          //  progressDialog.show();
            MerchantStatsModel.chartPresenter = this;
            MerchantStatsModel.doctor(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), qrCode);
        } else {
            ShowAlertInformation.showNetworkDialog(context);
        }



        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((View) object);
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


    private void setData(ArrayList<PieEntry> entries1,PieChart pieChart) {

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


    private void generateDataBar(List<YearlyData> yearlyData,BarChart bar_chart) {
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
       // dismissProgress();
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
            setData(entries,pieChart);
            //set the bar data
            generateDataBar(doctorStats.getTwelveMonths(),bar_chart);
        }
      //  dismissProgress();
    }

//    private void initProgress() {
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Loading...");
//    }
//
//    private void dismissProgress() {
//        if (null != progressDialog && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
}
