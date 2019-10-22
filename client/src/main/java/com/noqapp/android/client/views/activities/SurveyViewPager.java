package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.QuestionTypeEnum;
import com.noqapp.android.client.utils.AppUtils;

import java.util.HashMap;

public class SurveyViewPager extends BaseActivity {

    private HashMap<String, QuestionTypeEnum> temp;
    private EditText edt_text;
    private AppCompatRatingBar ratingBar;
    private RadioGroup rg_yes_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_survey_pager);
        initActionsViews(false);
        tv_toolbar_title.setText("Survey");
        temp = (HashMap<String, QuestionTypeEnum>) getIntent().getSerializableExtra("map");
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
    }
}
