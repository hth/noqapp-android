package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.IBConstant;

public class FeedbackKioskModeActivity extends BaseActivity {
    private RecyclerView rv_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_kiosk);
        rv_categories = findViewById(R.id.rv_categories);
        initActionsViews(false);
        actionbarBack.setVisibility(View.INVISIBLE);
        tv_toolbar_title.setText("Feedback Screen");
        Toast.makeText(this, "You are in feedback scren", Toast.LENGTH_SHORT).show();

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            String codeQR = bundle.getString(IBConstant.KEY_CODE_QR);


//            if (NetworkUtils.isConnectingToInternet(this)) {
//                showProgress();
//
//            } else {
//                ShowAlertInformation.showNetworkDialog(this);
//            }
        }
    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
