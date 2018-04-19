package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 10/4/18.
 */


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.UserUtils;

import java.io.FileNotFoundException;
import java.io.IOException;



import butterknife.BindView;
import butterknife.ButterKnife;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.iv_edit)
    protected ImageView iv_edit;
    @BindView(R.id.iv_profile)
    protected ImageView iv_profile;
    private final int SELECT_PICTURE = 110;

    @BindView(R.id.actv_user_name)
    protected AutoCompleteTextView actv_user_name;
    @BindView(R.id.actv_email)
    protected AutoCompleteTextView actv_email;
    @BindView(R.id.actv_gender)
    protected AutoCompleteTextView actv_gender;
    @BindView(R.id.edt_dob)
    protected EditText edt_dob;
    @BindView(R.id.edt_address)
    protected EditText edt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Profile");
        iv_edit.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        actv_user_name.setText(NoQueueBaseActivity.getUserName());
        tv_name.setText(NoQueueBaseActivity.getUserName());
        actv_email.setText(NoQueueBaseActivity.getMail());
        actv_gender.setText(NoQueueBaseActivity.getGender().equals("M")?"Male":"Female");
       // edt_address.setText(NoQueueBaseActivity.geta);
        edt_dob.setText(NoQueueBaseActivity.getUserDOB());

        try {
            if(!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                Uri imageUri = Uri.parse(NoQueueBaseActivity.getUserProfileUri());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                iv_profile.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                //  Bitmap bitmap = getPath(data.getData());
                //  iv_profile.setImageBitmap(bitmap);

                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    iv_profile.setImageBitmap(bitmap);
                    NoQueueBaseActivity.setUserProfileUri(selectedImage.toString());
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
}
