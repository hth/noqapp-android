package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.QuestionTypeEnum;


public class SurveyFragment extends BaseFragment implements View.OnClickListener {
    private EditText edt_text;
    private AppCompatRatingBar ratingBar;
    private RadioGroup rg_yes_no;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_survey, container, false);
        TextView tv_question = view.findViewById(R.id.tv_question);
        rg_yes_no = view.findViewById(R.id.rg_yes_no);
        edt_text = view.findViewById(R.id.edt_text);
        ratingBar = view.findViewById(R.id.ratingBar);
        rg_yes_no.setVisibility(View.GONE);
        edt_text.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String question = bundle.getString("question", "");
            QuestionTypeEnum q_type = (QuestionTypeEnum) bundle.getSerializable("q_type");
            switch (q_type) {
                case B:
                    rg_yes_no.setVisibility(View.VISIBLE);
                    break;
                case S:
                    break;
                case M:
                    break;
                case R:
                    ratingBar.setVisibility(View.VISIBLE);
                    break;
                case T:
                    edt_text.setVisibility(View.VISIBLE);
                    break;
            }
            tv_question.setText(question);
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.btn_send_invite:

                break;

        }
    }
}
