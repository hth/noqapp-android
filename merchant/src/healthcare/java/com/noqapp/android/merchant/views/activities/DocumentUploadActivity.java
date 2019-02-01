package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryModel;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.utils.PermissionUtils;
import com.noqapp.android.merchant.views.utils.FileUtils;

import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadPresenter, JsonMedicalRecordPresenter {

    private static final int PICK_IMAGE_CAMERA = 121;
    private static final int PICK_IMAGE_GALLERY = 122;
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ImageView[] imageViews = new ImageView[3];
    private ImageView[] imageViewsDelete = new ImageView[3];
    private String recordReferenceId, codeQR;
    private ImageView actionbarBack;
    private MedicalHistoryModel medicalHistoryModel;
    private ProgressDialog progressDialog;
    private JsonMedicalRecord jsonMedicalRecordTemp;
    private int selectPos = -1;
    private String userChoosenTask;
    Uri imageUri;

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
        ImageView iv_1 = findViewById(R.id.iv_1);
        ImageView iv_2 = findViewById(R.id.iv_2);
        ImageView iv_3 = findViewById(R.id.iv_3);
        imageViews[0] = iv_1;
        imageViews[1] = iv_2;
        imageViews[2] = iv_3;


        ImageView iv_delete_1 = findViewById(R.id.iv_delete_1);
        ImageView iv_delete_2 = findViewById(R.id.iv_delete_2);
        ImageView iv_delete_3 = findViewById(R.id.iv_delete_3);

        imageViewsDelete[0] = iv_delete_1;
        imageViewsDelete[1] = iv_delete_2;
        imageViewsDelete[2] = iv_delete_3;

        iv_1.setOnClickListener(this);
        iv_2.setOnClickListener(this);
        iv_3.setOnClickListener(this);
        iv_delete_1.setOnClickListener(this);
        iv_delete_2.setOnClickListener(this);
        iv_delete_3.setOnClickListener(this);


        Picasso.with(this).load(R.drawable.profile_blue).into(iv_1);
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_2);
        Picasso.with(this).load(R.drawable.profile_blue).into(iv_3);
        progressDialog.show();
        progressDialog.setMessage("Fetching documents...");
        medicalHistoryModel.setJsonMedicalRecordPresenter(this);
        medicalHistoryModel.existsMedicalRecord(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), codeQR, recordReferenceId);

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
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.MEDICAL_RECORD_DOES_NOT_EXISTS.getCode())) {
                Toast.makeText(this, "Please create medical record first to upload document", Toast.LENGTH_LONG).show();
                dismissProgress();
                finish();//close the current activity
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
        dismissProgress();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_1:
                selectPos = 0;
                selectImage();
//                if (ContextCompat.checkSelfPermission(DocumentUploadActivity.this, Manifest.permission.CAMERA)
//                        == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(DocumentUploadActivity.this, new String[]{Manifest.permission.CAMERA}, 55);
//                } else {
//                    selectImageNew();
//                }
                break;
            case R.id.iv_2:
                selectPos = 1;
                selectImage();
                break;
            case R.id.iv_3:
                selectPos = 2;
                selectImage();
                break;
            case R.id.iv_delete_1:
                if (jsonMedicalRecordTemp.getImages().size() >= 1) {
                    selectPos = 0;
                    deleteImage(jsonMedicalRecordTemp.getImages().get(0));
                }
                break;
            case R.id.iv_delete_2:
                if (jsonMedicalRecordTemp.getImages().size() >= 2) {
                    selectPos = 1;
                    deleteImage(jsonMedicalRecordTemp.getImages().get(1));
                }
                break;
            case R.id.iv_delete_3:
                if (jsonMedicalRecordTemp.getImages().size() >= 3) {
                    selectPos = 2;
                    deleteImage(jsonMedicalRecordTemp.getImages().get(2));
                }
                break;

        }
    }

    private void selectImage1() {
        if (isExternalStoragePermissionAllowed()) {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestStoragePermission();
        }
    }


