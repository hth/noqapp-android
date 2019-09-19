package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.WorkHistoryAdapter;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class ReportCaseHistoryActivity extends BaseActivity implements
        MedicalRecordListPresenter, View.OnClickListener {
    private LinkedHashMap<Date, List<JsonMedicalRecordList>> expandableListDetail = new LinkedHashMap<>();
    private RelativeLayout rl_empty;
    private TextView tv_from_date, tv_until_date;
    private Spinner sp_queue_list, sp_filter_type;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private final int PAGE_SIZE = 20;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private boolean isMoreToDownload = true;
    private RecyclerView rcv_work_history;
    private RelativeLayout bottomLayout;
    private WorkHistoryAdapter workHistoryAdapter;
    private ScrollView sv_filter;
    private Button btn_clear_filter;
    private SegmentedControl sc_month_from, sc_year_from, sc_month_until, sc_year_until;
    private ArrayList<String> monthList, yearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_history);
        initActionsViews(false);
        tv_toolbar_title.setText("My Work History");
        rl_empty = findViewById(R.id.rl_empty);
        sp_queue_list = findViewById(R.id.sp_queue_list);
        sp_filter_type = findViewById(R.id.sp_filter_type);
        bottomLayout = findViewById(R.id.loadItemsLayout_recyclerView);
        monthList = AppUtils.getMonths();
        yearList = AppUtils.getYearsTillNow();

        sc_month_from = findViewById(R.id.sc_month_from);
        sc_year_from = findViewById(R.id.sc_year_from);
        sc_month_until = findViewById(R.id.sc_month_until);
        sc_year_until = findViewById(R.id.sc_year_until);


        sc_month_from.addSegments(monthList);
        sc_year_from.addSegments(yearList);
        sc_month_until.addSegments(monthList);
        sc_year_until.addSegments(yearList);


        ImageView iv_filter = findViewById(R.id.iv_filter);
        iv_filter.setOnClickListener(this::onClick);

        sv_filter = findViewById(R.id.sv_filter);
        rcv_work_history = findViewById(R.id.rcv_work_history);
        rcv_work_history.setHasFixedSize(true);
        rcv_work_history.setLayoutManager(new LinearLayoutManager(this));
        rcv_work_history.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----", "end");
                    if (isMoreToDownload) {
                        callApi();
                    } else {
                        new CustomToast().showToast(ReportCaseHistoryActivity.this, "No more data to load");
                    }
                }
            }
        });
        ArrayList<String> filterOptions = new ArrayList<>();
        filterOptions.add("Select Options");
        filterOptions.addAll(MedicalRecordFieldFilterEnum.asListOfDescription());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_general, filterOptions);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_general);
        sp_filter_type.setAdapter(spinnerArrayAdapter);
        Button btn_filter = findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);
        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        btn_clear_filter = findViewById(R.id.btn_clear_filter);
        btn_clear_filter.setOnClickListener(this);

        tv_from_date = findViewById(R.id.tv_from_date);
        tv_from_date.setOnClickListener(this);

        tv_until_date = findViewById(R.id.tv_until_date);
        tv_until_date.setOnClickListener(this);
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        List<JsonTopic> qList = (List<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic");
        JsonTopic jsonTopic = new JsonTopic();
        jsonTopic.setDisplayName("Select Queue");
        qList.add(0, jsonTopic);
        QueueAdapter adapter = new QueueAdapter(this, qList);
        sp_queue_list.setAdapter(adapter);
    }

    private void createData(List<JsonMedicalRecord> temp) {
        if (null != temp && temp.size() > 0) {
            LinkedHashMap<Date, List<JsonMedicalRecordList>> tempList = new LinkedHashMap<>();
            for (int i = 0; i < temp.size(); i++) {
                try {
                    Date key = new Date(CommonHelper.SDF_YYYY_MM_DD.parse(temp.get(i).getCreateDate()).getTime());
                    if (null == tempList.get(key)) {
                        tempList.put(key, new ArrayList<>());
                        tempList.get(key).add(new JsonMedicalRecordList());
                    }
                    tempList.get(key).get(0).getJsonMedicalRecords().add(temp.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            expandableListDetail.putAll(tempList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                if (sc_month_from.getLastSelectedAbsolutePosition() == -1 || sc_year_from.getLastSelectedAbsolutePosition() == -1 ||
                        sc_month_until.getLastSelectedAbsolutePosition() == -1 || sc_year_until.getLastSelectedAbsolutePosition() == -1) {
                    tv_from_date.setText("");
                    tv_until_date.setText("");
                } else {
                    tv_from_date.setText(AppUtils.createAndParseDate(monthList.get(sc_month_from.getLastSelectedAbsolutePosition())
                            , yearList.get(sc_year_from.getLastSelectedAbsolutePosition())));
                    tv_until_date.setText(AppUtils.createAndParseDate(monthList.get(sc_month_until.getLastSelectedAbsolutePosition())
                            , yearList.get(sc_year_until.getLastSelectedAbsolutePosition())));
                }
                if (isValid()) {
                    callApi();
                    btn_clear_filter.setVisibility(View.VISIBLE);
                    sv_filter.setVisibility(View.GONE);
                    isMoreToDownload = true;
                    jsonMedicalRecords.clear();
                    expandableListDetail.clear();
                    if (null != workHistoryAdapter) {
                        workHistoryAdapter.resetData();
                        workHistoryAdapter = null;
                    }
                }
                break;
            case R.id.iv_filter: {
                sv_filter.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.iv_close: {
                sv_filter.setVisibility(View.GONE);
            }
            break;
            case R.id.btn_clear_filter: {
                sp_filter_type.setSelection(0);
                sp_queue_list.setSelection(0);
                tv_until_date.setText("");
                tv_from_date.setText("");
                btn_clear_filter.setVisibility(View.GONE);
            }
            break;
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(tv_from_date.getText().toString()) || TextUtils.isEmpty(tv_until_date.getText().toString())) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Both dates are required");
            return false;
        } else if (isEndDateNotAfterStartDate()) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Until Date should be after From Date");
            return false;
        } else if (sp_queue_list.getSelectedItemPosition() == 0) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Please select Queue");
            return false;
        } else if (sp_filter_type.getSelectedItemPosition() == 0) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Please select Filter Option");
            return false;
        }
        return isValid;
    }

    private void callApi() {
        bottomLayout.setVisibility(View.VISIBLE);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            JsonTopic jt = (JsonTopic) sp_queue_list.getSelectedItem();
            CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup()
                    .setCodeQR(jt.getCodeQR())
                    .setCurrentPosition(jsonMedicalRecords.size())
                    .setMedicalRecordFieldFilter(MedicalRecordFieldFilterEnum.byDescription((String) sp_filter_type.getSelectedItem()))
                    .setFrom(tv_from_date.getText().toString())
                    .setUntil(tv_until_date.getText().toString());
            medicalHistoryApiCalls.workHistory(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    codeQRDateRangeLookup);
        } else {
            ShowAlertInformation.showNetworkDialog(ReportCaseHistoryActivity.this);
        }

    }


    private boolean isEndDateNotAfterStartDate() {
        try {
            Date date1 = CommonHelper.SDF_YYYY_MM_DD.parse(tv_from_date.getText().toString());
            Date date2 = CommonHelper.SDF_YYYY_MM_DD.parse(tv_until_date.getText().toString());
            return date1.after(date2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        sv_filter.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        if (null != jsonMedicalRecordList) {
            Log.e("data", jsonMedicalRecordList.toString());
            jsonMedicalRecords.addAll(jsonMedicalRecordList.getJsonMedicalRecords());
            if (jsonMedicalRecordList.getJsonMedicalRecords().size() < PAGE_SIZE) {
                isMoreToDownload = false;
            } else {
                isMoreToDownload = true;
            }
            Log.e("data size", "" + jsonMedicalRecordList.getJsonMedicalRecords().size());
            if (jsonMedicalRecords.size() == 0) {
                rcv_work_history.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                createData(jsonMedicalRecords);
                List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
                if (null == workHistoryAdapter) {
                    workHistoryAdapter = new WorkHistoryAdapter(ReportCaseHistoryActivity.this,
                            expandableListTitle, expandableListDetail, MedicalRecordFieldFilterEnum.byDescription((String) sp_filter_type.getSelectedItem()));
                    rcv_work_history.setAdapter(workHistoryAdapter);
                } else {
                    workHistoryAdapter.notifyDataSetChanged();
                }
                if (expandableListTitle.size() <= 0) {
                    rcv_work_history.setVisibility(View.GONE);
                    rl_empty.setVisibility(View.VISIBLE);
                } else {
                    rcv_work_history.setVisibility(View.VISIBLE);
                    rl_empty.setVisibility(View.GONE);
                }
            }
        } else {
            rcv_work_history.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        }
        dismissProgress();
    }

    @Override
    public void medicalRecordDentalListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        dismissProgress();
    }

    @Override
    public void medicalRecordListError() {
        dismissProgress();
    }

}
