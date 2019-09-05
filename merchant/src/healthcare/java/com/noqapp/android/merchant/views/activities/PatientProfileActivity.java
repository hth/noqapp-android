package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
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
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.WorkDoneAdapter;
import com.noqapp.android.merchant.views.fragments.DentalStatusFragment;
import com.noqapp.android.merchant.views.fragments.MedicalHistoryFilteredFragment;
import com.noqapp.android.merchant.views.fragments.MedicalHistoryFragment;
import com.noqapp.android.merchant.views.pojos.ToothWorkDone;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PatientProfileActivity extends BaseActivity implements
        PatientProfilePresenter, JsonMedicalRecordPresenter {
    private long lastPress;
    private Toast backPressToast;
    public ProgressBar pb_physical;
    private TextView tv_patient_name, tv_address, tv_details;
    private ImageView iv_profile;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_height, tv_bp, tv_respiration;
    private JsonQueuedPerson jsonQueuedPerson;
    private String codeQR;
    private final String notAvailable = "N/A";
    private JsonMedicalRecord jsonMedicalRecordTemp;
    private JsonProfile jsonProfile;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private LinearLayout ll_dental_history;
    private boolean isDental = false;
    private String bizCategoryId = "";
    private ListView list_view;
    private TextView tv_empty_work_done;
    private ArrayList<ToothWorkDone> toothWorkDoneList = new ArrayList<>();
    private WorkDoneAdapter workDoneAdapter;

    public static PatientProfileActivity getPatientProfileActivity() {
        return patientProfileActivity;
    }

    public static PatientProfileActivity patientProfileActivity;


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
        bizCategoryId = getIntent().getStringExtra("bizCategoryId");
        isDental = MedicalDepartmentEnum.valueOf(bizCategoryId) == MedicalDepartmentEnum.DNT;

        ll_dental_history = findViewById(R.id.ll_dental_history);
        list_view = findViewById(R.id.list_view);
        tv_empty_work_done = findViewById(R.id.tv_empty_work_done);
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

        pb_physical = findViewById(R.id.pb_physical);
        TextView tv_start_diagnosis = findViewById(R.id.tv_start_diagnosis);
        tv_start_diagnosis.setOnClickListener(v -> {
            if (null == jsonProfile || null == jsonMedicalRecordTemp) {
                new CustomToast().showToast(PatientProfileActivity.this, "Please wait while patient data is loading...");
            } else {
                if (MedicalDepartmentEnum.valueOf(getIntent().getStringExtra("bizCategoryId")) == MedicalDepartmentEnum.SPS) {
                    Intent intent = new Intent(PatientProfileActivity.this, NeuroActivity.class);
                    intent.putExtra("qCodeQR", codeQR);
                    intent.putExtra("data", jsonQueuedPerson);
                    intent.putExtra("jsonMedicalRecord", jsonMedicalRecordTemp);
                    startActivity(intent);
                } else if (MedicalDepartmentEnum.valueOf(getIntent().getStringExtra("bizCategoryId")) == MedicalDepartmentEnum.NEU) {
                    Intent intent = new Intent(PatientProfileActivity.this, NeuroFullPageActivity.class);
                    intent.putExtra("qCodeQR", codeQR);
                    intent.putExtra("data", jsonQueuedPerson);
                    intent.putExtra("jsonMedicalRecord", jsonMedicalRecordTemp);
                    startActivity(intent);
                } else {
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
        TextView tv_hospital_schedule = findViewById(R.id.tv_hospital_schedule);
        tv_hospital_schedule.setOnClickListener(v -> {
            if (null == jsonProfile || null == jsonMedicalRecordTemp) {
                new CustomToast().showToast(PatientProfileActivity.this, "Please wait while patient data is loading...");
            } else {
                Intent intent = new Intent(PatientProfileActivity.this, HospitalVisitScheduleActivity.class);
                intent.putExtra("qCodeQR", codeQR);
                intent.putExtra("data", jsonQueuedPerson);
                intent.putExtra("jsonMedicalRecord", jsonMedicalRecordTemp);
                startActivity(intent);
            }
        });
        TextView tv_draw = findViewById(R.id.tv_draw);
        tv_draw.setVisibility(AppUtils.isRelease() ? View.GONE : View.VISIBLE);
        tv_draw.setOnClickListener(v -> {
            if (null == jsonProfile || null == jsonMedicalRecordTemp) {
                new CustomToast().showToast(PatientProfileActivity.this, "Please wait while patient data is loading...");
            } else {
                PermissionHelper permissionHelper = new PermissionHelper(PatientProfileActivity.this);
                if (permissionHelper.isStoragePermissionAllowed()) {
                    Intent intent = new Intent(PatientProfileActivity.this, DrawActivity.class);
                    intent.putExtra("qCodeQR", codeQR);
                    intent.putExtra("data", jsonQueuedPerson);
                    intent.putExtra("jsonMedicalRecord", jsonMedicalRecordTemp);
                    startActivity(intent);
                } else {
                    permissionHelper.requestStoragePermission();
                }
            }
        });

        Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
        medicalHistoryApiCalls = new MedicalHistoryApiCalls();
        medicalHistoryApiCalls.setJsonMedicalRecordPresenter(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            new AsyncTaskTemp().execute();
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

        FrameLayout fl_medical_history = findViewById(R.id.fl_medical_history);
        FrameLayout fl_medical_history_filtered = findViewById(R.id.fl_medical_history_filtered);
        TextView tv_history_all = findViewById(R.id.tv_history_all);
        TextView tv_history_filtered = findViewById(R.id.tv_history_filtered);
        tv_history_filtered.setOnClickListener(v -> {
            fl_medical_history_filtered.setVisibility(View.VISIBLE);
            fl_medical_history.setVisibility(View.GONE);
            tv_history_all.setTextColor(ContextCompat.getColor(PatientProfileActivity.this, R.color.white));
            tv_history_all.setBackground(null);
            tv_history_filtered.setTextColor(ContextCompat.getColor(PatientProfileActivity.this, R.color.pressed_color));
            tv_history_filtered.setBackground(ContextCompat.getDrawable(PatientProfileActivity.this, R.drawable.button_drawable_white));

        });
        tv_history_all.setOnClickListener(v -> {
            fl_medical_history_filtered.setVisibility(View.GONE);
            fl_medical_history.setVisibility(View.VISIBLE);
            tv_history_filtered.setTextColor(ContextCompat.getColor(PatientProfileActivity.this, R.color.white));
            tv_history_filtered.setBackground(null);
            tv_history_all.setTextColor(ContextCompat.getColor(PatientProfileActivity.this, R.color.pressed_color));
            tv_history_all.setBackground(ContextCompat.getDrawable(PatientProfileActivity.this, R.drawable.button_drawable_white));
        });
    }

    class AsyncTaskTemp extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_physical.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PatientProfileApiCalls profileModel = new PatientProfileApiCalls(PatientProfileActivity.this);
            profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
            MedicalHistoryFragment mhf = new MedicalHistoryFragment();
            Bundle b = new Bundle();
            b.putString("codeQR", codeQR);
            b.putSerializable("jsonQueuedPerson", jsonQueuedPerson);
            b.putString("bizCategoryId", bizCategoryId);
            mhf.setArguments(b);
            replaceFragmentWithoutBackStack(R.id.fl_medical_history, mhf);

            MedicalHistoryFilteredFragment mhff = new MedicalHistoryFilteredFragment();
            mhff.setArguments(b);
            replaceFragmentWithoutBackStack(R.id.fl_medical_history_filtered, mhff);


            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
            jsonMedicalRecord.setCodeQR(codeQR);
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
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        pb_physical.setVisibility(View.GONE);
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        pb_physical.setVisibility(View.GONE);
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
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

                if (isDental) {
                    ll_dental_history.setVisibility(View.VISIBLE);
                    Bundle b = new Bundle();
                    b.putSerializable("jsonMedicalRecord", jsonMedicalRecordTemp);
                    DentalStatusFragment dentalStatusFragment = new DentalStatusFragment();
                    dentalStatusFragment.setArguments(b);
                    replaceFragmentWithoutBackStack(R.id.fl_dental_history, dentalStatusFragment);
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

    public void updateWorkDone(List<JsonMedicalRecord> jsonMedicalRecords) {
        toothWorkDoneList.clear();
        for (int i = 0; i < jsonMedicalRecords.size(); i++) {
            JsonMedicalRecord jsonMedicalRecord = jsonMedicalRecords.get(i);
            String createdDate = "";
            try {
                createdDate = CommonHelper.SDF_YYYY_MM_DD.format(CommonHelper.SDF_ISO8601_FMT.parse(jsonMedicalRecord.getCreateDate()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(jsonMedicalRecord.getNoteToDiagnoser())) {
                parseWorkDoneData(jsonMedicalRecord.getNoteToDiagnoser(), createdDate);
            }
        }
        workDoneAdapter = new WorkDoneAdapter(this, toothWorkDoneList);
        list_view.setAdapter(workDoneAdapter);
        tv_empty_work_done.setVisibility(toothWorkDoneList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void parseWorkDoneData(String str, String createdDate) {
        try {
            String[] temp = str.split("\\|", -1);
            if (temp.length > 0) {
                for (int i = 0; i < temp.length; i++) {
                    String act = temp[i];
                    if (act.contains(":")) {
                        String[] strArray = act.split(":", -1);
                        String toothNum = strArray[0].trim();
                        String procedure = strArray[1];
                        String summary = strArray.length == 3 ? strArray[2] : "";
                        if (strArray.length > 3) {
                            String status = strArray[3].trim();
                            String unit = strArray[4];
                            String period = strArray[5];
                            toothWorkDoneList.add(new ToothWorkDone(toothNum, procedure, summary, status, unit, period, createdDate));
                        } else {
                            toothWorkDoneList.add(new ToothWorkDone(toothNum, procedure, "", createdDate));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}