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
import com.noqapp.android.common.utils.ImagePathReader;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ManagerProfileActivity extends ProfileActivity implements View.OnClickListener, QueueManagerPresenter {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_manager_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        tv_toolbar_title.setText("Doctor Profile");
        iv_profile = findViewById(R.id.iv_profile);
        iv_edit.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        webProfileId = getIntent().getStringExtra("webProfileId");
        managerName = getIntent().getStringExtra("managerName");
        managerImageUrl = getIntent().getStringExtra("managerImage");

        Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(UserProfileActivity.iv_profile);
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

        if(LaunchActivity.getLaunchActivity().isOnline()){
            new ProfessionalProfileModel(this).profile(UserUtils.getDeviceId(),webProfileId);
        }

    }


    @Override
    public void authenticationFailure(int errorCode) {

    }

    @Override
    public void queueManagerResponse(JsonProfessionalProfile jsonProfessionalProfile) {
        Log.v("queueManagerResponse", jsonProfessionalProfile.toString());
        userAdditionalInfoFragment.updateUI(jsonProfessionalProfile);
        userProfileFragment.updateUI(jsonProfessionalProfile.getStores());
    }

    @Override
    public void queueManagerError() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.iv_edit:
                // selectImage();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                //  Bitmap bitmap = getPath(data.getData());
                //  iv_profile.setImageBitmap(bitmap);

                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    iv_profile.setImageBitmap(bitmap);

                    String convertedPath = new ImagePathReader().getPathFromUri(this, selectedImage);
                    NoQueueBaseActivity.setUserProfileUri(convertedPath);

                    if (!TextUtils.isEmpty(convertedPath)) {
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        //   ProfileModel.imageUploadPresenter = this;
                        //  ProfileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), filePart);
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        userProfileFragment = new UserProfileFragment();
        userAdditionalInfoFragment = new UserAdditionalInfoFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "Profile");
        adapter.addFragment(userAdditionalInfoFragment, "Additional Infos");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs)
            loadTabs.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_name.setText(managerName);
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
