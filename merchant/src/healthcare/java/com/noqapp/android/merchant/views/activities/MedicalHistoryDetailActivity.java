package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 5/7/17.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileModel;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.Utils.AnimationUtils;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordFavouriteAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCommunicate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicalHistoryDetailActivity extends AppCompatActivity implements MedicalRecordPresenter, View.OnClickListener, IntellisensePresenter, AdapterCommunicate {
    private final String packageName = "com.google.android.apps.handwriting.ime";
    private final String CHIEF = "chief_complaint";
    private final String PAST_HISTORY = "past_history";
    private final String FAMILY_HISTORY = "family_history";
    private final String CLINICAL_FINDINGS = "clinical_findings";
    private final String PROVISIONAL_DIAGNOSIS = "provisional_diagnosis";
    private final String INVESTIGATION = "investigation";
    private final String KNOWN_ALLERGIES = "known_allergies";
    private final String FOLLOW_UP = "followup";
    private final String INSTRUCTIONS = "instructions";
    // medicine infos
    private final String MEDICINES_NAME = "medicines_name";
    private final String MEDICINES_TYPE = "medicines_type";
    private final String MEDICINES_DOSE = "medicines_dose";
    private final String MEDICINES_FREQUENCY = "medicines_frequency";
    private final String MEDICINES_DOSE_TIMINGS = "medicines_dose_timings";
    private final String MEDICINES_COURSE = "medicines_course";

    //

    private ImageView actionbarBack;
    private HashMap<String, ArrayList<String>> mHashmapTemp = null;
    private String qCodeQR = "";
    private AutoCompleteTextView actv_medicine_name, actv_complaints, actv_family_history, actv_past_history, actv_known_allergy, actv_clinical_finding, actv_provisional, actv_investigation,actv_instruction,actv_followup;
    private EditText edt_weight, edt_bp, edt_pulse;
    private JsonQueuedPerson jsonQueuedPerson;
    private Button btn_update;
    private ListView listview,listview_favroite;
    private List<JsonMedicalMedicine> medicalRecordList = new ArrayList<>();
    private List<JsonMedicalMedicine> medicalRecordFavouriteList = new ArrayList<>();
    private MedicalRecordAdapter adapter;
    private MedicalRecordFavouriteAdapter adapterFavourite;
    private MedicalHistoryModel medicalHistoryModel;

    private AutoCompleteTextView actv_medicine_type;
    private AutoCompleteTextView actv_frequency;
    private AutoCompleteTextView actv_dose_timing;
    private AutoCompleteTextView actv_course;
    private AutoCompleteTextView actv_dose;
    private Button tv_add;
    private long lastPress;
    private Toast backPressToast;
    private TextView tv_assist;
    private LinearLayout ll_fav_medicines;
    private boolean isExpand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_details);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        medicalHistoryModel = new MedicalHistoryModel(this);
        listview = findViewById(R.id.listview);
        listview_favroite = findViewById(R.id.listview_favroite);
        adapter = new MedicalRecordAdapter(this, medicalRecordList,this);
        listview.setAdapter(adapter);

        medicalRecordFavouriteList = LaunchActivity.getLaunchActivity().getFavouriteMedicines();
        if(null == medicalRecordFavouriteList)
            medicalRecordFavouriteList = new ArrayList<>();
        adapterFavourite = new MedicalRecordFavouriteAdapter(this,medicalRecordFavouriteList,this);
        listview_favroite.setAdapter(adapterFavourite);

        actv_complaints = findViewById(R.id.actv_complaints);
        actv_past_history = findViewById(R.id.actv_past_history);
        actv_family_history = findViewById(R.id.actv_family_history);

        actv_known_allergy = findViewById(R.id.actv_known_allergy);
        actv_clinical_finding = findViewById(R.id.actv_clinical_finding);
        actv_provisional = findViewById(R.id.actv_provisional);
        actv_investigation = findViewById(R.id.actv_investigation);
        actv_followup = findViewById(R.id.actv_followup);
        actv_instruction = findViewById(R.id.actv_instruction);

        edt_weight = findViewById(R.id.edt_weight);
        edt_bp = findViewById(R.id.edt_bp);
        edt_pulse = findViewById(R.id.edt_pulse);
        ll_fav_medicines = findViewById(R.id.ll_fav_medicines);

        actv_medicine_name = findViewById(R.id.actv_medicine_name);
        actv_medicine_type = findViewById(R.id.actv_medicine_type);
        actv_dose = findViewById(R.id.actv_dose);
        actv_frequency = findViewById(R.id.actv_frequency);
        actv_dose_timing = findViewById(R.id.actv_dose_timing);
        actv_course = findViewById(R.id.actv_course);
        tv_add = findViewById(R.id.tv_add);
        tv_assist = findViewById(R.id.tv_assist);
        tv_assist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpand) {
                    AnimationUtils.expand(ll_fav_medicines);
                    Drawable img = getResources().getDrawable( R.drawable.arrow_up );
                    tv_assist.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                   // iv_store_open_status.setBackground(ContextCompat.getDrawable(MedicalHistoryDetailActivity.this, R.drawable.arrow_down));
                } else {
                    AnimationUtils.collapse(ll_fav_medicines);
                   // iv_store_open_status.setBackground(ContextCompat.getDrawable(MedicalHistoryDetailActivity.this, R.drawable.arrow_up));
                    Drawable img = getResources().getDrawable( R.drawable.arrow_down );
                    tv_assist.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                }
                isExpand = !isExpand;
            }
        });


        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidEntry()) {
                    JsonMedicalMedicine jsonMedicalMedicine = new JsonMedicalMedicine();

                    jsonMedicalMedicine.setDailyFrequency(actv_frequency.getText().toString());
                    jsonMedicalMedicine.setStrength(actv_dose.getText().toString());
                    jsonMedicalMedicine.setMedicationType(actv_medicine_type.getText().toString());
                    jsonMedicalMedicine.setMedicationWithFood(actv_dose_timing.getText().toString());
                    jsonMedicalMedicine.setCourse(actv_course.getText().toString());
                    jsonMedicalMedicine.setName(actv_medicine_name.getText().toString());
                    if(!medicalRecordList.contains(jsonMedicalMedicine)) {
                        medicalRecordList.add(0, jsonMedicalMedicine);
                        adapter.notifyDataSetChanged();
                        updateSuggetions(actv_medicine_name, MEDICINES_NAME);
                        setSuggetions(actv_medicine_name, MEDICINES_NAME,false);
                        updateSuggetions(actv_dose, MEDICINES_DOSE);
                        setSuggetions(actv_dose, MEDICINES_DOSE,false);
                        updateSuggetions(actv_frequency, MEDICINES_FREQUENCY);
                        setSuggetions(actv_frequency, MEDICINES_FREQUENCY,false);
                        updateSuggetions(actv_course, MEDICINES_COURSE);
                        setSuggetions(actv_course, MEDICINES_COURSE,false);
                        updateSuggetions(actv_medicine_type, MEDICINES_TYPE);
                        setSuggetions(actv_medicine_type, MEDICINES_TYPE,false);
                        updateSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS);
                        setSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS,false);
                        // update medicine related info because we are setting the fields blank
                        LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);
                        actv_medicine_name.setText("");
                        actv_dose.setText("");
                        actv_frequency.setText("");
                        actv_course.setText("");
                        actv_medicine_type.setText("");
                        actv_dose_timing.setText("");
                    }else{
                        Toast.makeText(MedicalHistoryDetailActivity.this, "medicine already added", Toast.LENGTH_LONG).show();
                    }

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
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            Button btn_no = customDialogView.findViewById(R.id.btn_no);
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
            mHashmapTemp.put(MEDICINES_NAME, new ArrayList<String>());
            mHashmapTemp.put(FOLLOW_UP, new ArrayList<String>());
            mHashmapTemp.put(INSTRUCTIONS, new ArrayList<String>());

            mHashmapTemp.put(MEDICINES_TYPE, new ArrayList<String>());
            mHashmapTemp.put(MEDICINES_DOSE, new ArrayList<String>());
            mHashmapTemp.put(MEDICINES_FREQUENCY, new ArrayList<String>());
            mHashmapTemp.put(MEDICINES_DOSE_TIMINGS, new ArrayList<String>());
            mHashmapTemp.put(MEDICINES_COURSE, new ArrayList<String>());

            LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);
        } else {
            try {
                mHashmapTemp = gson.fromJson(strOutput, type);

                setSuggetions(actv_complaints, CHIEF,true);
                setSuggetions(actv_past_history, PAST_HISTORY,true);
                setSuggetions(actv_family_history, FAMILY_HISTORY,true);
                setSuggetions(actv_known_allergy, KNOWN_ALLERGIES,true);
                setSuggetions(actv_clinical_finding, CLINICAL_FINDINGS,true);
                setSuggetions(actv_provisional, PROVISIONAL_DIAGNOSIS,true);
                setSuggetions(actv_investigation, INVESTIGATION,true);
                setSuggetions(actv_followup, FOLLOW_UP,true);
                setSuggetions(actv_instruction, INSTRUCTIONS,true);

                setSuggetions(actv_medicine_name, MEDICINES_NAME,false);
                setSuggetions(actv_medicine_type, MEDICINES_TYPE,false);
                setSuggetions(actv_dose, MEDICINES_DOSE,false);
                setSuggetions(actv_frequency, MEDICINES_FREQUENCY,false);
                setSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS,false);
                setSuggetions(actv_course, MEDICINES_COURSE,false);


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
        if (TextUtils.isEmpty(actv_dose.getText().toString())) {
            isValid = false;
            errorMsg += "Please select dose for the  medicine \n";
        }
        if (TextUtils.isEmpty(actv_frequency.getText().toString())) {
            isValid = false;
            errorMsg += "Please select frequency for the  medicine \n";
        }
        if (TextUtils.isEmpty(actv_course.getText().toString())) {
            isValid = false;
            errorMsg += "Please select course for the  medicine \n";
        }
        if (TextUtils.isEmpty(actv_medicine_type.getText().toString())) {
            isValid = false;
            errorMsg += "Please select medication type for the  medicine \n";
        }
        if (TextUtils.isEmpty(actv_dose_timing.getText().toString())) {
            isValid = false;
            errorMsg += "Please select dose timing for the  medicine \n";
        }
        if (!errorMsg.equalsIgnoreCase(""))
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

        return isValid;
    }

    private void setSuggetions(final AutoCompleteTextView actv, String key, boolean isThreashold) {
        if (null == mHashmapTemp.get(key))
            mHashmapTemp.put(key, new ArrayList<String>());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, mHashmapTemp.get(key));
        actv.setAdapter(adapter);
        if (isThreashold) {
            actv.setThreshold(1);
        } else {
            actv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    actv.showDropDown();
                    return false;
                }
            });
        }
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
        updateSuggetions(actv_followup, FOLLOW_UP);
        updateSuggetions(actv_instruction, INSTRUCTIONS);
        //update medicine when add button click
        //updateSuggetions(actv_medicine_name, MEDICINES);
