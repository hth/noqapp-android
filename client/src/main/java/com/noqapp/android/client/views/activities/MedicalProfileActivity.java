package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCall;
import com.noqapp.android.client.presenter.MedicalRecordProfilePresenter;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserMedicalProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalProfile;
import com.noqapp.android.common.model.types.medical.BloodTypeEnum;
import com.noqapp.android.common.model.types.medical.OccupationEnum;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

public class MedicalProfileActivity extends BaseActivity implements MedicalRecordProfilePresenter, View.OnClickListener {

    private TextView tv_weight, tv_pulse, tv_temperature, tv_height, tv_bp, tv_respiration;
    private TextView tv_medicine_allergy, tv_family_history, tv_past_history, tv_known_allergy, tv_blood_type_update_msg;
    private SegmentedControl sc_blood_type;
    private ArrayList<String> sc_blood_type_data = new ArrayList<>();
    private SegmentedControl sc_occupation_type;
    private ArrayList<String> sc_occupation_type_data = new ArrayList<>();
    private JsonMedicalProfile jsonMedicalProfile;
    private UserMedicalProfileApiCall userMedicalProfileApiCall;
    private ImageView iv_edit_blood_type, iv_edit_medical_history;
    private TextView tv_update_blood_type, tv_update_medical_history, tv_cancel_medical_history;
    private MedicalProfile medicalProfile;
    private EditText edt_medicine_allergy, edt_known_allergy, edt_past_history, edt_family_history;

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

        ImageView iv_profile = findViewById(R.id.iv_profile);
        TextView tv_patient_name = findViewById(R.id.tv_patient_name);
        TextView tv_patient_age_gender = findViewById(R.id.tv_patient_age_gender);
        tv_medicine_allergy = findViewById(R.id.tv_medicine_allergy);
        tv_family_history = findViewById(R.id.tv_family_history);
        tv_past_history = findViewById(R.id.tv_past_history);
        tv_known_allergy = findViewById(R.id.tv_known_allergy);
        tv_blood_type_update_msg = findViewById(R.id.tv_blood_type_update_msg);
        sc_blood_type = findViewById(R.id.sc_blood_type);
        iv_edit_blood_type = findViewById(R.id.iv_edit_blood_type);
        iv_edit_medical_history = findViewById(R.id.iv_edit_medical_history);
        tv_update_blood_type = findViewById(R.id.tv_update_blood_type);
        tv_update_medical_history = findViewById(R.id.tv_update_medical_history);
        tv_cancel_medical_history = findViewById(R.id.tv_cancel_medical_history);


        edt_medicine_allergy = findViewById(R.id.edt_medicine_allergy);
        edt_known_allergy = findViewById(R.id.edt_known_allergy);
        edt_past_history = findViewById(R.id.edt_past_history);
        edt_family_history = findViewById(R.id.edt_family_history);


        iv_edit_blood_type.setOnClickListener(this);
        iv_edit_medical_history.setOnClickListener(this);
        tv_update_blood_type.setOnClickListener(this);
        tv_update_medical_history.setOnClickListener(this);
        tv_cancel_medical_history.setOnClickListener(this);
        sc_blood_type_data.clear();
        sc_blood_type_data.addAll(BloodTypeEnum.asListOfDescription());
        sc_blood_type.addSegments(sc_blood_type_data);

        sc_occupation_type = findViewById(R.id.sc_occupation_type);
        sc_occupation_type_data.clear();
        sc_occupation_type_data.addAll(OccupationEnum.asListOfDescription());
        sc_occupation_type.addSegments(sc_occupation_type_data);
        JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
        medicalProfile = (MedicalProfile) getIntent().getSerializableExtra("medicalProfile");

