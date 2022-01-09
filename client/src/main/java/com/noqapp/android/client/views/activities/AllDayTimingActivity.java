package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.open.StoreDetailImpl;
import com.noqapp.android.client.presenter.StoreHoursPresenter;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AllDayTimingAdapter;
import com.noqapp.android.common.beans.JsonHourList;

public class AllDayTimingActivity extends BaseActivity implements StoreHoursPresenter {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoqApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_day_timing);
        listview = findViewById(R.id.listview);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.weekly_hours));
        Intent bundle = getIntent();
        if (null != bundle && null != bundle.getExtras()) {
            String codeQR = bundle.getExtras().getString(IBConstant.KEY_CODE_QR, "");
            if (!TextUtils.isEmpty(codeQR)) {
                if (NetworkUtils.isConnectingToInternet(this)) {
                    showProgress();
                    new StoreDetailImpl(this).storeHours(UserUtils.getDeviceId(), codeQR);
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        }
    }

    @Override
    public void storeHoursResponse(JsonHourList jsonHourList) {
        dismissProgress();
        if (null != jsonHourList && jsonHourList.getJsonHours().size() > 0) {
            AllDayTimingAdapter adapter = new AllDayTimingAdapter(this, jsonHourList.getJsonHours());
            listview.setAdapter(adapter);
        }
    }
}
