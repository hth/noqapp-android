package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCall;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.UserMedicalProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonUserMedicalProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;
import com.noqapp.android.common.model.types.medical.BloodTypeEnum;
import com.noqapp.android.common.model.types.medical.OccupationEnum;
import com.squareup.picasso.Picasso;

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
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
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

            JsonMedicalPhysical jsonMedicalPhysical = jsonMedicalProfile.getJsonMedicalPhysicals().get(0);
            JsonUserMedicalProfile jsonUserMedicalProfile = jsonMedicalProfile.getJsonUserMedicalProfile();
            if (null != jsonMedicalPhysical) {
                Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
                try {
                    if (!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                        Picasso.get()
                                .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, NoQueueBaseActivity.getUserProfileUri()))
                                .placeholder(ImageUtils.getProfilePlaceholder(this))
                                .error(ImageUtils.getProfileErrorPlaceholder(this))
                                .into(iv_profile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tv_patient_name.setText(NoQueueBaseActivity.getUserName());
            tv_patient_age_gender.setText(new AppUtilities().calculateAge(NoQueueBaseActivity.getUserDOB()) + " (" + NoQueueBaseActivity.getGender() + ")");
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
            if (null != jsonMedicalPhysical) {
                if (null != jsonMedicalPhysical.getRespiratory()) {
                    tv_respiration.setText(jsonMedicalPhysical.getRespiratory());
                } else {
                    tv_respiration.setText(notAvailable);
                }
                if (null != jsonMedicalPhysical.getHeight()) {
                    tv_height.setText(jsonMedicalPhysical.getHeight());
                } else {
                    tv_height.setText(notAvailable);
                }
                if (null != jsonMedicalPhysical.getPulse()) {
                    tv_pulse.setText(jsonMedicalPhysical.getPulse());
                } else {
                    tv_pulse.setText(notAvailable);
                }
                if (null != jsonMedicalPhysical.getBloodPressure() && jsonMedicalPhysical.getBloodPressure().length == 2) {
                    tv_bp.setText(jsonMedicalPhysical.getBloodPressure()[0] + "/" + jsonMedicalPhysical.getBloodPressure()[1]);
                } else {
                    tv_bp.setText(notAvailable);
                }
                if (null != jsonMedicalPhysical.getWeight()) {
                    tv_weight.setText(jsonMedicalPhysical.getWeight());
                } else {
                    tv_weight.setText(notAvailable);
                }
                if (null != jsonMedicalPhysical.getTemperature()) {
                    tv_temperature.setText(jsonMedicalPhysical.getTemperature());
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
        }
        dismissProgress();
    }
}
