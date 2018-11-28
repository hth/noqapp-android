package com.noqapp.android.merchant.views.activities;

import com.google.gson.Gson;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalCasePojo;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.DiagnosisFragment;
import com.noqapp.android.merchant.views.fragments.ExaminationFragment;
import com.noqapp.android.merchant.views.fragments.InstructionFragment;
import com.noqapp.android.merchant.views.fragments.PrimaryCheckupFragment;
import com.noqapp.android.merchant.views.fragments.PrintFragment;
import com.noqapp.android.merchant.views.fragments.LabTestsFragment;
import com.noqapp.android.merchant.views.fragments.SymptomsFragment;
import com.noqapp.android.merchant.views.fragments.TreatmentFragment;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.pojos.FormDataObj;
import com.noqapp.android.merchant.views.utils.TestCaseObjects;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import java.util.ArrayList;

public class MedicalCaseActivity extends AppCompatActivity implements MenuHeaderAdapter.OnItemClickListener {

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
    private ExaminationFragment examinationFragment;
    private DiagnosisFragment diagnosisFragment;
    private InstructionFragment instructionFragment;
    private PrintFragment printFragment;
    private TestCaseObjects testCaseObjects;

    public MedicalCasePojo getMedicalCasePojo() {
        return medicalCasePojo;
    }

    private MedicalCasePojo medicalCasePojo;

    public static MedicalCaseActivity getMedicalCaseActivity() {
        return medicalCaseActivity;
    }

    private static MedicalCaseActivity medicalCaseActivity;
    public FormDataObj formDataObj;


    public JsonQueuedPerson jsonQueuedPerson;
    public String codeQR;

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
        if( null == testCaseObjects)
            testCaseObjects = new TestCaseObjects();
        initLists();
        medicalCasePojo = new MedicalCasePojo();
        viewPager =  findViewById(R.id.pager);

