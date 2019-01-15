package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.PreferredBusinessPresenter;
import com.noqapp.android.merchant.model.PreferredBusinessModel;
import com.noqapp.android.merchant.presenter.beans.JsonPreferredBusinessList;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.DiagnosisFragment;
import com.noqapp.android.merchant.views.fragments.InstructionFragment;
import com.noqapp.android.merchant.views.fragments.LabTestsFragment;
import com.noqapp.android.merchant.views.fragments.PrimaryCheckupFragment;
import com.noqapp.android.merchant.views.fragments.PrintFragment;
import com.noqapp.android.merchant.views.fragments.SymptomsFragment;
import com.noqapp.android.merchant.views.fragments.TreatmentFragment;
import com.noqapp.android.merchant.views.pojos.CaseHistory;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.pojos.FormDataObj;
import com.noqapp.android.merchant.views.pojos.TestCaseObjects;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import com.google.gson.Gson;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalCaseActivity extends AppCompatActivity implements MenuHeaderAdapter.OnItemClickListener, PreferredBusinessPresenter {

    private ViewPager viewPager;
    private long lastPress;
    private Toast backPressToast;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ArrayList<String> data = new ArrayList<>();
    private PrimaryCheckupFragment primaryCheckupFragment;
    public SymptomsFragment symptomsFragment;
    private LabTestsFragment labTestsFragment;
    private TreatmentFragment treatmentFragment;
    private DiagnosisFragment diagnosisFragment;
    private InstructionFragment instructionFragment;
    private PrintFragment printFragment;
    public boolean isGynae = false;

    public boolean isGynae() {
        return isGynae;
    }

    public TestCaseObjects getTestCaseObjects() {
        return testCaseObjects;
    }

    private TestCaseObjects testCaseObjects;
    private LoadTabs loadTabs;

    public JsonMedicalRecord getJsonMedicalRecord() {
        return jsonMedicalRecord;
    }

    private JsonMedicalRecord jsonMedicalRecord;

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
    public JsonPreferredBusinessList jsonPreferredBusinessList;


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
            testCaseObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), TestCaseObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            testCaseObjects = new TestCaseObjects();
        }
        if (null == testCaseObjects)
            testCaseObjects = new TestCaseObjects();
        caseHistory = new CaseHistory();
        viewPager = findViewById(R.id.pager);
        rcv_header = findViewById(R.id.rcv_header);
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
        String bizCategoryId = getIntent().getStringExtra("bizCategoryId");
        if (!TextUtils.isEmpty(bizCategoryId))
            isGynae = MedicalDepartmentEnum.valueOf(bizCategoryId) == MedicalDepartmentEnum.OGY;
        JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
        caseHistory.setName(jsonProfile.getName());
        caseHistory.setAddress(jsonProfile.getAddress());
        caseHistory.setDetails("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg");
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        loadTabs = new LoadTabs();
        loadTabs.execute();


        if (LaunchActivity.getLaunchActivity().isOnline()) {
            PreferredBusinessModel preferredBusinessModel = new PreferredBusinessModel(this);
            preferredBusinessModel.getAllPreferredStores(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        }

    }

    @Override
    public void preferredBusinessResponse(JsonPreferredBusinessList jsonPreferredBusinessList) {
        this.jsonPreferredBusinessList = jsonPreferredBusinessList;
        Log.e("Pref business list: ", jsonPreferredBusinessList.toString());
    }

    @Override
    public void preferredBusinessError() {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        updateSuggestions();
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

    public void updateSuggestions() {
        Map<String, List<DataObj>> mapList = new HashMap<>();
        mapList.put(HealthCareServiceEnum.MRI.getName(), testCaseObjects.clearListSelection(testCaseObjects.getMriList()));
        mapList.put(HealthCareServiceEnum.SCAN.getName(), testCaseObjects.clearListSelection(testCaseObjects.getScanList()));
        mapList.put(HealthCareServiceEnum.SONO.getName(), testCaseObjects.clearListSelection(testCaseObjects.getSonoList()));
        mapList.put(HealthCareServiceEnum.XRAY.getName(), testCaseObjects.clearListSelection(testCaseObjects.getXrayList()));
        mapList.put(HealthCareServiceEnum.PATH.getName(), testCaseObjects.clearListSelection(testCaseObjects.getPathologyList()));
        mapList.put(Constants.MEDICINE, testCaseObjects.clearListSelection(testCaseObjects.getMedicineList()));
        mapList.put(Constants.SYMPTOMS, testCaseObjects.clearListSelection(testCaseObjects.getSymptomsList()));
        mapList.put(Constants.PROVISIONAL_DIAGNOSIS, testCaseObjects.clearListSelection(testCaseObjects.getProDiagnosisList()));
        mapList.put(Constants.DIAGNOSIS, testCaseObjects.clearListSelection(testCaseObjects.getDiagnosisList()));
        mapList.put(Constants.INSTRUCTION, testCaseObjects.clearListSelection(testCaseObjects.getInstructionList()));
        LaunchActivity.getLaunchActivity().setSuggestionsPrefs(mapList);
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
        labTestsFragment.saveData();
        treatmentFragment.saveData();
        instructionFragment.saveData();
        diagnosisFragment.saveData();
        printFragment.updateUI();
    }

    private void initLists() {

        formDataObj.getMriList().clear();
        formDataObj.getScanList().clear();
        formDataObj.getSonoList().clear();
        formDataObj.getXrayList().clear();
        formDataObj.getPathologyList().clear();


        // Add selected list
        formDataObj.getMriList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getMriList()));
        formDataObj.getScanList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getScanList()));
        formDataObj.getSonoList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getSonoList()));
        formDataObj.getXrayList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getXrayList()));
        //
        formDataObj.getPathologyList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getPathologyList()));
        //

        if (isGynae) {
            formDataObj.getSymptomsList().clear();
            formDataObj.getSymptomsList().addAll(MedicalDataStatic.Gynae.getSymptoms());
            formDataObj.getSymptomsList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getSymptomsList()));


            formDataObj.getObstreticsList().clear();
            formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());


            formDataObj.getDiagnosisList().clear();
            formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Gynae.getDiagnosis());
            formDataObj.getDiagnosisList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getDiagnosisList()));


            formDataObj.getProvisionalDiagnosisList().clear();
            formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Gynae.getProvisionalDiagnosis());
            formDataObj.getProvisionalDiagnosisList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getProDiagnosisList()));
        } else {
            formDataObj.getSymptomsList().clear();
            formDataObj.getSymptomsList().addAll(MedicalDataStatic.Pediatrician.getSymptoms());
            formDataObj.getSymptomsList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getSymptomsList()));

            formDataObj.getObstreticsList().clear();
            // formDataObj.getObstreticsList().addAll(MedicalDataStatic.Gynae.getObstretics());

            formDataObj.getDiagnosisList().clear();
            formDataObj.getDiagnosisList().addAll(MedicalDataStatic.Pediatrician.getDiagnosis());
            formDataObj.getDiagnosisList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getDiagnosisList()));

            formDataObj.getProvisionalDiagnosisList().clear();
            formDataObj.getProvisionalDiagnosisList().addAll(MedicalDataStatic.Pediatrician.getProvisionalDiagnosis());
            formDataObj.getProvisionalDiagnosisList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getProDiagnosisList()));
        }


        formDataObj.getMedicineList().clear();
        // Add selected list
        formDataObj.getMedicineList().addAll(testCaseObjects.clearListSelection(testCaseObjects.getMedicineList()));
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
        formDataObj.getInstructionList().addAll(convertToStringList(testCaseObjects.getInstructionList()));
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
        diagnosisFragment = new DiagnosisFragment();
        labTestsFragment = new LabTestsFragment();
        treatmentFragment = new TreatmentFragment();
        instructionFragment = new InstructionFragment();
        printFragment = new PrintFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(primaryCheckupFragment, "FRAG" + 0);
        adapter.addFragment(symptomsFragment, "FRAG" + 1);
        adapter.addFragment(diagnosisFragment, "FRAG" + 2);
        adapter.addFragment(labTestsFragment, "FRAG" + 3);
        adapter.addFragment(treatmentFragment, "FRAG" + 4);
        adapter.addFragment(instructionFragment, "FRAG" + 5);
        adapter.addFragment(printFragment, "FRAG" + 6);
        menuAdapter = new MenuHeaderAdapter(data, this, this);
        rcv_header.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(data.size());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<String> convertToStringList(List<DataObj> temp) {
        ArrayList<String> strList = new ArrayList<>();
        if (null != temp && temp.size() > 0)
            for (int i = 0; i < temp.size(); i++) {
                strList.add(temp.get(i).getShortName());
            }
        return strList;
    }

}
