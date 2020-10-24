package com.noqapp.android.merchant.views.activities;

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
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.common.utils.FileUtils;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.body.merchant.LabFile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.utils.PermissionUtils;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ImageUploadAdapter;
import com.noqapp.android.merchant.views.interfaces.LabFilePresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class DocumentUploadActivity extends BaseActivity implements View.OnClickListener,
        ImageUploadPresenter, LabFilePresenter, ImageUploadAdapter.OnItemClickListener {

    private final int PICK_IMAGE_CAMERA = 101;
    private final int PICK_IMAGE_GALLERY = 102;
    private final int PERMISSION_REQUEST_CAMERA = 103;
    private String transactionId;
    private PurchaseOrderApiCalls purchaseOrderApiCalls;
    private ProgressDialog progressDialogImage;
    private LabFile labFileTemp;
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
        if (LaunchActivity.isTablet) {
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
        actionbarBack.setOnClickListener(v -> finish());
        purchaseOrderApiCalls = new PurchaseOrderApiCalls();
        purchaseOrderApiCalls.setImageUploadPresenter(this);
        transactionId = getIntent().getStringExtra("transactionId");
        String codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        setProgressMessage("Loading data...");
        rcv_photo = findViewById(R.id.rcv_photo);
        rcv_photo.setLayoutManager(new GridLayoutManager(this, columnCount));
        frame_image = findViewById(R.id.frame_image);
        iv_large = findViewById(R.id.iv_large);
        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        fab_add_image = findViewById(R.id.fab_add_image);
        fab_add_image.setOnClickListener(v -> {
            if (null != labFileTemp && null != labFileTemp.getFiles()) {
                if (labFileTemp.getFiles().size() < Constants.MAX_IMAGE_UPLOAD_LIMIT) {
                    selectImage();
                } else {
                    new CustomToast().showToast(DocumentUploadActivity.this, "Maximum " + Constants.MAX_IMAGE_UPLOAD_LIMIT + " image allowed");
                }
            } else {
                selectImage();
            }
        });

        showProgress();
        setProgressMessage("Fetching documents...");
        purchaseOrderApiCalls.setLabFilePresenter(this);
        purchaseOrderApiCalls.showAttachment(AppInitialize.getDeviceID(),
                AppInitialize.getEmail(), AppInitialize.getAuth(), new LabFile().setTransactionId(transactionId));

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
            if (eej.getSystemErrorCode().equals(MobileSystemErrorCodeEnum.MEDICAL_RECORD_DOES_NOT_EXISTS.getCode())) {
                new CustomToast().showToast(this, "Please create medical record first to upload document");
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

    private String getMimeType(String filePath) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }


    @Override
    public void imageUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image upload", "" + jsonResponse);
        if (Constants.SUCCESS == jsonResponse.getResponse()) {

            if (null == labFileTemp.getFiles())
                labFileTemp.setFiles(new ArrayList<String>());
            labFileTemp.getFiles().add(jsonResponse.getData());
            showAttachmentResponse(labFileTemp);
            new CustomToast().showToast(this, "Document upload successfully! Change will be reflect after 5 min");
        } else {
            new CustomToast().showToast(this, "Failed to update document");
        }

    }

    @Override
    public void imageRemoveResponse(JsonResponse jsonResponse) {
        dismissProgress();
        Log.v("Image removed", "" + jsonResponse.getResponse());
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            if (-1 != selectPos) {
                labFileTemp.getFiles().remove(selectPos);
                showAttachmentResponse(labFileTemp);
            }
            selectPos = -1;
            new CustomToast().showToast(this, "Document removed successfully!");
        } else {
            new CustomToast().showToast(this, "Failed to remove document");
        }
    }

    @Override
    public void imageUploadError() {
        dismissProgress();
    }


    private void initProgress() {
        progressDialogImage = new ProgressDialog(this);
        progressDialogImage.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialogImage.setIndeterminate(true);
        progressDialogImage.setCancelable(true);
        // progressDialogImage.show();
        progressDialogImage.setContentView(R.layout.progress_lay);
    }

    private void deleteImage(final String imageName) {
        ShowCustomDialog showDialog = new ShowCustomDialog(this);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                try {
                    selectPos = labFileTemp.getFiles().indexOf(imageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LabFile labFile = new LabFile().
                        setTransactionId(transactionId)
                        .setDeleteAttachment(imageName);
                showProgress();
                setProgressMessage("Deleting image...");
                purchaseOrderApiCalls.removeAttachment(AppInitialize.getDeviceID(),
                        AppInitialize.getEmail(), AppInitialize.getAuth(), labFile);

            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog("Delete Image", "Do you want to delete it?");
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
        final CharSequence[] items = {"Camera", "Choose from Library",
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

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = getRealPathFromURI(imageUri);
            if (!TextUtils.isEmpty(path)) {
                showProgress();
                setProgressMessage("Uploading document");
                String type = getMimeType(path);
                File file = new File(path);
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);
                purchaseOrderApiCalls.addAttachment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
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
                        showProgress();
                        setProgressMessage("Uploading document");
                        String type = getMimeType(convertedPath);
                        //  Log.e("File type :", type);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);
                        purchaseOrderApiCalls.addAttachment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
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
            Picasso.get()
                    .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + labFileTemp.getRecordReferenceId() + "/" + imageUrl)
                    .into(iv_large, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressDialogImage.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            progressDialogImage.dismiss();
                        }


                    });
            frame_image.setVisibility(View.VISIBLE);
            isExpandScreenOpen = true;
        } else {
            new CustomToast().showToast(this, "Image not available");
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
    public void showAttachmentResponse(LabFile labFile) {
        labFileTemp = labFile;
        if (null != labFileTemp) {
            Log.e("data", labFileTemp.toString());
            if (null != labFileTemp.getFiles()) {
                ImageUploadAdapter imageUploadAdapter = new ImageUploadAdapter(labFileTemp.getFiles(), this, labFileTemp.getRecordReferenceId(), this);
                rcv_photo.setAdapter(imageUploadAdapter);
            }
        }
        dismissProgress();
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
