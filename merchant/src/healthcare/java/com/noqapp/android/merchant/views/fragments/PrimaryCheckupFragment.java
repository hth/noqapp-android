package com.noqapp.android.merchant.views.fragments;


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
import com.noqapp.android.merchant.presenter.beans.MedicalRecordListPresenter;
import com.noqapp.android.merchant.presenter.beans.body.FindMedicalProfile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.customviews.MeterView;

import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimaryCheckupFragment extends Fragment implements PatientProfilePresenter, MedicalRecordListPresenter, JsonMedicalRecordPresenter, MeterView.MeterViewValueChanged {

    private ProgressDialog progressDialog;
    private TextView tv_patient_name, tv_address, tv_details;
    private ImageView iv_profile;
    private AutoCompleteTextView actv_past_history, actv_known_allergy, actv_family_history;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private MeterView mv_weight1, mv_weight2, mv_pulse, mv_temperature1, mv_temperature2, mv_oxygen;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_oxygen, tv_bp_high, tv_bp_low;
    private DiscreteSeekBar dsb_bp_low, dsb_bp_high;
    private SwitchCompat sc_enable_pulse, sc_enable_temp, sc_enable_weight, sc_enable_oxygen, sc_enable_bp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_primary_checkup, container, false);
        tv_patient_name = v.findViewById(R.id.tv_patient_name);
        tv_address = v.findViewById(R.id.tv_address);
        tv_details = v.findViewById(R.id.tv_details);
        iv_profile = v.findViewById(R.id.iv_profile);
        actv_past_history = v.findViewById(R.id.actv_past_history);
        actv_family_history = v.findViewById(R.id.actv_family_history);
        actv_known_allergy = v.findViewById(R.id.actv_known_allergy);
        mv_weight1 = v.findViewById(R.id.mv_weight1);
        mv_weight2 = v.findViewById(R.id.mv_weight2);
        mv_pulse = v.findViewById(R.id.mv_pulse);
        mv_temperature2 = v.findViewById(R.id.mv_temperature2);
        mv_temperature1 = v.findViewById(R.id.mv_temperature1);
        mv_oxygen = v.findViewById(R.id.mv_oxygen);


        tv_weight = v.findViewById(R.id.tv_weight);
        tv_pulse = v.findViewById(R.id.tv_pulse);
        tv_temperature = v.findViewById(R.id.tv_temperature);
        tv_oxygen = v.findViewById(R.id.tv_oxygen);
        tv_bp_high = v.findViewById(R.id.tv_bp_high);
        tv_bp_low = v.findViewById(R.id.tv_bp_low);

        sc_enable_pulse = v.findViewById(R.id.sc_enable_pulse);
        sc_enable_temp = v.findViewById(R.id.sc_enable_temp);
        sc_enable_weight = v.findViewById(R.id.sc_enable_weight);
        sc_enable_oxygen = v.findViewById(R.id.sc_enable_oxygen);
        sc_enable_bp = v.findViewById(R.id.sc_enable_bp);

        sc_enable_pulse.setChecked(false);
        sc_enable_temp.setChecked(false);
        sc_enable_weight.setChecked(false);
        sc_enable_oxygen.setChecked(false);
        sc_enable_bp.setChecked(false);

        final Button ll_pulse_disable = v.findViewById(R.id.ll_pulse_disable);
        final Button ll_temp_disable = v.findViewById(R.id.ll_temp_disable);
        final Button ll_weight_disable = v.findViewById(R.id.ll_weight_disable);
        final Button ll_oxygen_disable = v.findViewById(R.id.ll_oxygen_disable);
        final Button ll_bp_disable = v.findViewById(R.id.ll_bp_disable);
        sc_enable_pulse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_pulse_disable.setVisibility(View.GONE);
                } else {
                    ll_pulse_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_temp_disable.setVisibility(View.GONE);
                } else {
                    ll_temp_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_oxygen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_oxygen_disable.setVisibility(View.GONE);
                } else {
                    ll_oxygen_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_weight_disable.setVisibility(View.GONE);
                } else {
                    ll_weight_disable.setVisibility(View.VISIBLE);
                }
            }
        });
        sc_enable_bp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_bp_disable.setVisibility(View.GONE);
                } else {
                    ll_bp_disable.setVisibility(View.VISIBLE);
                }
            }
        });

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

        Picasso.with(getActivity()).load(R.drawable.profile_avatar).into(iv_profile);
        dsb_bp_low = v.findViewById(R.id.dsb_bp_low);
        dsb_bp_high = v.findViewById(R.id.dsb_bp_high);
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
        // dsb_bp_low.setProgress(89); set value to discrete
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProgress();
        MedicalHistoryModel medicalHistoryModel = new MedicalHistoryModel(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            PatientProfileModel profileModel = new PatientProfileModel(this);
            profileModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new FindMedicalProfile().setCodeQR(getArguments().getString("qCodeQR")).setQueueUserId(getArguments().getString("qUserId")));
            medicalHistoryModel.fetch(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), new FindMedicalProfile().setCodeQR(getArguments().getString("qCodeQR")).setQueueUserId(getArguments().getString("qUserId")));

            JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
            jsonMedicalRecord.setRecordReferenceId(getArguments().getString("refrenceID"));
            jsonMedicalRecord.setCodeQR(getArguments().getString("qCodeQR"));
            medicalHistoryModel.setJsonMedicalRecordPresenter(this);
            medicalHistoryModel.retrieveMedicalRecord(BaseLaunchActivity.getDeviceID(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);


        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void patientProfileResponse(JsonProfile jsonProfile) {
        updateUI(jsonProfile);
        dismissProgress();
    }

    private void updateUI(JsonProfile jsonProfile) {
        tv_patient_name.setText(jsonProfile.getName() + " (" + new AppUtils().calculateAge(jsonProfile.getBirthday()) + ", " + jsonProfile.getGender().name() + ")");
        tv_address.setText(jsonProfile.getAddress());
        tv_details.setText(Html.fromHtml("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg"));
        actv_past_history.setText("Diabetic , High blood pressure");
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
        new ErrorResponseHandler().processError(getActivity(), eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void medicalRecordListResponse(JsonMedicalRecordList jsonMedicalRecordList) {
        Log.d("data", jsonMedicalRecordList.toString());
        if (!jsonMedicalRecordList.getJsonMedicalRecords().isEmpty()) {
            jsonMedicalRecords = jsonMedicalRecordList.getJsonMedicalRecords();
        }
        Collections.reverse(jsonMedicalRecords);
        MedicalCaseActivity.getMedicalCaseActivity().symptomsFragment.updateList(jsonMedicalRecords);
        dismissProgress();
    }

    @Override
    public void medicalRecordListError() {
        dismissProgress();
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            try {
                if (null != jsonMedicalRecord.getMedicalPhysical().getOxygen()) {
                    mv_oxygen.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getOxygen()));
                    sc_enable_oxygen.setChecked(true);
                } else {
                    sc_enable_oxygen.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getPulse()) {
                    mv_pulse.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getPulse()));
                    sc_enable_pulse.setChecked(true);
                } else {
                    sc_enable_pulse.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    dsb_bp_high.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0]));
                    dsb_bp_low.setProgress(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]));
                    sc_enable_bp.setChecked(true);
                } else {
                    sc_enable_bp.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    if (jsonMedicalRecord.getMedicalPhysical().getWeight().contains(".")) {
                        String[] temp = jsonMedicalRecord.getMedicalPhysical().getWeight().split("\\.");
                        mv_weight1.setValue(Integer.parseInt(temp[0]));
                        mv_weight2.setValue(Integer.parseInt(temp[1]));
                        sc_enable_weight.setChecked(true);
                    } else {
                        sc_enable_weight.setChecked(false);
                    }
                } else {
                    sc_enable_weight.setChecked(false);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    if (jsonMedicalRecord.getMedicalPhysical().getTemperature().contains(".")) {
                        String[] temp = jsonMedicalRecord.getMedicalPhysical().getTemperature().split("\\.");
                        mv_temperature1.setValue(Integer.parseInt(temp[0]));
                        mv_temperature2.setValue(Integer.parseInt(temp[1]));
                        sc_enable_temp.setChecked(true);
                    } else {
                        sc_enable_temp.setChecked(false);
                    }
                } else {
                    sc_enable_temp.setChecked(false);
                }
                meterViewValueChanged(mv_pulse);
                meterViewValueChanged(mv_weight1);
                meterViewValueChanged(mv_temperature1);
                meterViewValueChanged(mv_oxygen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dismissProgress();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void loadProfilePic(String imageUrl) {
        Picasso.with(getActivity()).load(R.drawable.profile_avatar).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getActivity())
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + imageUrl)
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveData() {
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setName(tv_patient_name.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setAddress(tv_address.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setDetails(tv_details.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setKnownAllergies(actv_known_allergy.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPastHistory(actv_past_history.getText().toString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setFamilyHistory(actv_family_history.getText().toString());
        if (sc_enable_pulse.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPulse(mv_pulse.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPulse(null);
        }
        if (sc_enable_bp.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setBloodPressure(new String[]{String.valueOf(dsb_bp_high.getProgress()), String.valueOf(dsb_bp_low.getProgress())});
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setBloodPressure(null);
        }
        if (sc_enable_weight.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setWeight(mv_weight1.getValueAsString() + "." + mv_weight2.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setWeight(null);
        }
        if (sc_enable_temp.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setTemperature(mv_temperature1.getValueAsString() + "." + mv_temperature2.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setTemperature(null);
        }
        if (sc_enable_oxygen.isChecked()) {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setOxygenLevel(mv_oxygen.getValueAsString());
        } else {
            MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setOxygenLevel(null);
        }
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
