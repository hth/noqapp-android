package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleAdapter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class HospitalVisitScheduleActivity extends BaseActivity {
    private long lastPress;
    private Toast backPressToast;
    private String codeQR;
    private ArrayList<JsonHospitalVisitSchedule> temp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immunization);
        codeQR = getIntent().getStringExtra("qCodeQR");
        RecyclerView rcv_header = findViewById(R.id.rcv_header);
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());
        initList();
        HospitalVisitScheduleAdapter hospitalVisitScheduleAdapter = new HospitalVisitScheduleAdapter(this, temp, null);
        rcv_header.setAdapter(hospitalVisitScheduleAdapter);
    }

    private void initList() {
        JsonHospitalVisitSchedule aa = new JsonHospitalVisitSchedule();
        aa.setHeader("Birth");
        aa.setVisitingFor(getList(5));
        temp.add(aa);

        JsonHospitalVisitSchedule bb = new JsonHospitalVisitSchedule();
        bb.setHeader("6-8 Weeks");
        bb.setVisitingFor(getList(3));
        temp.add(bb);

        JsonHospitalVisitSchedule cc = new JsonHospitalVisitSchedule();
        cc.setHeader("10-12 Weeks");
        cc.setVisitingFor(getList(7));
        temp.add(cc);

        JsonHospitalVisitSchedule dd = new JsonHospitalVisitSchedule();
        dd.setHeader("14-16 Weeks");
        dd.setVisitingFor(getList(5));
        temp.add(dd);
    }

    private ArrayList<String> getList(int size) {
        ArrayList<String> temp = new ArrayList<>();
        Random rand = new Random();
        int max = size;
        int min = 0;
        int randomNum = rand.nextInt((max - min) + 1) + min;
        for (int i = 0; i < size; i++) {
           // JsonHospitalVisitSchedule immuneObj = new JsonHospitalVisitSchedule();
          //  immuneObj.setExpectedDate("22-11-2019");
           // immuneObj.setHeader("PCV 1");
          //  if (randomNum == i)
                //immuneObj.setImmunizationDate("YES");
            temp.add("PCV 1");
        }
        return temp;
    }


    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
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
}