        rcv_header =  findViewById(R.id.rcv_header);
        data.add("Primary checkup");
        data.add("Symptoms");
        data.add("Examination");
        data.add("Investigation");
        data.add("Tests");
        data.add("Treatment");
        data.add("Instructions");
        data.add("Submit");
        primaryCheckupFragment = new PrimaryCheckupFragment();
        Bundle bppf = new Bundle();
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        codeQR = getIntent().getStringExtra("qCodeQR");
        bppf.putString("qUserId", jsonQueuedPerson.getQueueUserId());
        bppf.putString("qCodeQR", codeQR);
        primaryCheckupFragment.setArguments(bppf);
        symptomsFragment = new SymptomsFragment();
        examinationFragment = new ExaminationFragment();
        diagnosisFragment = new DiagnosisFragment();
        labTestsFragment = new LabTestsFragment();
        treatmentFragment = new TreatmentFragment();
        instructionFragment = new InstructionFragment();
        printFragment= new PrintFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(primaryCheckupFragment, "FRAG" + 0);
        adapter.addFragment(symptomsFragment, "FRAG" + 1);
        adapter.addFragment(examinationFragment, "FRAG" + 2);
        adapter.addFragment(diagnosisFragment, "FRAG" + 3);
        adapter.addFragment(labTestsFragment, "FRAG" + 4);
        adapter.addFragment(treatmentFragment, "FRAG" + 5);
        adapter.addFragment(instructionFragment, "FRAG" + 6);
        adapter.addFragment(printFragment, "FRAG" + 7);

        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());


        menuAdapter = new MenuHeaderAdapter(data, this, this);
        rcv_header.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(data.size());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        adapter.notifyDataSetChanged();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
       // saveAllData();
    }


    private void initLists(){
        
        formDataObj.getRadiologyList().clear();
        formDataObj.getRadiologyList().add(new DataObj("VSC Pelvis (TVS)", false));
        formDataObj.getRadiologyList().add(new DataObj("4D Anamoly", false));
        formDataObj.getRadiologyList().add(new DataObj("VSC (obst) NT", false));
        formDataObj.getRadiologyList().add(new DataObj("VSC (obst) c", false));
        formDataObj.getRadiologyList().add(new DataObj("Color Doppler", false));
        // Add selected list
        formDataObj.getRadiologyList().addAll(testCaseObjects.getMriList());
        formDataObj.getRadiologyList().addAll(testCaseObjects.getScanList());
        formDataObj.getRadiologyList().addAll(testCaseObjects.getSonoList());
        formDataObj.getRadiologyList().addAll(testCaseObjects.getXrayList());
        //

        formDataObj.getPathologyList().clear();
        formDataObj.getPathologyList().add(new DataObj("ANC profile", false));
        formDataObj.getPathologyList().add(new DataObj("HCV", false));
        formDataObj.getPathologyList().add(new DataObj("T3T4T5H", false));
        formDataObj.getPathologyList().add(new DataObj("HbA1C", false));
        formDataObj.getPathologyList().add(new DataObj("Hb Electrophoresis", false));
        formDataObj.getPathologyList().add(new DataObj("BS 2hrs after 75gm of glucos", false));
        // Add selected list
        formDataObj.getPathologyList().addAll(testCaseObjects.getPathologyList());
        //
      
        formDataObj.getSymptomsList().clear();
        formDataObj.getSymptomsList().add(new DataObj("Fever", false));
        formDataObj.getSymptomsList().add(new DataObj("Cold", false));
        formDataObj.getSymptomsList().add(new DataObj("Vomiting", false));
        formDataObj.getSymptomsList().add(new DataObj("No Fever", false));
        formDataObj.getSymptomsList().add(new DataObj("Cough", false));
        formDataObj.getSymptomsList().add(new DataObj("Throat pain", false));
        formDataObj.getSymptomsList().add(new DataObj("4D Anamoly", false));
        formDataObj.getSymptomsList().add(new DataObj("Skin rash", false));
        formDataObj.getSymptomsList().add(new DataObj("Constipation", false));
        formDataObj.getSymptomsList().add(new DataObj("Color Doppler", false));
        formDataObj.getSymptomsList().add(new DataObj("Nausea", false));
        formDataObj.getSymptomsList().add(new DataObj("4D Anamoly", false));
        formDataObj.getSymptomsList().add(new DataObj("Throat Congested", false));
        formDataObj.getSymptomsList().add(new DataObj("Chest crepts", false));
        formDataObj.getSymptomsList().add(new DataObj("Eye discharge", false));
        formDataObj.getSymptomsList().add(new DataObj("Loose stools", false));
        formDataObj.getSymptomsList().add(new DataObj("Abdomainal pain", false));

        
        formDataObj.getDiagnosisList().clear();
        formDataObj.getDiagnosisList().add(new DataObj("Hand foot and mouth", false));
        formDataObj.getDiagnosisList().add(new DataObj("Acute Gastritis", false));
        formDataObj.getDiagnosisList().add(new DataObj("For Vaccination", false));
        formDataObj.getDiagnosisList().add(new DataObj("Prugo", false));
        formDataObj.getDiagnosisList().add(new DataObj("Follow up", false));
        formDataObj.getDiagnosisList().add(new DataObj("Viral Fever", false));
        formDataObj.getDiagnosisList().add(new DataObj("Bullous impetigo", false));
        formDataObj.getDiagnosisList().add(new DataObj("Lower respiratory tract infection", false));

        formDataObj.getProvisionalDiagnosisList().clear();
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Hand foot and mouth", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Acute Gastritis", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("For Vaccination", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Prugo", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Follow up", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Viral Fever", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Bullous impetigo", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Lower respiratory tract infection", false));
        
        formDataObj.getMedicineList().clear();
        formDataObj.getMedicineList().add(new DataObj("Tab  Crocin (500 mg)", PharmacyCategoryEnum.TA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Syr  Ondem", PharmacyCategoryEnum.SY.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Syr  Adrenaline", PharmacyCategoryEnum.SY.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Albendazole", PharmacyCategoryEnum.TA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Cap  B-complex", PharmacyCategoryEnum.CA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Balofloxacin", PharmacyCategoryEnum.TA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Calcium", PharmacyCategoryEnum.TA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Cap  Carbimazole", PharmacyCategoryEnum.CA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Oil  Castor Oil", PharmacyCategoryEnum.LO.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Erdosteine", PharmacyCategoryEnum.TA.getDescription(),false));

        formDataObj.getMedicineList().add(new DataObj("Cap  Folic Acid", PharmacyCategoryEnum.CA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Heparin", PharmacyCategoryEnum.TA.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("High Protein Supplement", PharmacyCategoryEnum.PW.getDescription(),false));
        formDataObj.getMedicineList().add(new DataObj("Cap  Fluoride", PharmacyCategoryEnum.CA.getDescription(),false));

        // Add selected list
        formDataObj.getMedicineList().addAll(testCaseObjects.getMedicineList());
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
    }

    private void saveAllData(){
        primaryCheckupFragment.saveData();
        symptomsFragment.saveData();
        labTestsFragment.saveData();
        treatmentFragment.saveData();
        instructionFragment.saveData();
        examinationFragment.saveData();
        diagnosisFragment.saveData();
        printFragment.updateUI();
    }
}
