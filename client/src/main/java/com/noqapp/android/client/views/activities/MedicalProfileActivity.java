package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCall;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.UserMedicalProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserMedicalProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;
import com.noqapp.android.common.model.types.medical.BloodTypeEnum;
import com.noqapp.android.common.model.types.medical.OccupationEnum;

import java.util.ArrayList;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class MedicalProfileActivity extends BaseActivity implements MedicalRecordProfilePresenter {

    private TextView tv_weight, tv_pulse, tv_temperature, tv_height, tv_bp, tv_respiration;
    private TextView tv_patient_name, tv_patient_age_gender, tv_medicine_allergy, tv_family_history, tv_past_history, tv_known_allergy;
    private ImageView iv_profile;
    private SegmentedControl sc_blood_type;
    private ArrayList<String> sc_blood_type_data = new ArrayList<>();
    private SegmentedControl sc_occupation_type;
    private ArrayList<String> sc_occupation_type_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_profile);
        initActionsViews(false);
        tv_toolbar_title.setText(getString(R.string.medical_profile));
        tv_weight = findViewById(R.id.tv_weight);
        tv_pulse = findViewById(R.id.tv_pulse);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_height = findViewById(R.id.tv_height);
        tv_bp = findViewById(R.id.tv_bp);
        tv_respiration = findViewById(R.id.tv_respiration);

        iv_profile = findViewById(R.id.iv_profile);
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_patient_age_gender = findViewById(R.id.tv_patient_age_gender);
        tv_medicine_allergy = findViewById(R.id.tv_medicine_allergy);
        tv_family_history = findViewById(R.id.tv_family_history);
        tv_past_history = findViewById(R.id.tv_past_history);
        tv_known_allergy = findViewById(R.id.tv_known_allergy);
        sc_blood_type = findViewById(R.id.sc_blood_type);
        sc_blood_type_data.clear();
        sc_blood_type_data.addAll(BloodTypeEnum.asListOfDescription());
        sc_blood_type.addSegments(sc_blood_type_data);

        sc_occupation_type = findViewById(R.id.sc_occupation_type);
        sc_occupation_type_data.clear();
        sc_occupation_type_data.addAll(OccupationEnum.asListOfDescription());
        sc_occupation_type.addSegments(sc_occupation_type_data);
        JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
        AppUtilities.loadProfilePic(iv_profile, jsonProfile.getProfileImage(), this);
        tv_patient_name.setText(jsonProfile.getName());
        tv_patient_age_gender.setText(new AppUtilities().calculateAge(jsonProfile.getBirthday()) + " (" + jsonProfile.getGender() + ")");

        if (NetworkUtils.isConnectingToInternet(this)) {
            if (UserUtils.isLogin()) {
                UserMedicalProfileApiCall userMedicalProfileApiCall = new UserMedicalProfileApiCall();
                userMedicalProfileApiCall.setMedicalRecordProfilePresenter(this);
                UserMedicalProfile userMedicalProfile = (UserMedicalProfile) getIntent().getSerializableExtra("userMedicalProfile");
                userMedicalProfileApiCall.medicalProfile(UserUtils.getEmail(), UserUtils.getAuth(), userMedicalProfile);
                progressDialog.show();
            } else {
                Toast.makeText(this, "Please login to see the details", Toast.LENGTH_LONG).show();
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            new ErrorResponseHandler().processError(this, eej);
//            if(eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.MEDICAL_PROFILE_DOES_NOT_EXISTS.getCode())){
//                finish();
//            }
        }
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }


    @Override
    public void medicalRecordProfileResponse(JsonMedicalProfile jsonMedicalProfile) {
        if (null != jsonMedicalProfile && jsonMedicalProfile.getJsonMedicalPhysicals().size() > 0) {
            JsonUserMedicalProfile jsonUserMedicalProfile = jsonMedicalProfile.getJsonUserMedicalProfile();
            tv_medicine_allergy.setText(jsonUserMedicalProfile.getMedicineAllergies());
            tv_known_allergy.setText(jsonUserMedicalProfile.getKnownAllergies());
            tv_family_history.setText(jsonUserMedicalProfile.getFamilyHistory());
            tv_past_history.setText(jsonUserMedicalProfile.getPastHistory());
            if (null != jsonUserMedicalProfile.getBloodType()) {
                int index = sc_blood_type_data.indexOf(jsonUserMedicalProfile.getBloodType().getDescription());
                if (-1 != index)
                    sc_blood_type.setSelectedSegment(index);
            }
            if (null != jsonUserMedicalProfile.getOccupation()) {
                int index = sc_occupation_type_data.indexOf(jsonUserMedicalProfile.getOccupation().getDescription());
                if (-1 != index)
                    sc_occupation_type.setSelectedSegment(index);
            }
            Log.e("Data", jsonMedicalProfile.toString());
            String notAvailable = "N/A";

            boolean isPulse = false, isBloodPressure = false, isTemperature = false, isWeigth = false, isHeight = false, isRR = false;
            String pulse = "", bloodpressure = "", temperature = "", weight = "", height = "", rr = "";
            for (int i = 0; i < jsonMedicalProfile.getJsonMedicalPhysicals().size(); i++) {
                JsonMedicalPhysical jsonMedicalPhysical = jsonMedicalProfile.getJsonMedicalPhysicals().get(i);
                if (isPulse) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getPulse()) {
                        pulse = jsonMedicalPhysical.getPulse();
                        isPulse = true;
                    }
                }
                if (isBloodPressure) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getBloodPressure() && jsonMedicalPhysical.getBloodPressure().length == 2) {
                        bloodpressure = jsonMedicalPhysical.getBloodPressure()[0] + "/" + jsonMedicalPhysical.getBloodPressure()[1];
                        isBloodPressure = true;
                    }
                }
                if (isTemperature) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getTemperature()) {
                        temperature = jsonMedicalPhysical.getTemperature();
                        isTemperature = true;
                    }
                }
                if (isWeigth) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getWeight()) {
                        weight = jsonMedicalPhysical.getWeight();
                        isWeigth = true;
                    }
                }
                if (isHeight) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getHeight()) {
                        height = jsonMedicalPhysical.getHeight();
                        isHeight = true;
                    }
                }
                if (isRR) {
                    //Do nothing
                } else {
                    if (null != jsonMedicalPhysical.getRespiratory()) {
                        rr = jsonMedicalPhysical.getRespiratory();
                        isRR = true;
                    }
                }
                if(isBloodPressure && isHeight && isPulse && isRR && isTemperature && isWeigth)
                    break;

            }
            tv_pulse.setText(TextUtils.isEmpty(pulse) ? notAvailable : pulse);
            tv_bp.setText(TextUtils.isEmpty(bloodpressure) ? notAvailable : bloodpressure);
            tv_weight.setText(TextUtils.isEmpty(weight) ? notAvailable : weight);
            tv_temperature.setText(TextUtils.isEmpty(temperature) ? notAvailable : temperature);
            tv_respiration.setText(TextUtils.isEmpty(rr) ? notAvailable : rr);
            tv_height.setText(TextUtils.isEmpty(height) ? notAvailable : height);
        }
        dismissProgress();
    }
}
