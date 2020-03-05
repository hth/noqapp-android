package com.noqapp.android.merchant.views.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.CustomCalendar;
import com.noqapp.android.common.views.activities.DatePickerActivity;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class NeuroActivity extends BaseActivity {
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    private SegmentedControl sc_seizure_history, sc_personal_history;
    private ArrayList<String> seizure_history_data = new ArrayList<>();
    private ArrayList<String> personal_history_data = new ArrayList<>();
    private TextView tv_date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuro);
        initActionsViews(true);
        tv_toolbar_title.setText("Neuro Form");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        sc_seizure_history = findViewById(R.id.sc_seizure_history);
        sc_personal_history = findViewById(R.id.sc_personal_history);
        seizure_history_data.clear();
        seizure_history_data.add("Hypertension");
        seizure_history_data.add("Diabetes");
        seizure_history_data.add("Bronchial Asthma");
        seizure_history_data.add("Epilepsy");
        seizure_history_data.add("Heart Disease");
        seizure_history_data.add("Tuberous sclerosis");
        seizure_history_data.add("LVEF < 25%");
        seizure_history_data.add("Respiratory Dysfunction");
        seizure_history_data.add("Other");

        sc_seizure_history.addSegments(seizure_history_data);


        personal_history_data.clear();
        personal_history_data.add("Smoking");
        personal_history_data.add("Alcohol");
        personal_history_data.add("Tobacco Chewing");
        personal_history_data.add("Other");
        sc_personal_history.addSegments(personal_history_data);
        TextView tv_initial_assessment_time = findViewById(R.id.tv_initial_assessment_time);
        tv_initial_assessment_time.setOnClickListener(new TextViewClick(tv_initial_assessment_time, this));
        TextView tv_final_assessment_time = findViewById(R.id.tv_final_assessment_time);
        tv_final_assessment_time.setOnClickListener(new TextViewClick(tv_final_assessment_time, this));
        TextView tv_coordinate_time = findViewById(R.id.tv_coordinate_time);

        tv_coordinate_time.setOnClickListener(new TextViewClick(tv_coordinate_time, this));

        tv_date = findViewById(R.id.tv_date);
        tv_date.setOnClickListener(v -> {
            CustomCalendar customCalendar = new CustomCalendar(NeuroActivity.this,true);
            customCalendar.setDateSelectListener(new CustomCalendar.DateSelectListener() {
                @Override
                public void calendarDate(String date) {
                    tv_date.setText(date);
                }
            });
            customCalendar.showDobCalendar();

//            Intent in = new Intent(NeuroActivity.this, DatePickerActivity.class);
//            startActivityForResult(in, Constants.RC_DATE_PICKER);
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_DATE_PICKER && resultCode == Activity.RESULT_OK) {
            String date = data.getStringExtra("result");
            if (!TextUtils.isEmpty(date) && CommonHelper.isDateBeforeToday(this, date))
                tv_date.setText(date);
        }
    }

    private class TextViewClick implements View.OnClickListener {
        private TextView textView;
        private Context context;

        private TextViewClick(TextView textView, Context context) {
            this.textView = textView;
            this.context = context;

        }

        @Override
        public void onClick(View view) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedHour == 0 && selectedMinute == 0) {
                        new CustomToast().showToast(context, getString(R.string.error_time));
                    } else {
                        textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }
            }, hour, minute, false);//Yes 24 hour time
            //mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }
}
