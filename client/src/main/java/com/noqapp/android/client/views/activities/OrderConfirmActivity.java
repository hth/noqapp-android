package com.noqapp.android.client.views.activities;

import android.os.Bundle;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;

import butterknife.ButterKnife;

public class OrderConfirmActivity  extends BaseActivity {

    private JsonPurchaseOrder jsonPurchaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        initActionsViews(false);

        tv_toolbar_title.setText(getString(R.string.screen_order_confirm));
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");

//
//        if (LaunchActivity.getLaunchActivity().isOnline()) {
//            progressDialog.show();
//            if (UserUtils.isLogin()) {
//                MedicalRecordApiModel.medicalRecordPresenter = this;
//                MedicalRecordApiModel.getMedicalRecord(UserUtils.getEmail(), UserUtils.getAuth());
//
//            } else {
//                //Give error
//            }
//        } else {
//            ShowAlertInformation.showNetworkDialog(this);
//        }

    }

}
