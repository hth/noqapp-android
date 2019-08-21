package com.noqapp.android.merchant.views.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.views.pojos.TempNeuroObj;

import java.util.ArrayList;
import java.util.Calendar;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;
import segmented_control.widget.custom.android.com.segmentedcontrol.item_row_column.SegmentViewHolder;
import segmented_control.widget.custom.android.com.segmentedcontrol.listeners.OnSegmentSelectedListener;

public class NeuroFullPageActivity extends BaseActivity {
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    private String right = "";
    private String left = "";
    private String reflex_value = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuro_full);
        initActionsViews(true);
        tv_toolbar_title.setText("Neuro Form");
        //  jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        // jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        TableLayout tl_pediatric_reflexes = findViewById(R.id.tl_pediatric_reflexes);
        tl_pediatric_reflexes.removeAllViews();

        ArrayList<TempNeuroObj> tempNeuroObjs1 = getPReflexesTableList();
        for (int i = 0; i < tempNeuroObjs1.size() + 1; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.table_row_pediatric, null);
            TextView tv_header = view.findViewById(R.id.tv_header);
            TextView tv_value = view.findViewById(R.id.tv_value);
            if (i == 0) {
                tv_header.setText("Parameter");
                tv_value.setText("I / AP / ND");
                // tv_header.setGravity(Gravity.CENTER );
                tv_header.setTypeface(null, Typeface.BOLD);
                tv_value.setTypeface(null, Typeface.BOLD);

            } else {
                TempNeuroObj tempNeuroObj = tempNeuroObjs1.get(i - 1);
                tv_header.setText(tempNeuroObj.getTitle());
                tv_value.setText(tempNeuroObj.getRightValue());
                tv_header.setTypeface(null, tempNeuroObj.isHeader() ? Typeface.BOLD : Typeface.NORMAL);
                tv_value.setTypeface(null, Typeface.NORMAL);
                if (!tempNeuroObj.isHeader()) {
                    view.setOnClickListener(v -> openPReflexesOptions(tv_value));
                }
            }
            tl_pediatric_reflexes.addView(view);
        }

        TableLayout tl_voluntary_control = findViewById(R.id.tl_voluntary_control);
        tl_voluntary_control.removeAllViews();
        ArrayList<TempNeuroObj> tempNeuroObjs = getTableList();
        for (int i = 0; i < tempNeuroObjs.size() + 1; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.table_row, null);
            TextView tv_header = view.findViewById(R.id.tv_header);
            TextView tv_right = view.findViewById(R.id.tv_right);
            TextView tv_left = view.findViewById(R.id.tv_left);
            if (i == 0) {
                tv_header.setText("");
                tv_right.setTypeface(null, Typeface.BOLD);
                tv_left.setTypeface(null, Typeface.BOLD);
                tv_right.setText("Right");
                tv_left.setText("Left");
            } else {
                TempNeuroObj tempNeuroObj = tempNeuroObjs.get(i - 1);
                tv_header.setText(tempNeuroObj.getTitle());
                tv_right.setText(tempNeuroObj.getRightValue());
                tv_left.setText(tempNeuroObj.getLeftValue());
                tv_header.setTypeface(null, tempNeuroObj.isHeader() ? Typeface.BOLD : Typeface.NORMAL);
                tv_right.setTypeface(null, Typeface.NORMAL);
                tv_left.setTypeface(null, Typeface.NORMAL);
                view.setOnClickListener(v -> openOptions(tv_right, tv_left));
            }
            tl_voluntary_control.addView(view);
        }


    }

    private void openOptions(TextView tv_right, TextView tv_left) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_set_valountary_control, null, false);
        right = "";
        left = "";
        SegmentedControl sc_right = customDialogView.findViewById(R.id.sc_right);
        sc_right.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    right = (String) segmentViewHolder.getSegmentData();
                }
            }
        });
        SegmentedControl sc_left = customDialogView.findViewById(R.id.sc_left);
        sc_left.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    left = (String) segmentViewHolder.getSegmentData();
                }
            }
        });
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        btn_cancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_add.setOnClickListener(v -> {
            if (TextUtils.isEmpty(right) || TextUtils.isEmpty(left)) {
                new CustomToast().showToast(NeuroFullPageActivity.this, "please select status for both right & left");
            } else {
                tv_left.setText(left);
                tv_right.setText(right);
                right = "";
                left = "";
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    private void openPReflexesOptions(TextView tv_value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_set_p_reflexesl, null, false);
        reflex_value = "";
        SegmentedControl sc_right = customDialogView.findViewById(R.id.sc_right);
        sc_right.addOnSegmentSelectListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(SegmentViewHolder segmentViewHolder, boolean isSelected, boolean isReselected) {
                if (isSelected) {
                    reflex_value = (String) segmentViewHolder.getSegmentData();
                }
            }
        });
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Button btn_cancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btn_add = customDialogView.findViewById(R.id.btn_add);
        btn_cancel.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_add.setOnClickListener(v -> {
            if (TextUtils.isEmpty(reflex_value)) {
                new CustomToast().showToast(NeuroFullPageActivity.this, "please select Pediatric Reflexes");
            } else {
                switch (reflex_value) {
                    case "Integrated":
                        tv_value.setText("I");
                        break;
                    case "Abnormally Present":
                        tv_value.setText("AP");
                        break;
                    case "Not Developed":
                        tv_value.setText("ND");
                        break;
                }
                reflex_value = "";
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
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


    private ArrayList<TempNeuroObj> getPReflexesTableList() {
        ArrayList<TempNeuroObj> tempNeuroObjs = new ArrayList<>();
        tempNeuroObjs.add(new TempNeuroObj("PRIMITIVE REFLEXES", true));
        tempNeuroObjs.add(new TempNeuroObj("Rooting"));
        tempNeuroObjs.add(new TempNeuroObj("Sucking"));
        tempNeuroObjs.add(new TempNeuroObj("Palmar grasp"));
        tempNeuroObjs.add(new TempNeuroObj("Plantar grasp"));
        tempNeuroObjs.add(new TempNeuroObj("Rotation"));
        tempNeuroObjs.add(new TempNeuroObj("Moro's"));
        tempNeuroObjs.add(new TempNeuroObj("Galant's"));
        tempNeuroObjs.add(new TempNeuroObj("Landau's"));
        tempNeuroObjs.add(new TempNeuroObj("Placing"));
        tempNeuroObjs.add(new TempNeuroObj("SPINAL", true));
        tempNeuroObjs.add(new TempNeuroObj("Flx withdrawal"));
        tempNeuroObjs.add(new TempNeuroObj("Ext thrust"));
        tempNeuroObjs.add(new TempNeuroObj("Crossed ext"));
        tempNeuroObjs.add(new TempNeuroObj("BRAINSTEM", true));
        tempNeuroObjs.add(new TempNeuroObj("ATNR"));
        tempNeuroObjs.add(new TempNeuroObj("STNR"));
        tempNeuroObjs.add(new TempNeuroObj("TLR"));
        tempNeuroObjs.add(new TempNeuroObj("MID BRAIN", true));
        tempNeuroObjs.add(new TempNeuroObj("Neck righting"));
        tempNeuroObjs.add(new TempNeuroObj("Head on body"));
        tempNeuroObjs.add(new TempNeuroObj("Body on body"));
        tempNeuroObjs.add(new TempNeuroObj("Labyrithine"));
        tempNeuroObjs.add(new TempNeuroObj("CORTICAL", true));
        tempNeuroObjs.add(new TempNeuroObj("Protective ext"));
        tempNeuroObjs.add(new TempNeuroObj("Equilibrium ractions"));


        return tempNeuroObjs;
    }

    private ArrayList<TempNeuroObj> getTableList() {
        ArrayList<TempNeuroObj> tempNeuroObjs = new ArrayList<>();
        tempNeuroObjs.add(new TempNeuroObj("Hip", true));
        tempNeuroObjs.add(new TempNeuroObj("Flexion"));
        tempNeuroObjs.add(new TempNeuroObj("Extension"));
        tempNeuroObjs.add(new TempNeuroObj("Abduction"));
        tempNeuroObjs.add(new TempNeuroObj("Adduction"));
        tempNeuroObjs.add(new TempNeuroObj("Rotation"));

        tempNeuroObjs.add(new TempNeuroObj("Knee", true));
        tempNeuroObjs.add(new TempNeuroObj("Flexion"));
        tempNeuroObjs.add(new TempNeuroObj("Extension"));
        tempNeuroObjs.add(new TempNeuroObj("Ankle"));
        tempNeuroObjs.add(new TempNeuroObj("Dorsiflexion"));
        tempNeuroObjs.add(new TempNeuroObj("Plantarflexion"));
        tempNeuroObjs.add(new TempNeuroObj("Inversion"));
        tempNeuroObjs.add(new TempNeuroObj("Eversion"));
        tempNeuroObjs.add(new TempNeuroObj("Toes"));

        tempNeuroObjs.add(new TempNeuroObj("Shoulder", true));
        tempNeuroObjs.add(new TempNeuroObj("Flexion"));
        tempNeuroObjs.add(new TempNeuroObj("Extension"));
        tempNeuroObjs.add(new TempNeuroObj("Abduction"));
        tempNeuroObjs.add(new TempNeuroObj("Adduction"));
        tempNeuroObjs.add(new TempNeuroObj("Rotation"));

        tempNeuroObjs.add(new TempNeuroObj("Elbow", true));
        tempNeuroObjs.add(new TempNeuroObj("Flexion"));
        tempNeuroObjs.add(new TempNeuroObj("Extension"));
        tempNeuroObjs.add(new TempNeuroObj("Supination"));
        tempNeuroObjs.add(new TempNeuroObj("Pronation"));

        tempNeuroObjs.add(new TempNeuroObj("Wrist", true));
        tempNeuroObjs.add(new TempNeuroObj("Flexion"));
        tempNeuroObjs.add(new TempNeuroObj("Extension"));
        tempNeuroObjs.add(new TempNeuroObj("Finger"));
        return tempNeuroObjs;
    }
}
