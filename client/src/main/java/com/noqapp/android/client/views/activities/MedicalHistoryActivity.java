package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.MedicalRecordApiModel;
import com.noqapp.android.client.presenter.interfaces.MedicalRecordPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.NetworkChangeReceiver;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicalHistoryActivity extends BaseActivity implements MedicalRecordPresenter {


    private ListView listview;
    private TextView tv_empty;
    private EventBus bus = EventBus.getDefault();
    private NetworkChangeReceiver myReceiver = new NetworkChangeReceiver();
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private Context context;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);
        listview = findViewById(R.id.listview);
        FrameLayout frame_layout = findViewById(R.id.frame_layout);
        tv_empty = findViewById(R.id.tv_empty);
        initActionsViews(false);
        context = this;
        snackbar = Snackbar
                .make(frame_layout, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
//                .setAction("RETRY", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                    }
//                });
//        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(myReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
        if (NetworkUtils.isConnectingToInternet(context)) {
            showSnackBar(true);
        } else {
            showSnackBar(false);
        }

        tv_toolbar_title.setText(getString(R.string.medical_history));

        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        if (!UserUtils.isLogin()) {
            Toast.makeText(this, "Please login to see the details", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void medicalRecordResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.reverse(jsonMedicalRecords);
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, jsonMedicalRecords);
        listview.setAdapter(adapter);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
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
    public void medicalRecordError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        AppUtilities.authenticationProcessing(this,errorCode);
    }

    @Subscribe
    public void onEvent(Boolean name) {
        Log.e("name value: ", String.valueOf(name));
        if (NetworkUtils.isConnectingToInternet(context)) {
            //Toast.makeText(context, "Connection true", Toast.LENGTH_SHORT).show();
            showSnackBar(true);
            if (UserUtils.isLogin()) {
                if (jsonMedicalRecords.size() == 0) {
                    new MedicalRecordApiModel(this).getMedicalRecord(UserUtils.getEmail(), UserUtils.getAuth());
                    progressDialog.show();
                }
            }
        } else {
            showSnackBar(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(myReceiver);
        }
    }

    private void showSnackBar(boolean isHide) {
        if (isHide)
            snackbar.dismiss();
        else
            snackbar.show();
    }
}
