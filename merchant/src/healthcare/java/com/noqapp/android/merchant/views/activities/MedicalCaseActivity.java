package com.noqapp.android.merchant.views.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.PreferredBusinessApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusiness;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessBucket;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.ExaminationTabFragment;
import com.noqapp.android.merchant.views.fragments.InstructionFragment;
import com.noqapp.android.merchant.views.fragments.LabTestsFragment;
import com.noqapp.android.merchant.views.fragments.PrimaryCheckupFragment;
import com.noqapp.android.merchant.views.fragments.PrintFragment;
import com.noqapp.android.merchant.views.fragments.SymptomsFragment;
import com.noqapp.android.merchant.views.fragments.TreatmentTabFragment;
import com.noqapp.android.merchant.views.pojos.CaseHistory;
import com.noqapp.android.merchant.views.pojos.FormDataObj;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;
import com.noqapp.android.merchant.views.pojos.PreferredStoreInfo;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.util.ArrayList;
import java.util.List;

public class MedicalCaseActivity extends BaseActivity implements
        MenuHeaderAdapter.OnItemClickListener, PreferredBusinessPresenter {
    private ViewPager viewPager;
    private long lastPress;
    private Toast backPressToast;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ArrayList<String> data = new ArrayList<>();
    private PrimaryCheckupFragment primaryCheckupFragment;
    public SymptomsFragment symptomsFragment;
    private LabTestsFragment labTestsFragment;
    private TreatmentTabFragment treatmentTabFragment;
    private ExaminationTabFragment examinationTabFragment;
    private InstructionFragment instructionFragment;
    private PrintFragment printFragment;
    public boolean isGynae = false;
    public boolean isGynae() {
        return isGynae;
    }
    public PreferenceObjects getPreferenceObjects() {
        return preferenceObjects;
    }
    private PreferenceObjects preferenceObjects;
    private LoadTabs loadTabs;
    public JsonMedicalRecord getJsonMedicalRecord() {
        return jsonMedicalRecord;
    }
    public JsonMedicalRecord jsonMedicalRecord;
    public CaseHistory getCaseHistory() {
        return caseHistory;
    }
    private CaseHistory caseHistory;
    public static MedicalCaseActivity getMedicalCaseActivity() {
        return medicalCaseActivity;
    }
    private static MedicalCaseActivity medicalCaseActivity;
    public FormDataObj formDataObj;
    public JsonQueuedPerson jsonQueuedPerson;
    public String codeQR;
    public List<JsonPreferredBusiness> jsonPreferredBusiness;
    public String bizCategoryId;
    private ProgressBar pb_case;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        medicalCaseActivity = this;
        formDataObj = new FormDataObj();
        try {
            preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), PreferenceObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        if (null == preferenceObjects) {
            preferenceObjects = new PreferenceObjects();
            initStores();
        }
        caseHistory = new CaseHistory();
        viewPager = findViewById(R.id.pager);
        rcv_header = findViewById(R.id.rcv_header);
        pb_case = findViewById(R.id.pb_case);
        data.add("Primary checkup");
        data.add("Symptoms");
        data.add("Examination");
        data.add("Investigation");
        data.add("Treatment");
        data.add("Instructions");
        data.add("Preview");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        codeQR = getIntent().getStringExtra("qCodeQR");
        bizCategoryId = getIntent().getStringExtra("bizCategoryId");
        if (!TextUtils.isEmpty(bizCategoryId))
            isGynae = MedicalDepartmentEnum.valueOf(bizCategoryId) == MedicalDepartmentEnum.OGY;
        JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
        caseHistory.setName(jsonProfile.getName());
        caseHistory.setAddress(jsonProfile.getAddress());
        caseHistory.setAge(new AppUtils().calculateAge(jsonProfile.getBirthday()));
        caseHistory.setGender(jsonProfile.getGender().name());
        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                saveAllData();
                rcv_header.smoothScrollToPosition(position);
                menuAdapter.setSelected_pos(position);
                menuAdapter.notifyDataSetChanged();
                if(position == 6)
                    printFragment.updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            PreferredBusinessApiCalls preferredBusinessModel = new PreferredBusinessApiCalls(MedicalCaseActivity.this);
            preferredBusinessModel.getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadTabs = new LoadTabs();
                loadTabs.execute();
            }
        }, 100);


    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessBucket jsonPreferredBusinessBucket) {
        if (null != jsonPreferredBusinessBucket && jsonPreferredBusinessBucket.getJsonPreferredBusinessLists() != null && jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().size() > 0) {
            for (int i = 0; i < jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().size(); i++) {
                if(jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().get(i).getCodeQR().equals(codeQR)){
                    this.jsonPreferredBusiness = jsonPreferredBusinessBucket.getJsonPreferredBusinessLists().get(i).getPreferredBusinesses();
                    return;
                }
            }
        }
        Log.e("Pref business list: ", jsonPreferredBusinessBucket.toString());
    }

    @Override
    public void preferredBusinessError() {

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        updateSuggestions();
        if (currentTime - lastPress > 3000) {
            backPressToast = new CustomToast().getToast(this, getString(R.string.exit_medical_screen));
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

    public void updateSuggestions() {
        PreferenceObjects temp = new PreferenceObjects();
        temp.setMriList(preferenceObjects.clearListSelection(preferenceObjects.getMriList()));
        temp.setScanList(preferenceObjects.clearListSelection(preferenceObjects.getScanList()));
        temp.setSonoList(preferenceObjects.clearListSelection(preferenceObjects.getSonoList()));
        temp.setXrayList(preferenceObjects.clearListSelection(preferenceObjects.getXrayList()));
        temp.setPathologyList(preferenceObjects.clearListSelection(preferenceObjects.getPathologyList()));
        temp.setSpecList(preferenceObjects.clearListSelection(preferenceObjects.getSpecList()));
        temp.setMedicineList(preferenceObjects.clearListSelection(preferenceObjects.getMedicineList()));
        temp.setSymptomsList(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));
        temp.setProDiagnosisList(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
        temp.setDiagnosisList(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));
        temp.setInstructionList(preferenceObjects.getInstructionList());
        temp.setPreferredStoreInfoHashMap(preferenceObjects.getPreferredStoreInfoHashMap());
        LaunchActivity.getLaunchActivity().setSuggestionsPrefs(temp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs) {
            loadTabs.cancel(true);
        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
        // saveAllData();
    }

    private void saveAllData() {
        primaryCheckupFragment.saveData();
        symptomsFragment.saveData();
        examinationTabFragment.saveData();
        labTestsFragment.saveData();
        treatmentTabFragment.saveData();
        instructionFragment.saveData();
    }

    private void initLists() {

        formDataObj.getMriList().clear();
        formDataObj.getScanList().clear();
        formDataObj.getSonoList().clear();
        formDataObj.getXrayList().clear();
        formDataObj.getSpecList().clear();
        formDataObj.getPathologyList().clear();


        // Add selected list
        formDataObj.getMriList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getMriList()));
        formDataObj.getScanList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getScanList()));
        formDataObj.getSonoList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSonoList()));
        formDataObj.getXrayList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getXrayList()));
        formDataObj.getSpecList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSpecList()));
        //
        formDataObj.getPathologyList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getPathologyList()));
        //

        switch (MedicalDepartmentEnum.valueOf(bizCategoryId)) {
            case OGY: {
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Gynae.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));


                formDataObj.getObstreticsList().clear();
                formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());


                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Gynae.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));


                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Gynae.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;
            case PAE: {
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Pediatrician.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));

                formDataObj.getObstreticsList().clear();
                // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Pediatrician.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));

                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Pediatrician.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;
            case ORT: {
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Ortho.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));

                formDataObj.getObstreticsList().clear();
                // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Ortho.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));

                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Ortho.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;
            case GSR: {
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Surgeon.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));

                formDataObj.getObstreticsList().clear();
                // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Surgeon.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));

                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Surgeon.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;
            case DNT: {
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Dental.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));

                formDataObj.getObstreticsList().clear();
                // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Dental.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));

                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Dental.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;
            default: { // General Physician is default
                formDataObj.getSymptomsList().clear();
                formDataObj.getSymptomsList().addAll(MedicalDataStatic.Physician.getSymptoms());
                formDataObj.getSymptomsList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getSymptomsList()));

                formDataObj.getObstreticsList().clear();
                // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

                formDataObj.getDiagnosisList().clear();
                formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Physician.getDiagnosis());
                formDataObj.getDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getDiagnosisList()));

                formDataObj.getProvisionalDiagnosisList().clear();
                formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Physician.getProvisionalDiagnosis());
                formDataObj.getProvisionalDiagnosisList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getProDiagnosisList()));
            }
            break;

        }


        formDataObj.getMedicineList().clear();
        // Add selected list
        formDataObj.getMedicineList().addAll(preferenceObjects.clearListSelection(preferenceObjects.getMedicineList()));
        //
        formDataObj.getInstructionList().clear();
        formDataObj.getInstructionList().add("Steam Inhalation");
        formDataObj.getInstructionList().add("Plenty of fluids");
        formDataObj.getInstructionList().add("Nebulization");
        formDataObj.getInstructionList().add("Plenty of Green vegetables & fruits");
        formDataObj.getInstructionList().add("To avoid strong smells of perfumes, deodrent, agarbatti dhoop");
        formDataObj.getInstructionList().add("No oil & no spicy food ");
        formDataObj.getInstructionList().add("Drink more water");
        formDataObj.getInstructionList().add("Increase content of salad in your diet");
        formDataObj.getInstructionList().add("Consume less sugar");
        formDataObj.getInstructionList().add("30 min Brisk walking a day");
        formDataObj.getInstructionList().add("Drink milk every day");
        formDataObj.getInstructionList().add("Unwilling to admit");
        formDataObj.getInstructionList().add("Admit immediately");
        formDataObj.getInstructionList().addAll(preferenceObjects.getInstructionList());
    }

    private class LoadTabs extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                setupViewPager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupViewPager() {
        initLists();
        primaryCheckupFragment = new PrimaryCheckupFragment();
        Bundle bppf = new Bundle();
        bppf.putString("qUserId", jsonQueuedPerson.getQueueUserId());
        bppf.putString("qCodeQR", codeQR);
        bppf.putString("refrenceID", jsonQueuedPerson.getRecordReferenceId());
        primaryCheckupFragment.setArguments(bppf);
        symptomsFragment = new SymptomsFragment();
        examinationTabFragment = new ExaminationTabFragment();
        labTestsFragment = new LabTestsFragment();
        treatmentTabFragment = new TreatmentTabFragment();
        instructionFragment = new InstructionFragment();
        printFragment = new PrintFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(primaryCheckupFragment, "FRAG" + 0);
        adapter.addFragment(symptomsFragment, "FRAG" + 1);
        adapter.addFragment(examinationTabFragment, "FRAG" + 2);
        adapter.addFragment(labTestsFragment, "FRAG" + 3);
        adapter.addFragment(treatmentTabFragment, "FRAG" + 4);
        adapter.addFragment(instructionFragment, "FRAG" + 5);
        adapter.addFragment(printFragment, "FRAG" + 6);
        menuAdapter = new MenuHeaderAdapter(data, this, this);
        rcv_header.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(data.size());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        adapter.notifyDataSetChanged();
        pb_case.setVisibility(View.GONE);
    }

    public FlexboxLayoutManager getFlexBoxLayoutManager(Context context) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    private void initStores() {
        for (int i = 0; i < LaunchActivity.merchantListFragment.getTopics().size(); i++) {
            preferenceObjects.getPreferredStoreInfoHashMap().put(LaunchActivity.merchantListFragment.getTopics().get(i).getCodeQR(), new PreferredStoreInfo());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
