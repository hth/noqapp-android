package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.model.PatientProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordListPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;

import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientProfileActivity extends AppCompatActivity implements PatientProfilePresenter, MedicalRecordListPresenter, JsonMedicalRecordPresenter{
    private long lastPress;
    private Toast backPressToast;
    private ProgressDialog progressDialog;
    private TextView tv_patient_name, tv_address, tv_details;
    private ImageView iv_profile;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private TextView tv_weight, tv_pulse, tv_temperature, tv_height, tv_bp, tv_respiration;
    private JsonQueuedPerson jsonQueuedPerson;
    private String codeQR;
    private ListView listview;
    private final String notAvailable = "N/A";
    private JsonMedicalRecord jsonMedicalRecordTemp;
    private JsonProfile jsonProfile;
    private TextView tv_empty_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        codeQR = getIntent().getStringExtra("qCodeQR");
        listview = findViewById(R.id.listview);
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_address = findViewById(R.id.tv_address);
        tv_details = findViewById(R.id.tv_details);
        iv_profile = findViewById(R.id.iv_profile);

        tv_weight = findViewById(R.id.tv_weight);
        tv_pulse = findViewById(R.id.tv_pulse);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_height = findViewById(R.id.tv_height);
        tv_bp = findViewById(R.id.tv_bp);
        tv_respiration = findViewById(R.id.tv_respiration);

        tv_empty_list = findViewById(R.id.tv_empty_list);
        TextView tv_start_diagnosis = findViewById(R.id.tv_start_diagnosis);
        tv_start_diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientProfileActivity.this, MedicalCaseActivity.class);
                intent.putExtra("qCodeQR", codeQR);
                intent.putExtra("data", jsonQueuedPerson);
                intent.putExtra("medicalPhysical", jsonMedicalRecordTemp);
                intent.putExtra("jsonProfile",jsonProfile);
                startActivity(intent);
                finish();
            }
        });

        Picasso.with(this).load(R.drawable.profile_avatar).into(iv_profile);
        initProgress();
        MedicalHistoryModel medicalHistoryModel = new MedicalHistoryModel(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            PatientProfileModel profileModel = new PatientProfileModel(this);
            profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
            medicalHistoryModel.fetch(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));

            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setCodeQR(codeQR);
            medicalHistoryModel.setJsonMedicalRecordPresenter(this);
            medicalHistoryModel.retrieveMedicalRecord(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);


        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        updateUI(jsonProfile);
        dismissProgress();
    }

    private void updateUI(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
        tv_patient_name.setText(jsonProfile.getName() + " (" + new AppUtils().calculateAge(jsonProfile.getBirthday()) + ", " + jsonProfile.getGender().name() + ")");
        tv_address.setText(jsonProfile.getAddress());
        tv_details.setText(Html.fromHtml("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg"));
        loadProfilePic(jsonProfile.getProfileImage());
    }

    @Override
    public void patientProfileError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
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
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.reverse(jsonMedicalRecords);
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, jsonMedicalRecords);
        listview.setAdapter(adapter);
        if(null == jsonMedicalRecords ||jsonMedicalRecords.size() == 0){
            tv_empty_list.setVisibility(View.VISIBLE);
        }
        dismissProgress();
    }

    @Override
    public void medicalRecordListError() {
        dismissProgress();
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        jsonMedicalRecordTemp = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            try {
                if(null != jsonMedicalRecord.getMedicalPhysical()) {
                    if (null != jsonMedicalRecord.getMedicalPhysical().getRespiratory()) {
                        tv_respiration.setText(jsonMedicalRecord.getMedicalPhysical().getRespiratory());
                    } else {
                        tv_respiration.setText(notAvailable);
                    }
                    if (null != jsonMedicalRecord.getMedicalPhysical().getHeight()) {
                        tv_height.setText(jsonMedicalRecord.getMedicalPhysical().getHeight());
                    } else {
                        tv_height.setText(notAvailable);
                    }
                    if (null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                        tv_pulse.setText(jsonMedicalRecord.getMedicalPhysical().getPulse());
                    } else {
                        tv_pulse.setText(notAvailable);
                    }
                    if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                        tv_bp.setText(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0] + "/" + jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]);
                    } else {
                        tv_bp.setText(notAvailable);
                    }
                    if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                        tv_weight.setText(jsonMedicalRecord.getMedicalPhysical().getWeight());
                    } else {
                        tv_weight.setText(notAvailable);
                    }
                    if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                        tv_temperature.setText(jsonMedicalRecord.getMedicalPhysical().getTemperature());
                    } else {
                        tv_temperature.setText(notAvailable);
                    }
                }else{
                    tv_pulse.setText(notAvailable);
                    tv_bp.setText(notAvailable);
                    tv_weight.setText(notAvailable);
                    tv_temperature.setText(notAvailable);
                    tv_respiration.setText(notAvailable);
                    tv_height.setText(notAvailable);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dismissProgress();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void loadProfilePic(String imageUrl) {
        Picasso.with(this).load(R.drawable.profile_avatar).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(this)
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + imageUrl)
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(this, getString(R.string.exit_medical_screen), Toast.LENGTH_LONG);
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
        }
    }
}
