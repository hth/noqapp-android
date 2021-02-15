package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.StoreSettingApiCalls;
import com.noqapp.android.merchant.presenter.beans.body.merchant.StoreHours;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.interfaces.StoreHoursSettingPresenter;

public class NotifyStocksActivity extends BaseActivity implements StoreHoursSettingPresenter {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_stocks);
        initActionsViews(false);
        tv_toolbar_title.setText("Notify Customers about stock");
        Button btn_submit = findViewById(R.id.btn_submit);
        StoreSettingApiCalls storeSettingApiCalls = new StoreSettingApiCalls(this);
        btn_submit.setOnClickListener((View v) -> {
            setProgressMessage("Sending message...");
            showProgress();
            storeSettingApiCalls.notifyFreshStockArrival(
                    UserUtils.getDeviceId(),
                    UserUtils.getEmail(),
                    UserUtils.getAuth());
        });
    }

    @Override
    public void queueStoreHoursSettingResponse(StoreHours storeHours) {
        dismissProgress();
    }

    @Override
    public void queueStoreHoursSettingModifyResponse(JsonResponse jsonResponse) {
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "Message sent successfully");
            finish();
        } else {
            new CustomToast().showToast(this, "Failed sending message");
            //Rejected from  server
            ErrorEncounteredJson eej = jsonResponse.getError();
            if (null != eej) {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
        dismissProgress();
    }

    @Override
    public void queueStoreHoursSettingError() {
        dismissProgress();
    }
}
