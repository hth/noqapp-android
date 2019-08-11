package com.noqapp.android.merchant.views.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.medical.JsonMedicalRecord;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.MedicalFilesDB;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.views.adapters.ToothAdapter;
import com.noqapp.android.merchant.views.pojos.ToothInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DentalActivity extends BaseActivity {
    public JsonQueuedPerson jsonQueuedPerson;
    public JsonMedicalRecord jsonMedicalRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dental);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(v -> onBackPressed());
        tv_toolbar_title.setText("Dental Chart");
        jsonQueuedPerson = (JsonQueuedPerson) getIntent().getSerializableExtra("data");
        jsonMedicalRecord = (JsonMedicalRecord) getIntent().getSerializableExtra("jsonMedicalRecord");
        RecyclerView rcv_tooth = findViewById(R.id.rcv_tooth);
        rcv_tooth.setLayoutManager(new GridLayoutManager(this, 16));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());

        int imageFilePathTop = R.drawable.tooth_o_2_1;
        List<Integer> drawables = getFrontAllViews();
        List<ToothInfo> toothInfos = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            ToothInfo toothInfo = new ToothInfo();
            toothInfo.setToothNumber(i + 1);
            toothInfo.setToothFrontView(drawables.get(i));
            toothInfo.setToothTopView(imageFilePathTop);
            toothInfo.setFrontViewDrawables(getFrontOptionViews());
            toothInfo.setTopViewDrawables(getTopOptionViews());
            toothInfos.add(toothInfo);
        }
        ToothAdapter toothAdapter = new ToothAdapter(toothInfos, this);
        rcv_tooth.setAdapter(toothAdapter);
        Button btn_save_upload = findViewById(R.id.btn_save_upload);
        btn_save_upload.setOnClickListener(v -> {
            getCaptureAndUploadBitmap();
        });
    }

    private void getCaptureAndUploadBitmap() {
        try {
            View u = findViewById(R.id.scroll);
            u.setDrawingCacheEnabled(true);
            ScrollView z = findViewById(R.id.scroll);
            z.setBackgroundColor(getResources().getColor(R.color.white));
            int x = z.getChildAt(0).getTop();
            int y = z.getChildAt(0).getLeft();
            int totalHeight = z.getChildAt(0).getHeight();
            int totalWidth = z.getChildAt(0).getWidth();
            u.layout(0, 0, totalWidth, totalHeight);
            u.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(u.getDrawingCache());
            u.setDrawingCacheEnabled(false);

            //Save bitmap
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "NoQueue");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String extr = Environment.getExternalStorageDirectory().toString() + File.separator + "NoQueue";
          //  String fileName = new SimpleDateFormat("yyyyMMddhhmm'_report.jpg'").format(new Date());
            String fileName = new SimpleDateFormat("'NoQueue_" + jsonQueuedPerson.getCustomerName() + "_'yyyyMMddhhmm'.jpg'", Locale.getDefault()).format(new Date());

            File myPath = new File(extr, fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Screen", "screen");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != fos)
                    fos.flush();
                if (null != bitmap) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            u.layout(x, y, totalWidth, totalHeight);
            if (myPath.exists()) {
                MedicalFilesDB.insertMedicalFile(jsonMedicalRecord.getRecordReferenceId(), myPath.getAbsolutePath());
                new CustomToast().showToast(this, "File saved to SD Card.It will upload with case history");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Integer> getTopOptionViews() {
        List<Integer> drawables = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_o_2_" + (i + 1)), "drawable", this.getPackageName());
            drawables.add(id);
        }
        return drawables;
    }

    private List<Integer> getFrontOptionViews() {
        List<Integer> drawables = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_o_1_" + (i + 1)), "drawable", this.getPackageName());
            drawables.add(id);
        }
        return drawables;
    }

    private List<Integer> getFrontAllViews() {
        List<Integer> drawables = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_1_" + (i + 1)), "drawable", this.getPackageName());
            drawables.add(id);
        }
        return drawables;
    }
}