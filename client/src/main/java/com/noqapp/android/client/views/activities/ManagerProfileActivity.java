package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.open.ProfessionalProfileImpl;
import com.noqapp.android.client.presenter.QueueManagerPresenter;
import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.client.views.fragments.UserProfileFragment;
import com.noqapp.android.common.beans.JsonReview;
import com.noqapp.android.common.beans.JsonReviewList;
import com.noqapp.android.common.model.types.category.MedicalDepartmentEnum;
import com.noqapp.android.common.utils.CommonHelper;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagerProfileActivity extends ProfileActivity implements QueueManagerPresenter {

    private TextView tv_name;
    private TextView tv_experience, tv_total_review, tv_total_rating;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private UserProfileFragment userProfileFragment;
    private UserAdditionalInfoFragment userAdditionalInfoFragment;
    private MedicalDepartmentEnum medicalDepartmentEnum;
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
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
        tv_total_review = findViewById(R.id.tv_total_review);
        tv_total_rating = findViewById(R.id.tv_total_rating);
        tv_name.setText(managerName);
        AppUtils.loadProfilePic(iv_profile, managerImageUrl, this);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();

        if (isOnline()) {
            setProgressMessage("Loading doctor's profile...");
            showProgress();
            new ProfessionalProfileImpl(this).profile(UserUtils.getDeviceId(), webProfileId);
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
            userProfileFragment.updateUI(jsonProfessionalProfile);

            float val = getReviewCountTotal(jsonProfessionalProfile.getReviews());
            if (val == 0) {
                tv_total_rating.setVisibility(View.INVISIBLE);
                tv_total_review.setVisibility(View.INVISIBLE);
            } else {
                tv_total_rating.setVisibility(View.VISIBLE);
                tv_total_review.setVisibility(View.VISIBLE);
            }
            tv_total_rating.setText(String.valueOf(AppUtils.round(val)));
            tv_total_review.setText("Reviews");
            tv_total_review.setPaintFlags(tv_total_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_total_review.setOnClickListener(v -> {
                Intent in = new Intent(ManagerProfileActivity.this, AllReviewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) jsonReviews);
                bundle.putString("storeName", tv_name.getText().toString());
                in.putExtras(bundle);
                startActivity(in);
            });
        } else {
            Log.v("queueManagerResponse", "null data received");
        }
        dismissProgress();
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

    private float getReviewCountTotal(Map<String, JsonReviewList> reviews) {
        float reviewCount = 0;
        try {
            if (null != reviews && reviews.size() > 0) {
                float value = 0;
                float div = 0;
                for (Map.Entry<String, JsonReviewList> entry : reviews.entrySet()) {
                    if (entry.getValue().getJsonReviews().size() > 0) {
                        value += entry.getValue().getAggregateRatingCount();
                        div += entry.getValue().getJsonReviews().size();
                        jsonReviews.addAll(entry.getValue().getJsonReviews());
                    }
                }
                reviewCount = value / div;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviewCount;
    }
}
