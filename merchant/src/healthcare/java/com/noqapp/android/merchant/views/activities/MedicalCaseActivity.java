package com.noqapp.android.merchant.views.activities;

import com.google.gson.Gson;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.medical.PharmacyCategoryEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.MedicalCasePojo;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.DiagnosisFragment;
import com.noqapp.android.merchant.views.fragments.InstructionFragment;
import com.noqapp.android.merchant.views.fragments.PrimaryCheckupFragment;
import com.noqapp.android.merchant.views.fragments.PrintFragment;
import com.noqapp.android.merchant.views.fragments.LabTestsFragment;
import com.noqapp.android.merchant.views.fragments.SymptomsFragment;
import com.noqapp.android.merchant.views.fragments.TreatmentFragment;
import com.noqapp.android.merchant.views.pojos.DataObj;
import com.noqapp.android.merchant.views.pojos.FormDataObj;
import com.noqapp.android.merchant.views.pojos.TestCaseObjects;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
    private DiagnosisFragment diagnosisFragment;
    private InstructionFragment instructionFragment;
    private PrintFragment printFragment;
    private TestCaseObjects testCaseObjects;
    private LoadTabs loadTabs;
    private JsonMedicalRecord jsonMedicalRecord;
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
        if (null == testCaseObjects)
            testCaseObjects = new TestCaseObjects();
        medicalCasePojo = new MedicalCasePojo();
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
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("medicalPhysical");
        codeQR = getIntent().getStringExtra("qCodeQR");
        JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
        medicalCasePojo.setName(jsonProfile.getName());
        medicalCasePojo.setAddress(jsonProfile.getAddress());
        medicalCasePojo.setDetails("<b> Blood Group: </b> B+ ,<b> Weight: </b> 75 Kg");
        medicalCasePojo.setAge(new AppUtils().calculateAge(jsonProfile.getBirthday()));
        medicalCasePojo.setGender(jsonProfile.getGender().name());
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


        formDataObj.getPathologyList().add(new DataObj("CBC", "CBC", "", false));
        formDataObj.getPathologyList().add(new DataObj("RFT", "RFT", "", false));
        formDataObj.getPathologyList().add(new DataObj("LFT", "LFT", "", false));
        formDataObj.getPathologyList().add(new DataObj(" 3H", " 3H", "", false));
        formDataObj.getPathologyList().add(new DataObj("BLOOD GROUP", "BLOOD GROUP", "", false));
        formDataObj.getSonoList().add(new DataObj("USG ABDOMEN ( OBSTRETICS )", "USG ABDOMEN ( OBSTRETICS )", "", false));
        formDataObj.getSonoList().add(new DataObj("TVS", "TVS", "", false));
        formDataObj.getPathologyList().add(new DataObj("TSH PROFILE", "TSH PROFILE", "", false));
        formDataObj.getPathologyList().add(new DataObj("BSL F & PP", "BSL F & PP", "", false));
        formDataObj.getPathologyList().add(new DataObj("HBA1C", "HBA1C", "", false));
        formDataObj.getPathologyList().add(new DataObj("FSH", "FSH", "", false));
        formDataObj.getPathologyList().add(new DataObj("LH", "LH", "", false));
        formDataObj.getPathologyList().add(new DataObj("PROLACTINE", "PROLACTINE", "", false));
        formDataObj.getPathologyList().add(new DataObj("E2", "E2", "", false));
        formDataObj.getPathologyList().add(new DataObj("ANC profile", "ANC profile", "", false));
        formDataObj.getSonoList().add(new DataObj("DOUBLE MARKER", "DOUBLE MARKER", "", false));
        formDataObj.getSonoList().add(new DataObj("TRIPPLE MARKER", "TRIPPLE MARKER", "", false));
        formDataObj.getSonoList().add(new DataObj("ANAMOLY SCAN", "ANAMOLY SCAN", "", false));


