package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.beans.medical.JsonPathology;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileModel;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.Utils.AnimationUtils;
import com.noqapp.android.merchant.views.Utils.GridItem;
import com.noqapp.android.merchant.views.Utils.TestCaseString;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.GridAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordFavouriteAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCommunicate;
import com.noqapp.android.merchant.views.interfaces.GridCommunication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedicalCaseFragment extends Fragment implements MedicalRecordPresenter, View.OnClickListener, IntellisensePresenter, AdapterCommunicate, GridCommunication {
    

    private String jsonText =   "{\n" +
            "  \"pathology\": {\n" +
            "    \"blood\": [\n" +
            "      \"Prostate-Specific Antigen (PSA)\",\n" +
            "      \"Thyroid Stimulating Hormone (TSH)\",\n" +
            "      \"Testosterone (Free)\",\n" +
            "      \"Estradiol\",\n" +
            "      \"Pregnenolone\",\n" +
            "      \"Homocysteine\",\n" +
            "      \"Hemoglobin A1C (HbA1C)\",\n" +
            "      \"Dihydrotestosterone (DHT)\"\n" +
            "    ],\n" +
            "    \"urine\": [\n" +
            "      \"Urinary Methylmalonic Acid (MMA)\",\n" +
            "      \"Urinalysis\",\n" +
            "      \"Neurotransmitter Panel\",\n" +
            "      \"Iodine\",\n" +
            "      \"Protein and Creatinine\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"radiology\": {\n" +
            "    \"x_ray\": [\n" +
            "      \"Bone Density (BMD)\",\n" +
            "      \"Ribs (Oblique)\",\n" +
            "      \"Acromioclavicular (AC) Joint\",\n" +
            "      \"Teeth and bones X-rays\",\n" +
            "      \"Submentovertex (S.M.V.)\"\n" +
            "    ],\n" +
            "    \"mri\": [\n" +
            "      \"Angiography (MRA) Head\",\n" +
            "      \"Venography (MRV) Brain\",\n" +
            "      \"Pituitary With Contrast\",\n" +
            "      \"Angiography (MRA) Neck\",\n" +
            "      \"Cholangiopancreatography (MRCP)\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"general\": [\n" +
            "    \"ecg\",\n" +
            "    \"cag\"\n" +
            "  ]\n" +
            "}";

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

    private HashMap<String, ArrayList<String>> hashmap = null;
    private String qCodeQR = "";
    private AutoCompleteTextView actv_medicine_name, actv_complaints, actv_family_history, actv_past_history, actv_known_allergy, actv_clinical_finding, actv_provisional, actv_investigation, actv_instruction, actv_followup;
    private EditText edt_weight, edt_bp, edt_pulse, edt_temperature, edt_oxygen;
    private JsonQueuedPerson jsonQueuedPerson;
    private Button btn_update;
    private ListView listview, listview_favroite;
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
    private TextView tv_assist, tv_favourite_text;
    private LinearLayout ll_fav_medicines;
    private boolean isExpand;
    private RadioGroup rg_duration;

    private SegmentedControl sc_blood,sc_urine;
    private ArrayList<String> sc_blood_data = new ArrayList<>();
    private ArrayList<String> sc_urine_data = new ArrayList<>();
    private TestCaseString testCaseString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_case, container, false);
        try {
            testCaseString = new Gson().fromJson(jsonText, TestCaseString.class);
        }catch (Exception e){
            e.printStackTrace();
        }


        medicalHistoryModel = new MedicalHistoryModel(this);
        listview = view.findViewById(R.id.listview);
        listview_favroite = view.findViewById(R.id.listview_favroite);
        adapter = new MedicalRecordAdapter(getActivity(), medicalRecordList, this);
        listview.setAdapter(adapter);

        medicalRecordFavouriteList = LaunchActivity.getLaunchActivity().getFavouriteMedicines();
        if (null == medicalRecordFavouriteList)
            medicalRecordFavouriteList = new ArrayList<>();
        adapterFavourite = new MedicalRecordFavouriteAdapter(getActivity(), medicalRecordFavouriteList, this);
        listview_favroite.setAdapter(adapterFavourite);
        sc_blood = view.findViewById(R.id.sc_blood);
        sc_urine = view.findViewById(R.id.sc_urine);
        TextView tv_urine = view.findViewById(R.id.tv_urine);
        tv_urine.setPaintFlags(tv_urine.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        TextView tv_blood_test = view.findViewById(R.id.tv_blood_test);
        tv_blood_test.setPaintFlags(tv_blood_test.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

        ArrayList<String> data = testCaseString.getPathology().getBlood();
        ArrayList<String> data1 = testCaseString.getPathology().getUrine();
        ArrayList<GridItem> gridItems = new ArrayList<>();
        ArrayList<GridItem> gridItems1 = new ArrayList<>();
        for (int i =0 ; i< data.size();i++){
            gridItems.add(new GridItem().setFavourite(false).setKey("pathology").setLabel(data.get(i)).setSelect(false).setFavourite(true));
        }
        for (int i =0 ; i< data1.size();i++){
            gridItems1.add(new GridItem().setFavourite(false).setKey("pathology").setLabel(data1.get(i)).setSelect(false).setFavourite(false));
        }
        GridView gv_blood =  view.findViewById(R.id.gv_blood);
        GridView gv_urine =  view.findViewById(R.id.gv_urine);
        gv_blood.setAdapter(new GridAdapter(getActivity(),gridItems,this,"blood"));
        gv_urine.setAdapter(new GridAdapter(getActivity(),gridItems1,this,"urine"));

        actv_complaints = view.findViewById(R.id.actv_complaints);
        actv_past_history = view.findViewById(R.id.actv_past_history);
        actv_family_history = view.findViewById(R.id.actv_family_history);

        actv_known_allergy = view.findViewById(R.id.actv_known_allergy);
        actv_clinical_finding = view.findViewById(R.id.actv_clinical_finding);
        actv_provisional = view.findViewById(R.id.actv_provisional);
        actv_investigation = view.findViewById(R.id.actv_investigation);
        actv_followup = view.findViewById(R.id.actv_followup);
        actv_instruction = view.findViewById(R.id.actv_instruction);

        edt_weight = view.findViewById(R.id.edt_weight);
        edt_bp = view.findViewById(R.id.edt_bp);
        edt_pulse = view.findViewById(R.id.edt_pulse);
        edt_temperature = view.findViewById(R.id.edt_temperature);
        edt_oxygen = view.findViewById(R.id.edt_oxygen);
        ll_fav_medicines = view.findViewById(R.id.ll_fav_medicines);

        actv_medicine_name = view.findViewById(R.id.actv_medicine_name);
        actv_medicine_type = view.findViewById(R.id.actv_medicine_type);
        actv_dose = view.findViewById(R.id.actv_dose);
        actv_frequency = view.findViewById(R.id.actv_frequency);
        actv_dose_timing = view.findViewById(R.id.actv_dose_timing);
        actv_course = view.findViewById(R.id.actv_course);
        tv_add = view.findViewById(R.id.tv_add);
        tv_assist = view.findViewById(R.id.tv_assist);
        rg_duration = view.findViewById(R.id.rg_duration);
        tv_favourite_text = view.findViewById(R.id.tv_favourite_text);
        tv_favourite_text.setVisibility(medicalRecordFavouriteList.size() != 0 ? View.GONE : View.VISIBLE);
        tv_assist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpand) {
                    AnimationUtils.expand(ll_fav_medicines);
                    Drawable img = getResources().getDrawable(R.drawable.arrow_up);
                    tv_assist.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                } else {
                    AnimationUtils.collapse(ll_fav_medicines);
                    Drawable img = getResources().getDrawable(R.drawable.arrow_down);
                    tv_assist.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                }
                isExpand = !isExpand;
                tv_favourite_text.setVisibility(medicalRecordFavouriteList.size() != 0 ? View.GONE : View.VISIBLE);
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
                    if (!medicalRecordList.contains(jsonMedicalMedicine)) {
                        medicalRecordList.add(0, jsonMedicalMedicine);
                        adapter.notifyDataSetChanged();
                        updateSuggetions(actv_medicine_name, MEDICINES_NAME);
                        setSuggetions(actv_medicine_name, MEDICINES_NAME, false);
                        updateSuggetions(actv_dose, MEDICINES_DOSE);
                        setSuggetions(actv_dose, MEDICINES_DOSE, false);
                        updateSuggetions(actv_frequency, MEDICINES_FREQUENCY);
                        setSuggetions(actv_frequency, MEDICINES_FREQUENCY, false);
                        updateSuggetions(actv_course, MEDICINES_COURSE);
                        setSuggetions(actv_course, MEDICINES_COURSE, false);
                        updateSuggetions(actv_medicine_type, MEDICINES_TYPE);
                        setSuggetions(actv_medicine_type, MEDICINES_TYPE, false);
                        updateSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS);
                        setSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS, false);
                        // update medicine related info because we are setting the fields blank
                        LaunchActivity.getLaunchActivity().setSuggestions(hashmap);
                        actv_medicine_name.setText("");
                        actv_dose.setText("");
                        actv_frequency.setText("");
                        actv_course.setText("");
                        actv_medicine_type.setText("");
                        actv_dose_timing.setText("");
                    } else {
                        Toast.makeText(getActivity(), "medicine already added", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        qCodeQR = getArguments().getString("qCodeQR");
        jsonQueuedPerson = (JsonQueuedPerson) getArguments().getSerializable("data");
        btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

        if (!appInstalledOrNot(packageName)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater_inner = LayoutInflater.from(getActivity());
            builder.setTitle(null);
            View customDialogView = inflater_inner.inflate(R.layout.dialog_logout, null, false);
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
            hashmap = new HashMap<>();
            hashmap.put(CHIEF, new ArrayList<String>());
            hashmap.put(PAST_HISTORY, new ArrayList<String>());
            hashmap.put(FAMILY_HISTORY, new ArrayList<String>());
            hashmap.put(CLINICAL_FINDINGS, new ArrayList<String>());
            hashmap.put(PROVISIONAL_DIAGNOSIS, new ArrayList<String>());
            hashmap.put(INVESTIGATION, new ArrayList<String>());
            hashmap.put(KNOWN_ALLERGIES, new ArrayList<String>());
            hashmap.put(MEDICINES_NAME, new ArrayList<String>());
            hashmap.put(FOLLOW_UP, new ArrayList<String>());
            hashmap.put(INSTRUCTIONS, new ArrayList<String>());

            hashmap.put(MEDICINES_TYPE, new ArrayList<String>());
            hashmap.put(MEDICINES_DOSE, new ArrayList<String>());
            hashmap.put(MEDICINES_FREQUENCY, new ArrayList<String>());
            hashmap.put(MEDICINES_DOSE_TIMINGS, new ArrayList<String>());
            hashmap.put(MEDICINES_COURSE, new ArrayList<String>());

            LaunchActivity.getLaunchActivity().setSuggestions(hashmap);
        } else {
            try {
                hashmap = gson.fromJson(strOutput, type);

                setSuggetions(actv_complaints, CHIEF, true);
                setSuggetions(actv_past_history, PAST_HISTORY, true);
                setSuggetions(actv_family_history, FAMILY_HISTORY, true);
                setSuggetions(actv_known_allergy, KNOWN_ALLERGIES, true);
                setSuggetions(actv_clinical_finding, CLINICAL_FINDINGS, true);
                setSuggetions(actv_provisional, PROVISIONAL_DIAGNOSIS, true);
                setSuggetions(actv_investigation, INVESTIGATION, true);
                setSuggetions(actv_followup, FOLLOW_UP, true);
                setSuggetions(actv_instruction, INSTRUCTIONS, true);

                setSuggetions(actv_medicine_name, MEDICINES_NAME, false);
                setSuggetions(actv_medicine_type, MEDICINES_TYPE, false);
                setSuggetions(actv_dose, MEDICINES_DOSE, false);
                setSuggetions(actv_frequency, MEDICINES_FREQUENCY, false);
                setSuggetions(actv_dose_timing, MEDICINES_DOSE_TIMINGS, false);
                setSuggetions(actv_course, MEDICINES_COURSE, false);


                Log.v("JSON", hashmap.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private boolean isValidEntry() {
        boolean isValid = true;
        String errorMsg = "";
        if (TextUtils.isEmpty(actv_medicine_name.getText().toString())) {
            isValid = false;
            errorMsg = "Please enter medicine name";
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();

        return isValid;
    }

    private void setSuggetions(final AutoCompleteTextView actv, String key, boolean isThreashold) {
        if (null == hashmap.get(key))
            hashmap.put(key, new ArrayList<String>());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, hashmap.get(key));
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
            if (!hashmap.get(key).contains(actv.getText().toString())) {
                hashmap.get(key).add(actv.getText().toString());
            }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    public void onStop() {
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

        LaunchActivity.getLaunchActivity().setSuggestions(hashmap);

        M_MerchantProfileModel m_merchantProfileModel = new M_MerchantProfileModel(this);
        m_merchantProfileModel.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(LaunchActivity.getLaunchActivity().getSuggestions()));

    }

    private boolean validate() {
        btn_update.setBackgroundResource(R.drawable.button_drawable);
        btn_update.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
        btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        boolean isValid = true;
        new AppUtils().hideKeyBoard(getActivity());

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
            Toast.makeText(getActivity(), "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void medicalRecordError() {
        Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
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
                        jsonMedicalRecord.setRecordReferenceId(jsonQueuedPerson.getRecordReferenceId());
                        jsonMedicalRecord.setFormVersion(FormVersionEnum.valueOf(BuildConfig.MEDICAL_FORM_VERSION));
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
                                .setWeight(edt_weight.getText().toString())
                                .setOxygen(edt_oxygen.getText().toString())
                                .setTemperature(edt_temperature.getText().toString());
                        ArrayList<JsonPathology> pathologies = new ArrayList<>();
                        if (sc_urine_data.size() > 0) {
                            for (int i = 0; i < sc_urine_data.size(); i++) {
                                pathologies.add(new JsonPathology().setName(sc_urine_data.get(i)));
                            }
                        }
                        if (sc_blood_data.size() > 0) {
                            for (int i = 0; i < sc_blood_data.size(); i++) {
                                pathologies.add(new JsonPathology().setName(sc_blood_data.get(i)));
                            }
                        }
                        if (sc_urine_data.size() > 0 || sc_blood_data.size() > 0)
                            jsonMedicalRecord.setPathologies(pathologies);

                        jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                        jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineList());
                        jsonMedicalRecord.setPlanToPatient(actv_instruction.getText().toString());
                        if (!actv_followup.getText().toString().equals("")) {
                            String value = actv_followup.getText().toString();
                            int selectedId = rg_duration.getCheckedRadioButtonId();
                            if (selectedId == R.id.rb_months) {
                                jsonMedicalRecord.setFollowUpInDays(String.valueOf(Integer.parseInt(value) * 30));
                            } else {
                                jsonMedicalRecord.setFollowUpInDays(value);
                            }
                        }
                        medicalHistoryModel.add(LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);
                    } else {
                        Toast.makeText(getActivity(), "It seems you forget to add the medicine which you  entered.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please fill at least one field", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


//    @Override
//    public void onBackPressed() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastPress > 3000) {
//            backPressToast = Toast.makeText(getActivity(), getString(R.string.exit_medical_screen), Toast.LENGTH_LONG);
//            backPressToast.show();
//            lastPress = currentTime;
//        } else {
//            if (backPressToast != null) {
//                backPressToast.cancel();
//            }
//            //super.onBackPressed();
//            finish();
//        }
//    }


    @Override
    public void updateFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordFavouriteList.contains(jsonMedicalMedicine))
                Toast.makeText(getActivity(), "medicine already marked as favroite", Toast.LENGTH_LONG).show();
            else
                medicalRecordFavouriteList.add(jsonMedicalMedicine);
        } else
            medicalRecordFavouriteList.remove(jsonMedicalMedicine);
        LaunchActivity.getLaunchActivity().setFavouriteMedicines(medicalRecordFavouriteList);
        adapterFavourite = new MedicalRecordFavouriteAdapter(getActivity(), medicalRecordFavouriteList,this);
        listview_favroite.setAdapter(adapterFavourite);
        tv_favourite_text.setVisibility(medicalRecordFavouriteList.size() != 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void updateNonFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordList.contains(jsonMedicalMedicine))
                Toast.makeText(getActivity(), "medicine already added", Toast.LENGTH_LONG).show();
            else
                medicalRecordList.add(jsonMedicalMedicine);
        } else {
            medicalRecordList.remove(jsonMedicalMedicine);
            jsonMedicalMedicine.setFavourite(false);
            medicalRecordList.add(jsonMedicalMedicine);

        }
        adapter = new MedicalRecordAdapter(getActivity(), medicalRecordList, this);
        listview.setAdapter(adapter);
        tv_favourite_text.setVisibility(medicalRecordFavouriteList.size() != 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void addDeleteItems(String value, boolean isAdded, String key) {
        if(key.equals("blood")) {
            if (isAdded) {
                sc_blood_data.add(value);
            } else {
                sc_blood_data.remove(value);
            }
            sc_blood.removeAllSegments();
            sc_blood.addSegments(sc_blood_data);
        }else if(key.equals("urine")) {
            if (isAdded) {
                sc_urine_data.add(value);
            } else {
                sc_urine_data.remove(value);
            }
            sc_urine.removeAllSegments();
            sc_urine.addSegments(sc_urine_data);
        }
    }
}
