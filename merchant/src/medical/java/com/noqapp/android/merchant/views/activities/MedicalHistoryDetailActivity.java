package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.common.beans.JsonResponse;
import com.noqapp.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.common.beans.medical.JsonMedicalRecord;
import com.noqapp.common.model.types.MedicationTypeEnum;
import com.noqapp.common.model.types.MedicationWithFoodEnum;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicalHistoryDetailActivity extends AppCompatActivity implements MedicalRecordPresenter, View.OnClickListener {
    private final String packageName = "com.google.android.apps.handwriting.ime";
    private ImageView actionbarBack;
    private HashMap<String, ArrayList<String>> mHashmapTemp = null;
    private String qCodeQR = "";
    private AutoCompleteTextView actv_medicine_name,actv_complaints, actv_family_history, actv_past_history, actv_known_allergy, actv_clinical_finding, actv_provisional, actv_investigation;
    private EditText edt_weight, edt_bp, edt_pulse;
    private final String CHIEF = "chief_complaint";
    private final String PAST_HISTORY = "past_history";
    private final String FAMILY_HISTORY = "family_history";
    private final String CLINICAL_FINDINGS = "clinical_findings";
    private final String PROVISIONAL_DIAGNOSIS = "provisional_diagnosis";
    private final String INVESTIGATION = "investigation";
    private final String KNOWN_ALLERGIES = "known_allergies";
    private final String MEDICINES_TREATMENT_ADVICE = "medicines_treatment_advice";

    private JsonQueuedPerson jsonQueuedPerson;
    private Button btn_update;
    private ListView listview;
    private List<JsonMedicalMedicine> medicalRecordList = new ArrayList<>();
    private MedicalRecordAdapter adapter;


    private Spinner sp_medication;
    private Spinner sp_frequency;
    private Spinner sp_dose_timing;
    private Spinner sp_course;
    private Spinner sp_dose;
    private Button tv_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        MedicalHistoryModel.medicalRecordPresenter = this;
        listview = findViewById(R.id.listview);
        adapter = new MedicalRecordAdapter(this, medicalRecordList);
        listview.setAdapter(adapter);
        actv_complaints = findViewById(R.id.actv_complaints);
        actv_past_history = findViewById(R.id.actv_past_history);
        actv_family_history = findViewById(R.id.actv_family_history);

        actv_known_allergy = findViewById(R.id.actv_known_allergy);
        actv_clinical_finding = findViewById(R.id.actv_clinical_finding);
        actv_provisional = findViewById(R.id.actv_provisional);
        actv_investigation = findViewById(R.id.actv_investigation);

        edt_weight = findViewById(R.id.edt_weight);
        edt_bp = findViewById(R.id.edt_bp);
        edt_pulse = findViewById(R.id.edt_pulse);


        actv_medicine_name = findViewById(R.id.actv_medicine_name);
        sp_medication = findViewById(R.id.sp_medication);
        sp_dose = findViewById(R.id.sp_dose);
        sp_frequency = findViewById(R.id.sp_frequency);
        sp_dose_timing = findViewById(R.id.sp_dose_timing);
        sp_course = findViewById(R.id.sp_course);
        tv_add = findViewById(R.id.tv_add);

        //
        ArrayAdapter<String> sp_medication_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        MedicationTypeEnum.asList());
        sp_medication_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_medication.setAdapter(sp_medication_adapter);

        ArrayAdapter<String> sp_dose_timing_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        MedicationWithFoodEnum.asList());
        sp_dose_timing_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_dose_timing.setAdapter(sp_dose_timing_adapter);

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidEntry()) {
                    JsonMedicalMedicine jsonMedicalMedicine = new JsonMedicalMedicine();

                    jsonMedicalMedicine.setDailyFrequency(sp_frequency.getSelectedItem().toString());
                    jsonMedicalMedicine.setStrength(sp_dose.getSelectedItem().toString());
                    jsonMedicalMedicine.setMedicationType(MedicationTypeEnum.get(sp_medication.getSelectedItem().toString()));
                    jsonMedicalMedicine.setMedicationWithFood(MedicationWithFoodEnum.get(sp_dose_timing.getSelectedItem().toString()));
                    jsonMedicalMedicine.setCourse(sp_course.getSelectedItem().toString());
                    jsonMedicalMedicine.setName(actv_medicine_name.getText().toString());

                    medicalRecordList.add(0, jsonMedicalMedicine);
                    adapter.notifyDataSetChanged();
                    updateSuggetions(actv_medicine_name, MEDICINES_TREATMENT_ADVICE);
                    setSuggetions(actv_medicine_name, MEDICINES_TREATMENT_ADVICE);
                    actv_medicine_name.setText("");
                    sp_dose.setSelection(0);
                    sp_frequency.setSelection(0);
                    sp_course.setSelection(0);
                }
            }
        });

        qCodeQR = getIntent().getStringExtra("qCodeQR");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Medical History");
        if (!appInstalledOrNot(packageName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = (Button) customDialogView.findViewById(R.id.btn_yes);
            Button btn_no = (Button) customDialogView.findViewById(R.id.btn_no);
            TextView tv_title = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            btn_yes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                    mAlertDialog.dismiss();

                }
            });
            mAlertDialog.show();
            btn_yes.setText("Download");
            btn_no.setText("Cancel");
            tv_msg.setText("Download the Scribble writer app to make your life easy.");
            tv_title.setText("Scribble app missing");
        }


        String strOutput = LaunchActivity.getLaunchActivity().getSuggestions();
        Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {
        }.getType();
        Gson gson = new Gson();
        if (TextUtils.isEmpty(strOutput) || strOutput.equalsIgnoreCase("null")) {
            Log.v("JSON", "empty json");
            mHashmapTemp = new HashMap<>();
            mHashmapTemp.put(CHIEF, new ArrayList<String>());
            mHashmapTemp.put(PAST_HISTORY, new ArrayList<String>());
            mHashmapTemp.put(FAMILY_HISTORY, new ArrayList<String>());
            mHashmapTemp.put(CLINICAL_FINDINGS, new ArrayList<String>());
            mHashmapTemp.put(PROVISIONAL_DIAGNOSIS, new ArrayList<String>());
            mHashmapTemp.put(INVESTIGATION, new ArrayList<String>());
            mHashmapTemp.put(KNOWN_ALLERGIES, new ArrayList<String>());
            mHashmapTemp.put(MEDICINES_TREATMENT_ADVICE, new ArrayList<String>());

            LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);
        } else {
            try {
                mHashmapTemp = gson.fromJson(strOutput, type);

                setSuggetions(actv_complaints, CHIEF);
                setSuggetions(actv_past_history, PAST_HISTORY);
                setSuggetions(actv_family_history, FAMILY_HISTORY);
                setSuggetions(actv_known_allergy, KNOWN_ALLERGIES);
                setSuggetions(actv_clinical_finding, CLINICAL_FINDINGS);
                setSuggetions(actv_provisional, PROVISIONAL_DIAGNOSIS);
                setSuggetions(actv_investigation, INVESTIGATION);
                setSuggetions(actv_medicine_name, MEDICINES_TREATMENT_ADVICE);
                Log.v("JSON", mHashmapTemp.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isValidEntry() {
        boolean isValid = true;
        String errorMsg = "";
        if (TextUtils.isEmpty(actv_medicine_name.getText().toString())) {
            isValid = false;
            errorMsg = "Please enter medicine name";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            return isValid;
        }
        if (sp_dose.getSelectedItemPosition() == 0) {
            isValid = false;
            errorMsg += "Please select dose for the  medicine \n";
        }
        if (sp_frequency.getSelectedItemPosition() == 0) {
            isValid = false;
            errorMsg += "Please select frequency for the  medicine \n";
        }
        if (sp_course.getSelectedItemPosition() == 0) {
            isValid = false;
            errorMsg += "Please select course for the  medicine \n";
        }
        if (!errorMsg.equalsIgnoreCase(""))
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

        return isValid;
    }

    private void setSuggetions(AutoCompleteTextView actv, String key) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(key));
        actv.setAdapter(adapter);
        actv.setThreshold(1);
        actv.setThreshold(1);
    }

    private void updateSuggetions(AutoCompleteTextView actv, String key) {
        if (!actv.getText().toString().equals(""))
            if (!mHashmapTemp.get(key).contains(actv.getText().toString())) {
                mHashmapTemp.get(key).add(actv.getText().toString());
            }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateSuggetions(actv_complaints, CHIEF);
        updateSuggetions(actv_past_history, PAST_HISTORY);
        updateSuggetions(actv_family_history, FAMILY_HISTORY);
        updateSuggetions(actv_known_allergy, KNOWN_ALLERGIES);
        updateSuggetions(actv_clinical_finding, CLINICAL_FINDINGS);
        updateSuggetions(actv_provisional, PROVISIONAL_DIAGNOSIS);
        updateSuggetions(actv_investigation, INVESTIGATION);
       // updateSuggetions(actv_medicine_name, MEDICINES); update this when add button click
        LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);

    }

    private boolean validate() {
        btn_update.setBackgroundResource(R.drawable.button_drawable);
        btn_update.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        boolean isValid = true;
        new AppUtils().hideKeyBoard(this);

        if (TextUtils.isEmpty(actv_complaints.getText()) &&
                TextUtils.isEmpty(actv_past_history.getText()) &&
                TextUtils.isEmpty(actv_family_history.getText()) &&
                TextUtils.isEmpty(actv_known_allergy.getText()) &&
                TextUtils.isEmpty(actv_clinical_finding.getText()) &&
                TextUtils.isEmpty(actv_provisional.getText()) &&
                TextUtils.isEmpty(actv_investigation.getText())) {
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        if (jsonResponse.getResponse() == 1) {
            Toast.makeText(this, "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void medicalRecordError() {
        Toast.makeText(this, "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void authenticationFailure(int errorCode) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_update:
                if (validate()) {

                    if (TextUtils.isEmpty(actv_medicine_name.getText().toString())) {
                        JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                        jsonMedicalRecord.setCodeQR(qCodeQR);
                        jsonMedicalRecord.setQueueUserId(StringUtils.isBlank(jsonQueuedPerson.getQueueUserId()) ? "100000000032" : jsonQueuedPerson.getQueueUserId());

//                    if (StringUtils.isBlank(jsonQueuedPerson.getQueueUserId())) {
//                        throw new RuntimeException("Need QID");
//                        //TODO better error handling
//                    }
                        jsonMedicalRecord.setQueueUserId(jsonQueuedPerson.getQueueUserId());
                        jsonMedicalRecord.setChiefComplain(actv_complaints.getText().toString());
                        jsonMedicalRecord.setPastHistory(actv_past_history.getText().toString());
                        jsonMedicalRecord.setFamilyHistory(actv_family_history.getText().toString());
                        jsonMedicalRecord.setKnownAllergies(actv_known_allergy.getText().toString());
                        jsonMedicalRecord.setClinicalFinding(actv_clinical_finding.getText().toString());
                        jsonMedicalRecord.setProvisionalDifferentialDiagnosis(actv_provisional.getText().toString());

                        JsonMedicalPhysical jsonMedicalPhysical = new JsonMedicalPhysical()
                                .setBloodPressure(new String[]{edt_bp.getText().toString()})
                                .setPluse(edt_pulse.getText().toString())
                                .setWeight(edt_weight.getText().toString());

                        jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                        jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineList());

                        MedicalHistoryModel.add(LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);
                    } else {
                        Toast.makeText(MedicalHistoryDetailActivity.this, "It seems you forget to add the medicine which you  entered.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MedicalHistoryDetailActivity.this, "Please fill at least one field", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