//        formDataObj.getMriList().add(new DataObj("VSC Pelvis (TVS)", false));
//        formDataObj.getMriList().add(new DataObj("4D Anamoly", false));
//        formDataObj.getMriList().add(new DataObj("VSC (obst) NT", false));
//        formDataObj.getMriList().add(new DataObj("VSC (obst) c", false));
//        formDataObj.getMriList().add(new DataObj("Color Doppler", false));
        // Add selected list
        formDataObj.getMriList().addAll(testCaseObjects.getMriList());
        formDataObj.getScanList().addAll(testCaseObjects.getScanList());
        formDataObj.getSonoList().addAll(testCaseObjects.getSonoList());
        formDataObj.getXrayList().addAll(testCaseObjects.getXrayList());
        //


//        formDataObj.getPathologyList().add(new DataObj("ANC profile", false));
//        formDataObj.getPathologyList().add(new DataObj("HCV", false));
//        formDataObj.getPathologyList().add(new DataObj("T3T4T5H", false));
//        formDataObj.getPathologyList().add(new DataObj("HbA1C", false));
//        formDataObj.getPathologyList().add(new DataObj("Hb Electrophoresis", false));
//        formDataObj.getPathologyList().add(new DataObj("BS 2hrs after 75gm of glucos", false));
        // Add selected list
        formDataObj.getPathologyList().addAll(testCaseObjects.getPathologyList());
        //

        formDataObj.getSymptomsList().clear();
