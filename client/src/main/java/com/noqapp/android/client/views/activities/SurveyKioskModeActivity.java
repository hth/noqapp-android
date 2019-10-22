package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SurveyApiCalls;
import com.noqapp.android.client.model.types.QuestionTypeEnum;
import com.noqapp.android.client.presenter.SurveyPresenter;
import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.LanguageGridAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SurveyKioskModeActivity extends BaseActivity implements SurveyPresenter,
        LanguageGridAdapter.OnItemClickListener {
    private RecyclerView rv_languages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_kiosk);
        rv_languages = findViewById(R.id.rv_languages);
        if (AppUtils.isTablet(this)) {
            rv_languages.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            rv_languages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
        initActionsViews(false);
        actionbarBack.setVisibility(View.INVISIBLE);
        tv_toolbar_title.setText(NoQueueBaseActivity.getKioskModeInfo().getBizName());
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
        List<Locale> keys = new ArrayList<>(jsonQuestionnaire.getQuestions().keySet());
        LanguageGridAdapter recyclerView_Adapter = new LanguageGridAdapter(
                this, keys, jsonQuestionnaire.getQuestions(), this);
        rv_languages.setAdapter(recyclerView_Adapter);
        dismissProgress();
        // LaunchActivity.clearPreferences();
    }


    @Override
    public void onLanguageSelected(Map<String, QuestionTypeEnum> item) {
        HashMap<String, QuestionTypeEnum> temp = new HashMap<>(item);
        Intent in = new Intent(this, SurveyViewPager.class);
        in.putExtra("map", temp);
        startActivity(in);

    }
}