//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        File destination;
//        if (requestCode == PICK_IMAGE_GALLERY) {
//            if (resultCode == RESULT_OK) {
//
//                Uri picUri = data.getData();
//                Bitmap bitmap;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
//                    bitmap = rotateImageIfRequired(bitmap, picUri);
//                    bitmap = getResizedBitmap(bitmap, 500);
//                    if (-1 != selectPos) {
//                        imageViews[selectPos].setImageBitmap(bitmap);
//                        imageViewsDelete[selectPos].setVisibility(View.VISIBLE);
//                    }
//
//                    String convertedPath = new ImagePathReader().getPathFromUri(this, picUri);
//                    if (!TextUtils.isEmpty(convertedPath)) {
//                        progressDialog.show();
//                        progressDialog.setMessage("Uploading document");
//                        String type = getMimeType(this, picUri);
//                        File file = new File(convertedPath);
//                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
//                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
//                        medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }else if (requestCode == PICK_IMAGE_CAMERA) {
//            try {
//                if (getPickImageResultUri(data) != null) {
//                    Uri picUri = getPickImageResultUri(data);
//
//
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
////                    Uri selectedImage = data.getData();
////                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
////                    bitmap = rotateImageIfRequired(bitmap, selectedImage);
////                    bitmap = getResizedBitmap(bitmap, 500);
//
//
//                    //  String imgPath = photoFile.getAbsolutePath();
//                    if (-1 != selectPos) {
//                        imageViews[selectPos].setImageBitmap(bitmap);
//                        imageViewsDelete[selectPos].setVisibility(View.VISIBLE);
//                    }
//                }
////                if (!TextUtils.isEmpty(imgPath)) {
////                    progressDialog.show();
////                    progressDialog.setMessage("Uploading document");
////                    String type = getMimeType(this, selectedImage);
////                    File file = new File(imgPath);
////                    MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
////                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
////                    medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
////                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private String getMimeType(String filePath) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
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
    public void imageUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image upload", "" + jsonResponse);
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Document upload successfully! Change will be reflect after 5 min", Toast.LENGTH_LONG).show();
            if (null == jsonMedicalRecordTemp.getImages())
                jsonMedicalRecordTemp.setImages(new ArrayList<String>());
            jsonMedicalRecordTemp.getImages().add(jsonResponse.getData());
            jsonMedicalRecordResponse(jsonMedicalRecordTemp);
        } else {
            Toast.makeText(this, "Failed to update document", Toast.LENGTH_LONG).show();
            if (-1 != selectPos) {
                Picasso.with(this).load(R.drawable.profile_blue).into(imageViews[selectPos]);
                imageViewsDelete[selectPos].setVisibility(View.GONE);
            }
        }
        selectPos = -1;
    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            if (-1 != selectPos) {
                Picasso.with(this).load(R.drawable.profile_blue).into(imageViews[selectPos]);
                imageViewsDelete[selectPos].setVisibility(View.GONE);
                jsonMedicalRecordTemp.getImages().remove(selectPos);
                jsonMedicalRecordResponse(jsonMedicalRecordTemp);
            }
            selectPos = -1;
            Toast.makeText(this, "Document removed successfully!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to remove document", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void imageUploadError() {
        if (-1 != selectPos) {
            Picasso.with(this).load(R.drawable.profile_blue).into(imageViews[selectPos]);
            imageViewsDelete[selectPos].setVisibility(View.GONE);
            selectPos = -1;
        }
        dismissProgress();
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        jsonMedicalRecordTemp = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            if (null != jsonMedicalRecord.getImages() && jsonMedicalRecord.getImages().size() > 0) {

                for (int i = 0; i < jsonMedicalRecord.getImages().size(); i++) {
                    try {
                        if (!TextUtils.isEmpty(jsonMedicalRecord.getImages().get(i))) {
                            Picasso.with(this)
                                    .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + jsonMedicalRecord.getImages().get(i))
                                    .into(imageViews[i]);
                            imageViewsDelete[i].setVisibility(View.VISIBLE);

                        } else {
                            imageViewsDelete[i].setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        dismissProgress();
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

    private void deleteImage(final String imageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
        tvtitle.setText("Delete Image");
        tv_msg.setText("Do you want to delete it?");
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(recordReferenceId);
                jsonMedicalRecord.setImages(new ArrayList<String>() {
                    {
                        add(imageName);
                    }
                });
                progressDialog.show();
                progressDialog.setMessage("Deleting image...");
                medicalHistoryModel.removeImage(BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(), jsonMedicalRecord);


                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtils.checkPermission(DocumentUploadActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_GALLERY);
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICK_IMAGE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == PICK_IMAGE_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private void onCaptureImageResult(Intent data) {
        try {
            // Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = getRealPathFromURI(imageUri);
//            File destination = new File(Environment.getExternalStorageDirectory(),
//                    System.currentTimeMillis() + ".jpeg");
//            Log.e("File path:",path);
//            FileOutputStream fo;
//            try {
//                destination.createNewFile();
//                fo = new FileOutputStream(destination);
//                fo.write(bytes.toByteArray());
//                fo.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if (-1 != selectPos) {
                imageViews[selectPos].setImageBitmap(thumbnail);
                imageViewsDelete[selectPos].setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(path)) {
                progressDialog.show();
                progressDialog.setMessage("Uploading document");
                String type = getMimeType(path);
                File file = new File(path);
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
            }
//            if (!TextUtils.isEmpty(destination.getAbsolutePath())) {
//                progressDialog.show();
//                progressDialog.setMessage("Uploading document");
//                String type = getMimeType(destination.getAbsolutePath());
//                File file = new File(destination.getAbsolutePath());
//                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
//                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
//                medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        imageUri = null;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                try {
                    String convertedPath = new FileUtils().getFilePath(this, data.getData());
                    Log.e("file path temp:", convertedPath);

                    if (-1 != selectPos) {
                        imageViews[selectPos].setImageBitmap(bm);
                        imageViewsDelete[selectPos].setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(convertedPath)) {
                            progressDialog.show();
                            progressDialog.setMessage("Uploading document");
                            String type = getMimeType(convertedPath);
                            Log.e("File type :", type);
                            File file = new File(convertedPath);
                            MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                            medicalHistoryModel.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
