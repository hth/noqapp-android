package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.ViewAllHistoryExpListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportCaseHistoryActivity extends BaseActivity implements
        MedicalRecordListPresenter, View.OnClickListener {
    private Map<Date, List<JsonMedicalRecordList>> expandableListDetail = new HashMap<>();
    private FixedHeightListView fh_list_view;
    private RelativeLayout rl_empty;
    private TextView tv_from_date, tv_until_date;
    private Spinner sp_queue_list, sp_filter_type;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private ScrollView scroll_view;
    private Button btn_filter;
    private final int RC_DATE_PICKER_FROM = 11;
    private final int RC_DATE_PICKER_UNTIL = 12;
    private LinearLayout ll_paging;
    private final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_history);
        initActionsViews(false);
        tv_toolbar_title.setText("My Work History");
        fh_list_view = findViewById(R.id.fh_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        ll_paging = findViewById(R.id.ll_paging);
        scroll_view = findViewById(R.id.scroll_view);
        sp_queue_list = findViewById(R.id.sp_queue_list);
        sp_filter_type = findViewById(R.id.sp_filter_type);
        ArrayList<String> filterOptions = new ArrayList<>();
        filterOptions.add("Select Options");
        filterOptions.addAll(MedicalRecordFieldFilterEnum.asListOfDescription());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_general, filterOptions);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_general);
        sp_filter_type.setAdapter(spinnerArrayAdapter);
        btn_filter = findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);

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

        TextView tv_page_1 = findViewById(R.id.tv_page_1);
        TextView tv_page_2 = findViewById(R.id.tv_page_2);
        TextView tv_page_3 = findViewById(R.id.tv_page_3);
        TextView tv_page_4 = findViewById(R.id.tv_page_4);
        TextView tv_page_5 = findViewById(R.id.tv_page_5);
        tv_page_1.setOnClickListener(this::onClick);
        tv_page_2.setOnClickListener(this::onClick);
        tv_page_3.setOnClickListener(this::onClick);
        tv_page_4.setOnClickListener(this::onClick);
        tv_page_5.setOnClickListener(this::onClick);
        ll_paging.setVisibility(View.GONE);

    }

    private void createData(List<JsonMedicalRecord> temp) {
        if (null != temp && temp.size() > 0) {
            HashMap<Date, List<JsonMedicalRecordList>> tempList = new HashMap<>();
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
            expandableListDetail = new TreeMap(tempList).descendingMap();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                callApi(1);
                break;
            case R.id.tv_from_date: {
                Intent in = new Intent(this, DatePickerActivity.class);
                startActivityForResult(in, RC_DATE_PICKER_FROM);
            }
            break;
            case R.id.tv_until_date: {
                Intent in = new Intent(this, DatePickerActivity.class);
                startActivityForResult(in, RC_DATE_PICKER_UNTIL);
            }
            break;
            case R.id.tv_page_1:
            case R.id.tv_page_2:
            case R.id.tv_page_3:
            case R.id.tv_page_4:
            case R.id.tv_page_5:
                new CustomToast().showToast(ReportCaseHistoryActivity.this, ((TextView) v).getText().toString() + " Clicked");
                callApi(Integer.parseInt((String) v.getTag()));
                break;
        }

    }

    private void callApi(int page) {
        if (TextUtils.isEmpty(tv_from_date.getText().toString()) || TextUtils.isEmpty(tv_until_date.getText().toString())) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Both dates are required");
        } else if (isEndDateNotAfterStartDate()) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Until Date should be after From Date");
        } else if (sp_queue_list.getSelectedItemPosition() == 0) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Please select Queue");
        } else if (sp_filter_type.getSelectedItemPosition() == 0) {
            new CustomToast().showToast(ReportCaseHistoryActivity.this, "Please select Filter Option");
        } else {
            setProgressMessage("Fetching data...");
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                JsonTopic jt = (JsonTopic) sp_queue_list.getSelectedItem();
                showProgress();
                int currentDataCount = page * PAGE_SIZE;
                CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup()
                        .setCodeQR(jt.getCodeQR())
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_DATE_PICKER_UNTIL && resultCode == Activity.RESULT_OK) {
            String date = data.getStringExtra("result");
            if (!TextUtils.isEmpty(date) && isDateBeforeToday(date))
                tv_until_date.setText(CommonHelper.convertDOBToValidFormat(date));
        } else if (requestCode == RC_DATE_PICKER_FROM && resultCode == Activity.RESULT_OK) {
            String date = data.getStringExtra("result");
            if (!TextUtils.isEmpty(date) && isDateBeforeToday(date))
                tv_from_date.setText(CommonHelper.convertDOBToValidFormat(date));
        }
    }

    private boolean isDateBeforeToday(String selectedDay) {
        try {
            Date selectedDate = CommonHelper.SDF_DOB_FROM_UI.parse(selectedDay);
            int date_diff = new Date().compareTo(selectedDate);
            if (date_diff >= 0) {
                return true;
            } else {
                new CustomToast().showToast(ReportCaseHistoryActivity.this, "Future date not allowed");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
        expandableListDetail.clear();
        ll_paging.setVisibility(View.VISIBLE);
        if (null != jsonMedicalRecordList) {
            Log.e("data", jsonMedicalRecordList.toString());
            Log.e("data size", "" + jsonMedicalRecordList.getJsonMedicalRecords().size());
            if (jsonMedicalRecordList.getJsonMedicalRecords().size() == 0) {
                fh_list_view.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                createData(jsonMedicalRecordList.getJsonMedicalRecords());
                List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
                ViewAllHistoryExpListAdapter adapter = new ViewAllHistoryExpListAdapter(ReportCaseHistoryActivity.this,
                        expandableListTitle, expandableListDetail, MedicalRecordFieldFilterEnum.byDescription((String) sp_filter_type.getSelectedItem()));
                fh_list_view.setAdapter(adapter);
                if (expandableListTitle.size() <= 0) {
                    fh_list_view.setVisibility(View.GONE);
                    rl_empty.setVisibility(View.VISIBLE);
                } else {
                    fh_list_view.setVisibility(View.VISIBLE);
                    rl_empty.setVisibility(View.GONE);
                    scroll_view.post(() -> scroll_view.scrollTo(0, btn_filter.getBottom()));
                }
            }
        } else {
            fh_list_view.setVisibility(View.GONE);
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
