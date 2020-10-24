package com.noqapp.android.merchant.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.body.merchant.FindMedicalProfile;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionHelper;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.HospitalVisitScheduleFragment;
import com.noqapp.android.merchant.views.utils.PdfHospitalVisitGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HospitalVisitScheduleActivity extends BaseActivity implements HospitalVisitSchedulePresenter {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String qUserId = "";
    private List<JsonHospitalVisitSchedule> immunizationList = new ArrayList<>();
    private List<JsonHospitalVisitSchedule> vaccinationList = new ArrayList<>();
    private RelativeLayout rl_empty;
    private LinearLayout ll_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        rl_empty = findViewById(R.id.rl_empty);
        ll_data = findViewById(R.id.ll_data);
        initActionsViews(true);
        tv_toolbar_title.setText("Upcoming Visit");
        String codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        JsonQueuedPerson jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        JsonMedicalRecord jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        Button btn_print_pdf = findViewById(R.id.btn_print_pdf);
        PermissionHelper permissionHelper = new PermissionHelper(this);
        btn_print_pdf.setOnClickListener(v -> {
            if (permissionHelper.isStoragePermissionAllowed()) {
                PdfHospitalVisitGenerator pdfGenerator = new PdfHospitalVisitGenerator(HospitalVisitScheduleActivity.this);
                pdfGenerator.createPdf(immunizationList, jsonQueuedPerson, jsonMedicalRecord);
            } else {
                permissionHelper.requestStoragePermission();
            }
        });
        qUserId = jsonQueuedPerson.getQueueUserId();
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        if (new NetworkUtil(this).isOnline()) {
            showProgress();
            MedicalHistoryApiCalls medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
            medicalHistoryApiCalls.hospitalVisitSchedule(
                    AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(),
                    new FindMedicalProfile().setCodeQR(codeQR).setQueueUserId(jsonQueuedPerson.getQueueUserId()));
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }

    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        dismissProgress();
        Log.e("immunization", jsonHospitalVisitScheduleList.toString());
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = jsonHospitalVisitScheduleList.getJsonHospitalVisitSchedules();
        for (int i = 0; i < jsonHospitalVisitSchedules.size(); i++) {
            switch (jsonHospitalVisitSchedules.get(i).getHospitalVisitFor()) {
                case IMU:
                    immunizationList.add(jsonHospitalVisitSchedules.get(i));
                    break;
                case VAC:
                    vaccinationList.add(jsonHospitalVisitSchedules.get(i));
                    break;
            }
        }
        HospitalVisitScheduleFragment hvsfVaccine = new HospitalVisitScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) vaccinationList);
        bundle.putString("qUserId", qUserId);
        hvsfVaccine.setArguments(bundle);

        HospitalVisitScheduleFragment hvsfImmune = new HospitalVisitScheduleFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) immunizationList);
        b.putString("qUserId", qUserId);
        hvsfImmune.setArguments(b);
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        if (vaccinationList.size() > 0) {
            adapter.addFragment(hvsfVaccine, "Vaccination");
        }
        if (immunizationList.size() > 0) {
            adapter.addFragment(hvsfImmune, "Immunization");
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (immunizationList.size() <= 0 && vaccinationList.size() <= 0) {
            ll_data.setVisibility(View.GONE);
            rl_empty.setVisibility(View.VISIBLE);
        } else {
            ll_data.setVisibility(View.VISIBLE);
            rl_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        dismissProgress();
        // Do nothing
    }
}
