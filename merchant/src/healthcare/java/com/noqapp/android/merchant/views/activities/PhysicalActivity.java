package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.customviews.MeterView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import info.hoang8f.android.segmented.SegmentedGroup;


public class PhysicalActivity extends AppCompatActivity implements MedicalRecordPresenter,JsonMedicalRecordPresenter , MeterView.MeterViewValueChanged{
    private ProgressDialog progressDialog;
    private MeterView mv_weight1, mv_weight2, mv_pulse, mv_temperature1, mv_temperature2, mv_oxygen;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_oxygen, tv_bp_high, tv_bp_low;
    private DiscreteSeekBar dsb_bp_low, dsb_bp_high;
    private EditText actv_followup;
    private SegmentedGroup rg_duration;
    private MedicalHistoryModel medicalHistoryModel;
    private JsonQueuedPerson jsonQueuedPerson;
    protected boolean isDialog = false;
    protected ImageView actionbarBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDialog) {
            if (new AppUtils().isTablet(getApplicationContext())) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        if (isDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.60);
            int height = (int) (metrics.heightPixels * 0.70);
            getWindow().setLayout(screenWidth, height);
        }

        medicalHistoryModel = new MedicalHistoryModel(this);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setBackgroundResource(R.drawable.cross);

        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.setScrollBarFadeDuration(0);
        scroll_view.setScrollbarFadingEnabled(false);
        initProgress();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_physical));
        TextView tv_title = findViewById(R.id.tv_title);
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        final String codeQR = getIntent().getStringExtra("qCodeQR");
        tv_title.setText(jsonQueuedPerson.getCustomerName());
        actv_followup = findViewById(R.id.actv_followup);
        rg_duration = findViewById(R.id.rg_duration);
        mv_weight1 = findViewById(R.id.mv_weight1);
        mv_weight2 = findViewById(R.id.mv_weight2);
        mv_pulse = findViewById(R.id.mv_pulse);
        mv_temperature2 = findViewById(R.id.mv_temperature2);
        mv_temperature1 = findViewById(R.id.mv_temperature1);
        mv_oxygen = findViewById(R.id.mv_oxygen);


        tv_weight = findViewById(R.id.tv_weight);
        tv_pulse = findViewById(R.id.tv_pulse);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_oxygen = findViewById(R.id.tv_oxygen);
        tv_bp_high = findViewById(R.id.tv_bp_high);
        tv_bp_low = findViewById(R.id.tv_bp_low);


        mv_pulse.setMeterViewValueChanged(this);
        mv_temperature2.setMeterViewValueChanged(this);
        mv_temperature1.setMeterViewValueChanged(this);
        mv_weight1.setMeterViewValueChanged(this);
        mv_weight2.setMeterViewValueChanged(this);
        mv_oxygen.setMeterViewValueChanged(this);


        meterViewValueChanged(mv_pulse);
        meterViewValueChanged(mv_weight1);
        meterViewValueChanged(mv_temperature1);
        meterViewValueChanged(mv_oxygen);
        
        dsb_bp_low = findViewById(R.id.dsb_bp_low);
        dsb_bp_high = findViewById(R.id.dsb_bp_high);
        dsb_bp_low.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                tv_bp_low.setText("Diastolic: " + String.valueOf(value));
                return value;
            }
        });
        dsb_bp_high.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                tv_bp_high.setText("Systolic: " + String.valueOf(value));
                return value;
            }
        });

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Patient data...");
                progressDialog.show();
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
                jsonMedicalRecord.setFormVersion(FormVersionEnum.MFD1);
                jsonMedicalRecord.setCodeQR(codeQR);
                jsonMedicalRecord.setQueueUserId(jsonQueuedPerson.getQueueUserId());
                JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                        .setBloodPressure(new String[]{String.valueOf(dsb_bp_high.getProgress()), String.valueOf(dsb_bp_low.getProgress())})
                        .setPluse(String.valueOf(mv_pulse.getValue()))
                        .setWeight(mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString())
                        .setOxygen(String.valueOf(mv_oxygen.getValue()))
                        .setTemperature(mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
                jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);

                if (!actv_followup.getText().toString().equals("")) {
                    String value = actv_followup.getText().toString();
                    int selectedId = rg_duration.getCheckedRadioButtonId();
                    if (selectedId == R.id.rb_months) {
                        jsonMedicalRecord.setFollowUpInDays(String.valueOf(Integer.parseInt(value) * 30));
                    } else {
                        jsonMedicalRecord.setFollowUpInDays(value);
                    }
                }
                medicalHistoryModel.add(
                        BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(),
                        jsonMedicalRecord);
            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setCodeQR(codeQR);
            medicalHistoryModel.setJsonMedicalRecordPresenter(this);
            medicalHistoryModel.retrieveMedicalRecord(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(),jsonMedicalRecord);

        } else {
            ShowAlertInformation.showNetworkDialog(PhysicalActivity.this);
        }
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Patient data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void medicalRecordError() {
        dismissProgress();
        Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            try {
                actv_followup.setText(jsonMedicalRecord.getFollowUpInDays());
                if (null != jsonMedicalRecord.getMedicalPhysical().getOxygen())
                    mv_oxygen.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getOxygen()));
                if (null != jsonMedicalRecord.getMedicalPhysical().getPluse())
                    mv_pulse.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getPluse()));
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    dsb_bp_high.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0]));
                    dsb_bp_low.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]));
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    if(jsonMedicalRecord.getMedicalPhysical().getWeight().contains(".")){
                        String [] temp = jsonMedicalRecord.getMedicalPhysical().getWeight().split("\\.");
                        mv_weight1.setValue(Integer.parseInt(temp[0]));
                        mv_weight2.setValue(Integer.parseInt(temp[1]));
                    }
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    if(jsonMedicalRecord.getMedicalPhysical().getTemperature().contains(".")){
                        String [] temp = jsonMedicalRecord.getMedicalPhysical().getTemperature().split("\\.");
                        mv_temperature1.setValue(Integer.parseInt(temp[0]));
                        mv_temperature2.setValue(Integer.parseInt(temp[1]));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dismissProgress();
    }

    @Override
    public void meterViewValueChanged(View v) {
        switch (v.getId()) {
            case R.id.mv_pulse:
                tv_pulse.setText("Pulse: " + mv_pulse.getValueAsString());
                break;
            case R.id.mv_weight1:
            case R.id.mv_weight2:
                tv_weight.setText("Weight: " + mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString());
                break;
            case R.id.mv_temperature1:
            case R.id.mv_temperature2:
                tv_temperature.setText("Temp: " + mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
                break;
            case R.id.mv_oxygen:
                tv_oxygen.setText("Oxygen: " + mv_oxygen.getValueAsString());
                break;

            default:
                break;
        }
    }
}
