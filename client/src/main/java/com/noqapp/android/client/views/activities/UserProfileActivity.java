package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.common.beans.JsonProfile;
import com.noqapp.common.beans.JsonResponse;
import com.noqapp.common.presenter.ImageUploadPresenter;
import com.noqapp.common.utils.ImagePathReader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener, ImageUploadPresenter, ProfilePresenter {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.iv_edit)
    protected ImageView iv_edit;

    @BindView(R.id.iv_add_dependent)
    protected ImageView iv_add_dependent;
    public static ImageView iv_profile;
    private final int SELECT_PICTURE = 110;
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @BindView(R.id.edt_birthday)
    protected EditText edt_birthday;
    @BindView(R.id.edt_address)
    protected EditText edt_address;

    public String gender = "";

    @BindView(R.id.edt_phone)
    protected EditText edt_phoneNo;

    @BindView(R.id.edt_name)
    protected EditText edt_Name;

    @BindView(R.id.edt_email)
    protected EditText edt_Mail;


    @BindView(R.id.tv_male)
    protected EditText tv_male;

    @BindView(R.id.tv_female)
    protected EditText tv_female;

    @BindView(R.id.tv_migrate)
    protected TextView tv_migrate;

    @BindView(R.id.ll_gender)
    protected LinearLayout ll_gender;

    @BindView(R.id.ll_dependent)
    protected LinearLayout ll_dependent;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        initActionsViews(false);
        iv_profile = findViewById(R.id.iv_profile);
        iv_edit.setOnClickListener(this);
        loadProfilePic();
        tv_toolbar_title.setText("Profile");
        iv_profile.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        updateUI();
        edt_birthday.setInputType(InputType.TYPE_NULL);
        edt_birthday.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_migrate.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();

        iv_add_dependent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(UserProfileActivity.this,UserProfileEditActivity.class);
                in.putExtra(NoQueueBaseActivity.IS_DEPENDENT,true);
               // in.putExtra(NoQueueBaseActivity.DEPENDENT_PROFILE,new JsonProfile());

                startActivity(in);
            }
        });

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            ProfileModel.profilePresenter = this;
            ProfileModel.fetchProfile(UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    private void loadProfilePic() {
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
    }


    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        Log.v("Image upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void imageUploadError() {

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

            case R.id.tv_migrate:
                Intent migrate = new Intent(this, MigrateActivity.class);
                startActivity(migrate);
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
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
        dismissProgress();
        updateUI();
    }

    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {

    }


    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void queueError(String error) {

    }

    @Override
    public void authenticationFailure(int errorCode) {
        //TODO(chandra)
    }

    private void updateUI() {
        edt_Name.setText(NoQueueBaseActivity.getUserName());
        tv_name.setText(NoQueueBaseActivity.getUserName());
        edt_phoneNo.setText(NoQueueBaseActivity.getPhoneNo());
        edt_Mail.setText(NoQueueBaseActivity.getMail().toString().contains("noqapp.com") ? "" :
                NoQueueBaseActivity.getMail());
        edt_phoneNo.setEnabled(false);
        edt_Mail.setEnabled(false);
        edt_Name.setEnabled(false);
        edt_birthday.setEnabled(false);
        edt_address.setEnabled(false);
        edt_address.setText(NoQueueBaseActivity.getAddress());
        int id = 0;
        if (NoQueueBaseActivity.getGender().equals("M")) {
            id = R.id.tv_male;
        } else {
            id = R.id.tv_female;
        }
        switch (id) {
            case R.id.tv_male:
                gender = "M";
                tv_female.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_male.setBackgroundResource(R.drawable.gender_redbg);
                SpannableString ss = new SpannableString("Male  ");
                Drawable d = getResources().getDrawable(R.drawable.check_white);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ss.setSpan(span, 5, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_male.setText(ss);
                tv_male.setTextColor(Color.WHITE);
                tv_female.setTextColor(Color.BLACK);
                tv_female.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
            case R.id.tv_female:
                gender = "F";
                tv_female.setBackgroundResource(R.drawable.gender_redbg);
                tv_male.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_female.setCompoundDrawablePadding(0);
                tv_male.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tv_male.setTextColor(Color.BLACK);
                tv_female.setTextColor(Color.WHITE);
                SpannableString ss1 = new SpannableString("Female  ");
                Drawable d1 = getResources().getDrawable(R.drawable.check_white);
                d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight());
                ImageSpan span1 = new ImageSpan(d1, ImageSpan.ALIGN_BASELINE);
                ss1.setSpan(span1, 7, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_female.setText(ss1);
                break;


        }
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = dateFormatter.format(fromUser.parse(NoQueueBaseActivity.getUserDOB()));
            edt_birthday.setText(reformattedStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JsonProfile> jsonProfiles = LaunchActivity.getLaunchActivity().getUserProfile().getDependents();
        ll_dependent.removeAllViews();
        if (null != jsonProfiles && jsonProfiles.size() > 0) {
            for (int j = 0; j < jsonProfiles.size(); j++) {
                final JsonProfile jsonProfile =jsonProfiles.get(j);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View listitem_dependent = inflater.inflate(R.layout.listitem_dependent, null);
                ImageView iv_delete = listitem_dependent.findViewById(R.id.iv_delete);
                ImageView iv_edit = listitem_dependent.findViewById(R.id.iv_edit);
                TextView tv_title = listitem_dependent.findViewById(R.id.tv_title);
                tv_title.setText(jsonProfile.getName());
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UserProfileActivity.this,"Delete: "+jsonProfile.toString(),Toast.LENGTH_LONG).show();
                    }
                });
                iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UserProfileActivity.this,"Edit: "+jsonProfile.toString(),Toast.LENGTH_LONG).show();
                    }
                });
                ll_dependent.addView(listitem_dependent);
            }
        }
        loadProfilePic();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}

