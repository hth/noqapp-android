package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.MedicalCaseFragment;
import com.noqapp.android.merchant.views.fragments.MedicalRecordListFragment;
import com.noqapp.android.merchant.views.fragments.PatientProfileFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MedicalCaseHistoryTabbed extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private MedicalCaseFragment medicalCaseFragment;
    private PatientProfileFragment patientProfileFragment;
    private MedicalRecordListFragment medicalRecordListFragment;
    private long lastPress;
    private Toast backPressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_case);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();

    }


    private class LoadTabs extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        try {
            medicalCaseFragment = new MedicalCaseFragment();
            Bundle b = new Bundle();
            b.putSerializable("data", getIntent().getSerializableExtra("data"));
            b.putString("qCodeQR", getIntent().getStringExtra("qCodeQR"));
            medicalCaseFragment.setArguments(b);

            patientProfileFragment = new PatientProfileFragment();
            Bundle bppf = new Bundle();
            bppf.putString("qUserId", ((JsonQueuedPerson) getIntent().getSerializableExtra("data")).getQueueUserId());
            bppf.putString("qCodeQR", getIntent().getStringExtra("qCodeQR"));
            patientProfileFragment.setArguments(bppf);
            medicalRecordListFragment = new MedicalRecordListFragment();
            medicalRecordListFragment.setArguments(bppf);

            TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(medicalCaseFragment, "Medical Case");
            adapter.addFragment(patientProfileFragment, "Patient Profile");
            adapter.addFragment(medicalRecordListFragment, "Patient Medical Records");
            viewPager.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs)
            loadTabs.cancel(true);
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


}