        AppUtilities.loadProfilePic(iv_profile, jsonProfile.getProfileImage(), this);
        tv_patient_name.setText(jsonProfile.getName());
        tv_patient_age_gender.setText(new AppUtilities().calculateAge(jsonProfile.getBirthday()) + " (" + jsonProfile.getGender() + ")");
        userMedicalProfileApiCall = new UserMedicalProfileApiCall();
        userMedicalProfileApiCall.setMedicalRecordProfilePresenter(this);
        if (NetworkUtils.isConnectingToInternet(this)) {
            if (UserUtils.isLogin()) {
                userMedicalProfileApiCall.medicalProfile(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
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
        this.jsonMedicalProfile = jsonMedicalProfile;
        showHideMedicalEdit(false);
        if (null != jsonMedicalProfile) {
            JsonUserMedicalProfile jsonUserMedicalProfile = jsonMedicalProfile.getJsonUserMedicalProfile();
            tv_medicine_allergy.setText(jsonUserMedicalProfile.getMedicineAllergies());
            tv_known_allergy.setText(jsonUserMedicalProfile.getKnownAllergies());
            tv_family_history.setText(jsonUserMedicalProfile.getFamilyHistory());
            tv_past_history.setText(jsonUserMedicalProfile.getPastHistory());
            if (null != jsonUserMedicalProfile.getBloodType()) {
                int index = sc_blood_type_data.indexOf(jsonUserMedicalProfile.getBloodType().getDescription());
                if (-1 != index) {
                    sc_blood_type.setSelectedSegment(index);
                    sc_blood_type.setEnabled(false);
                    iv_edit_blood_type.setVisibility(View.INVISIBLE);
                    tv_update_blood_type.setVisibility(View.GONE);
                    tv_blood_type_update_msg.setVisibility(View.VISIBLE);
                } else {
                    sc_blood_type.setEnabled(true);
                    iv_edit_blood_type.setVisibility(View.VISIBLE);
                    tv_blood_type_update_msg.setVisibility(View.GONE);
                }
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
            if(null != jsonMedicalProfile.getJsonMedicalPhysicals())
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
                if (isBloodPressure && isHeight && isPulse && isRR && isTemperature && isWeigth)
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_edit_medical_history:
                showHideMedicalEdit(true);
                break;
            case R.id.tv_cancel_medical_history:
                showHideMedicalEdit(false);
                break;
            case R.id.tv_update_medical_history: {
                if (TextUtils.isEmpty(edt_medicine_allergy.getText().toString())
                        && TextUtils.isEmpty(edt_family_history.getText().toString())
                        && TextUtils.isEmpty(edt_past_history.getText().toString())
                        && TextUtils.isEmpty(edt_known_allergy.getText().toString())) {
                    Toast.makeText(this, "Edit at least one field", Toast.LENGTH_SHORT).show();
                } else {
                    JsonUserMedicalProfile jump;
                    if (null == jsonMedicalProfile) {
                        // In case of no medical record created jump is null
                        jump = new JsonUserMedicalProfile();
                    } else {
                        jump = jsonMedicalProfile.getJsonUserMedicalProfile();
                    }
                    if (!TextUtils.isEmpty(edt_medicine_allergy.getText().toString())) {
                        jump.setMedicineAllergies(edt_medicine_allergy.getText().toString());
                    }
                    if (!TextUtils.isEmpty(edt_family_history.getText().toString())) {
                        jump.setFamilyHistory(edt_family_history.getText().toString());
                    }
                    if (!TextUtils.isEmpty(edt_past_history.getText().toString())) {
                        jump.setPastHistory(edt_past_history.getText().toString());
                    }
                    if (!TextUtils.isEmpty(edt_known_allergy.getText().toString())) {
                        jump.setKnownAllergies(edt_known_allergy.getText().toString());
                    }
                    medicalProfile.setJsonUserMedicalProfile(jump);
                    userMedicalProfileApiCall.updateUserMedicalProfile(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
                    progressDialog.setMessage("Updating medical history....");
                    progressDialog.show();
                }
            }
            break;
            case R.id.iv_edit_blood_type:
                tv_update_blood_type.setVisibility(View.VISIBLE);
                iv_edit_blood_type.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_black));
                break;
            case R.id.tv_update_blood_type: {
                if (-1 == sc_blood_type.getSelectedAbsolutePosition()) {
                    Toast.makeText(this, "Please select blood type to update ", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtils.isConnectingToInternet(this)) {
                        ShowCustomDialog showDialog = new ShowCustomDialog(MedicalProfileActivity.this, true);
                        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                try {
                                    JsonUserMedicalProfile jump;
                                    if (null == jsonMedicalProfile) {
                                        // In case of no medical record created jump is null
                                        jump = new JsonUserMedicalProfile();
                                    } else {
                                        jump = jsonMedicalProfile.getJsonUserMedicalProfile();
                                    }
                                    jump.setBloodType(BloodTypeEnum.getEnum(sc_blood_type_data.get(sc_blood_type.getSelectedAbsolutePosition())));
                                    medicalProfile.setJsonUserMedicalProfile(jump);
                                    userMedicalProfileApiCall.updateUserMedicalProfile(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
                                    progressDialog.setMessage("Updating blood type....");
                                    progressDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void btnNegativeClick() {
                                tv_update_blood_type.setVisibility(View.GONE);
                                iv_edit_blood_type.setBackground(ContextCompat.getDrawable(MedicalProfileActivity.this, R.drawable.edit_orange));
                                sc_blood_type.clearSelection();
                            }
                        });
                        showDialog.displayDialog("Alert", "Blood Type change is permanent. Future change will be performed only by medical practitioner.");
                    } else {
                        ShowAlertInformation.showNetworkDialog(this);
                    }
                }
            }
            break;
        }
    }

    private void showHideMedicalEdit(boolean isShown) {
        tv_update_medical_history.setVisibility(isShown ? View.VISIBLE : View.GONE);
        tv_cancel_medical_history.setVisibility(isShown ? View.VISIBLE : View.GONE);
        edt_medicine_allergy.setVisibility(isShown ? View.VISIBLE : View.GONE);
        edt_family_history.setVisibility(isShown ? View.VISIBLE : View.GONE);
        edt_past_history.setVisibility(isShown ? View.VISIBLE : View.GONE);
        edt_known_allergy.setVisibility(isShown ? View.VISIBLE : View.GONE);
        if (isShown) {
            edt_medicine_allergy.setText(tv_medicine_allergy.getText().toString());
            edt_family_history.setText(tv_family_history.getText().toString());
            edt_past_history.setText(tv_past_history.getText().toString());
            edt_known_allergy.setText(tv_known_allergy.getText().toString());
            iv_edit_medical_history.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_black));
        } else {
            edt_medicine_allergy.setText("");
            edt_family_history.setText("");
            edt_past_history.setText("");
            edt_known_allergy.setText("");
            iv_edit_medical_history.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_orange));
        }
    }
}
