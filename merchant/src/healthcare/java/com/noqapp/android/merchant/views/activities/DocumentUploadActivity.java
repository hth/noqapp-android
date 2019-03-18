package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.FileUtils;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.JsonMedicalRecordPresenter;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.FileUtilsPdf;
import com.noqapp.android.merchant.utils.PermissionUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ImageUploadAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadPresenter, JsonMedicalRecordPresenter, ImageUploadAdapter.OnItemClickListener {

    private static final int PICK_IMAGE_CAMERA = 101;
    private static final int PICK_IMAGE_GALLERY = 102;
    private static final int PICK_PDF = 103;
    private static final int PERMISSION_REQUEST_CAMERA = 103;
    private String recordReferenceId;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogImage;
    private JsonMedicalRecord jsonMedicalRecordTemp;
    private String userChoosenTask;
    private Uri imageUri;
    private FrameLayout frame_image;
    private ImageView iv_large;
    private RecyclerView rcv_photo;
    private int selectPos;
    private FloatingActionButton fab_add_image;
    private boolean isExpandScreenOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int columnCount = 1;
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            columnCount = 3;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            columnCount = 2;
        }
        super.onCreate(savedInstanceState);
        initProgress();
        setContentView(R.layout.activity_upload_image);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Upload Documents");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        recordReferenceId = getIntent().getStringExtra("recordReferenceId");
        String codeQR = getIntent().getStringExtra("qCodeQR");
        rcv_photo = findViewById(R.id.rcv_photo);
        rcv_photo.setLayoutManager(new GridLayoutManager(this, columnCount));
        frame_image = findViewById(R.id.frame_image);
        iv_large = findViewById(R.id.iv_large);
        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        fab_add_image = findViewById(R.id.fab_add_image);
        fab_add_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != jsonMedicalRecordTemp && null != jsonMedicalRecordTemp.getImages()) {
                    if (jsonMedicalRecordTemp.getImages().size() < Constants.MAX_IMAGE_UPLOAD_LIMIT) {
                        selectImage();
                    } else {
                        Toast.makeText(DocumentUploadActivity.this, "Maximum " + Constants.MAX_IMAGE_UPLOAD_LIMIT + " image allowed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectImage();
                }
            }
        });

        progressDialog.show();
        progressDialog.setMessage("Fetching documents...");
        medicalHistoryApiCalls.setJsonMedicalRecordPresenter(this);
        medicalHistoryApiCalls.existsMedicalRecord(BaseLaunchActivity.getDeviceID(),
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
            case R.id.iv_close:
                frame_image.setVisibility(View.GONE);
                iv_large.setBackgroundResource(0);
                fab_add_image.show();
                isExpandScreenOpen = false;
                break;

        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
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
        }

    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            if (-1 != selectPos) {
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
        dismissProgress();
    }

    @Override
    public void jsonMedicalRecordResponse(JsonMedicalRecord jsonMedicalRecord) {
        jsonMedicalRecordTemp = jsonMedicalRecord;
        if (null != jsonMedicalRecord) {
            Log.e("data", jsonMedicalRecord.toString());
            if (null != jsonMedicalRecord.getImages()) {
                ImageUploadAdapter imageUploadAdapter = new ImageUploadAdapter(jsonMedicalRecord.getImages(), this, recordReferenceId, this);
                rcv_photo.setAdapter(imageUploadAdapter);
            }
        }
        dismissProgress();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading data...");


        progressDialogImage = new ProgressDialog(this);
        progressDialogImage.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialogImage.setIndeterminate(true);
        progressDialogImage.setCancelable(true);
        // progressDialogImage.show();
        progressDialogImage.setContentView(R.layout.progress_lay);

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
                try {
                    selectPos = jsonMedicalRecordTemp.getImages().indexOf(imageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord.setRecordReferenceId(recordReferenceId);
                jsonMedicalRecord.setImages(new ArrayList<String>() {
                    {
                        add(imageName);
                    }
                });
                progressDialog.show();
                progressDialog.setMessage("Deleting image...");
                medicalHistoryApiCalls.removeImage(BaseLaunchActivity.getDeviceID(),
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
            case PermissionUtils.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;

            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Choose from Library", //"Select Pdf",
                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtils.checkPermission(DocumentUploadActivity.this);

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Select Pdf")) {
                    userChoosenTask = "Select Pdf";
                    if (result)
                        selectPdfIntent();

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

    private void selectPdfIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF);
    }

    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(DocumentUploadActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(DocumentUploadActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICK_IMAGE_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == PICK_IMAGE_CAMERA) {
                onCaptureImageResult(data);
            } else if (requestCode == PICK_PDF) {
                onCapturePdfResult(data);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void onCapturePdfResult(Intent data) {
        try {
            Uri uri = data.getData();
            String path = FileUtilsPdf.getRealPath(this, uri);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String type = getMimeType(uri);
                String displayName = file.getName();
                //Toast.makeText(this, "Pdf Name is : " + displayName, Toast.LENGTH_LONG).show();
                progressDialog.show();
                progressDialog.setMessage("Uploading document");
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", displayName, RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
            }

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Log.e("pdf file upload failed",e.getMessage());
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            // Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = getRealPathFromURI(imageUri);
            if (!TextUtils.isEmpty(path)) {
                progressDialog.show();
                progressDialog.setMessage("Uploading document");
                String type = getMimeType(imageUri);
                File file = new File(path);
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageUri = null;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                // Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                try {
                    String convertedPath = new FileUtils().getFilePath(this, data.getData());
                    Log.e("file path temp:", convertedPath);
                    if (!TextUtils.isEmpty(convertedPath)) {
                        progressDialog.show();
                        progressDialog.setMessage("Uploading document");
                        String type = getMimeType(data.getData());
                        //  Log.e("File type :", type);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), recordReferenceId);
                        medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void imageEnlargeClick(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            progressDialogImage.show();
            progressDialogImage.setContentView(R.layout.progress_lay);
            Picasso.with(DocumentUploadActivity.this)
                    .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + imageUrl)
                    .into(iv_large, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressDialogImage.dismiss();
                        }

                        @Override
                        public void onError() {
                            progressDialogImage.dismiss();
                        }
                    });
            frame_image.setVisibility(View.VISIBLE);
            isExpandScreenOpen = true;
        } else {
            Toast.makeText(this, "Image not available", Toast.LENGTH_LONG).show();
            frame_image.setVisibility(View.GONE);
            isExpandScreenOpen = false;
        }
        fab_add_image.hide();
    }

    @Override
    public void imageDeleteClick(String imageUrl) {
        deleteImage(imageUrl);
    }


    @Override
    public void onBackPressed() {
        if (isExpandScreenOpen) {
            frame_image.setVisibility(View.GONE);
            isExpandScreenOpen = false;
        } else {
            super.onBackPressed();
        }
    }
}
