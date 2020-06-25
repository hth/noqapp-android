package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.pojos.MenuDrawer;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.database.utils.MedicalFilesDB;
import com.noqapp.android.merchant.model.database.utils.NotificationDB;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.FileUploadOperation;
import com.noqapp.android.merchant.views.pojos.MedicalFile;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LaunchActivity
        extends BaseLaunchActivity
        implements LoginActivity.LoginCallBack, RegistrationActivity.RegisterCallBack {

    private TextView tv_badge;
    private FirebaseAnalytics fireBaseAnalytics;

    public FirebaseAnalytics getFireBaseAnalytics() {
        return fireBaseAnalytics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        Log.v("device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // to hide the default action bar title
        tv_name = findViewById(R.id.tv_name);
        tv_badge = findViewById(R.id.tv_badge);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        fl_notification.setVisibility(View.VISIBLE);
        fl_notification.setOnClickListener(view -> {
            Intent in = new Intent(launchActivity, NotificationActivity.class);
            startActivity(in);
        });
        if (LaunchActivity.isTablet) {
            list_fragment = findViewById(R.id.frame_layout);
            list_detail_fragment = findViewById(R.id.list_detail_fragment);
        }
        initDrawer();

        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            deviceApiCalls.isSupportedAppVersion(UserUtils.getDeviceId());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if (notify_count > 0) {
            tv_badge.setVisibility(View.VISIBLE);
        } else {
            tv_badge.setVisibility(View.INVISIBLE);
        }

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

    @Override
    public void updateMenuList(boolean showChart) {
        super.updateMenuList(showChart);
        try {
            if (launchActivity.getUserProfile().getUserLevel() == UserLevelEnum.S_MANAGER) {
                List<MenuDrawer> childModelsList = new ArrayList<>();
                childModelsList.add(new MenuDrawer(getString(R.string.menu_preference), false, false, R.drawable.case_history));
                childModelsList.add(new MenuDrawer(getString(R.string.menu_pref_store), false, false, R.drawable.pharmacy));
                menuDrawerItems.add(2, new MenuDrawer("Medical Settings", true, true, R.drawable.medical_settings, childModelsList));
            }
            menuDrawerItems.add(2, new MenuDrawer("Add New Patient", true, false, R.drawable.add_user));
            List<MenuDrawer> childModelsList = new ArrayList<>();
            childModelsList.add(new MenuDrawer("List of Patients", false, false, R.drawable.all_patient));
            childModelsList.add(new MenuDrawer("My Work History", false, false, R.drawable.all_history));
            menuDrawerItems.add(3, new MenuDrawer("Reports", true, true, R.drawable.reports, childModelsList));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callPreference() {
        super.callPreference();
        Intent intentPreference = new Intent(launchActivity, PreferenceActivity.class);
        startActivity(intentPreference);

    }

    @Override
    public void callPreferredStore() {
        super.callPreferredStore();
        Intent intentPreference = new Intent(launchActivity, PreferredStoreActivity.class);
        startActivity(intentPreference);
    }

    @Override
    public void callAllHistory() {
        super.callAllHistory();
        if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
            Intent in1 = new Intent(launchActivity, ReportCaseHistoryActivity.class);
            in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
            startActivity(in1);
        } else {
            new CustomToast().showToast(launchActivity, "No queue available");
        }
    }

    @Override
    public void callAllPatient() {
        super.callAllPatient();
        if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
            Intent in1 = new Intent(launchActivity, ReportPastPatientActivity.class);
            in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
            startActivity(in1);
        } else {
            new CustomToast().showToast(launchActivity, "No queue available");
        }
    }

    @Override
    public void callAddPatient() {
        super.callAddPatient();
        Intent intentAddPatient = new Intent(launchActivity, LoginActivity.class);
        startActivity(intentAddPatient);
        LoginActivity.loginCallBack = this;
        RegistrationActivity.registerCallBack = this;
    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        new CustomToast().showToast(this, "User already exist with this number");
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        new CustomToast().showToast(this, "User added successfully with this number");
    }

    public void uploadMedicalFiles() {
        List<MedicalFile> medicalFileList = MedicalFilesDB.getMedicalFileList();
        if (medicalFileList.size() > 0) {
            Log.e("Medical files count", "" + medicalFileList.size());
            for (int i = 0; i < medicalFileList.size(); i++) {
                new FileUploadOperation(this, medicalFileList.get(i)).execute();
            }
        }
    }
}
