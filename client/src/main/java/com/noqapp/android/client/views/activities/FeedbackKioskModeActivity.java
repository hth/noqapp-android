package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SurveyApiCalls;
import com.noqapp.android.client.presenter.SurveyPresenter;
import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;

public class FeedbackKioskModeActivity extends BaseActivity implements SurveyPresenter {
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
        Toast.makeText(this, "You are in feedback screen", Toast.LENGTH_SHORT).show();
        String codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        if (!TextUtils.isEmpty(codeQR)) {
            if (NetworkUtils.isConnectingToInternet(this)) {
                showProgress();
                SurveyApiCalls surveyApiCalls = new SurveyApiCalls(this);
                surveyApiCalls.survey(UserUtils.getEmail(), UserUtils.getAuth());
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
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

    @Override
    public void surveyResponse(JsonQuestionnaire jsonQuestionnaire) {
        Log.e("survey response", jsonQuestionnaire.toString());
        dismissProgress();
       // LaunchActivity.clearPreferences();
    }
}
