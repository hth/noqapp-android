package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.customviews.MeterView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import info.hoang8f.android.segmented.SegmentedGroup;


public class PhysicalDialogActivity extends AppCompatActivity implements MedicalRecordPresenter {
    private ProgressDialog progressDialog;
    private MeterView mv_weight, mv_pulse, mv_temperature, mv_oxygen;
    private EditText edt_bp;
    private EditText actv_followup;
    private SegmentedGroup rg_duration;
    private MedicalHistoryModel medicalHistoryModel;
    private JsonQueuedPerson jsonQueuedPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//            if (new AppUtils().isTablet(getApplicationContext())) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            } else {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        this.setFinishOnTouchOutside(false);
        medicalHistoryModel = new MedicalHistoryModel(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.60);
        int height = (int) (metrics.heightPixels * 0.80);
        getWindow().setLayout(screenWidth, height);

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
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
        edt_bp = findViewById(R.id.edt_bp);
        mv_weight = findViewById(R.id.mv_weight);
        mv_pulse = findViewById(R.id.mv_pulse);
        mv_temperature = findViewById(R.id.mv_temperature);
        mv_oxygen = findViewById(R.id.mv_oxygen);
        actv_followup = findViewById(R.id.actv_followup);
        rg_duration = findViewById(R.id.rg_duration);
        mv_pulse.setNumbersOf(3, 0);
        mv_oxygen.setNumbersOf(2, 0);
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
                        .setBloodPressure(new String[]{edt_bp.getText().toString()})
                        .setPluse(String.valueOf(mv_pulse.getValue()))
                        .setWeight(String.valueOf(mv_weight.getDoubleValue()))
                        .setOxygen(String.valueOf(mv_oxygen.getValue()))
                        .setTemperature(String.valueOf(mv_temperature.getDoubleValue()));
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
            //progressDialog.show();

        } else {
            ShowAlertInformation.showNetworkDialog(PhysicalDialogActivity.this);
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
}
