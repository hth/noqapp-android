package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;


public class PrivacyActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        TextView tv_security_info = findViewById(R.id.tv_security_info);
        TextView tv_info = findViewById(R.id.tv_info);
        Button btn_privacy = findViewById(R.id.btn_privacy);
        Button btn_term_condition = findViewById(R.id.btn_term_condition);
        initActionsViews(true);
        tv_toolbar_title.setText(getString(R.string.legal));
        btn_privacy.setOnClickListener(this);
        btn_term_condition.setOnClickListener(this);
        tv_info.setText(getString(R.string.bullet) + " We do not track your activities \n" +
            getString(R.string.bullet) + " We do not share your personal information with anyone\n" +
            getString(R.string.bullet) + " We are not affiliated to any social media\n" +
            getString(R.string.bullet) + " When you join a queue, a secure communication is between you, doctor and hospital.");

        tv_security_info.setText("256-bit encryption and physical security that bank uses.");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_privacy:
                if (isOnline()) {
                    Intent in = new Intent(PrivacyActivity.this, WebViewActivity.class);
                    in.putExtra(IBConstant.KEY_URL, Constants.URL_PRIVACY_POLICY);
                    in.putExtra("title", "Privacy In Detail");
                    startActivity(in);
                } else {
                    ShowAlertInformation.showNetworkDialog(PrivacyActivity.this);
                }
                break;
            case R.id.btn_term_condition:
                if (isOnline()) {
                    Intent in = new Intent(PrivacyActivity.this, WebViewActivity.class);
                    in.putExtra(IBConstant.KEY_URL, Constants.URL_TERM_CONDITION);
                    in.putExtra("title", "Terms & Conditions");
                    startActivity(in);
                } else {
                    ShowAlertInformation.showNetworkDialog(PrivacyActivity.this);
                }
                break;
        }
    }
}
