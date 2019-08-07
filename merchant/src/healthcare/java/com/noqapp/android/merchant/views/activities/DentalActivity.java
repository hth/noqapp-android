package com.noqapp.android.merchant.views.activities;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.ImageUploadPresenter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.MedicalHistoryApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.UserUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DentalActivity extends BaseActivity implements View.OnClickListener,
        ImageUploadPresenter {
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    public final int[] DENTAl_DRAWABLES = new int[32];
    public final ImageView[] imageViews = new ImageView[32];
    private LinearLayout ll_canvas;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    private Button btn_save_upload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dental);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(v -> onBackPressed());
        tv_toolbar_title.setText("Dental Chart");
        ll_canvas = findViewById(R.id.ll_canvas);
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        for (int i = 0; i < imageViews.length; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("iv_" + (i + 1)), "id", this.getPackageName());
            imageViews[i] = findViewById(id);
            DENTAl_DRAWABLES[i] = R.drawable.tooth;
            imageViews[i].setBackgroundResource(DENTAl_DRAWABLES[i]);
            imageViews[i].setOnClickListener(this);
        }
        btn_save_upload = findViewById(R.id.btn_save_upload);
        btn_save_upload.setOnClickListener(v -> {
            getCaptureAndUploadBitmap();
        });
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DentalActivity.this);
        LayoutInflater inflater = LayoutInflater.from(DentalActivity.this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_tooth_issue, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        ImageView[] imageViewsDialog = new ImageView[8];
        int[] DENTAl_DRAWABLES_DIALOG = new int[8];
        for (int i = 0; i < imageViewsDialog.length; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("iv_dialog_" + (i + 1)), "id", this.getPackageName());
            imageViewsDialog[i] = customDialogView.findViewById(id);
            DENTAl_DRAWABLES_DIALOG[i] = R.drawable.tooth_decay;
            imageViewsDialog[i].setBackgroundResource(DENTAl_DRAWABLES_DIALOG[i]);
            imageViewsDialog[i].setOnClickListener(v13 -> {
                mAlertDialog.dismiss();
                v.setBackgroundResource(R.drawable.tooth_decay);
            });
        }

        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);
        Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v1 -> mAlertDialog.dismiss());
        btn_yes.setOnClickListener(v12 -> mAlertDialog.dismiss());
        mAlertDialog.show();

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
            new CustomToast().showToast(this, "Document removed successfully!");
        } else {
            new CustomToast().showToast(this, "Failed to remove document");
        }
    }

    @Override
    public void imageUploadError() {
        dismissProgress();
    }

    private void getCaptureAndUploadBitmap() {
        View u = findViewById(R.id.scroll);
        u.setDrawingCacheEnabled(true);
        ScrollView z = (ScrollView) findViewById(R.id.scroll);
        int x = z.getChildAt(0).getTop();
        int y = z.getChildAt(0).getLeft();
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();
        u.layout(0, 0, totalWidth, totalHeight);
        u.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(u.getDrawingCache());
        u.setDrawingCacheEnabled(false);

        //Save bitmap
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "NoQueue");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String extr = Environment.getExternalStorageDirectory().toString() + File.separator + "NoQueue";
        String fileName = new SimpleDateFormat("yyyyMMddhhmm'_report.jpg'").format(new Date());
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "Screen", "screen");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        u.layout(x, y, totalWidth, totalHeight);
        String type = getMimeType(Uri.fromFile(myPath));
        MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", myPath.getName(), RequestBody.create(MediaType.parse(type), myPath));
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonMedicalRecord.getRecordReferenceId());
        medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
        showProgress();
        setProgressMessage("Uploading document");
    }
}