//        updateSuggetions(actv_medicine_type, MEDICINES_TYPE);
//        updateSuggetions(actv_dose, MEDICINES_DOSE);
//        updateSuggetions(actv_frequency, MEDICINES_FREQUENCY);
//        updateSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS);
//        updateSuggetions(actv_course, MEDICINES_COURSE);

        LaunchActivity.getLaunchActivity().setSuggestions(mHashmapTemp);

        M_MerchantProfileModel m_merchantProfileModel = new M_MerchantProfileModel(this);
        m_merchantProfileModel.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(LaunchActivity.getLaunchActivity().getSuggestions()));

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
                TextUtils.isEmpty(actv_instruction.getText()) &&
                TextUtils.isEmpty(actv_followup.getText()) &&
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
    public void intellisenseResponse(JsonResponse jsonResponse) {
        Log.v("intellesence upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void intellisenseError() {
        Log.v("intellesence upload: ", "error");
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
                        jsonMedicalRecord.setQueueUserId(jsonQueuedPerson.getQueueUserId());
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

                        medicalHistoryModel.add(LaunchActivity.getLaunchActivity().getDeviceID(),
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


    @Override
    public void updateFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordFavouriteList.contains(jsonMedicalMedicine))
                Toast.makeText(this, "medicine already marked as favroite", Toast.LENGTH_LONG).show();
            else
                medicalRecordFavouriteList.add(jsonMedicalMedicine);
        } else
            medicalRecordFavouriteList.remove(jsonMedicalMedicine);
        LaunchActivity.getLaunchActivity().setFavouriteMedicines(medicalRecordFavouriteList);
        adapterFavourite = new MedicalRecordFavouriteAdapter(this, medicalRecordFavouriteList, this);
        listview_favroite.setAdapter(adapterFavourite);
    }

    @Override
    public void updateNonFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordList.contains(jsonMedicalMedicine))
                Toast.makeText(this, "medicine already added", Toast.LENGTH_LONG).show();
            else
                medicalRecordList.add(jsonMedicalMedicine);
        } else {
            medicalRecordList.remove(jsonMedicalMedicine);
            jsonMedicalMedicine.setFavourite(false);
            medicalRecordList.add(jsonMedicalMedicine);
        }
        adapter = new MedicalRecordAdapter(this, medicalRecordList, this);
        listview.setAdapter(adapter);

    }
}
