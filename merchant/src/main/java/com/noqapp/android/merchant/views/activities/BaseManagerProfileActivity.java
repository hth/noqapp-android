package com.noqapp.android.merchant.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.FileUtils;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.ShowUploadImageDialog;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MerchantProfileApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.MerchantReviewQListFragment;
import com.noqapp.android.merchant.views.fragments.UserProfileFragment;
import com.noqapp.android.merchant.views.interfaces.MerchantPresenter;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BaseManagerProfileActivity extends BaseActivity implements View.OnClickListener,
    MerchantPresenter, ImageUploadPresenter {

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
    protected MerchantReviewQListFragment merchantReviewQListFragment;
    private MerchantProfileApiCalls merchantProfileApiCalls;
    protected TabViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_manager_profile);
        initActionsViews(true);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        merchantProfileApiCalls = new MerchantProfileApiCalls();
        iv_profile = findViewById(R.id.iv_profile);
        tv_remove_image = findViewById(R.id.tv_remove_image);
        iv_profile.setOnClickListener(this);
        tv_remove_image.setOnClickListener(this);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        loadTabs = new LoadTabs();
        loadTabs.execute();
        if (new NetworkUtil(this).isOnline()) {
            showProgress();
            setProgressMessage("Fetching profile...");
            merchantProfileApiCalls.setMerchantPresenter(this);
            merchantProfileApiCalls.fetch(
                UserUtils.getDeviceId(),
                AppInitialize.getEmail(),
                AppInitialize.getAuth());
        }
    }

    @Override
    public void merchantResponse(JsonMerchant jsonMerchant) {
        if (null != jsonMerchant && null != jsonMerchant.getJsonProfile()) {
            AppInitialize.setUserName(jsonMerchant.getJsonProfile().getName());
            AppInitialize.setUserLevel(jsonMerchant.getJsonProfile().getUserLevel().name());
            AppInitialize.setUserProfile(jsonMerchant.getJsonProfile());
            tv_profile_name.setText(jsonMerchant.getJsonProfile().getName());
            Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
            loadProfilePic(jsonMerchant.getJsonProfile().getProfileImage());
            if (null != userProfileFragment)
                userProfileFragment.updateUI(jsonMerchant.getJsonProfile());
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
        finish();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                new CustomToast().showToast(this, getString(R.string.error_account_block));
                LaunchActivity.getLaunchActivity().clearLoginData(false);
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
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

    private void loadProfilePic(String imageUrl) {
        Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                    .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + imageUrl)
                    .into(iv_profile);
                tv_remove_image.setVisibility(View.VISIBLE);
            } else {
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
                showProgress();
                setProgressMessage("Removing profile image");
                merchantProfileApiCalls.setImageUploadPresenter(this);
                merchantProfileApiCalls.removeImage(UserUtils.getDeviceId(), UserUtils.getEmail(),
                    UserUtils.getAuth(), new UpdateProfile().setQueueUserId(AppInitialize.getUserProfile().getQueueUserId()));
            }
            break;
        }
    }

    private void selectImage() {
        if (isExternalStoragePermissionAllowed()) {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_PICTURE);
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
                    String convertedPath = new FileUtils().getFilePath(this, data.getData());
                    if (!TextUtils.isEmpty(convertedPath)) {
                        ShowUploadImageDialog uploadImageDialog = new ShowUploadImageDialog(BaseManagerProfileActivity.this);
                        uploadImageDialog.setDialogClickListener(new ShowUploadImageDialog.DialogClickListener() {
                            @Override
                            public void btnPositiveClick() {
                                iv_profile.setImageBitmap(bitmap);
                                showProgress();
                                setProgressMessage("Updating profile image");
                                String type = getMimeType(BaseManagerProfileActivity.this, selectedImage);
                                File file = new File(convertedPath);
                                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(file, MediaType.parse(type)));
                                RequestBody profileImageOfQid = RequestBody.create(AppInitialize.getUserProfile().getQueueUserId(), MediaType.parse("text/plain"));
                                merchantProfileApiCalls.setImageUploadPresenter(BaseManagerProfileActivity.this);
                                merchantProfileApiCalls.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, profileImageOfQid);
                            }

                            @Override
                            public void btnNegativeClick() {
                                //Do nothing
                            }
                        });
                        uploadImageDialog.displayDialog(bitmap);
                    }
                } catch (Exception e) {
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

    protected void setupViewPager(ViewPager viewPager) {
        userProfileFragment = new UserProfileFragment();
        adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "Profile");
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
            new CustomToast().showToast(this, "Profile image change successfully! Change will be reflect after 5 min");
            tv_remove_image.setVisibility(View.VISIBLE);
        } else {
            new CustomToast().showToast(this, "Failed to update profile image");
        }
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Picasso.get().load(R.drawable.profile_avatar).into(iv_profile);
            tv_remove_image.setVisibility(View.GONE);
            AppInitialize.setUserProfile(AppInitialize.getUserProfile().setProfileImage(""));
            new CustomToast().showToast(this, "Profile image removed successfully!");
        } else {
            new CustomToast().showToast(this, "Failed to remove profile image");
        }
    }

    @Override
    public void imageUploadError() {

    }
}
