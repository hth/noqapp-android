package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonMedicalRecordList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.interfaces.PatientProfilePresenter;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.model.PatientProfileApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.merchant.views.interfaces.MedicalRecordListPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PatientProfileActivity extends BaseActivity implements
        PatientProfilePresenter, MedicalRecordListPresenter, JsonMedicalRecordPresenter {
    private long lastPress;
    private Toast backPressToast;
    public ProgressBar pb_physical, pb_history;
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
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    public static PatientProfileActivity patientProfileActivity;


    public static PatientProfileActivity getPatientProfileActivity() {
        return patientProfileActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        patientProfileActivity = this;
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
        pb_physical = findViewById(R.id.pb_physical);
        pb_history = findViewById(R.id.pb_history);
        TextView tv_start_diagnosis = findViewById(R.id.tv_start_diagnosis);
        tv_start_diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == jsonProfile || null == jsonMedicalRecordTemp){
                    new CustomToast().showToast(PatientProfileActivity.this,"Please wait while patient data is loading...");
                }else {
                    Intent intent = new Intent(PatientProfileActivity.this, MedicalCaseActivity.class);
                    intent.putExtra("qCodeQR", codeQR);
                    intent.putExtra("data", jsonQueuedPerson);
                    intent.putExtra("jsonMedicalRecord", jsonMedicalRecordTemp);
                    intent.putExtra("jsonProfile", jsonProfile);
                    intent.putExtra("bizCategoryId", getIntent().getStringExtra("bizCategoryId"));
                    startActivity(intent);
                    finish();
                }
            }
        });

        Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            new AsyncTaskTemp().execute();
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    class AsyncTaskTemp extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_history.setVisibility(View.VISIBLE);
            pb_physical.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PatientProfileApiCalls profileModel = new PatientProfileApiCalls(PatientProfileActivity.this);
            profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
            medicalHistoryApiCalls.historical(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));

            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setCodeQR(codeQR);
            medicalHistoryApiCalls.setJsonMedicalRecordPresenter(PatientProfileActivity.this);
            medicalHistoryApiCalls.retrieveMedicalRecord(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        updateUI(jsonProfile);
    }

    private void updateUI(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
        tv_patient_name.setText(jsonProfile.getName() + " (" + new AppUtils().calculateAge(jsonProfile.getBirthday()) + ", " + jsonProfile.getGender().name() + ")");
        tv_address.setText(jsonProfile.getAddress());

        loadProfilePic(jsonProfile.getProfileImage());
    }

    @Override
    public void patientProfileError() {
        pb_physical.setVisibility(View.GONE);
    }

    @Override
    public void authenticationFailure() {
        pb_physical.setVisibility(View.GONE);
        pb_history.setVisibility(View.GONE);
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        pb_physical.setVisibility(View.GONE);
        pb_history.setVisibility(View.GONE);
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        pb_physical.setVisibility(View.GONE);
        pb_history.setVisibility(View.GONE);
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.sort(jsonMedicalRecords, new Comparator<JsonMedicalRecord>() {
            public int compare(JsonMedicalRecord o1, JsonMedicalRecord o2) {
                try {
                    return CommonHelper.SDF_YYYY_MM_DD.parse(o2.getCreateDate()).compareTo(CommonHelper.SDF_YYYY_MM_DD.parse(o1.getCreateDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(this, jsonMedicalRecords);
        listview.setAdapter(adapter);
        if (null == jsonMedicalRecords || jsonMedicalRecords.size() == 0) {
            tv_empty_list.setVisibility(View.VISIBLE);
        }
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void medicalRecordListError() {
        pb_history.setVisibility(View.GONE);
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        jsonMedicalRecordTemp = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            try {
                if (null != jsonMedicalRecord.getJsonUserMedicalProfile() && null != jsonMedicalRecord.getJsonUserMedicalProfile().getBloodType()) {
                    tv_details.setText(Html.fromHtml("<b> Blood Group: </b>" + jsonMedicalRecord.getJsonUserMedicalProfile().getBloodType().getDescription()));
                } else {
                    tv_details.setText(Html.fromHtml("<b> Blood Group: </b> N/A"));
                }
                if (null != jsonMedicalRecord.getMedicalPhysical()) {
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
                } else {
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
        pb_physical.setVisibility(View.GONE);
    }

    private void loadProfilePic(String imageUrl) {
        Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
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
            backPressToast = new CustomToast().getToast(this, getString(R.string.exit_medical_screen));
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

    public void updateList(){
        pb_history.setVisibility(View.VISIBLE);
        PatientProfileApiCalls profileModel = new PatientProfileApiCalls(PatientProfileActivity.this);
        profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
        medicalHistoryApiCalls.historical(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));


    }
}
