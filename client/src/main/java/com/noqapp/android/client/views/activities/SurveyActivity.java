package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.SurveyResponseApiCalls;
import com.noqapp.android.client.model.types.QuestionTypeEnum;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.beans.JsonQuestionnaire;
import com.noqapp.android.client.presenter.beans.body.Survey;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.HashMap;

public class SurveyActivity extends BaseActivity implements ResponsePresenter {

    private HashMap<String, QuestionTypeEnum> temp;
    private EditText edt_text;
    private AppCompatRatingBar ratingBar;
    private RadioGroup rg_yes_no;
    private JsonQuestionnaire jsonQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_survey_pager);
        initActionsViews(false);
        tv_toolbar_title.setText("Survey");
        temp = (HashMap<String, QuestionTypeEnum>) getIntent().getSerializableExtra("map");
        jsonQuestionnaire = (JsonQuestionnaire) getIntent().getSerializableExtra("survey");
        TextView tv_q_rating = findViewById(R.id.tv_q_rating);
        TextView tv_q_yes_no = findViewById(R.id.tv_q_yes_no);
        TextView tv_q_edit = findViewById(R.id.tv_q_edit);
        rg_yes_no = findViewById(R.id.rg_yes_no);
        edt_text = findViewById(R.id.edt_text);
        ratingBar = findViewById(R.id.ratingBar);
        AppUtils.hideViews(tv_q_rating, tv_q_yes_no, tv_q_edit, rg_yes_no, edt_text, ratingBar);
        if (null != temp && temp.size() > 0) {
            int i = 0;
            for (HashMap.Entry<String, QuestionTypeEnum> entry : temp.entrySet()) {
                String question = entry.getKey();
                QuestionTypeEnum q_type = entry.getValue();
                switch (q_type) {
                    case B:
                        rg_yes_no.setVisibility(View.VISIBLE);
                        tv_q_yes_no.setVisibility(View.VISIBLE);
                        tv_q_yes_no.setText(question);
                        break;
                    case S:
                        break;
                    case M:
                        break;
                    case R:
                        ratingBar.setVisibility(View.VISIBLE);
                        tv_q_rating.setVisibility(View.VISIBLE);
                        tv_q_rating.setText(question);
                        break;
                    case T:
                        edt_text.setVisibility(View.VISIBLE);
                        tv_q_edit.setVisibility(View.VISIBLE);
                        tv_q_edit.setText(question);
                        break;
                }

            }
        }

        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(v -> {
            setProgressMessage("Submitting survey " + "...");
            if (NetworkUtils.isConnectingToInternet(this)) {
                showProgress();
                SurveyResponseApiCalls surveyResponseApiCalls = new SurveyResponseApiCalls(SurveyActivity.this);
                Survey survey = new Survey();
                survey.setBizNameId(jsonQuestionnaire.getBizNameId());
                survey.setQuestionnaireId(jsonQuestionnaire.getQuestionnaireId());
                survey.setBizStoreId(NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                survey.setBizStoreId(NoQueueBaseActivity.getUserProfile().getBizStoreIds().get(0));
                survey.setOverallRating(5);
                survey.setDetailedResponse(new String[]{"YES", "Please improve UI"});
                surveyResponseApiCalls.surveyResponse(UserUtils.getDeviceId(), survey);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }

        });
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == Constants.SUCCESS) {
                new CustomToast().showToast(this, "survey submitted successfully !!!");
            } else {
                new CustomToast().showToast(this, "Error while submitting survey");
            }
        } else {
            new CustomToast().showToast(this, "Error while submitting survey");
        }
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        dismissProgress();
    }
}
