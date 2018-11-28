package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.ImagePathReader;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.merchant.views.fragments.UserProfileFragment;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;

import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileNotFoundException;

public class ManagerProfileActivity extends AppCompatActivity implements View.OnClickListener, MerchantPresenter, ImageUploadPresenter {

    private TextView tv_profile_name;
    private Button tv_remove_image;
    private ImageView iv_profile;
    private final int SELECT_PICTURE = 110;
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoadTabs loadTabs;
    private UserProfileFragment userProfileFragment;
    private UserAdditionalInfoFragment userAdditionalInfoFragment;
    private ImageView actionbarBack;
    private MerchantProfileModel merchantProfileModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        initProgress();
        setContentView(R.layout.activity_queue_manager_profile);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_profile_name = findViewById(R.id.tv_profile_name);
        merchantProfileModel = new MerchantProfileModel();
        iv_profile = findViewById(R.id.iv_profile);
        tv_remove_image = findViewById(R.id.tv_remove_image);
        iv_profile.setOnClickListener(this);
        tv_remove_image.setOnClickListener(this);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            merchantProfileModel.setMerchantPresenter(this);
            merchantProfileModel.fetch(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth());
        }
    }


    @Override
    public void merchantResponse(JsonMerchant jsonMerchant) {
        if (null != jsonMerchant) {
            LaunchActivity.getLaunchActivity().setUserName(jsonMerchant.getJsonProfile().getName());
            LaunchActivity.getLaunchActivity().setUserLevel(jsonMerchant.getJsonProfile().getUserLevel().name());
            LaunchActivity.getLaunchActivity().setUserProfile(jsonMerchant.getJsonProfile());
            tv_profile_name.setText(jsonMerchant.getJsonProfile().getName());
            userProfileFragment.updateUI(jsonMerchant.getJsonProfile());
            if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
                // Additional profile will be only visible to store manager
                switch (jsonMerchant.getJsonProfile().getBusinessType()) {
                    case DO:
                        userAdditionalInfoFragment.updateUI(jsonMerchant.getJsonProfessionalProfile());
                        break;
                    default:
                        //Do nothing
                }
            }
            Picasso.with(this).load(R.drawable.profile_avatar).into(iv_profile);
            loadProfilePic(jsonMerchant.getJsonProfile().getProfileImage());
        }
        dismissProgress();
    }

    @Override
    public void merchantError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
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

    private void loadProfilePic(String imageUrl) {
        Picasso.with(this).load(R.drawable.profile_avatar).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(this)
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + imageUrl)
                        .into(iv_profile);
                tv_remove_image.setVisibility(View.VISIBLE);
            }else {
                tv_remove_image.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_remove_image.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_profile:
                selectImage();
                break;
            case R.id.tv_remove_image: {
                progressDialog.show();
                progressDialog.setMessage("Removing profile image");
                merchantProfileModel.setImageUploadPresenter(this);
                merchantProfileModel.removeImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new UpdateProfile().setQueueUserId(LaunchActivity.getLaunchActivity().getUserProfile().getQueueUserId()));
            }
            break;
            case R.id.iv_edit:
                // selectImage();
                break;
        }
    }

    private void selectImage() {
        if (isExternalStoragePermissionAllowed()) {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestStoragePermission();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    iv_profile.setImageBitmap(bitmap);

                    String convertedPath = new ImagePathReader().getPathFromUri(this, selectedImage);
                    // NoQueueBaseActivity.setUserProfileUri(convertedPath);

                    if (!TextUtils.isEmpty(convertedPath)) {
                        progressDialog.show();
                        progressDialog.setMessage("Updating profile image");
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody profileImageOfQid = RequestBody.create(MediaType.parse("text/plain"), LaunchActivity.getLaunchActivity().getUserProfile().getQueueUserId());
                        merchantProfileModel.setImageUploadPresenter(this);
                        merchantProfileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private String getMimeType(Context context, Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void setupViewPager(ViewPager viewPager) {
        userProfileFragment = new UserProfileFragment();
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "Profile");
        if (LaunchActivity.getLaunchActivity().getUserLevel() == UserLevelEnum.S_MANAGER) {
            // Additional profile will be only visible to store manager
            switch (LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()) {
                case DO:
                    userAdditionalInfoFragment = new UserAdditionalInfoFragment();
                    adapter.addFragment(userAdditionalInfoFragment, "Professional Profile");
                    break;
                default:
                    //Do nothing
            }
        }
        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != loadTabs)
            loadTabs.cancel(true);
    }

    private boolean isExternalStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                STORAGE_PERMISSION_PERMS,
                STORAGE_PERMISSION_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //tv_name.setText(LaunchActivity.getLaunchActivity().getUserName());
    }

    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image upload", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Profile image change successfully! Change will be reflect after 5 min", Toast.LENGTH_LONG).show();
            tv_remove_image.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Picasso.with(this).load(R.drawable.profile_avatar).into(iv_profile);
            tv_remove_image.setVisibility(View.GONE);
            LaunchActivity.getLaunchActivity().setUserProfile( LaunchActivity.getLaunchActivity().getUserProfile().setProfileImage(""));
            Toast.makeText(this, "Profile image removed successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to remove profile image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageUploadError() {

    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
