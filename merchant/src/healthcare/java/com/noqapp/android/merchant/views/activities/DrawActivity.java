package com.noqapp.android.merchant.views.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.GridView;
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
import com.noqapp.android.merchant.views.adapters.ColorPaletteAdapter;
import com.noqapp.android.merchant.views.customviews.DrawingView;

import java.io.File;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DrawActivity extends BaseActivity implements View.OnClickListener,
        ColorPaletteAdapter.OnColorSelectedListener, ImageUploadPresenter {
    private DrawingView drawingView;
    private Button btn_select_picture, btn_save_picture, btn_select_color;
    private Bitmap alteredBitmap;
    private int pointerColor = Color.GREEN;
    private MedicalHistoryApiCalls medicalHistoryApiCalls;
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    public final int[] MATERIAL_COLORS = {
            0xFFF44336, // RED 500
            0xFFE91E63, // PINK 500
            0xFFFF2C93, // LIGHT PINK 500
            0xFF9C27B0, // PURPLE 500
            0xFF673AB7, // DEEP PURPLE 500
            0xFF3F51B5, // INDIGO 500
            0xFF2196F3, // BLUE 500
            0xFF03A9F4, // LIGHT BLUE 500
            0xFF00BCD4, // CYAN 500
            0xFF009688, // TEAL 500
            0xFF4CAF50, // GREEN 500
            0xFF8BC34A, // LIGHT GREEN 500
            0xFFCDDC39, // LIME 500
            0xFFFFEB3B, // YELLOW 500
            0xFFFFC107, // AMBER 500
            0xFFFF9800, // ORANGE 500
            0xFF795548, // BROWN 500
            0xFF607D8B, // BLUE GREY 500
            0xFF9E9E9E, // GREY 500
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(v -> onBackPressed());
        tv_toolbar_title.setText("Select and draw");
        medicalHistoryApiCalls = new MedicalHistoryApiCalls(this);
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        drawingView = findViewById(R.id.drawingView);
        btn_select_picture = findViewById(R.id.btn_select_picture);
        btn_save_picture = findViewById(R.id.btn_save_picture);
        btn_select_color = findViewById(R.id.btn_select_color);
        btn_save_picture.setOnClickListener(this);
        btn_select_picture.setOnClickListener(this);
        btn_select_color.setOnClickListener(this);
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


    public void onClick(View v) {
        if (v == btn_select_picture) {
            Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        } else if (v == btn_save_picture) {
            if (alteredBitmap != null) {
                try {
                    ContentValues contentValues = new ContentValues(3);
                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw On Me");
                    Uri imageFileUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
                    alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageFileOS);
                    String type = getMimeType(imageFileUri);
                    String path = getRealPathFromURI(imageFileUri);
                    File file = new File(path);
                    MultipartBody.Part profileImageFile = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse(type), file));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonMedicalRecord.getRecordReferenceId());
                    medicalHistoryApiCalls.appendImage(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), profileImageFile, requestBody);
                    showProgress();
                    setProgressMessage("Uploading document");
                } catch (Exception e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
        } else if (v == btn_select_color) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
            LayoutInflater inflater = LayoutInflater.from(DrawActivity.this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_color_picker, null, false);
            TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
            GridView gridView = customDialogView.findViewById(R.id.grid_view_color);
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mAlertDialog.setCanceledOnTouchOutside(false);
            mAlertDialog.setCancelable(false);
            ColorPaletteAdapter adapter = new ColorPaletteAdapter(DrawActivity.this, MATERIAL_COLORS);
            gridView.setAdapter(adapter);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            Button btn_no = customDialogView.findViewById(R.id.btn_no);
            btn_no.setOnClickListener(v1 -> mAlertDialog.dismiss());
            btn_yes.setOnClickListener(v12 -> mAlertDialog.dismiss());
            try {
                mAlertDialog.show();
            } catch (Exception e) {
                // WindowManager$BadTokenException will be caught and the app would not display
                // the 'Force Close' message
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();
            try {
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                bmpFactoryOptions.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                        imageFileUri), null, bmpFactoryOptions);
                alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getHeight(), bmp.getConfig());
                drawingView.setNewImage(alteredBitmap, bmp, pointerColor);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }


    @Override
    public void onColorSelected(int color) {
        pointerColor = color;
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

    }
}