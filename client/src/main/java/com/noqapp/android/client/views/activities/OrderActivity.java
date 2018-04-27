package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity {

    @BindView(R.id.tv_user_name)
    protected TextView tv_user_name;
    @BindView(R.id.tv_user_address)
    protected TextView tv_user_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initActionsViews(false);

        tv_toolbar_title.setText(getString(R.string.screen_order));
        tv_user_name.setText(NoQueueBaseActivity.getUserName());
        tv_user_address.setText("No Address");

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
