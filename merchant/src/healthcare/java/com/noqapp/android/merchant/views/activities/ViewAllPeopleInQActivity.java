package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.CodeQRDateRangeLookup;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ViewAllExpandableListAdapter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ViewAllPeopleInQActivity extends BaseActivity implements QueuePersonListPresenter {

    private Map<Date, List<JsonQueuePersonList>> expandableListDetail = new HashMap<>();
    private ExpandableListView listview;
    private RelativeLayout rl_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_people);
        listview = findViewById(R.id.exp_list_view);
        rl_empty = findViewById(R.id.rl_empty);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.screen_queue_history));
        setProgressMessage("Fetching data...");
        if (new NetworkUtil(this).isOnline()) {
            showProgress();
            ManageQueueApiCalls manageQueueApiCalls = new ManageQueueApiCalls();
            manageQueueApiCalls.setQueuePersonListPresenter(this);
            CodeQRDateRangeLookup codeQRDateRangeLookup = new CodeQRDateRangeLookup().
                    setCodeQR(getIntent().getStringExtra("codeQR")).setFrom(AppUtils.earlierDayAsDateFormat(7))
                    .setUntil(AppUtils.todayAsDateFormat());
            manageQueueApiCalls.getAllQueuePersonListHistory(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                    codeQRDateRangeLookup);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            Log.e("data", jsonQueuePersonList.toString());
            Log.e("data size", "" + jsonQueuePersonList.getQueuedPeople().size());
            createData(jsonQueuePersonList.getQueuedPeople());
            List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
            ViewAllExpandableListAdapter adapter = new ViewAllExpandableListAdapter(ViewAllPeopleInQActivity.this, expandableListTitle,
                    expandableListDetail, getIntent().getBooleanExtra("visibility", false));
            listview.setAdapter(adapter);
            if (expandableListTitle.size() <= 0) {
                listview.setVisibility(View.GONE);
                rl_empty.setVisibility(View.VISIBLE);
            } else {
                listview.setVisibility(View.VISIBLE);
                rl_empty.setVisibility(View.GONE);
            }

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
