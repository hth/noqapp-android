package com.noqapp.android.client.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.noqapp.android.client.presenter.beans.SurveyQuestion;
import com.noqapp.android.client.presenter.beans.body.Survey;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.KioskStringConstants;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends BaseActivity implements ResponsePresenter, View.OnClickListener {
    private List<SurveyQuestion> temp;


    private LinearLayout ll_rating;
    private JsonQuestionnaire jsonQuestionnaire;
    private TextView[] tvs = new TextView[10];
    private int selectPos = -1;
    private CardView cv_rating;
    private TextView tv_left, tv_right;
    private LinearLayout ll_items;
    private ArrayList<View> viewLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_survey);
        initActionsViews(false);
        tv_toolbar_title.setText(KioskStringConstants.SURVEY_TITLE);
        temp = (List<SurveyQuestion>) getIntent().getSerializableExtra("list");
        jsonQuestionnaire = (JsonQuestionnaire) getIntent().getSerializableExtra("survey");
        ll_items = findViewById(R.id.ll_items);
        ll_rating = findViewById(R.id.ll_rating);
        tv_left = findViewById(R.id.tv_left);
        tv_right = findViewById(R.id.tv_right);
        viewLists = new ArrayList<>();
        TextView tv_q_rating = findViewById(R.id.tv_q_rating);

        cv_rating = findViewById(R.id.cv_rating);

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
        tv_left.setText(KioskStringConstants.STR_WORST);
        tv_right.setText(KioskStringConstants.STR_BEST);

        AppUtils.hideViews(tv_q_rating, ll_rating, cv_rating);
        if (null != temp && temp.size() > 0) {
            for (SurveyQuestion entry : temp) {
                String question = entry.getQuestion();
                QuestionTypeEnum q_type = entry.getQuestionType();
                LayoutInflater inflater = LayoutInflater.from(this);
                View inflatedLayout = inflater.inflate(R.layout.survey_item, null, false);
                EditText edt_text = inflatedLayout.findViewById(R.id.edt_text);
                TextView tv_q_edit = inflatedLayout.findViewById(R.id.tv_q_edit);
                TextView tv_q_yes_no = inflatedLayout.findViewById(R.id.tv_q_yes_no);
                RadioGroup rg_yes_no = inflatedLayout.findViewById(R.id.rg_yes_no);
                RadioButton rb_yes = inflatedLayout.findViewById(R.id.rb_yes);
                RadioButton rb_no = inflatedLayout.findViewById(R.id.rb_no);
                CardView cv_yes_no = inflatedLayout.findViewById(R.id.cv_yes_no);
                CardView cv_edit = inflatedLayout.findViewById(R.id.cv_edit);
                AppUtils.hideViews(tv_q_yes_no, tv_q_edit, rg_yes_no, edt_text, cv_yes_no, cv_edit);
                switch (q_type) {
                    case B: {
                        cv_yes_no.setVisibility(View.VISIBLE);
                        rg_yes_no.setVisibility(View.VISIBLE);
                        tv_q_yes_no.setVisibility(View.VISIBLE);
                        rb_yes.setText(KioskStringConstants.YES);
                        rb_no.setText(KioskStringConstants.NO);
                        tv_q_yes_no.setText(question);
                        ll_items.addView(inflatedLayout);
                        viewLists.add(rb_yes);
                    }
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
                        ll_items.addView(inflatedLayout);
                        viewLists.add(edt_text);
                        break;
                }
            }
        }

        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this::onClick);
        Button btn_update = findViewById(R.id.btn_update);
        btn_clear.setText(KioskStringConstants.RESET);
        btn_update.setText(KioskStringConstants.SUBMIT);
        btn_update.setOnClickListener(v -> {
            if (selectPos < 0) {
                new CustomToast().showToast(SurveyActivity.this, KioskStringConstants.EMPTY_ERROR);
            } else {
                setProgressMessage(KioskStringConstants.PROGRESS_TITLE);
                if (NetworkUtils.isConnectingToInternet(this)) {
                    showProgress();
                    SurveyResponseApiCalls surveyResponseApiCalls = new SurveyResponseApiCalls(SurveyActivity.this);
                    Survey survey = new Survey();
                    survey.setBizNameId(jsonQuestionnaire.getBizNameId());
                    survey.setQuestionnaireId(jsonQuestionnaire.getQuestionnaireId());
                    survey.setCodeQR(AppInitialize.getKioskModeInfo().getKioskCodeQR());
                    survey.setBizStoreId(AppInitialize.getUserProfile().getCodeQRAndBizStoreIds().get(AppInitialize.getKioskModeInfo().getKioskCodeQR()));
                    survey.setOverallRating(selectPos);
                    survey.setDetailedResponse(getFormData());
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
                new CustomToast().showToast(this, KioskStringConstants.SUCCESS_RESPONSE);
                finish();
            } else {
                new CustomToast().showToast(this, KioskStringConstants.FAILURE_RESPONSE);
            }
        } else {
            new CustomToast().showToast(this, KioskStringConstants.FAILURE_RESPONSE);
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
            for (int i = 0; i < viewLists.size(); i++) {
                if (viewLists.get(i) instanceof EditText)
                    ((EditText) viewLists.get(i)).setText("");
                else if (viewLists.get(i) instanceof RadioButton)
                    ((RadioButton) viewLists.get(i)).setChecked(true);
            }
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

    private String[] getFormData() {
        String[] temp = new String[viewLists.size()];
        for (int i = 0; i < viewLists.size(); i++) {
            if (viewLists.get(i) instanceof EditText)
                temp[i] = ((EditText) viewLists.get(i)).getText().toString();
            else if (viewLists.get(i) instanceof RadioButton) {
                temp[i] = ((RadioButton) viewLists.get(i)).isChecked() ? "1" : "0";
            }
        }
        return temp;
    }
}