//        formDataObj.getSymptomsList().add(new DataObj("Fever", false));
//        formDataObj.getSymptomsList().add(new DataObj("Cold", false));
//        formDataObj.getSymptomsList().add(new DataObj("Vomiting", false));
//        formDataObj.getSymptomsList().add(new DataObj("No Fever", false));
//        formDataObj.getSymptomsList().add(new DataObj("Cough", false));
//        formDataObj.getSymptomsList().add(new DataObj("Throat pain", false));
//        formDataObj.getSymptomsList().add(new DataObj("4D Anamoly", false));
//        formDataObj.getSymptomsList().add(new DataObj("Skin rash", false));
//        formDataObj.getSymptomsList().add(new DataObj("Constipation", false));
//        formDataObj.getSymptomsList().add(new DataObj("Color Doppler", false));
//        formDataObj.getSymptomsList().add(new DataObj("Nausea", false));
//        formDataObj.getSymptomsList().add(new DataObj("4D Anamoly", false));
//        formDataObj.getSymptomsList().add(new DataObj("Throat Congested", false));
//        formDataObj.getSymptomsList().add(new DataObj("Chest crepts", false));
//        formDataObj.getSymptomsList().add(new DataObj("Eye discharge", false));
//        formDataObj.getSymptomsList().add(new DataObj("Loose stools", false));
//        formDataObj.getSymptomsList().add(new DataObj("Abdomainal pain", false));


        formDataObj.getSymptomsList().add(new DataObj("DISCHARGE FROM VAGINA", "PV DISCHARGE", "", false));
        formDataObj.getSymptomsList().add(new DataObj("ITCHING AT VAGINA", "ITCHING AT VAGINA", "", false));
        formDataObj.getSymptomsList().add(new DataObj("DYSPAREUNIA ( PAINFUL INTERCOURSE )", "DYSPAREUNIA ( PAINFUL INTERCOURSE )", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PAIN IN LOWER ABDOMEN", "PAIN IN LOWER ABDOMEN", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PER VAGINAL BLEEDING", "PV BLEED", "", false));
        formDataObj.getSymptomsList().add(new DataObj("MENORRHAGIA", "MENORRHAGIA", "", false));
        formDataObj.getSymptomsList().add(new DataObj("MENOMETRORRHAGIA", "MENOMETRORRHAGIA", "", false));
        formDataObj.getSymptomsList().add(new DataObj("POLYMENORRHOEA", "POLYMENORRHOEA", "", false));
        formDataObj.getSymptomsList().add(new DataObj("FEVER", "FEVER", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PAIN IN BREAST ( MASTALGIA )", "PAIN IN BREAST ( MASTALGIA )", "", false));
        formDataObj.getSymptomsList().add(new DataObj("AMENORRHOEA", "AMENORRHOEA", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PALLOR", "PALLOR", "", false));
        formDataObj.getSymptomsList().add(new DataObj("LOOSE MOTIONS", "LOOSE MOTIONS", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PAIN AT VAGINAL REGION", "PAIN AT VAGINAL REGION", "", false));
        formDataObj.getSymptomsList().add(new DataObj("WEIGHT GAIN", "WEIGHT GAIN", "", false));
        formDataObj.getSymptomsList().add(new DataObj("WHITE DISCHARGE PER VAGINAL ( LEUCORRHOEA )", "WHITE DISCHARGE PER VAGINAL ( LEUCORRHOEA )", "", false));
        formDataObj.getSymptomsList().add(new DataObj("BURNING MICTURATION", "BURNING MICTURATION", "", false));
        formDataObj.getSymptomsList().add(new DataObj("DELAYED & IRREGULAR CYCLES", "DELAYED & IRREGULAR CYCLES", "", false));
        formDataObj.getSymptomsList().add(new DataObj("PAIN AT ILIAC REGION", "PAIN AT ILIAC REGION", "", false));
        formDataObj.getSymptomsList().add(new DataObj("POST MENOPAUSAL SYMPTOMS", "POST MENOPAUSAL SYMPTOMS", "", false));
        formDataObj.getSymptomsList().add(new DataObj("HEAVINESS IN LOWER ABDOMEN", "HEAVINESS IN LOWER ABDOMEN", "", false));


        formDataObj.getObstreticsList().clear();
        formDataObj.getObstreticsList().add(new DataObj("PAIN IN ABDOMEN", "PAIN IN ABDOMEN", "", false));
        formDataObj.getObstreticsList().add(new DataObj("PER VAGINAL BLEEDING", "PV BLEED", "", false));
        formDataObj.getObstreticsList().add(new DataObj("PER VAGINAL LEAKING", "PV LEAK", "", false));
        formDataObj.getObstreticsList().add(new DataObj("PAIN AT ILIAC REGION", "PAIN AT ILIAC REGION", "", false));
        formDataObj.getObstreticsList().add(new DataObj("DIFFICULTY IN BREATHING", "DIFFICULTY IN BREATHING", "", false));
        formDataObj.getObstreticsList().add(new DataObj("SWELLING OVER FEETS", "SWELLING OVER FEETS", "", false));
        formDataObj.getObstreticsList().add(new DataObj("CONVULSION IN PREGANANCY", "CONVULSION IN PREGANANCY", "", false));
        formDataObj.getObstreticsList().add(new DataObj("CONSTIPATION", "CONSTIPATION", "", false));
        formDataObj.getObstreticsList().add(new DataObj("HYPERTENSION IN PREGNANCY", "HYPERTENSION IN PREGNANCY", "", false));
        formDataObj.getObstreticsList().add(new DataObj("GESTATIONAL DIABETES", "GESTATIONAL DIABETES", "", false));


        formDataObj.getDiagnosisList().clear();
        formDataObj.getDiagnosisList().add(new DataObj("MENORRHAGIA", "MENORRHAGIA", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("VAGINITIS", "VAGINITIS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("VULVOVAGINITIS", "VULVOVAGINITIS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("CERVICITIS", "CERVICITIS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("PELVIC INFLAMMATORY DISEASE", "PID", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("UTERINE FIBROID", "UTERINE FIBROID", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ENDOMETROSIS", "ENDOMETROSIS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("INFERTILITY : PRIMARY", "INFERTILITY : PRIMARY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("INFERTILITY – SECONDARY", "INFERTILITY – SECONDARY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("5 - 40 WEEKS PREGNANCY", "5 - 40 WEEKS PREGNANCY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("EARLY PREGANCY WITH PV BLEEDING", "EARLY PREGNANCY WITH PV BLEEDING", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("VESICULAR MOLE", "VESICULAR MOLE", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("BARTHOLIN CYST", "BARTHOLIN CYST", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("BARTHOLIN ABSCESS", "BARTHOLIN ABSCESS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("PREMATURE DELIVERY", "PREMATURE DELIVERY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("MISSED ABORTION", "MISSED ABORTION", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("THREATENED ABORTION", "THREATENED ABORTION", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("COMPLETE ABORTION", "COMPLETE ABORTION", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("PREMATURE CONTRACTION", "PREMATURE CONTRACTION", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("INTRAUTERINE GROWTH RETARDATION WITH PREGANANCY", "IUGR WITH PREG", "", false));
        formDataObj.getDiagnosisList().add(new DataObj(" PLACENTA PREVIA WITH PREGNANCY", "PL. PREVIA WITH PREG", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("FULL TERM PREGNANCY WITH LABOUR PAIN", "FTP WITH LP", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("FULL TERM PREGNANCY WITH LEAKING", "FTP WITH LEAKING", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("POST PARTEM HAEMORRHAGE", "PPH", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ANTE PARTEM HAEMORRHAGE", "APH", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("HYPEREMESIS GRAVIDANUM", "HYPEREMESIS GRAVIDANUM", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ECTOPIC PREGNANCY", "ECTOPIC PREGNANCY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("CHOCOLATE CYST IN OVERY", "CHOCOLATE CYST IN OVERY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("POLYCYSTIC OVARIAN DISEASE", "PCOD", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("UTERINE POLYP", "UTERINE POLYP", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("VAGINAL POLYP", "VAGINAL POLYP", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ABRUPTIO PLACENTA", "ABRUPTIO PLACENTA", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("UTERINE CARCINOMA", "CA UTERUS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("FIBROADENOMA", "FIBROADENOMA", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("HYDROMNIOS", "HYDROMNIOS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("BREAST CANCER", "CA BREAST", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("MASTITIS", "MASTITIS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ANAEMIA IN PREGNANCY", "ANAEMIA IN PREGNANCY", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("ECLAMPSIA", "ECLAMPSIA", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("PRURITUS VULVAE", "PRURITUS VULVAE", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("CERVICAL CARCINOMA", "CA CERVIX", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("PREGNANCY INDUCED HYPERTENSION", "PIH", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("OLIGOHYDRAMNIOS", "OLIGOHYDRAMNIOS", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 1ST TRIMESTER", "MTP 1", "", false));
        formDataObj.getDiagnosisList().add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 2ND TRIMESTER", "MTP 2", "", false));


//
//        formDataObj.getDiagnosisList().add(new DataObj("Hand foot and mouth", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Acute Gastritis", false));
//        formDataObj.getDiagnosisList().add(new DataObj("For Vaccination", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Prugo", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Follow up", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Viral Fever", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Bullous impetigo", false));
//        formDataObj.getDiagnosisList().add(new DataObj("Lower respiratory tract infection", false));

        formDataObj.getProvisionalDiagnosisList().clear();

        formDataObj.getProvisionalDiagnosisList().add(new DataObj("MENORRHAGIA", "MENORRHAGIA", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("VAGINITIS", "VAGINITIS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("VULVOVAGINITIS", "VULVOVAGINITIS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("CERVICITIS", "CERVICITIS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("PELVIC INFLAMMATORY DISEASE", "PID", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("UTERINE FIBROID", "UTERINE FIBROID", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ENDOMETROSIS", "ENDOMETROSIS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("INFERTILITY : PRIMARY", "INFERTILITY : PRIMARY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("INFERTILITY – SECONDARY", "INFERTILITY – SECONDARY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("5 - 40 WEEKS PREGNANCY", "5 - 40 WEEKS PREGNANCY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("EARLY PREGANCY WITH PV BLEEDING", "EARLY PREGNANCY WITH PV BLEEDING", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("VESICULAR MOLE", "VESICULAR MOLE", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("BARTHOLIN CYST", "BARTHOLIN CYST", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("BARTHOLIN ABSCESS", "BARTHOLIN ABSCESS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("PREMATURE DELIVERY", "PREMATURE DELIVERY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("MISSED ABORTION", "MISSED ABORTION", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("THREATENED ABORTION", "THREATENED ABORTION", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("COMPLETE ABORTION", "COMPLETE ABORTION", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("PREMATURE CONTRACTION", "PREMATURE CONTRACTION", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("INTRAUTERINE GROWTH RETARDATION WITH PREGANANCY", "IUGR WITH PREG", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj(" PLACENTA PREVIA WITH PREGNANCY", "PL. PREVIA WITH PREG", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("FULL TERM PREGNANCY WITH LABOUR PAIN", "FTP WITH LP", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("FULL TERM PREGNANCY WITH LEAKING", "FTP WITH LEAKING", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("POST PARTEM HAEMORRHAGE", "PPH", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ANTE PARTEM HAEMORRHAGE", "APH", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("HYPEREMESIS GRAVIDANUM", "HYPEREMESIS GRAVIDANUM", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ECTOPIC PREGNANCY", "ECTOPIC PREGNANCY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("CHOCOLATE CYST IN OVERY", "CHOCOLATE CYST IN OVERY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("POLYCYSTIC OVARIAN DISEASE", "PCOD", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("UTERINE POLYP", "UTERINE POLYP", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("VAGINAL POLYP", "VAGINAL POLYP", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ABRUPTIO PLACENTA", "ABRUPTIO PLACENTA", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("UTERINE CARCINOMA", "CA UTERUS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("FIBROADENOMA", "FIBROADENOMA", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("HYDROMNIOS", "HYDROMNIOS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("BREAST CANCER", "CA BREAST", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("MASTITIS", "MASTITIS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ANAEMIA IN PREGNANCY", "ANAEMIA IN PREGNANCY", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("ECLAMPSIA", "ECLAMPSIA", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("PRURITUS VULVAE", "PRURITUS VULVAE", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("CERVICAL CARCINOMA", "CA CERVIX", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("PREGNANCY INDUCED HYPERTENSION", "PIH", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("OLIGOHYDRAMNIOS", "OLIGOHYDRAMNIOS", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 1ST TRIMESTER", "MTP 1", "", false));
        formDataObj.getProvisionalDiagnosisList().add(new DataObj("MEDICAL TERMINATION OF PREGNANCY: 2ND TRIMESTER", "MTP 2", "", false));


//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Hand foot and mouth", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Acute Gastritis", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("For Vaccination", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Prugo", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Follow up", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Viral Fever", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Bullous impetigo", false));
//        formDataObj.getProvisionalDiagnosisList().add(new DataObj("Pro Lower respiratory tract infection", false));

        formDataObj.getMedicineList().clear();
        formDataObj.getMedicineList().add(new DataObj("Tab  Crocin (500 mg)", PharmacyCategoryEnum.TA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Syr  Ondem", PharmacyCategoryEnum.SY.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Syr  Adrenaline", PharmacyCategoryEnum.SY.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Albendazole", PharmacyCategoryEnum.TA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Cap  B-complex", PharmacyCategoryEnum.CA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Balofloxacin", PharmacyCategoryEnum.TA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Calcium", PharmacyCategoryEnum.TA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Cap  Carbimazole", PharmacyCategoryEnum.CA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Oil  Castor Oil", PharmacyCategoryEnum.LO.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Erdosteine", PharmacyCategoryEnum.TA.getDescription(), false));

        formDataObj.getMedicineList().add(new DataObj("Cap  Folic Acid", PharmacyCategoryEnum.CA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Tab  Heparin", PharmacyCategoryEnum.TA.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("High Protein Supplement", PharmacyCategoryEnum.PW.getDescription(), false));
        formDataObj.getMedicineList().add(new DataObj("Cap  Fluoride", PharmacyCategoryEnum.CA.getDescription(), false));

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
        bppf.putSerializable("medicalPhysical",jsonMedicalRecord);
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

}
