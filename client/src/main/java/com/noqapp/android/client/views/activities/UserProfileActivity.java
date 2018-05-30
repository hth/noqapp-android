package com.noqapp.android.client.views.activities;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.ImageUploadPresenter;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.utils.ImagePathReader;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.client.views.fragments.UserProfileFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener,ImageUploadPresenter {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.iv_edit)
    protected ImageView iv_edit;
    public static ImageView iv_profile;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        tv_toolbar_title.setText("Profile");
        iv_profile = findViewById(R.id.iv_profile);
        iv_edit.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        Picasso.with(this).load(R.drawable.profile_avatar).into(UserProfileActivity.iv_profile);
        try {
            if (!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                Picasso.with(this)
                        .load(BuildConfig.AWSS3 + BuildConfig.PROFILE_BUCKET + NoQueueBaseActivity.getUserProfileUri())
                        .into(UserProfileActivity.iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        loadTabs =new LoadTabs();
        loadTabs.execute();

    }


    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        Log.v("Image upload", ""+jsonResponse.getResponse());
    }

    @Override
    public void imageUploadError() {

    }

    @Override
    public void authenticationFailure(int errorCode) {

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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
                        ProfileModel.imageUploadPresenter = this;
                        ProfileModel.uploadImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), filePart);
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
        userAdditionalInfoFragment = new UserAdditionalInfoFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userProfileFragment, "Profile");
        adapter.addFragment(userAdditionalInfoFragment, "Additional Details");
        viewPager.setAdapter(adapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != loadTabs)
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
        tv_name.setText(NoQueueBaseActivity.getUserName());
    }
}
