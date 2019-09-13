package com.noqapp.android.merchant.views.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.ViewAllHistoryExpListAdapter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllHistoryActivity extends BaseActivity implements QueuePersonListPresenter, View.OnClickListener {
    private Map<Date, List<JsonQueuePersonList>> expandableListDetail = new HashMap<>();
    private ListView listview;
    private RelativeLayout rl_empty;
    private TextView tv_from_date, tv_until_date;
    private Spinner sp_queue_list, sp_filter_type;
    private ManageQueueApiCalls manageQueueApiCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_history);
        initActionsViews(false);
        tv_toolbar_title.setText("All History");
        listview = findViewById(R.id.exp_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        sp_queue_list = findViewById(R.id.sp_queue_list);
        sp_filter_type = findViewById(R.id.sp_filter_type);
        ArrayList<String> filterOptions = new ArrayList<>();
        filterOptions.add("Select Options");
        filterOptions.add("Work Done");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        filterOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_filter_type.setAdapter(spinnerArrayAdapter);
        Button btn_filter = findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);

        tv_from_date = findViewById(R.id.tv_from_date);
        tv_from_date.setOnClickListener(this);

        tv_until_date = findViewById(R.id.tv_until_date);
        tv_until_date.setOnClickListener(this);
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        List<JsonTopic> qList = (List<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic");
        JsonTopic jsonTopic = new JsonTopic();
        jsonTopic.setDisplayName("Select Queue");
        qList.add(0, jsonTopic);
        QueueAdapter adapter = new QueueAdapter(this, qList);
        sp_queue_list.setAdapter(adapter);
//        sp_queue_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    setProgressMessage("Fetching data...");
//                    if (LaunchActivity.getLaunchActivity().isOnline()) {
//                        JsonTopic jt = (JsonTopic) sp_queue_list.getSelectedItem();
//                        showProgress();
//                        CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup().
//                                setCodeQR(jt.getCodeQR()).setFrom(AppUtils.earlierDayAsDateFormat(7))
//                                .setUntil(AppUtils.todayAsDateFormat());
//                        manageQueueApiCalls.getAllQueuePersonListHistory(
//                                UserUtils.getDeviceId(), UserUtils.getEmail(),
//                                UserUtils.getAuth(), codeQRDateRangeLookup);
//                    } else {
//                        ShowAlertInformation.showNetworkDialog(AllHistoryActivity.this);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
        //       });
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        expandableListDetail.clear();
        if (null != jsonQueuePersonList) {
            Log.e("data", jsonQueuePersonList.toString());
            Log.e("data size", "" + jsonQueuePersonList.getQueuedPeople().size());
            if (jsonQueuePersonList.getQueuedPeople().size() == 0) {
                listview.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                createData(jsonQueuePersonList.getQueuedPeople());
                List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
                ViewAllHistoryExpListAdapter adapter = new ViewAllHistoryExpListAdapter(AllHistoryActivity.this, expandableListTitle, expandableListDetail);
                listview.setAdapter(adapter);
                if (expandableListTitle.size() <= 0) {
                    listview.setVisibility(View.GONE);
                    rl_empty.setVisibility(View.VISIBLE);
                } else {
                    listview.setVisibility(View.VISIBLE);
                    rl_empty.setVisibility(View.GONE);
                }
            }
        } else {
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }

    private void createData(List<JsonQueuedPerson> temp) {
        if (null != temp && temp.size() > 0) {
            HashMap<Date, List<JsonQueuePersonList>> tempList = new HashMap<>();
            for (int i = 0; i < temp.size(); i++) {
                try {
                    Date key = new Date(CommonHelper.SDF_YYYY_MM_DD.parse(temp.get(i).getCreated()).getTime());
                    if (null == tempList.get(key)) {
                        tempList.put(key, new ArrayList<>());
                        tempList.get(key).add(new JsonQueuePersonList());
                    }
                    tempList.get(key).get(0).getQueuedPeople().add(temp.get(i));
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
                                //.setFrom(AppUtils.earlierDayAsDateFormat(7))
                                // .setUntil(AppUtils.todayAsDateFormat());
                                .setFrom(tv_from_date.getText().toString())
                                .setUntil(tv_until_date.getText().toString());
                        manageQueueApiCalls.getAllQueuePersonListHistory(
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
}
