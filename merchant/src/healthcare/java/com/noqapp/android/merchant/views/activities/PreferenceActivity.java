package com.noqapp.android.merchant.views.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileApiCalls;
import com.noqapp.android.merchant.model.MasterLabApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.fragments.LocalPreferenceHCServiceFragment;
import com.noqapp.android.merchant.views.fragments.MedicineFragment;
import com.noqapp.android.merchant.views.fragments.PreferenceHCServiceFragment;
import com.noqapp.android.merchant.views.fragments.PreferredSettingCategoryList;
import com.noqapp.android.merchant.views.interfaces.FilePresenter;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;


public class PreferenceActivity extends BaseActivity implements FilePresenter, IntellisensePresenter,
        MenuHeaderAdapter.OnItemClickListener, PreferredSettingCategoryList.CategoryListener {
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static PreferenceActivity preferenceActivity;
    private ArrayList<JsonMasterLab> masterData = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataSono = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataScan = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataMri = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataXray = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataPath = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataSpec = new ArrayList<>();
    private PreferenceHCServiceFragment preferenceSonoFragment, preferencePathFragment, preferenceMriFragment,
            preferenceScanFragment, preferenceXrayFragment, preferenceSpecFragment;
    private LocalPreferenceHCServiceFragment preSymptomsFragment, prefProDiagnosisFragment,
            prefDiagnosisFragment, preInstructionFragment,preDentalProcedureFragment;
    private MedicineFragment medicineFragment;

    public PreferenceObjects getPreferenceObjects() {
        return preferenceObjects;
    }

    public PreferenceObjects preferenceObjects;

    public static PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Case History Settings");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(v -> onBackPressed());
        preferenceActivity = this;
        try {
            preferenceObjects = new Gson().fromJson(AppInitialize.getSuggestionsPrefs(), PreferenceObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            preferenceObjects = new PreferenceObjects();
        }
        if (null == preferenceObjects)
            preferenceObjects = new PreferenceObjects();


        PreferredSettingCategoryList preferredSettingCategoryList = new PreferredSettingCategoryList();
        preferredSettingCategoryList.setCategoryListener(this);
        replaceFragmentWithoutBackStack(R.id.frame_list, preferredSettingCategoryList);

        medicineFragment = new MedicineFragment();

        preferenceMriFragment = new PreferenceHCServiceFragment();
        preferenceMriFragment.setArguments(getBundle(0));
        preferenceScanFragment = new PreferenceHCServiceFragment();
        preferenceScanFragment.setArguments(getBundle(1));
        preferenceSonoFragment = new PreferenceHCServiceFragment();
        preferenceSonoFragment.setArguments(getBundle(2));
        preferenceXrayFragment = new PreferenceHCServiceFragment();
        preferenceXrayFragment.setArguments(getBundle(3));
        preferencePathFragment = new PreferenceHCServiceFragment();
        preferencePathFragment.setArguments(getBundle(4));
        preferenceSpecFragment = new PreferenceHCServiceFragment();
        preferenceSpecFragment.setArguments(getBundle(5));

        preSymptomsFragment = new LocalPreferenceHCServiceFragment();
        preSymptomsFragment.setArguments(getBundle(0));
        prefProDiagnosisFragment = new LocalPreferenceHCServiceFragment();
        prefProDiagnosisFragment.setArguments(getBundle(1));
        prefDiagnosisFragment = new LocalPreferenceHCServiceFragment();
        prefDiagnosisFragment.setArguments(getBundle(2));
        preInstructionFragment = new LocalPreferenceHCServiceFragment();
        preInstructionFragment.setArguments(getBundle(3));
        preDentalProcedureFragment = new LocalPreferenceHCServiceFragment();
        preDentalProcedureFragment.setArguments(getBundle(4));

        if (isStoragePermissionAllowed()) {
            callFileApi();
        } else {
            requestStoragePermission();
        }

    }

    private void callFileApi() {
        showProgress();
        setProgressMessage("Updating data...");
        MasterLabApiCalls masterLabApiCalls = new MasterLabApiCalls();
        masterLabApiCalls.setFilePresenter(this);
        masterLabApiCalls.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
    }

    @Override
    public void onBackPressed() {
        AppInitialize.setSuggestionsPrefs(preferenceObjects);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        M_MerchantProfileApiCalls m_merchantProfileApiCalls = new M_MerchantProfileApiCalls(this);
        m_merchantProfileApiCalls.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(AppInitialize.getSuggestionsPrefs()));
    }

    @Override
    public void intellisenseResponse(JsonResponse jsonResponse) {
        Log.v("IntelliSense upload", "" + jsonResponse.getResponse());
    }

    @Override
    public void intellisenseError() {
        Log.v("IntelliSense upload: ", "error");
    }

    private boolean isStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    @Override
    public void menuHeaderClick(int pos) {
        //  viewPager.setCurrentItem(pos);
        // saveAllData();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                STORAGE_PERMISSION_PERMS,
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void fileError() {
        dismissProgress();
    }

    @Override
    public void fileResponse(File temp) {
        dismissProgress();
        {
            if (null != temp) {
                try {
                    File destination = new File(Environment.getExternalStorageDirectory() + "/UnZipped/");
                    uncompressTarGZ(temp, destination);
                } catch (Exception e) {
                    Log.e("Failed file loading {}", e.getLocalizedMessage(), e);
                    //TODO make sure to increase the date as not to fetch again
                }
            }
        }
    }

    private Bundle getBundle(int pos) {
        Bundle b = new Bundle();
        b.putInt("type", pos);
        return b;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            try {
                //both remaining permission allowed
                if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    callFileApi();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//one remaining permission allowed
                    callFileApi();
                } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //No permission allowed
                    //Do nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void uncompressTarGZ(File tarFile, File dest) {
        try {
            dest.mkdir();
            TarArchiveInputStream tarIn = new TarArchiveInputStream(
                    new GzipCompressorInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(
                                            tarFile
                                    )
                            )
                    )
            );
            masterData.clear();
            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
            while (tarEntry != null) {
                File destPath = new File(dest, tarEntry.getName());
                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();
                } else {
                    destPath.createNewFile();
                    byte[] btoRead = new byte[1024];
                    BufferedOutputStream bout =
                            new BufferedOutputStream(new FileOutputStream(destPath));
                    int len = 0;

                    while ((len = tarIn.read(btoRead)) != -1) {
                        bout.write(btoRead, 0, len);
                    }

                    bout.close();
                    btoRead = null;

                    if (destPath.getName().endsWith(".csv")) {
                        int lineCount = 0;
                        try {
                            BufferedReader buffer = new BufferedReader(new FileReader(destPath.getAbsolutePath()));
                            String line;
                            while ((line = buffer.readLine()) != null) {
                                lineCount++;
                                try {
                                    String[] strArray = line.split(",");
                                    masterData.add(new JsonMasterLab().
                                            setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    if (strArray[2].equals(HealthCareServiceEnum.SCAN.getName())) {
                                        masterDataScan.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.SONO.getName())) {
                                        masterDataSono.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.MRI.getName())) {
                                        masterDataMri.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.XRAY.getName())) {
                                        masterDataXray.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.PATH.getName())) {
                                        masterDataPath.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.SPEC.getName())) {
                                        masterDataSpec.add(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    }
                                    Log.e("data is :", line);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("data parsing error :", line);
                                }

                            }
                        } catch (Exception e) {
                            Log.e("Loading file=" + destPath + " line=" + lineCount + " reason={}", e.getLocalizedMessage(), e);
                            throw new RuntimeException("Loading file=" + destPath + " line=" + lineCount);
                        }
                    }
                    if (destPath.exists()) {
                        Log.e("File exist:", destPath.getAbsolutePath());
                        destPath.delete();
                    }
                }
                tarEntry = tarIn.getNextTarEntry();
            }
            tarIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tarFile.exists()) {
            Log.e("File exist:", tarFile.getAbsolutePath());
            tarFile.delete();
        }
        preferenceXrayFragment.setMasterLabArrayList(masterDataXray);
        preferenceSonoFragment.setMasterLabArrayList(masterDataSono);
        preferenceScanFragment.setMasterLabArrayList(masterDataScan);
        preferenceMriFragment.setMasterLabArrayList(masterDataMri);
        preferencePathFragment.setMasterLabArrayList(masterDataPath);
        preferenceSpecFragment.setMasterLabArrayList(masterDataSpec);
    }

    @Override
    public void categorySelected(int id) {
        switch (id) {
            case R.id.cv_mri:
                replaceFragmentWithBackStack(R.id.frame_list, preferenceMriFragment, "MRI");
                break;
            case R.id.cv_ctscan:
                replaceFragmentWithBackStack(R.id.frame_list, preferenceScanFragment, "Scan");
                break;
            case R.id.cv_sono:
                replaceFragmentWithBackStack(R.id.frame_list, preferenceSonoFragment, "Sono");
                break;
            case R.id.cv_xray:
                replaceFragmentWithBackStack(R.id.frame_list, preferenceXrayFragment, "Xray");
                break;
            case R.id.cv_path:
                replaceFragmentWithBackStack(R.id.frame_list, preferencePathFragment, "Path");
                break;
            case R.id.cv_special:
                replaceFragmentWithBackStack(R.id.frame_list, preferenceSpecFragment, "Special");
                break;
            case R.id.cv_medicine:
                replaceFragmentWithBackStack(R.id.frame_list, medicineFragment, "Medicine");
                break;
            case R.id.cv_symptoms:
                replaceFragmentWithBackStack(R.id.frame_list, preSymptomsFragment, "Symptoms");
                break;
            case R.id.cv_pro_diagnosis:
                replaceFragmentWithBackStack(R.id.frame_list, prefProDiagnosisFragment, "Physcio");
                break;
            case R.id.cv_diagnosis:
                replaceFragmentWithBackStack(R.id.frame_list, prefDiagnosisFragment, "Diagnosis");
                break;
            case R.id.cv_instruction:
                replaceFragmentWithBackStack(R.id.frame_list, preInstructionFragment, "Instruction");
                break;
                case R.id.cv_dental_procedure:
                replaceFragmentWithBackStack(R.id.frame_list, preDentalProcedureFragment, "Instruction");
                break;


        }
    }
}
