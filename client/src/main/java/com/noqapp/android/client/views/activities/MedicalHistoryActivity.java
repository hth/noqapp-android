package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.MedicalRecordApiCall;
import com.noqapp.android.client.presenter.MedicalRecordPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.utils.CommonHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MedicalHistoryActivity extends BaseActivity implements MedicalRecordPresenter {


    private ListView listview;
    private RelativeLayout rl_empty;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        listview = findViewById(R.id.listview);
        FrameLayout frame_layout = findViewById(R.id.frame_layout);
        rl_empty = findViewById(R.id.rl_empty);
        initActionsViews(false);
        context = this;

        tv_toolbar_title.setText(getString(R.string.medical_history));

        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        if (NetworkUtils.isConnectingToInternet(context)) {

            if (UserUtils.isLogin()) {
                if (jsonMedicalRecords.size() == 0) {
                    new MedicalRecordApiCall(this).history(UserUtils.getEmail(), UserUtils.getAuth());
                    progressDialog.show();
                }
            } else {
                Toast.makeText(this, "Please login to see the details", Toast.LENGTH_LONG).show();
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }


    @Override
    public void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.sort(jsonMedicalRecords, new Comparator<JsonMedicalRecord>() {
            public int compare(JsonMedicalRecord o1, JsonMedicalRecord o2) {
                try {
                    return CommonHelper.SDF_YYYY_MM_DD.parse(o2.getCreateDate()).compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o1.getCreateDate()));
                }catch (Exception e){
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, jsonMedicalRecords);
        listview.setAdapter(adapter);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", jsonMedicalRecords.get(i));
                in.putExtras(bundle);
                startActivity(in);
            }
        });
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }
}
