package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfessionalProfileModel;
import com.noqapp.android.client.presenter.QueueManagerPresenter;
import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.client.views.fragments.UserProfileFragment;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.utils.CommonHelper;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


public class ManagerProfileActivity extends ProfileActivity implements QueueManagerPresenter {

    private TextView tv_name;
    private TextView tv_experience;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private UserProfileFragment userProfileFragment;
    private UserAdditionalInfoFragment userAdditionalInfoFragment;
    private MedicalDepartmentEnum medicalDepartmentEnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_manager_profile);
        initActionsViews(false);
        tv_toolbar_title.setText("Doctor Profile");
        ImageView iv_profile = findViewById(R.id.iv_profile);
        String webProfileId = getIntent().getStringExtra("webProfileId");
        String managerName = getIntent().getStringExtra("managerName");
        String managerImageUrl = getIntent().getStringExtra("managerImage");
        medicalDepartmentEnum = MedicalDepartmentEnum.valueOf(getIntent().getStringExtra("bizCategoryId"));
        tv_name = findViewById(R.id.tv_name);
        tv_experience = findViewById(R.id.tv_experience);
        tv_name.setText(managerName);
        Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(managerImageUrl)) {
                Picasso.with(this)
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, managerImageUrl))
                        .placeholder(ImageUtils.getProfilePlaceholder(this))
                        .error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Fetching doctor's profile...");
            progressDialog.show();
            new ProfessionalProfileModel(this).profile(UserUtils.getDeviceId(), webProfileId);
        }
    }


    @Override
    public void queueManagerResponse(JsonProfessionalProfile jsonProfessionalProfile) {
        if (null != jsonProfessionalProfile) {
            Log.v("queueManagerResponse", jsonProfessionalProfile.toString());
            switch (medicalDepartmentEnum) {
                case PHY:
                    tv_name.setText(jsonProfessionalProfile.getName());
                    break;
                default:
                    tv_name.setText("Dr. " + jsonProfessionalProfile.getName());
            }
            if (!TextUtils.isEmpty(jsonProfessionalProfile.getPracticeStart())) {
                try {
                    // Format - practiceStart='2017-08-07'
                    DateTime dateTime = new DateTime(CommonHelper.SDF_YYYY_MM_DD.parse(jsonProfessionalProfile.getPracticeStart()));
                    Period period = new Period(dateTime, new DateTime());
                    tv_experience.setText(String.valueOf(period.getYears()) + "+ yrs experience");
                    if (0 == period.getYears())
                        tv_experience.setText(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            userAdditionalInfoFragment.updateUI(jsonProfessionalProfile);
            userProfileFragment.updateUI(jsonProfessionalProfile.getStores(), jsonProfessionalProfile.getAboutMe());
        } else {
            Log.v("queueManagerResponse", "null data received");
        }
        dismissProgress();
    }

    @Override
    public void queueManagerError() {
        dismissProgress();
    }


    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        ErrorResponseHandler.processError(this,eej);
    }

    private void setupViewPager(ViewPager viewPager) {
        userProfileFragment = new UserProfileFragment();
        userAdditionalInfoFragment = new UserAdditionalInfoFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "Profile");
        adapter.addFragment(userAdditionalInfoFragment, "Additional Info");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs) {
            loadTabs.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //tv_name.setText(managerName);
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
}
