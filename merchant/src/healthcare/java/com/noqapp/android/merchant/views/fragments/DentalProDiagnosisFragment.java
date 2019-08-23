package com.noqapp.android.merchant.views.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.medical.DentalOptionEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.database.utils.MedicalFilesDB;
import com.noqapp.android.merchant.views.activities.MedicalCaseActivity;
import com.noqapp.android.merchant.views.adapters.ToothAdapter;
import com.noqapp.android.merchant.views.pojos.ToothInfo;
import com.noqapp.android.merchant.views.pojos.ToothProcedure;
import com.noqapp.android.merchant.views.utils.MedicalDataStatic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DentalProDiagnosisFragment extends BaseFragment {
    private View view;
    private ToothAdapter toothAdapter;
    private RecyclerView rcv_tooth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.frag_dental, container, false);
        rcv_tooth = view.findViewById(R.id.rcv_tooth);
        rcv_tooth.setLayoutManager(new GridLayoutManager(getActivity(), 16));
        rcv_tooth.setItemAnimator(new DefaultItemAnimator());
        Button btn_save_upload = view.findViewById(R.id.btn_save_upload);
        btn_save_upload.setOnClickListener(v -> {
            getCaptureAndUploadBitmap();
        });
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToothProcedure imageFilePathTop = new ToothProcedure(R.drawable.tooth_o_2_1,DentalOptionEnum.NOR.getDescription());
        List<ToothProcedure> drawables = getFrontAllViews();
        List<ToothInfo> toothInfos = new ArrayList<>();
        List<String> toothNumbers = MedicalDataStatic.convertDataObjListAsStringList(MedicalDataStatic.Dental.getDentalDiagnosisList());
        for (int i = 0; i < 32; i++) {
            ToothInfo toothInfo = new ToothInfo();
            toothInfo.setToothNumber(Integer.parseInt(toothNumbers.get(i)));
            toothInfo.setToothFrontView(drawables.get(i));
            toothInfo.setToothDefaultFrontView(toothInfo.getToothFrontView());
            toothInfo.setToothTopView(imageFilePathTop);
            toothInfo.setToothDefaultTopView(toothInfo.getToothTopView());
            toothInfo.setFrontViewDrawables(getFrontOptionViews());
            toothInfo.setTopViewDrawables(getTopOptionViews());
            toothInfos.add(toothInfo);
        }
        toothAdapter = new ToothAdapter(toothInfos, getActivity());
        rcv_tooth.setAdapter(toothAdapter);
        try {
            if (null != MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord() && !TextUtils.isEmpty(
                    MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getProvisionalDifferentialDiagnosis())) {
                String temp = MedicalCaseActivity.getMedicalCaseActivity().getJsonMedicalRecord().getProvisionalDifferentialDiagnosis();
                toothAdapter.updateToothObj(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCaptureAndUploadBitmap() {
        try {
            View u = view.findViewById(R.id.scroll);
            u.setDrawingCacheEnabled(true);
            ScrollView z = view.findViewById(R.id.scroll);
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
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "NoQueue");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String extr = Environment.getExternalStorageDirectory().toString() + File.separator + "NoQueue";
            //  String fileName = new SimpleDateFormat("yyyyMMddhhmm'_report.jpg'").format(new Date());
            String fileName = new SimpleDateFormat("'NoQueue_" + MedicalCaseActivity.getMedicalCaseActivity().jsonQueuedPerson.getCustomerName() + "_'yyyyMMddhhmm'.jpg'", Locale.getDefault()).format(new Date());

            File myPath = new File(extr, fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Screen", "screen");
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
                MedicalFilesDB.insertMedicalFile(MedicalCaseActivity.getMedicalCaseActivity().jsonMedicalRecord.getRecordReferenceId(), myPath.getAbsolutePath());
                new CustomToast().showToast(getActivity(), "Saved image. This image will be available in case history.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<ToothProcedure> getTopOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_o_2_" + (i + 1)), "drawable", getActivity().getPackageName());
            drawables.add(new ToothProcedure(id, DentalOptionEnum.CAV.getDescription()));
        }
        return drawables;
    }

    private List<ToothProcedure> getFrontOptionViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_o_1_" + (i + 1)), "drawable", getActivity().getPackageName());
            drawables.add(new ToothProcedure(id, DentalOptionEnum.IMP.getDescription()));
        }
        return drawables;
    }

    private List<ToothProcedure> getFrontAllViews() {
        List<ToothProcedure> drawables = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            int id = this.getResources().getIdentifier(String.valueOf("tooth_1_" + (i + 1)), "drawable", getActivity().getPackageName());
            drawables.add(new ToothProcedure(id, DentalOptionEnum.NOR.getDescription()));
        }
        return drawables;
    }

    public void saveData() {
        // save to dental anatomy
        MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomy(toothAdapter.getSelectedData());
        if(!TextUtils.isEmpty(toothAdapter.getSelectedData())){
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomyFilled(true);
        }else{
            MedicalCaseActivity.getMedicalCaseActivity().getCaseHistory().setDentalAnatomyFilled(false);
        }
    }
}