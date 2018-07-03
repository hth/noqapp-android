package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.ManageQueueModel;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;
import com.noqapp.android.merchant.presenter.beans.JsonQueuePersonList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.presenter.beans.body.Served;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.OutOfSequenceListAdapter;
import com.noqapp.android.merchant.views.interfaces.ManageQueuePresenter;
import com.noqapp.android.merchant.views.interfaces.QueuePersonListPresenter;
import com.noqapp.common.beans.ErrorEncounteredJson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OutOfSequenceActivity extends AppCompatActivity implements QueuePersonListPresenter, ManageQueuePresenter {

    private ProgressDialog progressDialog;
    private TextView tv_toolbar_title;
    protected ImageView actionbarBack;
    private TextView tv_title;
    private String codeQR;
    protected boolean isDialog = false;

    private OutOfSequenceListAdapter adapter;
    private List<JsonQueuedPerson> jsonQueuedPersonArrayList;
    private ListView listview;
    private Context context;
    private Served served;
    private int lastSelectedPos = -1;
    private boolean queueStatus = false;
    private ManageQueueModel manageQueueModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        manageQueueModel = new ManageQueueModel(this);
        ManageQueueModel.manageQueuePresenter = this;
        context = this;
        setContentView(R.layout.activity_outofsequence);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.60);
            int height = (int) (metrics.heightPixels * 0.60);
            getWindow().setLayout(screenWidth, height);
        }
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        initProgress();
        tv_title = (TextView) findViewById(R.id.tv_title);
        listview = (ListView) findViewById(R.id.listview);
        String title = getIntent().getStringExtra("title");
        codeQR = getIntent().getStringExtra("codeQR");
        queueStatus = getIntent().getBooleanExtra("queueStatus",false);
        served = (Served) getIntent().getSerializableExtra("data");
        if (null != title) {
            tv_title.setText(title);
        }
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_out_of_sequence));
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            manageQueueModel.getAllQueuePersonList(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(OutOfSequenceActivity.this);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void queuePersonListResponse(JsonQueuePersonList jsonQueuePersonList) {
        if (null != jsonQueuePersonList) {
            jsonQueuedPersonArrayList = jsonQueuePersonList.getQueuedPeople();
           // Collections.reverse(jsonQueuedPersonArrayList);
            Collections.sort(
                    jsonQueuedPersonArrayList,
                    new Comparator<JsonQueuedPerson>()
                    {
                        public int compare(JsonQueuedPerson lhs, JsonQueuedPerson rhs)
                        {
                            int returnVal = 0;

                            if(lhs.getToken() < rhs.getToken()){
                                returnVal =  -1;
                            }else if(lhs.getToken() < rhs.getToken()){
                                returnVal =  1;
                            }else if(lhs.getToken() < rhs.getToken()){
                                returnVal =  0;
                            }
                            return returnVal;
                        }
                    }
            );

            adapter = new OutOfSequenceListAdapter(context, jsonQueuedPersonArrayList);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (queueStatus) {
                        if (jsonQueuedPersonArrayList.get(position).getQueueUserState() == QueueUserStateEnum.A) {
                            Toast.makeText(context, getString(R.string.error_client_left_queue), Toast.LENGTH_LONG).show();
                        } else {
                            if (TextUtils.isEmpty(jsonQueuedPersonArrayList.get(position).getServerDeviceId())) {
                                if (LaunchActivity.getLaunchActivity().isOnline()) {
                                    progressDialog.show();
                                    lastSelectedPos = position;
                                    served.setServedNumber(jsonQueuedPersonArrayList.get(position).getToken());
                                    ManageQueueModel.acquire(
                                            LaunchActivity.getLaunchActivity().getDeviceID(),
                                            LaunchActivity.getLaunchActivity().getEmail(),
                                            LaunchActivity.getLaunchActivity().getAuth(),
                                            served);
                                } else {
                                    ShowAlertInformation.showNetworkDialog(OutOfSequenceActivity.this);
                                }
                            } else if (jsonQueuedPersonArrayList.get(position).getServerDeviceId().equals(UserUtils.getDeviceId())) {
                                Toast.makeText(context, getString(R.string.error_client_acquired_by_you), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, getString(R.string.error_client_acquired), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        ShowAlertInformation.showThemeDialog(context, "Error", "Please start the queue to avail this facility");
                    }
                }
            });
        }
        dismissProgress();
    }

    @Override
    public void queuePersonListError() {
        dismissProgress();
    }

    @Override
    public void manageQueueResponse(JsonToken token) {
        if (null != token) {
            Intent intent = new Intent();
            intent.putExtra(Constants.CUSTOMER_ACQUIRE, true);
            intent.putExtra("data", token);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
        dismissProgress();
    }

    @Override
    public void manageQueueError(ErrorEncounteredJson errorEncounteredJson) {
        if (null != errorEncounteredJson && errorEncounteredJson.getSystemErrorCode().equals("350")) {
            Toast.makeText(context, getString(R.string.error_client_just_acquired), Toast.LENGTH_LONG).show();
            if (lastSelectedPos >= 0) {
                jsonQueuedPersonArrayList.get(lastSelectedPos).setServerDeviceId("XXX-XXXX-XXXX");
                lastSelectedPos = -1;
                adapter.notifyDataSetChanged();
                listview.invalidateViews();
                listview.refreshDrawableState();
            }
        }
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorcode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorcode == Constants.INVALID_CREDENTIAL) {
            Intent intent = new Intent();
            intent.putExtra(Constants.CLEAR_DATA, true);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }

}
