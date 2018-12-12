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
import com.noqapp.android.merchant.views.adapters.MedicalHistoryAdapter;
import com.noqapp.android.merchant.views.customviews.MeterView;

import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimaryCheckupFragment extends Fragment implements PatientProfilePresenter, MedicalRecordListPresenter, JsonMedicalRecordPresenter, MeterView.MeterViewValueChanged {

    private ProgressDialog progressDialog;
    private TextView tv_patient_name, tv_address, tv_details;
    private ListView listview;
    private ImageView iv_profile;
    private AutoCompleteTextView actv_past_history, actv_known_allergy, actv_family_history;
    private List<JsonMedicalRecord> jsonMedicalRecords = new ArrayList<>();
    private MeterView mv_weight, mv_pulse, mv_temperature, mv_oxygen, mv_bp_high, mv_bp_low;
    private TextView tv_weight, tv_pulse, tv_temperature, tv_oxygen, tv_bp_high, tv_bp_low;

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
        mv_weight = v.findViewById(R.id.mv_weight);
        mv_pulse = v.findViewById(R.id.mv_pulse);
        mv_temperature = v.findViewById(R.id.mv_temperature);
        mv_oxygen = v.findViewById(R.id.mv_oxygen);
        mv_bp_high = v.findViewById(R.id.mv_bp_high);
        mv_bp_low = v.findViewById(R.id.mv_bp_low);

        tv_weight = v.findViewById(R.id.tv_weight);
        tv_pulse = v.findViewById(R.id.tv_pulse);
        tv_temperature = v.findViewById(R.id.tv_temperature);
        tv_oxygen = v.findViewById(R.id.tv_oxygen);
        tv_bp_high = v.findViewById(R.id.tv_bp_systolic);
        tv_bp_low = v.findViewById(R.id.tv_bp_diastolic);

        mv_pulse.setNumbersOf(3, 0);
        mv_oxygen.setNumbersOf(2, 0);
        mv_bp_high.setNumbersOf(3, 0);
        mv_bp_high.setNumbersOf(3, 0);
        mv_bp_low.setNumbersOf(2, 0);
        mv_pulse.setMeterViewValueChanged(this);
        mv_temperature.setMeterViewValueChanged(this);
        mv_weight.setMeterViewValueChanged(this);
        mv_oxygen.setMeterViewValueChanged(this);
        mv_bp_high.setMeterViewValueChanged(this);
        mv_bp_low.setMeterViewValueChanged(this);

        Picasso.with(getActivity()).load(R.drawable.profile_avatar).into(iv_profile);
        listview = v.findViewById(R.id.listview);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Meter selected value: " + String.valueOf(mv_weight.getDoubleValue()), Toast.LENGTH_LONG).show();
                Log.e("pulse value", String.valueOf(mv_pulse.getValue()));
                Log.e("oxygen value", String.valueOf(mv_oxygen.getValue()));
                Log.e("temperature value", String.valueOf(mv_temperature.getDoubleValue()));
                Log.e("weight value", String.valueOf(mv_weight.getDoubleValue()));
            }
        });
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
        MedicalHistoryAdapter adapter = new MedicalHistoryAdapter(getActivity(), jsonMedicalRecords);
        listview.setAdapter(adapter);
        MedicalCaseActivity.getMedicalCaseActivity().symptomsFragment.updateList(jsonMedicalRecords);
        if (jsonMedicalRecords.size() <= 0) {
            listview.setVisibility(View.GONE);
        } else {
            listview.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent in = new Intent(MedicalHistoryActivity.this, MedicalHistoryDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", jsonMedicalRecords.get(i));
//                in.putExtras(bundle);
//                startActivity(in);
            }
        });
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
                if (null != jsonMedicalRecord.getMedicalPhysical().getOxygen())
                    mv_oxygen.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getOxygen()));
                if (null != jsonMedicalRecord.getMedicalPhysical().getPluse())
                    mv_pulse.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getPluse()));
                if (null != jsonMedicalRecord.getMedicalPhysical().getBloodPressure() && jsonMedicalRecord.getMedicalPhysical().getBloodPressure().length == 2) {
                    mv_bp_high.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[0]));
                    mv_bp_low.setValue(Integer.parseInt(jsonMedicalRecord.getMedicalPhysical().getBloodPressure()[1]));
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getWeight()) {
                    double d1 = Double.parseDouble(jsonMedicalRecord.getMedicalPhysical().getWeight()) * 100;
                    mv_weight.setValue((int) d1);
                }
                if (null != jsonMedicalRecord.getMedicalPhysical().getTemperature()) {
                    double d2 = Double.parseDouble(jsonMedicalRecord.getMedicalPhysical().getTemperature()) * 100;
                    mv_temperature.setValue((int) d2);
                }

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
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setPulse(mv_pulse.getValueAsString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setBloodPressure(new String[]{mv_bp_high.getValueAsString(), mv_bp_low.getValueAsString()});
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setWeight(mv_weight.getDoubleValueAsString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setTemperature(mv_temperature.getDoubleValueAsString());
        MedicalCaseActivity.getMedicalCaseActivity().getMedicalCasePojo().setOxygenLevel(mv_oxygen.getValueAsString());
    }

    @Override
    public void meterViewValueChanged(View v) {
        switch (v.getId()) {
            case R.id.mv_pulse:
                tv_pulse.setText("Pulse: " + String.valueOf(mv_pulse.getValue()));
                break;
            case R.id.mv_weight:
                tv_weight.setText("Weight: " + String.valueOf(mv_weight.getDoubleValue()));
                break;
            case R.id.mv_temperature:
                tv_temperature.setText("Temp: " + String.valueOf(mv_temperature.getDoubleValue()));
                break;
            case R.id.mv_oxygen:
                tv_oxygen.setText("Oxygen: " + String.valueOf(mv_oxygen.getValue()));
                break;
            case R.id.mv_bp_high:
                tv_bp_high.setText("Systolic: " + String.valueOf(mv_bp_high.getValue()));
                break;
            case R.id.mv_bp_low:
                tv_bp_low.setText("Diastolic: " + String.valueOf(mv_bp_low.getValue()));
                break;

            default:
                break;
        }
    }
}
