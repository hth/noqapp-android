package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.MedicalFilesDB;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.views.adapters.ColorPaletteAdapter;
import com.noqapp.android.merchant.views.customviews.DrawViewUndoRedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrawActivity extends BaseActivity implements View.OnClickListener,
        ColorPaletteAdapter.OnColorSelectedListener {
    private Button btn_select_picture, btn_save_picture;
    private ImageView btn_select_color, btn_undo, btn_redo;
    private Bitmap alteredBitmap;
    private int pointerColor = 0xFFF44336;
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;
    private DrawViewUndoRedo drawViewUndoRedo;
    private LinearLayout drawingViewLinear;
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
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        initActionsViews(true);
        tv_toolbar_title.setText("Select and draw");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        btn_select_picture = findViewById(R.id.btn_select_picture);
        btn_save_picture = findViewById(R.id.btn_save_picture);
        btn_select_color = findViewById(R.id.btn_select_color);
        btn_undo = findViewById(R.id.btn_undo);
        btn_redo = findViewById(R.id.btn_redo);
        btn_save_picture.setOnClickListener(this);
        btn_select_picture.setOnClickListener(this);
        btn_select_color.setOnClickListener(this);
        btn_undo.setOnClickListener(this);
        btn_redo.setOnClickListener(this);
        drawingViewLinear = findViewById(R.id.drawingViewLinear);
    }


    public void onClick(View v) {
        if (v == btn_select_picture) {
            Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        } else if (v == btn_save_picture) {
            if (alteredBitmap != null) {
                try {
                    drawViewUndoRedo.setDrawingCacheEnabled(true);
                    drawViewUndoRedo.buildDrawingCache(true);
                    Bitmap b = Bitmap.createBitmap(drawViewUndoRedo.getDrawingCache());
                    drawViewUndoRedo.setDrawingCacheEnabled(false);

                    //Save bitmap
                    String extr = Environment.getExternalStorageDirectory().toString() + File.separator + "NoQueue";
                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "NoQueue");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    //  String fileName = new SimpleDateFormat("yyyyMMddhhmm'_draw_report.jpg'").format(new Date());
                    String fileName = new SimpleDateFormat("'NoQueue_" + jsonQueuedPerson.getCustomerName() + "_'yyyyMMddhhmm'.jpg'", Locale.getDefault()).format(new Date());

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
                    if (myPath.exists()) {
                        MedicalFilesDB.insertMedicalFile(jsonMedicalRecord.getRecordReferenceId(), myPath.getAbsolutePath());
                        new CustomToast().showToast(this, "File saved to SD Card.It will upload with case history");
                        drawingViewLinear.removeAllViews();
                        drawViewUndoRedo = null;
                    }
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
            mAlertDialog.show();

        } else if (v == btn_redo) {
            if (null != drawViewUndoRedo) {
                drawViewUndoRedo.onClickRedo();
            }
        } else if (v == btn_undo) {
            if (null != drawViewUndoRedo) {
                drawViewUndoRedo.onClickUndo();
            }
        }
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


                drawViewUndoRedo = new DrawViewUndoRedo(this, bmp, pointerColor);
                drawingViewLinear.removeAllViews();
                drawingViewLinear.addView(drawViewUndoRedo);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }


    @Override
    public void onColorSelected(int color) {
        pointerColor = color;
        if (null != drawViewUndoRedo) {
            drawViewUndoRedo.setPointerColor(pointerColor);
        }
    }

}