package com.noqapp.android.merchant.views.fragments;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.beans.medical.JsonMedicalPathology;
import com.noqapp.android.common.beans.medical.JsonMedicalPhysical;
import com.noqapp.android.common.beans.medical.JsonMedicalRadiology;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.DailyFrequencyEnum;
import com.noqapp.android.common.model.types.medical.FormVersionEnum;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.FilePresenter;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileModel;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.model.PreferredBusinessModel;
import com.noqapp.android.merchant.model.database.utils.PreferredStoreDB;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.MedicalRecordPresenter;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.Utils.AnimationUtils;
import com.noqapp.android.merchant.views.Utils.TestCaseString;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.adapters.CustomSpinnerAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordAdapter;
import com.noqapp.android.merchant.views.adapters.MedicalRecordFavouriteAdapter;
import com.noqapp.android.merchant.views.adapters.TestListAdapter;
import com.noqapp.android.merchant.views.interfaces.AdapterCommunicate;
import com.noqapp.android.merchant.views.interfaces.ListCommunication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalCaseFragment extends Fragment implements MedicalRecordPresenter, FilePresenter, View.OnClickListener, IntellisensePresenter, AdapterCommunicate, ListCommunication, PreferredBusinessPresenter {

    private String jsonText = "{\n" +
            "  \"pathology\": [\n" +
            "    \"Prostate-Specific Antigen (PSA)\",\n" +
            "    \"Thyroid Stimulating Hormone (TSH)\",\n" +
            "    \"Testosterone (Free)\",\n" +
            "    \"Estradiol\",\n" +
            "    \"Pregnenolone\",\n" +
            "    \"Homocysteine\",\n" +
            "    \"Hemoglobin A1C (HbA1C)\",\n" +
            "    \"Dihydrotestosterone (DHT)\",\n" +
            "    \"Urinary Methylmalonic Acid (MMA)\",\n" +
            "    \"Urinalysis\",\n" +
            "    \"Neurotransmitter Panel\",\n" +
            "    \"Iodine\",\n" +
            "    \"Protein and Creatinine\"\n" +
            "  ],\n" +
            "  \"radiology\": [\n" +
            "    \"Bone Density (BMD)\",\n" +
            "    \"Ribs (Oblique)\",\n" +
            "    \"Acromioclavicular (AC) Joint\",\n" +
            "    \"Teeth and bones X-rays\",\n" +
            "    \"Submentovertex (S.M.V.)\",\n" +
            "    \"Angiography (MRA) Head\",\n" +
            "    \"Venography (MRV) Brain\",\n" +
            "    \"Pituitary With Contrast\",\n" +
            "    \"Angiography (MRA) Neck\",\n" +
            "    \"Cholangiopancreatography (MRCP)\"\n" +
            "  ],\n" +
            "  \"general\": [\n" +
            "    \"ecg\",\n" +
            "    \"cag\"\n" +
            "  ]\n" +
            "}";
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
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
    private Map<String, List<String>> map = null;
    private String qCodeQR = "";
    private AutoCompleteTextView actv_medicine_name, actv_complaints, actv_family_history, actv_past_history, actv_known_allergy, actv_clinical_finding, actv_provisional, actv_investigation, actv_instruction, actv_followup;
    private EditText edt_weight, edt_bp, edt_pulse, edt_temperature, edt_oxygen;
    private JsonQueuedPerson jsonQueuedPerson;
    private Button btn_update;
    private ListView listview, listview_favourite;
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
    private TextView tv_assist, tv_favourite_text;
    private LinearLayout ll_fav_medicines;
    private boolean isExpand;
    private RadioGroup rg_duration;
    private TestCaseString testCaseString;
    private TestListAdapter pathologyAdapter;
    private TestListAdapter radiologyAdapter;
    private ListView lv_pathology, lv_radiology;
    private ArrayList<String> lv_pathology_items = new ArrayList<>();
    private ArrayList<String> lv_radiology_items = new ArrayList<>();
    private AutoCompleteTextView actv_pathology, actv_radiology;
    private final String PATHOLOGY = "pathology";
    private final String RADIOLOGY = "radiology";
    private ListCommunication listCommunication;
    private JsonPreferredBusinessList jsonPreferredBusinessList;
    private Spinner sp_preferred_list;
    private String bizStoreID = "5b7a7079783cea2a6c2556fa";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_case, container, false);
        try {
            testCaseString = new Gson().fromJson(jsonText, TestCaseString.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listCommunication = this;

        ImageView iv_add_pathology = view.findViewById(R.id.iv_add_pathology);
        ImageView iv_add_radiology = view.findViewById(R.id.iv_add_radiology);
        medicalHistoryModel = new MedicalHistoryModel(this);
        sp_preferred_list = view.findViewById(R.id.sp_preferred_list);
        listview = view.findViewById(R.id.listview);
        listview_favourite = view.findViewById(R.id.listview_favroite);
        adapter = new MedicalRecordAdapter(getActivity(), medicalRecordList, this);
        listview.setAdapter(adapter);

        medicalRecordFavouriteList = LaunchActivity.getLaunchActivity().getFavouriteMedicines();
        if (null == medicalRecordFavouriteList) {
            medicalRecordFavouriteList = new ArrayList<>();
        }
        adapterFavourite = new MedicalRecordFavouriteAdapter(getActivity(), medicalRecordFavouriteList, this);
        listview_favourite.setAdapter(adapterFavourite);

        lv_pathology = view.findViewById(R.id.lv_pathology);
        pathologyAdapter = new TestListAdapter(getActivity(), lv_pathology_items, PATHOLOGY, this);
        lv_pathology.setAdapter(pathologyAdapter);

        lv_radiology = view.findViewById(R.id.lv_radiology);
        radiologyAdapter = new TestListAdapter(getActivity(), lv_radiology_items, RADIOLOGY, this);
        lv_radiology.setAdapter(radiologyAdapter);

        actv_pathology = view.findViewById(R.id.actv_pathology);
        final ArrayAdapter<String> actv_patholoy_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, testCaseString.getPathology());
        actv_pathology.setAdapter(actv_patholoy_adapter);
        actv_pathology.setThreshold(1);
        actv_pathology.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                if (!lv_pathology_items.contains(actv_patholoy_adapter.getItem(pos))) {
                    lv_pathology_items.add(actv_patholoy_adapter.getItem(pos));
                    pathologyAdapter = new TestListAdapter(getActivity(), lv_pathology_items, PATHOLOGY, listCommunication);
                    lv_pathology.setAdapter(pathologyAdapter);
                } else {
                    Toast.makeText(getActivity(), "Selected test case already added", Toast.LENGTH_LONG).show();
                }
                actv_pathology.setText("");
            }
        });
        iv_add_pathology.setOnClickListener(this);

        actv_radiology = view.findViewById(R.id.actv_radiology);
        final ArrayAdapter<String> actv_radiology_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, testCaseString.getRadiology());
        actv_radiology.setAdapter(actv_radiology_adapter);
        actv_radiology.setThreshold(1);
        actv_radiology.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                if (!lv_radiology_items.contains(actv_radiology_adapter.getItem(pos))) {
                    lv_radiology_items.add(actv_radiology_adapter.getItem(pos));
                    radiologyAdapter = new TestListAdapter(getActivity(), lv_radiology_items, RADIOLOGY, listCommunication);
                    lv_radiology.setAdapter(radiologyAdapter);
                } else {
                    Toast.makeText(getActivity(), "Selected test case already added", Toast.LENGTH_LONG).show();
                }
                actv_radiology.setText("");
            }
        });
        iv_add_radiology.setOnClickListener(this);
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
        Button tv_add = view.findViewById(R.id.tv_add);
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
                    jsonMedicalMedicine.setPharmacyCategory(actv_medicine_type.getText().toString());
                    jsonMedicalMedicine.setMedicationWithFood(actv_dose_timing.getText().toString());
                    jsonMedicalMedicine.setCourse(actv_course.getText().toString());
                    jsonMedicalMedicine.setName(actv_medicine_name.getText().toString());
                    if (!medicalRecordList.contains(jsonMedicalMedicine)) {
                        medicalRecordList.add(0, jsonMedicalMedicine);
                        adapter.notifyDataSetChanged();
                        if(!PreferredStoreDB.isMedicineExist(actv_medicine_name.getText().toString())) {
                            updateSuggestions(actv_medicine_name, MEDICINES_NAME);
                            setSuggestions(actv_medicine_name, MEDICINES_NAME, false);
                        }
                        updateSuggestions(actv_dose, MEDICINES_DOSE);
                        setSuggestions(actv_dose, MEDICINES_DOSE, false);
                        updateSuggestions(actv_frequency, MEDICINES_FREQUENCY);
                        setSuggestions(actv_frequency, MEDICINES_FREQUENCY, false);
                        updateSuggestions(actv_course, MEDICINES_COURSE);
                        setSuggestions(actv_course, MEDICINES_COURSE, false);
                        updateSuggestions(actv_medicine_type, MEDICINES_TYPE);
                        setSuggestions(actv_medicine_type, MEDICINES_TYPE, false);
                        updateSuggestions(actv_dose_timing, MEDICINES_DOSE_TIMINGS);
                        setSuggestions(actv_dose_timing, MEDICINES_DOSE_TIMINGS, false);
                        // update medicine related info because we are setting the fields blank
                        LaunchActivity.getLaunchActivity().setSuggestions(map);
                        actv_medicine_name.setText("");
                        actv_dose.setText("");
                        actv_frequency.setText("");
                        actv_course.setText("");
                        actv_medicine_type.setText("");
                        actv_dose_timing.setText("");
                    } else {
                        Toast.makeText(getActivity(), "Medicine already added", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        qCodeQR = getArguments().getString("qCodeQR");
        jsonQueuedPerson = (JsonQueuedPerson) getArguments().getSerializable("data");
        btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

        if (!isAppInstalled(packageName)) {
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
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
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
            map = new HashMap<>();
            map.put(CHIEF, new ArrayList<String>());
            map.put(PAST_HISTORY, new ArrayList<String>());
            map.put(FAMILY_HISTORY, new ArrayList<String>());
            map.put(CLINICAL_FINDINGS, new ArrayList<String>());
            map.put(PROVISIONAL_DIAGNOSIS, new ArrayList<String>());
            map.put(INVESTIGATION, new ArrayList<String>());
            map.put(KNOWN_ALLERGIES, new ArrayList<String>());
            map.put(MEDICINES_NAME, new ArrayList<String>());
            map.put(FOLLOW_UP, new ArrayList<String>());
            map.put(INSTRUCTIONS, new ArrayList<String>());

            map.put(MEDICINES_TYPE, new ArrayList<String>());
            map.put(MEDICINES_DOSE, new ArrayList<String>());
            map.put(MEDICINES_FREQUENCY, new ArrayList<String>());
            map.put(MEDICINES_DOSE_TIMINGS, new ArrayList<String>());
            map.put(MEDICINES_COURSE, new ArrayList<String>());
            setSuggestions(actv_medicine_name, MEDICINES_NAME, false); // set the default suggestion initially
            setSuggestions(actv_medicine_type, MEDICINES_TYPE, false);
            setSuggestions(actv_frequency, MEDICINES_FREQUENCY, false);
            LaunchActivity.getLaunchActivity().setSuggestions(map);
        } else {
            try {
                map = gson.fromJson(strOutput, type);

                setSuggestions(actv_complaints, CHIEF, true);
                setSuggestions(actv_past_history, PAST_HISTORY, true);
                setSuggestions(actv_family_history, FAMILY_HISTORY, true);
                setSuggestions(actv_known_allergy, KNOWN_ALLERGIES, true);
                setSuggestions(actv_clinical_finding, CLINICAL_FINDINGS, true);
                setSuggestions(actv_provisional, PROVISIONAL_DIAGNOSIS, true);
                setSuggestions(actv_investigation, INVESTIGATION, true);
                setSuggestions(actv_followup, FOLLOW_UP, true);
                setSuggestions(actv_instruction, INSTRUCTIONS, true);

                setSuggestions(actv_medicine_name, MEDICINES_NAME, false);
                setSuggestions(actv_medicine_type, MEDICINES_TYPE, false);
                setSuggestions(actv_dose, MEDICINES_DOSE, false);
                setSuggestions(actv_frequency, MEDICINES_FREQUENCY, false);
                setSuggestions(actv_dose_timing, MEDICINES_DOSE_TIMINGS, false);
                setSuggestions(actv_course, MEDICINES_COURSE, false);
                Log.v("JSON", map.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<String> temp = PharmacyCategoryEnum.asListOfDescription();
        List<String> temp_daily_frequency = DailyFrequencyEnum.asListOfDescription();
        updateDefineSuggestions(MEDICINES_TYPE, temp);
        updateDefineSuggestions(MEDICINES_FREQUENCY, temp_daily_frequency);
        if (isStoragePermissionAllowed()) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                PreferredBusinessModel preferredBusinessModel = new PreferredBusinessModel(this);
                preferredBusinessModel.getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), qCodeQR);
            }
        } else {
            requestStoragePermission();
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
            errorMsg += "Please select dose for the medicine \n";
        }
        if (TextUtils.isEmpty(actv_frequency.getText().toString())) {
            isValid = false;
            errorMsg += "Please select frequency for the medicine \n";
        }
        if (TextUtils.isEmpty(actv_course.getText().toString())) {
            isValid = false;
            errorMsg += "Please select course for the medicine \n";
        }
        if (TextUtils.isEmpty(actv_medicine_type.getText().toString())) {
            isValid = false;
            errorMsg += "Please select medication type for the medicine \n";
        }
        if (TextUtils.isEmpty(actv_dose_timing.getText().toString())) {
            isValid = false;
            errorMsg += "Please select dose timing for the medicine \n";
        }
        if (!errorMsg.equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    private void setSuggestions(final AutoCompleteTextView actv, String key, boolean isThreshold) {
        if (null == map.get(key)) {
            map.put(key, new ArrayList<String>());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, map.get(key));
        actv.setAdapter(adapter);
        if (isThreshold) {
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

    private void updateSuggestions(AutoCompleteTextView actv, String key) {
        if (!actv.getText().toString().equals("")) {
            if (!map.get(key).contains(actv.getText().toString())) {
                map.get(key).add(actv.getText().toString());
            }
        }
    }

    private boolean isAppInstalled(String uri) {
        try {
            PackageManager pm = getActivity().getPackageManager();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        updateSuggestions(actv_complaints, CHIEF);
        updateSuggestions(actv_past_history, PAST_HISTORY);
        updateSuggestions(actv_family_history, FAMILY_HISTORY);
        updateSuggestions(actv_known_allergy, KNOWN_ALLERGIES);
        updateSuggestions(actv_clinical_finding, CLINICAL_FINDINGS);
        updateSuggestions(actv_provisional, PROVISIONAL_DIAGNOSIS);
        updateSuggestions(actv_investigation, INVESTIGATION);
        updateSuggestions(actv_followup, FOLLOW_UP);
        updateSuggestions(actv_instruction, INSTRUCTIONS);
        //update medicine when add button click
        //updateSuggestions(actv_medicine_name, MEDICINES);
//        updateSuggestions(actv_medicine_type, MEDICINES_TYPE);
//        updateSuggestions(actv_dose, MEDICINES_DOSE);
//        updateSuggestions(actv_frequency, MEDICINES_FREQUENCY);
//        updateSuggestions(actv_dose_timing, MEDICINES_DOSE_TIMINGS);
//        updateSuggestions(actv_course, MEDICINES_COURSE);

        LaunchActivity.getLaunchActivity().setSuggestions(map);
        M_MerchantProfileModel m_merchantProfileModel = new M_MerchantProfileModel(this);
        m_merchantProfileModel.uploadIntellisense(
                UserUtils.getDeviceId(),
                UserUtils.getEmail(),
                UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(LaunchActivity.getLaunchActivity().getSuggestions()));
    }

    private boolean validate() {
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
        } else {
            btn_update.setBackgroundResource(R.drawable.button_drawable);
            btn_update.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorMobile));
            btn_update.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_small, 0);
        }
        return isValid;
    }

    @Override
    public void medicalRecordResponse(JsonResponse jsonResponse) {
        if (1 == jsonResponse.getResponse()) {
            Toast.makeText(getActivity(), "Medical History updated Successfully", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(getActivity(),eej);
    }

    @Override
    public void medicalRecordError() {
        Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_LONG).show();
    }

    @Override
    public void intellisenseResponse(JsonResponse jsonResponse) {
        Log.v("Intellisense upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void intellisenseError() {
        Log.v("Intellisense upload: ", "error");
    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList) {
        this.jsonPreferredBusinessList = jsonPreferredBusinessList;

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getActivity(), jsonPreferredBusinessList.getPreferredBusinesses());
        sp_preferred_list.setAdapter(spinAdapter);
        PreferredBusinessModel preferredBusinessModel = new PreferredBusinessModel(this);
        preferredBusinessModel.setFilePresenter(this);
        preferredBusinessModel.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), qCodeQR, jsonPreferredBusinessList.getPreferredBusinesses().get(0).getBizStoreId());
    }

    @Override
    public void preferredBusinessError() {

    }

    @Override
    public void fileResponse(File temp) {
        if (null != temp) {
            try {
                File destination = new File(Environment.getExternalStorageDirectory() + "/UnZipped/");

                Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
                archiver.extract(temp, destination);
                String path = Environment.getExternalStorageDirectory() + "/UnZipped";
                Log.d("Files", "Path: " + path);
                File directory = new File(path);
                directory.deleteOnExit();

                File[] files = directory.listFiles();
                Log.d("Files", "Size: " + files.length);
                for (File file : files) {
                    String fileName = file.getName();
                    Log.d("Files", "FileName:" + fileName);
                    if (fileName.endsWith(".csv")) {
                        int lineCount = 0;
                        try {
                            PreferredStoreDB.deletePreferredStore(fileName.substring(0, fileName.lastIndexOf("_")));
                            BufferedReader buffer = new BufferedReader(new FileReader(file.getAbsolutePath()));
                            String line;
                            while ((line = buffer.readLine()) != null) {
                                lineCount ++;
                                PreferredStoreDB.insertPreferredStore(line);
                            }
                        } catch (Exception e) {
                            Log.e("Loading file=" + fileName + " line=" + lineCount + " reason={}", e.getLocalizedMessage(), e);
                            throw new RuntimeException("Loading file=" + fileName + " line=" + lineCount);
                        }
                    }
                }
                for (File file : files) {
                    new File(path, file.getName()).delete();
                }
                directory.delete();
                //TODO(Chandra) pass dynamic value of product id
                final List<String> data = PreferredStoreDB.getPreferredStoreDataList(bizStoreID);
                updateDefineSuggestions(MEDICINES_NAME, data);
                actv_medicine_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        List<String> tempdata = PreferredStoreDB.getPreferredStoreDataList(bizStoreID,PharmacyCategoryEnum.getValue(actv_medicine_type.getAdapter().getItem(position).toString()));
                        removeDefineSuggestions(MEDICINES_NAME, data);
                        updateDefineSuggestions(MEDICINES_NAME, tempdata);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempdata);
                        actv_medicine_name.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                Log.e("Failed file loading {}", e.getLocalizedMessage(), e);
                //TODO make sure to increase the date as not to fetch again
            }
        }
    }

    private void updateDefineSuggestions(String key, List<String> data) {
        for (int k = 0; k < data.size(); k++) {
            if (!map.get(key).contains(data.get(k))) {
                map.get(key).add(data.get(k));
            }
        }
    }
    private void removeDefineSuggestions(String key, List<String> data) {
        for (int k = 0; k < data.size(); k++) {
            if (map.get(key).contains(data.get(k))) {
                map.get(key).remove(data.get(k));
            }
        }
    }

    @Override
    public void fileError() {

    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_pathology:
                if (TextUtils.isEmpty(actv_pathology.getText().toString())) {
                    Toast.makeText(getActivity(), "Pathology field is empty", Toast.LENGTH_LONG).show();
                } else {
                    if (containsIgnoreCase(lv_pathology_items, actv_pathology.getText().toString().trim())) {
                        Toast.makeText(getActivity(), "Selected test case already added", Toast.LENGTH_LONG).show();
                    } else {
                        lv_pathology_items.add(actv_pathology.getText().toString());
                        pathologyAdapter = new TestListAdapter(getActivity(), lv_pathology_items, PATHOLOGY, listCommunication);
                        lv_pathology.setAdapter(pathologyAdapter);
                    }
                    actv_pathology.setText("");
                }
                break;

            case R.id.iv_add_radiology:
                if (TextUtils.isEmpty(actv_radiology.getText().toString())) {
                    Toast.makeText(getActivity(), "Radiology field is empty ", Toast.LENGTH_LONG).show();
                } else {
                    if (containsIgnoreCase(lv_radiology_items, actv_radiology.getText().toString().trim())) {
                        Toast.makeText(getActivity(), "Selected test case already added", Toast.LENGTH_LONG).show();
                    } else {
                        lv_radiology_items.add(actv_radiology.getText().toString());
                        radiologyAdapter = new TestListAdapter(getActivity(), lv_radiology_items, RADIOLOGY, listCommunication);
                        lv_radiology.setAdapter(radiologyAdapter);
                    }
                    actv_radiology.setText("");
                }
                break;
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

                        if (lv_pathology_items.size() > 0) {
                            ArrayList<JsonMedicalPathology> pathologies = new ArrayList<>();
                            for (int i = 0; i < lv_pathology_items.size(); i++) {
                                pathologies.add(new JsonMedicalPathology().setName(lv_pathology_items.get(i)));
                            }
                            jsonMedicalRecord.setMedicalPathologies(pathologies);
                        }
                        if (lv_radiology_items.size() > 0) {
                            ArrayList<JsonMedicalRadiology> medicalRadiologies = new ArrayList<>();
                            for (int i = 0; i < lv_radiology_items.size(); i++) {
                                medicalRadiologies.add(new JsonMedicalRadiology().setName(lv_radiology_items.get(i)));
                            }
                            jsonMedicalRecord.setMedicalRadiologies(medicalRadiologies);
                        }

                        if (null != jsonPreferredBusinessList && null != jsonPreferredBusinessList.getPreferredBusinesses() && jsonPreferredBusinessList.getPreferredBusinesses().size() > 0)
                            jsonMedicalRecord.setStoreIdPharmacy(jsonPreferredBusinessList.getPreferredBusinesses().get(sp_preferred_list.getSelectedItemPosition()).getBizStoreId());

                        jsonMedicalRecord.setMedicalPhysical(jsonMedicalPhysical);
                        jsonMedicalRecord.setMedicalMedicines(adapter.getJsonMedicineListWithEnum());
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
                        medicalHistoryModel.add(
                                LaunchActivity.getLaunchActivity().getDeviceID(),
                                LaunchActivity.getLaunchActivity().getEmail(),
                                LaunchActivity.getLaunchActivity().getAuth(),
                                jsonMedicalRecord);
                    } else {
                        Toast.makeText(getActivity(), "It seems you forget to add the medicine which you entered.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please fill at least one field", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void updateFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordFavouriteList.contains(jsonMedicalMedicine)) {
                Toast.makeText(getActivity(), "Medicine already marked as favorite", Toast.LENGTH_LONG).show();
            } else {
                medicalRecordFavouriteList.add(jsonMedicalMedicine);
            }
        } else {
            medicalRecordFavouriteList.remove(jsonMedicalMedicine);
        }
        LaunchActivity.getLaunchActivity().setFavouriteMedicines(medicalRecordFavouriteList);
        adapterFavourite = new MedicalRecordFavouriteAdapter(getActivity(), medicalRecordFavouriteList, this);
        listview_favourite.setAdapter(adapterFavourite);
        tv_favourite_text.setVisibility(medicalRecordFavouriteList.size() != 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void updateNonFavouriteList(JsonMedicalMedicine jsonMedicalMedicine, boolean isAdded) {
        if (isAdded) {
            if (medicalRecordList.contains(jsonMedicalMedicine)) {
                Toast.makeText(getActivity(), "Medicine already added", Toast.LENGTH_LONG).show();
            } else {
                medicalRecordList.add(jsonMedicalMedicine);
            }
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
    public void updateList(ArrayList<String> list, String key) {
        if (key.equals(PATHOLOGY)) {
            lv_pathology_items = list;
            pathologyAdapter = new TestListAdapter(getActivity(), lv_pathology_items, PATHOLOGY, this);
            lv_pathology.setAdapter(pathologyAdapter);
        } else if (key.equals(RADIOLOGY)) {
            lv_radiology_items = list;
            radiologyAdapter = new TestListAdapter(getActivity(), lv_radiology_items, RADIOLOGY, this);
            lv_radiology.setAdapter(radiologyAdapter);
        }
    }

    private boolean containsIgnoreCase(List<String> list, String soughtFor) {
        for (String current : list) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                getActivity(),
                STORAGE_PERMISSION_PERMS,
                STORAGE_PERMISSION_CODE);
    }
}
