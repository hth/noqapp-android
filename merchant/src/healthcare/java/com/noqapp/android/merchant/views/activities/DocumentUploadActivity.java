package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.ImagePathReader;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.model.MerchantProfileModel;
import com.noqapp.android.merchant.presenter.beans.JsonMerchant;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.UserAdditionalInfoFragment;
import com.noqapp.android.merchant.views.fragments.UserProfileFragment;
import com.noqapp.android.merchant.views.fragments.UserProfileSettingFragment;
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

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadPresenter, JsonMedicalRecordPresenter {

    private ImageView iv_1, iv_2, iv_3;
    private final int SELECT_PICTURE = 110;
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String recordReferenceId, codeQR;
    private ImageView actionbarBack;
    private MedicalHistoryModel medicalHistoryModel;
    private ProgressDialog progressDialog;
    private JsonMedicalRecord jsonMedicalRecordTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        initProgress();
        setContentView(R.layout.activity_upload_image);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Upload Documents");
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        medicalHistoryModel = new MedicalHistoryModel(this);
        recordReferenceId = getIntent().getStringExtra("recordReferenceId");
        codeQR = getIntent().getStringExtra("qCodeQR");
        iv_1 = findViewById(R.id.iv_1);
        iv_2 = findViewById(R.id.iv_2);
        iv_3 = findViewById(R.id.iv_3);
        iv_1.setOnClickListener(this);
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_1);
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_2);
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_3);
        JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
        jsonMedicalRecord.setRecordReferenceId(recordReferenceId);
        jsonMedicalRecord.setCodeQR(codeQR);
        medicalHistoryModel.setJsonMedicalRecordPresenter(this);
        medicalHistoryModel.retrieveMedicalRecord(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);
    }


    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
        finish();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.ACCOUNT_INACTIVE.getCode())) {
                Toast.makeText(this, getString(R.string.error_account_block), Toast.LENGTH_LONG).show();
                LaunchActivity.getLaunchActivity().clearLoginData(false);
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_1:
            case R.id.iv_2:
            case R.id.iv_3:
                selectImage();
                break;
            case R.id.tv_remove_image: {
                progressDialog.show();
                progressDialog.setMessage("Removing profile image");
                //  merchantProfileModel.removeImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new UpdateProfile().setQueueUserId(LaunchActivity.getLaunchActivity().getUserProfile().getQueueUserId()));
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
                    iv_1.setImageBitmap(bitmap);

                    String convertedPath = new ImagePathReader().getPathFromUri(this, selectedImage);
                    // NoQueueBaseActivity.setUserProfileUri(convertedPath);

                    if (!TextUtils.isEmpty(convertedPath)) {
                        progressDialog.show();
                        progressDialog.setMessage("Uploading document");
                        String type = getMimeType(this, selectedImage);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                        medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        } else {
            Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_LONG).show();
            Picasso.with(this).load(R.drawable.profile_blue).into(iv_1);
        }
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Picasso.with(this).load(R.drawable.profile_blue).into(iv_1);
            LaunchActivity.getLaunchActivity().setUserProfile(LaunchActivity.getLaunchActivity().getUserProfile().setProfileImage(""));
            Toast.makeText(this, "Profile image removed successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to remove profile image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageUploadError() {
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_1);
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        jsonMedicalRecordTemp = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            if(null != jsonMedicalRecord.getImages() && jsonMedicalRecord.getImages().size()>0){
                try {
                    if (!TextUtils.isEmpty(jsonMedicalRecord.getImages().get(0))) {
                        Picasso.with(this)
                                .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId+"/"+jsonMedicalRecord.getImages().get(0))
                                .into(iv_1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
