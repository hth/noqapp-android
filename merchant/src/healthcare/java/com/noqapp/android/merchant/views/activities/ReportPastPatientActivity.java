package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.AllPatientHistoryExpListAdapter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class ReportPastPatientActivity extends BaseActivity implements QueuePersonListPresenter, View.OnClickListener {
    private Map<Date, List<JsonQueuePersonList>> expandableListDetail = new HashMap<>();
    private ExpandableListView listview;
    private RelativeLayout rl_empty;
    private JsonTopic jt;
    private ScrollView sv_filter;
    private Button btn_clear_filter;
    private Spinner sp_from_year, sp_from_month, sp_until_year, sp_until_month;
    private ArrayList<String> monthList, yearList;
    private TextView tv_from_date, tv_until_date;
    private Spinner sp_queue_list;
    private ManageQueueApiCalls manageQueueApiCalls;
    private AllPatientHistoryExpListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_patient);
        initActionsViews(false);
        tv_toolbar_title.setText("List of Patients");
        listview = findViewById(R.id.exp_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        monthList = AppUtils.getMonths();
        yearList = AppUtils.getYearsTillNow();
        tv_from_date = findViewById(R.id.tv_from_date);
        tv_until_date = findViewById(R.id.tv_until_date);

        Button btn_filter = findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(this);
        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        btn_clear_filter = findViewById(R.id.btn_clear_filter);
        btn_clear_filter.setOnClickListener(this);
        sp_from_year = findViewById(R.id.sp_from_year);
        sp_from_month = findViewById(R.id.sp_from_month);
        sp_until_year = findViewById(R.id.sp_until_year);
        sp_until_month = findViewById(R.id.sp_until_month);
        sv_filter = findViewById(R.id.sv_filter);

        ArrayAdapter<String> m_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item ,monthList);
        ArrayAdapter<String> y_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item ,yearList);

        sp_from_year.setAdapter(y_adapter);
        sp_until_year.setAdapter(y_adapter);
        sp_from_month.setAdapter(m_adapter);
        sp_until_month.setAdapter(m_adapter);
        ImageView iv_filter = findViewById(R.id.iv_filter);
        iv_filter.setOnClickListener(this::onClick);

        sp_queue_list = findViewById(R.id.sp_queue_list);
        manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        List<JsonTopic> qList = (List<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic");
        JsonTopic jsonTopic = new JsonTopic();
        jsonTopic.setDisplayName("Select Queue");
        qList.add(0, jsonTopic);
        QueueAdapter adapter = new QueueAdapter(this, qList);
        sp_queue_list.setAdapter(adapter);
        btn_clear_filter.performClick();
        if (qList.size() == 2) {
            sp_queue_list.setSelection(1);
        }

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
                if (null == adapter) {
                    adapter = new AllPatientHistoryExpListAdapter(ReportPastPatientActivity.this, expandableListTitle, expandableListDetail, jt);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
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
                        tempList.put(key, new ArrayList<JsonQueuePersonList>());
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
                if (sp_until_month.getSelectedItemPosition() == 0 || sp_until_year.getSelectedItemPosition() == 0  ||
                        sp_from_month.getSelectedItemPosition() == 0  || sp_from_year.getSelectedItemPosition() == 0 ) {
                    tv_from_date.setText("");
                    tv_until_date.setText("");
                } else {
                    tv_from_date.setText(AppUtils.createAndParseDate(monthList.get(sp_from_month.getSelectedItemPosition())
                            , yearList.get(sp_from_year.getSelectedItemPosition())));
                    tv_until_date.setText(AppUtils.createAndParseDate(monthList.get(sp_until_month.getSelectedItemPosition())
                            , yearList.get(sp_until_year.getSelectedItemPosition())));
                }
                if (isValid()) {
                    callApi();
                    btn_clear_filter.setVisibility(View.VISIBLE);
                    sv_filter.setVisibility(View.GONE);
                    expandableListDetail.clear();
                    if (null != adapter) {
                        adapter.resetData();
                        adapter = null;
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
                sp_queue_list.setSelection(0);
                tv_until_date.setText("");
                tv_from_date.setText("");
                btn_clear_filter.setVisibility(View.GONE);
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH)+1;
                sp_until_year.setSelection(yearList.indexOf(String.valueOf(year)));
                sp_until_month.setSelection(month);
                sp_from_year.setSelection(0);
                sp_from_month.setSelection(0);
            }
            break;
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(tv_from_date.getText().toString()) || TextUtils.isEmpty(tv_until_date.getText().toString())) {
            new CustomToast().showToast(ReportPastPatientActivity.this, "Both dates are required");
            return false;
        } else if (isEndDateNotAfterStartDate()) {
            new CustomToast().showToast(ReportPastPatientActivity.this, "Until Date should be after From Date");
            return false;
        } else if (sp_queue_list.getSelectedItemPosition() == 0) {
            new CustomToast().showToast(ReportPastPatientActivity.this, "Please select Queue");
            return false;
        }
        return isValid;
    }

    private void callApi() {
        setProgressMessage("Fetching data...");
        if (new NetworkUtil(this).isOnline()) {
            jt = (JsonTopic) sp_queue_list.getSelectedItem();
            showProgress();
            CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup()
                    .setCodeQR(jt.getCodeQR())
                  //  .setFrom(AppUtils.earlierDayAsDateFormat(7))
                  //  .setUntil(AppUtils.todayAsDateFormat())
                    .setFrom(tv_from_date.getText().toString())
                    .setUntil(tv_until_date.getText().toString());
            manageQueueApiCalls.getAllQueuePersonListHistory(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth(),
                    codeQRDateRangeLookup);
        } else {
            ShowAlertInformation.showNetworkDialog(ReportPastPatientActivity.this);
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
}
