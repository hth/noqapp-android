package com.noqapp.android.merchant.views.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.ViewAllHistoryExpListAdapter;
import com.noqapp.android.merchant.views.customviews.FixedHeightListView;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllHistoryActivity extends BaseActivity implements MedicalRecordListPresenter, View.OnClickListener {
    private Map<Date, List<JsonMedicalRecordList>> expandableListDetail = new HashMap<>();
    private FixedHeightListView listview;
    private RelativeLayout rl_empty;
    private TextView tv_from_date, tv_until_date;
    private Spinner sp_queue_list, sp_filter_type;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private ScrollView scroll_view;
    private Button btn_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_history);
        initActionsViews(false);
        tv_toolbar_title.setText("My Work History");
        listview = findViewById(R.id.fh_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        scroll_view = findViewById(R.id.scroll_view);
        sp_queue_list = findViewById(R.id.sp_queue_list);
        sp_filter_type = findViewById(R.id.sp_filter_type);
        ArrayList<String> filterOptions = new ArrayList<>();
        filterOptions.add("Select Options");
        filterOptions.add("Work Done");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
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
                if (TextUtils.isEmpty(tv_from_date.getText().toString()) || TextUtils.isEmpty(tv_until_date.getText().toString())) {
                    new CustomToast().showToast(AllHistoryActivity.this, "Both dates are required");
                } else if (isEndDateNotAfterStartDate()) {
                    new CustomToast().showToast(AllHistoryActivity.this, "Until Date should be after From Date");
                } else if (sp_queue_list.getSelectedItemPosition() == 0) {
                    new CustomToast().showToast(AllHistoryActivity.this, "Please select Queue");
                } else if (sp_filter_type.getSelectedItemPosition() == 0) {
                    new CustomToast().showToast(AllHistoryActivity.this, "Please select Filter Option");
                } else {
                    setProgressMessage("Fetching data...");
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        JsonTopic jt = (JsonTopic) sp_queue_list.getSelectedItem();
                        showProgress();
                        CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup().
                                setCodeQR(jt.getCodeQR())
                                .setPopulateField((String)sp_filter_type.getSelectedItem())
                                .setFrom(tv_from_date.getText().toString())
                                .setUntil(tv_until_date.getText().toString());
                        medicalHistoryApiCalls.workHistory(
                                UserUtils.getDeviceId(), UserUtils.getEmail(),
                                UserUtils.getAuth(), codeQRDateRangeLookup);
                    } else {
                        ShowAlertInformation.showNetworkDialog(AllHistoryActivity.this);
                    }
                }
                break;
            case R.id.tv_from_date:
                openDatePicker(tv_from_date);
                break;
            case R.id.tv_until_date:
                openDatePicker(tv_until_date);
                break;
        }

    }

    private void openDatePicker(final TextView tv) {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            Date current = newDate.getTime();
            int date_diff = new Date().compareTo(current);

            if (date_diff > 0) {
                tv.setText(CommonHelper.SDF_YYYY_MM_DD.format(newDate.getTime()));
            } else {
                new CustomToast().showToast(AllHistoryActivity.this, "Future date not allowed");
                tv.setText("");
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
        if (null != jsonMedicalRecordList) {
            Log.e("data", jsonMedicalRecordList.toString());
            Log.e("data size", "" + jsonMedicalRecordList.getJsonMedicalRecords().size());
            if (jsonMedicalRecordList.getJsonMedicalRecords().size() == 0) {
                listview.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                createData(jsonMedicalRecordList.getJsonMedicalRecords());
                List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
                ViewAllHistoryExpListAdapter adapter = new ViewAllHistoryExpListAdapter(AllHistoryActivity.this, expandableListTitle, expandableListDetail);
                listview.setAdapter(adapter);
                if (expandableListTitle.size() <= 0) {
                    listview.setVisibility(View.GONE);
                    rl_empty.setVisibility(View.VISIBLE);
                } else {
                    listview.setVisibility(View.VISIBLE);
                    rl_empty.setVisibility(View.GONE);
                    scroll_view.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.scrollTo(0, btn_filter.getBottom());
                        }
                    });
                }
            }
        } else {
            listview.setVisibility(View.GONE);
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
