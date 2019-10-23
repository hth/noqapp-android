package com.noqapp.android.client.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

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

public class SurveyActivity extends BaseActivity implements ResponsePresenter, View.OnClickListener {
    private HashMap<String, QuestionTypeEnum> temp;
    private EditText edt_text;
    private RadioGroup rg_yes_no;
    private RadioButton rb_yes;
    private LinearLayout ll_rating;
    private JsonQuestionnaire jsonQuestionnaire;
    private TextView[] tvs = new TextView[10];
    private int selectPos = -1;
    private CardView cv_rating, cv_yes_no, cv_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_survey);
        initActionsViews(false);
        tv_toolbar_title.setText("Survey");
        temp = (HashMap<String, QuestionTypeEnum>) getIntent().getSerializableExtra("map");
        jsonQuestionnaire = (JsonQuestionnaire) getIntent().getSerializableExtra("survey");
        ll_rating = findViewById(R.id.ll_rating);
        TextView tv_q_rating = findViewById(R.id.tv_q_rating);
        TextView tv_q_yes_no = findViewById(R.id.tv_q_yes_no);
        TextView tv_q_edit = findViewById(R.id.tv_q_edit);
        cv_rating = findViewById(R.id.cv_rating);
        cv_yes_no = findViewById(R.id.cv_yes_no);
        cv_edit = findViewById(R.id.cv_edit);
        rg_yes_no = findViewById(R.id.rg_yes_no);
        rb_yes = findViewById(R.id.rb_yes);
        edt_text = findViewById(R.id.edt_text);
        tvs[0] = findViewById(R.id.tv_1);
        tvs[1] = findViewById(R.id.tv_2);
        tvs[2] = findViewById(R.id.tv_3);
        tvs[3] = findViewById(R.id.tv_4);
        tvs[4] = findViewById(R.id.tv_5);
        tvs[5] = findViewById(R.id.tv_6);
        tvs[6] = findViewById(R.id.tv_7);
        tvs[7] = findViewById(R.id.tv_8);
        tvs[8] = findViewById(R.id.tv_9);
        tvs[9] = findViewById(R.id.tv_10);
        for (TextView tv : tvs) {
            tv.setOnClickListener(this::onClick);
        }

        AppUtils.hideViews(tv_q_rating, tv_q_yes_no, tv_q_edit, rg_yes_no, edt_text, ll_rating,
                cv_rating, cv_yes_no, cv_edit);
        if (null != temp && temp.size() > 0) {
            int i = 0;
            for (HashMap.Entry<String, QuestionTypeEnum> entry : temp.entrySet()) {
                String question = entry.getKey();
                QuestionTypeEnum q_type = entry.getValue();
                switch (q_type) {
                    case B:
                        cv_yes_no.setVisibility(View.VISIBLE);
                        rg_yes_no.setVisibility(View.VISIBLE);
                        tv_q_yes_no.setVisibility(View.VISIBLE);
                        tv_q_yes_no.setText(question);
                        break;
                    case S:
                        break;
                    case M:
                        break;
                    case R:
                        cv_rating.setVisibility(View.VISIBLE);
                        ll_rating.setVisibility(View.VISIBLE);
                        tv_q_rating.setVisibility(View.VISIBLE);
                        tv_q_rating.setText(question);
                        break;
                    case T:
                        cv_edit.setVisibility(View.VISIBLE);
                        edt_text.setVisibility(View.VISIBLE);
                        tv_q_edit.setVisibility(View.VISIBLE);
                        tv_q_edit.setText(question);
                        break;
                }

            }
        }

        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this::onClick);
        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(v -> {
            if (selectPos < 0) {
                new CustomToast().showToast(SurveyActivity.this, "Please rate overall rating");
            } else {
                setProgressMessage("Submitting feedback ...");
                if (NetworkUtils.isConnectingToInternet(this)) {
                    showProgress();
                    SurveyResponseApiCalls surveyResponseApiCalls = new SurveyResponseApiCalls(SurveyActivity.this);
                    Survey survey = new Survey();
                    survey.setBizNameId(jsonQuestionnaire.getBizNameId());
                    survey.setQuestionnaireId(jsonQuestionnaire.getQuestionnaireId());
                    survey.setCodeQR(NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                    survey.setBizStoreId(NoQueueBaseActivity.getUserProfile().getBizStoreIds().get(0));
                    survey.setOverallRating(selectPos);
                    survey.setDetailedResponse(new String[]{rb_yes.isChecked() ? "1" : "0", edt_text.getText().toString()});
                    surveyResponseApiCalls.surveyResponse(UserUtils.getDeviceId(), survey);
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
            }
        });
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == Constants.SUCCESS) {
                new CustomToast().showToast(this, "Thank you for your feedback");
                finish();
            } else {
                new CustomToast().showToast(this, "Error submitting feedback");
            }
        } else {
            new CustomToast().showToast(this, "Error submitting feedback");
        }
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        dismissProgress();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_clear) {
            selectPos = -1;
            resetRating();
            edt_text.setText("");
            rb_yes.setChecked(true);

        } else {
            resetRating();
            int tag = Integer.parseInt((String) v.getTag());
            tvs[tag - 1].setTextColor(Color.WHITE);
            tvs[tag - 1].setBackgroundResource(R.drawable.btn_bg_enable);
            selectPos = tag;
        }
    }

    private void resetRating() {
        for (TextView tv : tvs) {
            tv.setOnClickListener(this::onClick);
            tv.setTextColor(Color.parseColor("#333333"));
            tv.setBackgroundResource(R.drawable.edit_gray);
        }
    }
}
