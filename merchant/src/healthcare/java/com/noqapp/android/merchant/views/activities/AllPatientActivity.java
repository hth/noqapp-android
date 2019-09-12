package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.QueueAdapter;
import com.noqapp.android.merchant.views.adapters.ViewAllPatientExpListAdapter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllPatientActivity extends BaseActivity implements QueuePersonListPresenter {
    private Map<Date, List<JsonQueuePersonList>> expandableListDetail = new HashMap<>();
    private ExpandableListView listview;
    private RelativeLayout rl_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_patient);
        initActionsViews(false);
        tv_toolbar_title.setText("All Patient");
        listview = findViewById(R.id.exp_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        Spinner sp_queue_list = findViewById(R.id.sp_queue_list);
        ManageQueueApiCalls manageQueueApiCalls = new ManageQueueApiCalls();
        manageQueueApiCalls.setQueuePersonListPresenter(this);
        List<JsonTopic> qList = (List<JsonTopic>) getIntent().getExtras().getSerializable("jsonTopic");
        JsonTopic jsonTopic = new JsonTopic();
        jsonTopic.setDisplayName("Select Queue");
        qList.add(0, jsonTopic);
        QueueAdapter adapter = new QueueAdapter(this, qList);
        sp_queue_list.setAdapter(adapter);
        sp_queue_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    setProgressMessage("Fetching data...");
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        JsonTopic jt = (JsonTopic) sp_queue_list.getSelectedItem();
                        showProgress();
                        manageQueueApiCalls.getAllQueuePersonListHistory(
                                UserUtils.getDeviceId(),
                                UserUtils.getEmail(),
                                UserUtils.getAuth(),
                                jt.getCodeQR(),
                                new AppUtils().earlierDayAsDateFormat(7),
                                new AppUtils().todayAsDateFormat());
                    } else {
                        ShowAlertInformation.showNetworkDialog(AllPatientActivity.this);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                ViewAllPatientExpListAdapter adapter = new ViewAllPatientExpListAdapter(AllPatientActivity.this, expandableListTitle,
                        expandableListDetail);
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
}
