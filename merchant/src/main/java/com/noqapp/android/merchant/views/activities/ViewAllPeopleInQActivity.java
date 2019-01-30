package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ViewAllExpandableListAdapter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ViewAllPeopleInQActivity extends AppCompatActivity implements QueuePersonListPresenter {

    private Map<Date, List<JsonQueuePersonList>> expandableListDetail = new HashMap<>();
    private ProgressDialog progressDialog;
    private ExpandableListView listview;
    private TextView tv_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        listview = findViewById(R.id.exp_list_view);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_empty = findViewById(R.id.tv_empty);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_queue_history));
        initProgress();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            ManageQueueModel manageQueueModel = new ManageQueueModel();
            manageQueueModel.setQueuePersonListPresenter(this);
            manageQueueModel.getAllQueuePersonListHistory(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), getIntent().getStringExtra("codeQR"));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            Log.e("data", jsonQueuePersonList.toString());
            Log.e("data size", "" + jsonQueuePersonList.getQueuedPeople().size());
            createData(jsonQueuePersonList.getQueuedPeople());
            List<Date> expandableListTitle = new ArrayList<Date>(expandableListDetail.keySet());
            ViewAllExpandableListAdapter adapter = new ViewAllExpandableListAdapter(ViewAllPeopleInQActivity.this, expandableListTitle, expandableListDetail,getIntent().getBooleanExtra("visibility",false));
            listview.setAdapter(adapter);
            if (expandableListTitle.size() <= 0) {
                listview.setVisibility(View.GONE);
                tv_empty.setVisibility(View.VISIBLE);
            } else {
                listview.setVisibility(View.VISIBLE);
                tv_empty.setVisibility(View.GONE);
            }

        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
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
