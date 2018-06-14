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
import android.widget.LinearLayout;
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
import com.noqapp.common.beans.JsonResponse;
import com.noqapp.common.beans.medical.JsonMedicalRecord;
import com.noqapp.common.beans.medical.JsonMedicine;
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
    private AutoCompleteTextView edt_complaints, actv_family_history, edt_past_history;
    private EditText edt_known_allergy, edt_physical_exam, edt_clinical_finding, edt_provisional, edt_investigation, edt_treatment;
    private final String xray = "X-ray";
    private final String medicine = "Medicine";
    private final String mri = "MRI";
    private JsonQueuedPerson jsonQueuedPerson;
    private LinearLayout ll_medicines;
    private Button btn_update, btn_add;
    ArrayList<View> ll_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        MedicalHistoryModel.medicalRecordPresenter = this;
        ll_medicines = findViewById(R.id.ll_medicines);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);


        edt_complaints = (AutoCompleteTextView) findViewById(R.id.edt_complaints);
        edt_past_history = (AutoCompleteTextView) findViewById(R.id.edt_past_history);
        actv_family_history = (AutoCompleteTextView) findViewById(R.id.actv_family_history);

        edt_known_allergy = (EditText) findViewById(R.id.edt_known_allergy);
        edt_physical_exam = (EditText) findViewById(R.id.edt_physical_exam);
        edt_clinical_finding = (EditText) findViewById(R.id.edt_clinical_finding);
        edt_provisional = (EditText) findViewById(R.id.edt_provisional);
        edt_investigation = (EditText) findViewById(R.id.edt_investigation);
        edt_treatment = (EditText) findViewById(R.id.edt_treatment);
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
            mHashmapTemp.put(xray, new ArrayList<String>());
            mHashmapTemp.put(medicine, new ArrayList<String>());
            mHashmapTemp.put(mri, new ArrayList<String>());
            LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);
        } else {
            try {
                mHashmapTemp = gson.fromJson(strOutput, type);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(xray));
                edt_past_history.setAdapter(adapter);

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(medicine));
                edt_complaints.setAdapter(adapter1);
                edt_complaints.setThreshold(1);
                edt_past_history.setThreshold(1);
                Log.v("JSON", mHashmapTemp.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (!edt_past_history.getText().toString().equals(""))
            if (!mHashmapTemp.get(xray).contains(edt_past_history.getText().toString())) {
                mHashmapTemp.get(xray).add(edt_past_history.getText().toString());
            }
        if (!edt_complaints.getText().toString().equals(""))
            if (!mHashmapTemp.get(medicine).contains(edt_complaints.getText().toString())) {
                mHashmapTemp.get(medicine).add(edt_complaints.getText().toString());
            }
        LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);

    }

    private boolean validate() {
        btn_update.setBackgroundResource(R.drawable.button_drawable);
        btn_update.setTextColor(ContextCompat.getColor(this, R.color.colorMobile));
        btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        boolean isValid = true;
        new AppUtils().hideKeyBoard(this);

        if (TextUtils.isEmpty(edt_complaints.getText()) &&
                TextUtils.isEmpty(edt_past_history.getText()) &&
                TextUtils.isEmpty(actv_family_history.getText()) &&
                TextUtils.isEmpty(edt_known_allergy.getText()) &&
                TextUtils.isEmpty(edt_physical_exam.getText()) &&
                TextUtils.isEmpty(edt_clinical_finding.getText()) &&
                TextUtils.isEmpty(edt_provisional.getText()) &&
                TextUtils.isEmpty(edt_investigation.getText()) &&
                TextUtils.isEmpty(edt_treatment.getText())) {
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
            case R.id.btn_add:
                addMedicineView();
                break;
            case R.id.btn_update:
                if (validate()) {
                    JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                    jsonMedicalRecord.setCodeQR(qCodeQR);
                    jsonMedicalRecord.setQueueUserId(StringUtils.isBlank(jsonQueuedPerson.getQueueUserId()) ? "100000000032" : jsonQueuedPerson.getQueueUserId());

//                    if (StringUtils.isBlank(jsonQueuedPerson.getQueueUserId())) {
//                        throw new RuntimeException("Need QID");
//                        //TODO better error handling
//                    }
                    jsonMedicalRecord.setQueueUserId(jsonQueuedPerson.getQueueUserId());
                    jsonMedicalRecord.setChiefComplain(edt_complaints.getText().toString());
                    jsonMedicalRecord.setPastHistory(edt_past_history.getText().toString());
                    jsonMedicalRecord.setFamilyHistory(actv_family_history.getText().toString());
                    jsonMedicalRecord.setKnownAllergies(edt_known_allergy.getText().toString());
                    jsonMedicalRecord.setClinicalFinding(edt_clinical_finding.getText().toString());
                    jsonMedicalRecord.setProvisionalDifferentialDiagnosis(edt_provisional.getText().toString());

                    if (ll_list.size() > 1) {
                        List<JsonMedicine> jsonMedicines = new ArrayList<>();
                        for (int i = 0; i < ll_list.size(); i++) {
                            LinearLayout ll = (LinearLayout) ll_list.get(i);
                            EditText edt_medicine_name = ll.findViewById(R.id.edt_medicine_name);
                            Spinner sp_medication = ll_medicines.findViewById(R.id.sp_medication);
                            Spinner sp_dose = ll_medicines.findViewById(R.id.sp_dose);
                            Spinner sp_frequency = ll_medicines.findViewById(R.id.sp_frequency);
                            Spinner sp_dose_timing = ll_medicines.findViewById(R.id.sp_dose_timing);
                            Spinner sp_course = ll_medicines.findViewById(R.id.sp_course);
                            JsonMedicine jsonMedicine = new JsonMedicine();
                            jsonMedicine.setMedicationType(MedicationTypeEnum.get(sp_medication.getSelectedItem().toString()));
                            jsonMedicine.setCourse(sp_course.getSelectedItem().toString());
                            jsonMedicine.setDailyFrequency(sp_frequency.getSelectedItem().toString());
                            jsonMedicine.setName(edt_medicine_name.getText().toString());
                            jsonMedicine.setStrength(sp_dose.getSelectedItem().toString());
                            jsonMedicine.setMedicationWithFood(MedicationWithFoodEnum.get(sp_dose_timing.getSelectedItem().toString()));
                            jsonMedicines.add(jsonMedicine);
                        }
                        jsonMedicalRecord.setMedicines(jsonMedicines);
                    }
                    MedicalHistoryModel.add(LaunchActivity.getLaunchActivity().getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);
                } else {
                    Toast.makeText(MedicalHistoryDetailActivity.this, "Please fill atleast one field", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void addMedicineView() {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View medical_item = inflater.inflate(R.layout.medical_item, null);
        medical_item.setId(ll_list.size());
        ll_medicines.addView(medical_item);
        ll_list.add(medical_item);

        Spinner sp_medication = medical_item.findViewById(R.id.sp_medication);
       // sp_medication.setId((int)System.currentTimeMillis());

        Spinner sp_dose_timing = medical_item.findViewById(R.id.sp_dose_timing);
        ArrayAdapter<String> sp_medication_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        MedicationTypeEnum.asList()); //selected item will look like a spinner set from XML
        sp_medication_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_medication.setAdapter(sp_medication_adapter);

        ArrayAdapter<String> sp_dose_timing_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        MedicationWithFoodEnum.asList()); //selected item will look like a spinner set from XML
        sp_dose_timing_adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_dose_timing.setAdapter(sp_dose_timing_adapter);

        TextView tv_delete = medical_item.findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_medicines.removeView(medical_item);
            }
        });
    }
}
