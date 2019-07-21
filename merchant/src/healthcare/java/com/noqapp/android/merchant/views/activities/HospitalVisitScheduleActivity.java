package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.medical.JsonHospitalVisitSchedule;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.adapters.HospitalVisitScheduleAdapter;
import com.noqapp.android.merchant.views.pojos.ImmuneObjList;

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
    private ArrayList<ImmuneObjList> temp = new ArrayList<>();

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
        ImmuneObjList aa = new ImmuneObjList();
        aa.setHeaderTitle("Birth");
        aa.setImmuneObjs(getList(5));
        temp.add(aa);

        ImmuneObjList bb = new ImmuneObjList();
        bb.setHeaderTitle("6-8 Weeks");
        bb.setImmuneObjs(getList(3));
        temp.add(bb);

        ImmuneObjList cc = new ImmuneObjList();
        cc.setHeaderTitle("10-12 Weeks");
        cc.setImmuneObjs(getList(7));
        temp.add(cc);

        ImmuneObjList dd = new ImmuneObjList();
        dd.setHeaderTitle("14-16 Weeks");
        dd.setImmuneObjs(getList(5));
        temp.add(dd);
    }

    private ArrayList<JsonHospitalVisitSchedule> getList(int size) {
        ArrayList<JsonHospitalVisitSchedule> temp = new ArrayList<>();
        Random rand = new Random();
        int max = size;
        int min = 0;
        int randomNum = rand.nextInt((max - min) + 1) + min;
        for (int i = 0; i < size; i++) {
            JsonHospitalVisitSchedule immuneObj = new JsonHospitalVisitSchedule();
            immuneObj.setExpectedDate("22-11-2019");
            immuneObj.setName("PCV 1");
            if (randomNum == i)
                //immuneObj.setImmunizationDate("YES");
            temp.add(immuneObj);
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