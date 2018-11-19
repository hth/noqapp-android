package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalCasePojo;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.PrimaryCheckupFragment;
import com.noqapp.android.merchant.views.fragments.PrintFragment;
import com.noqapp.android.merchant.views.fragments.RecomondTestFragment;
import com.noqapp.android.merchant.views.fragments.SymptomsFragment;
import com.noqapp.android.merchant.views.fragments.TreatmentFragment;
import com.noqapp.android.merchant.views.pojos.DataObj;

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
    private SymptomsFragment symptomsFragment;
    private RecomondTestFragment recomondTestFragment;
    private TreatmentFragment treatmentFragment;
    private PrintFragment printFragment;

    public MedicalCasePojo getMedicalCasePojo() {
        return medicalCasePojo;
    }

    private MedicalCasePojo medicalCasePojo;

    public static MedicalCaseActivity getMedicalCaseActivity() {
        return medicalCaseActivity;
    }

    private static MedicalCaseActivity medicalCaseActivity;

    public ArrayList<DataObj> getRadiologyList() {
        return radiologyList;
    }

    public ArrayList<DataObj> getPathologyList() {
        return pathologyList;
    }

    public ArrayList<DataObj> getSymptomsList() {
        return symptomsList;
    }

    public ArrayList<DataObj> getDiagnosisList() {
        return diagnosisList;
    }

    public ArrayList<DataObj> getMedicineList() {
        return medicineList;
    }

    public ArrayList<String> getInstructionList() {
        return instructionList;
    }

    public void setRadiologyList(ArrayList<DataObj> radiologyList) {
        this.radiologyList = radiologyList;
    }

    public void setPathologyList(ArrayList<DataObj> pathologyList) {
        this.pathologyList = pathologyList;
    }

    public void setSymptomsList(ArrayList<DataObj> symptomsList) {
        this.symptomsList = symptomsList;
    }

    public void setDiagnosisList(ArrayList<DataObj> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public void setMedicineList(ArrayList<DataObj> medicineList) {
        this.medicineList = medicineList;
    }

    public void setInstructionList(ArrayList<String> instructionList) {
        this.instructionList = instructionList;
    }

    public ArrayList<DataObj> radiologyList = new ArrayList<>();
    public ArrayList<DataObj> pathologyList = new ArrayList<>();
    private ArrayList<DataObj> symptomsList = new ArrayList<>();
    private ArrayList<DataObj> diagnosisList = new ArrayList<>();
    private ArrayList<DataObj> medicineList = new ArrayList<>(); 
    private ArrayList<String> instructionList = new ArrayList<>();
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
        initLists();
        medicalCasePojo = new MedicalCasePojo();
        viewPager =  findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(5);
        rcv_header =  findViewById(R.id.rcv_header);
        data.add("Primary checkup");
        data.add("Symptoms");
        data.add("Tests");
        data.add("Treatment");
        data.add("Print");
        primaryCheckupFragment = new PrimaryCheckupFragment();
        Bundle bppf = new Bundle();
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        codeQR = getIntent().getStringExtra("qCodeQR");
        bppf.putString("qUserId", jsonQueuedPerson.getQueueUserId());
        bppf.putString("qCodeQR", codeQR);
        primaryCheckupFragment.setArguments(bppf);
        symptomsFragment = new SymptomsFragment();
        recomondTestFragment = new RecomondTestFragment();
        treatmentFragment = new TreatmentFragment();
        printFragment= new PrintFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(primaryCheckupFragment, "FRAG" + 0);
        adapter.addFragment(symptomsFragment, "FRAG" + 1);
        adapter.addFragment(recomondTestFragment, "FRAG" + 2);
        adapter.addFragment(treatmentFragment, "FRAG" + 3);
        adapter.addFragment(printFragment, "FRAG" + 4);

        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());


        menuAdapter = new MenuHeaderAdapter(data, this, this);
        rcv_header.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
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
        radiologyList.clear();
        radiologyList.add(new DataObj("VSC Pelvis (TVS)", false));
        radiologyList.add(new DataObj("4D Anamoly", false));
        radiologyList.add(new DataObj("VSC (obst) NT", false));
        radiologyList.add(new DataObj("VSC (obst) c", false));
        radiologyList.add(new DataObj("Color Doppler", false));


        pathologyList.clear();
        pathologyList.add(new DataObj("ANC profile", false));
        pathologyList.add(new DataObj("HCV", false));
        pathologyList.add(new DataObj("T3T4T5H", false));
        pathologyList.add(new DataObj("HbA1C", false));
        pathologyList.add(new DataObj("Hb Electrophoresis", false));
        pathologyList.add(new DataObj("BS 2hrs after 75gm of glucos", false));

        symptomsList.clear();
        symptomsList.add(new DataObj("Fever", false));
        symptomsList.add(new DataObj("Cold", false));
        symptomsList.add(new DataObj("Vomiting", false));
        symptomsList.add(new DataObj("No Fever", false));
        symptomsList.add(new DataObj("Cough", false));
        symptomsList.add(new DataObj("Throat pain", false));
        symptomsList.add(new DataObj("4D Anamoly", false));
        symptomsList.add(new DataObj("Skin rash", false));
        symptomsList.add(new DataObj("Constipation", false));
        symptomsList.add(new DataObj("Color Doppler", false));
        symptomsList.add(new DataObj("Nausea", false));
        symptomsList.add(new DataObj("4D Anamoly", false));
        symptomsList.add(new DataObj("Throat Congested", false));
        symptomsList.add(new DataObj("Chest crepts", false));
        symptomsList.add(new DataObj("Eye discharge", false));
        symptomsList.add(new DataObj("Loose stools", false));
        symptomsList.add(new DataObj("Abdomainal pain", false));


        diagnosisList.clear();
        diagnosisList.add(new DataObj("Hand foot and mouth", false));
        diagnosisList.add(new DataObj("Acute Gastritis", false));
        diagnosisList.add(new DataObj("For Vaccination", false));
        diagnosisList.add(new DataObj("Prugo", false));
        diagnosisList.add(new DataObj("Follow up", false));
        diagnosisList.add(new DataObj("Viral Fever", false));
        diagnosisList.add(new DataObj("Bullous impetigo", false));
        diagnosisList.add(new DataObj("Lower respiratory tract infection", false));

        medicineList.clear();
        medicineList.add(new DataObj("Tab  Crocin (500 mg)", PharmacyCategoryEnum.TA.getDescription(),false));
        medicineList.add(new DataObj("Syr  Ondem", PharmacyCategoryEnum.SY.getDescription(),false));
        medicineList.add(new DataObj("Syr  Adrenaline", PharmacyCategoryEnum.SY.getDescription(),false));
        medicineList.add(new DataObj("Tab  Albendazole", PharmacyCategoryEnum.TA.getDescription(),false));
        medicineList.add(new DataObj("Cap  B-complex", PharmacyCategoryEnum.CA.getDescription(),false));
        medicineList.add(new DataObj("Tab  Balofloxacin", PharmacyCategoryEnum.TA.getDescription(),false));
        medicineList.add(new DataObj("Tab  Calcium", PharmacyCategoryEnum.TA.getDescription(),false));
        medicineList.add(new DataObj("Cap  Carbimazole", PharmacyCategoryEnum.CA.getDescription(),false));
        medicineList.add(new DataObj("Oil  Castor Oil", PharmacyCategoryEnum.LO.getDescription(),false));
        medicineList.add(new DataObj("Tab  Erdosteine", PharmacyCategoryEnum.TA.getDescription(),false));

        medicineList.add(new DataObj("Cap  Folic Acid", PharmacyCategoryEnum.CA.getDescription(),false));
        medicineList.add(new DataObj("Tab  Heparin", PharmacyCategoryEnum.TA.getDescription(),false));
        medicineList.add(new DataObj("High Protein Supplement", PharmacyCategoryEnum.PW.getDescription(),false));
        medicineList.add(new DataObj("Cap  Fluoride", PharmacyCategoryEnum.CA.getDescription(),false));

        instructionList.clear();
        instructionList.add("Steam Inhalation");
        instructionList.add("Plenty of fluids");
        instructionList.add("Nebulization");
        instructionList.add("Plenty of Green vegetables & fruits");
        instructionList.add("To avoid strong smells of perfumes, deodrent, agarbatti dhoop");
        instructionList.add("No oil & no spicy food ");
        instructionList.add("Drink more water");
        instructionList.add("Increase content of salad in your diet");
        instructionList.add("Consume less sugar");
        instructionList.add("30 min Brisk walking a day");
        instructionList.add("Drink milk every day");
    }

    private void saveAllData(){
        primaryCheckupFragment.saveData();
        symptomsFragment.saveData();
        recomondTestFragment.saveData();
        treatmentFragment.saveData();
        printFragment.updateUI();
    }
}
