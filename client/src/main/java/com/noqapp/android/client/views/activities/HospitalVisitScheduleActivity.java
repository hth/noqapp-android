package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.UserMedicalProfileApiCalls;
import com.noqapp.android.client.presenter.beans.body.MedicalProfile;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.PdfHospitalVisitGenerator;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.HospitalVisitScheduleFragment;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.beans.medical.JsonHospitalVisitScheduleList;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.HospitalVisitSchedulePresenter;

import com.google.android.material.tabs.TabLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HospitalVisitScheduleActivity extends BaseActivity implements HospitalVisitSchedulePresenter {
    private RelativeLayout rl_empty;
    private LinearLayout ll_data;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(MyApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        initActionsViews(true);
        tv_toolbar_title.setText("Upcoming Hospital Visit");
        rl_empty = findViewById(R.id.rl_empty);
        ll_data = findViewById(R.id.ll_data);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        MedicalProfile medicalProfile = (MedicalProfile) getIntent().getSerializableExtra("medicalProfile");
        UserMedicalProfileApiCalls userMedicalProfileApiCalls = new UserMedicalProfileApiCalls();
        userMedicalProfileApiCalls.setHospitalVisitSchedulePresenter(this);
        if (NetworkUtils.isConnectingToInternet(this)) {
            if (UserUtils.isLogin()) {
                userMedicalProfileApiCalls.hospitalVisitSchedule(UserUtils.getEmail(), UserUtils.getAuth(), medicalProfile);
                setProgressMessage("Fetching immunization history...");
                showProgress();
            } else {
                new CustomToast().showToast(this, "Please login to see the details");
            }
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void hospitalVisitScheduleResponse(JsonHospitalVisitScheduleList jsonHospitalVisitScheduleList) {
        dismissProgress();
        Log.e("immunization", jsonHospitalVisitScheduleList.toString());
        List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = jsonHospitalVisitScheduleList.getJsonHospitalVisitSchedules();

        List<JsonHospitalVisitSchedule> immunizationList = new ArrayList<>();
        List<JsonHospitalVisitSchedule> vaccinationList = new ArrayList<>();
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
        hvsfVaccine.setArguments(bundle);

        HospitalVisitScheduleFragment hvsfImmune = new HospitalVisitScheduleFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) immunizationList);
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
        Button btn_print_pdf = findViewById(R.id.btn_print_pdf);
        btn_print_pdf.setOnClickListener(v -> {
            if (isExternalStoragePermissionAllowed()) {
                JsonProfile jsonProfile = (JsonProfile) getIntent().getSerializableExtra("jsonProfile");
                PdfHospitalVisitGenerator pdfGenerator = new PdfHospitalVisitGenerator(HospitalVisitScheduleActivity.this);
                pdfGenerator.createPdf(immunizationList, jsonProfile);
            } else {
                requestStoragePermission();
            }
        });
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
        // do nothing
    }

    private boolean isExternalStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                STORAGE_PERMISSION_PERMS,
                STORAGE_PERMISSION_CODE);
    }
}
