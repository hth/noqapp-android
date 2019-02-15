package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.body.store.LabFile;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.FileUtils;
import com.noqapp.android.merchant.utils.PermissionUtils;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.ImageUploadAdapter;
import com.noqapp.android.merchant.views.interfaces.LabFilePresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderModel;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, ImageUploadPresenter, LabFilePresenter, ImageUploadAdapter.OnItemClickListener {

    private final int PICK_IMAGE_CAMERA = 101;
    private final int PICK_IMAGE_GALLERY = 102;
    private final int PERMISSION_REQUEST_CAMERA = 103;
    private String transactionId;
    private PurchaseOrderModel purchaseOrderModel;
    private ProgressDialog progressDialog;
    private LabFile labFileTemp;
    private String userChoosenTask;
    private Uri imageUri;
    private FrameLayout frame_image;
    private ImageView iv_large;
    private RecyclerView rcv_photo;
    private int selectPos;
    private FloatingActionButton fab_add_image;
    private int columnCount = 2;
    private boolean isExpandScreenOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        purchaseOrderModel = new PurchaseOrderModel();
        purchaseOrderModel.setImageUploadPresenter(this);
        transactionId = getIntent().getStringExtra("transactionId");
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
                if (null != labFileTemp && null != labFileTemp.getFiles()) {
                    if (labFileTemp.getFiles().size() < Constants.MAX_IMAGE_UPLOAD_LIMIT) {
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
        purchaseOrderModel.setLabFilePresenter(this);
        purchaseOrderModel.showAttachment(BaseLaunchActivity.getDeviceID(),
                LaunchActivity.getLaunchActivity().getEmail(),
                LaunchActivity.getLaunchActivity().getAuth(), new LabFile().setTransactionId(transactionId));

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
            Toast.makeText(this, "Document upload successfully! Change will be reflect after 5 min", Toast.LENGTH_LONG).show();
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
                labFileTemp.getFiles().remove(selectPos);
                showAttachmentResponse(labFileTemp);
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
                try {
                    selectPos = labFileTemp.getFiles().indexOf(imageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LabFile labFile = new LabFile().
                        setTransactionId(transactionId)
                        .setDeleteAttachment(imageName);
                progressDialog.show();
                progressDialog.setMessage("Deleting image...");
                purchaseOrderModel.removeAttachment(BaseLaunchActivity.getDeviceID(),
                        LaunchActivity.getLaunchActivity().getEmail(),
                        LaunchActivity.getLaunchActivity().getAuth(), labFile);


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
                progressDialog.show();
                progressDialog.setMessage("Uploading document");
                String type = getMimeType(path);
                File file = new File(path);
                MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);
                purchaseOrderModel.addAttachment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
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
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                try {
                    String convertedPath = new FileUtils().getFilePath(this, data.getData());
                    Log.e("file path temp:", convertedPath);
                    if (!TextUtils.isEmpty(convertedPath)) {
                        progressDialog.show();
                        progressDialog.setMessage("Uploading document");
                        String type = getMimeType(convertedPath);
                        //  Log.e("File type :", type);
                        File file = new File(convertedPath);
                        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), transactionId);
                        purchaseOrderModel.addAttachment(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void imageEnlargeClick(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(DocumentUploadActivity.this)
                    .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + labFileTemp.getRecordReferenceId() + "/" + imageUrl)
                    .into(iv_large);
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
        if(isExpandScreenOpen){
            frame_image.setVisibility(View.GONE);
            isExpandScreenOpen = false;
        }else {
            super.onBackPressed();
        }
    }
}
