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
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.client.views.fragments.UserProfileFragment;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;

import com.squareup.picasso.Picasso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

public class ManagerProfileActivity extends ProfileActivity implements QueueManagerPresenter {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.iv_edit)
    protected ImageView iv_edit;
    private ImageView iv_profile;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private UserProfileFragment userProfileFragment;
    private UserAdditionalInfoFragment userAdditionalInfoFragment;
    private String webProfileId = "";
    private String managerName = "";
    private String managerImageUrl = "";
    private MedicalDepartmentEnum medicalDepartmentEnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_manager_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        tv_toolbar_title.setText("Doctor Profile");
        iv_profile = findViewById(R.id.iv_profile);
        webProfileId = getIntent().getStringExtra("webProfileId");
        managerName = getIntent().getStringExtra("managerName");
        managerImageUrl = getIntent().getStringExtra("managerImage");
        medicalDepartmentEnum = MedicalDepartmentEnum.valueOf(getIntent().getStringExtra("bizCategoryId"));
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
    public void authenticationFailure(int errorCode) {
        dismissProgress();
    }

    @Override
    public void queueManagerResponse(JsonProfessionalProfile jsonProfessionalProfile) {
        Log.v("queueManagerResponse", jsonProfessionalProfile.toString());
        switch (medicalDepartmentEnum) {
            case PHY:
                tv_name.setText(jsonProfessionalProfile.getName());
                break;
            default:
                tv_name.setText("Dr. " + jsonProfessionalProfile.getName());
        }
        userAdditionalInfoFragment.updateUI(jsonProfessionalProfile);
        userProfileFragment.updateUI(jsonProfessionalProfile.getStores(), jsonProfessionalProfile.getAboutMe());
        dismissProgress();
    }

    @Override
    public void queueManagerError() {
        dismissProgress();
    }


    private void setupViewPager(ViewPager viewPager) {
        userProfileFragment = new UserProfileFragment();
        userAdditionalInfoFragment = new UserAdditionalInfoFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
