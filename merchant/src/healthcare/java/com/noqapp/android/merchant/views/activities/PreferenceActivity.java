package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.IntellisensePresenter;
import com.noqapp.android.merchant.model.M_MerchantProfileApiCalls;
import com.noqapp.android.merchant.model.MasterLabApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.MedicineFragment;
import com.noqapp.android.merchant.views.fragments.PreferenceHCServiceFragment;
import com.noqapp.android.merchant.views.interfaces.FilePresenter;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import com.google.gson.Gson;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class PreferenceActivity extends AppCompatActivity implements FilePresenter, IntellisensePresenter, MenuHeaderAdapter.OnItemClickListener {
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ProgressDialog progressDialog;
    private ViewPager viewPager;
    private long lastPress;
    private Toast backPressToast;
    private static PreferenceActivity preferenceActivity;
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterData = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataSono = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataScan = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataMri = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataXray = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataPath = new ArrayList<>();
    private ArrayList<JsonMasterLab> masterDataSpec = new ArrayList<>();
    private PreferenceHCServiceFragment preferenceSonoFragment, preferencePathFragment, preferenceMriFragment, preferenceScanFragment, preferenceXrayFragment, preferenceSpecFragment;
    private MedicineFragment medicineFragment;
    public PreferenceObjects preferenceObjects;

    public static PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Case History Settings");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initProgress();
        preferenceActivity = this;
        try {
            preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsPrefs(), PreferenceObjects.class);
        } catch (Exception e) {
            e.printStackTrace();
            preferenceObjects = new PreferenceObjects();
        }
        if (null == preferenceObjects)
            preferenceObjects = new PreferenceObjects();
        viewPager = findViewById(R.id.pager);

        rcv_header = findViewById(R.id.rcv_header);
        data.add("MRI");
        data.add("CT Scan");
        data.add("SONOGRAPHY");
        data.add("X-RAY");
        data.add("Pathology");
        data.add("Special");
        data.add("Medicine");
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
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(preferenceMriFragment, "FRAG" + 0);
        adapter.addFragment(preferenceScanFragment, "FRAG" + 1);
        adapter.addFragment(preferenceSonoFragment, "FRAG" + 2);
        adapter.addFragment(preferenceXrayFragment, "FRAG" + 3);
        adapter.addFragment(preferencePathFragment, "FRAG" + 4);
        adapter.addFragment(preferenceSpecFragment, "FRAG" + 5);
        adapter.addFragment(medicineFragment, "FRAG" + 6);

        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());


        menuAdapter = new MenuHeaderAdapter(data, this, this);
        rcv_header.setAdapter(menuAdapter);
        menuAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(data.size());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        adapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //saveAllData();
                rcv_header.smoothScrollToPosition(position);
                menuAdapter.setSelected_pos(position);
                menuAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (isStoragePermissionAllowed()) {
            callFileApi();
        } else {
            requestStoragePermission();
        }

    }

    private void callFileApi() {
        progressDialog.show();
        MasterLabApiCalls masterLabApiCalls = new MasterLabApiCalls();
        masterLabApiCalls.setFilePresenter(this);
        masterLabApiCalls.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(this, getString(R.string.exit_medical_screen), Toast.LENGTH_LONG);
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
        }

        PreferenceObjects temp = new PreferenceObjects();
        temp.setMriList(preferenceMriFragment.clearListSelection());
        temp.setScanList(preferenceScanFragment.clearListSelection());
        temp.setSonoList(preferenceSonoFragment.clearListSelection());
        temp.setXrayList(preferenceXrayFragment.clearListSelection());
        temp.setPathologyList(preferencePathFragment.clearListSelection());
        temp.setSpecList(preferenceSpecFragment.clearListSelection());
        temp.setMedicineList(medicineFragment.getSelectedList());
        temp.setPreferredStoreInfoHashMap(preferenceObjects.getPreferredStoreInfoHashMap());

        LaunchActivity.getLaunchActivity().setSuggestionsPrefs(temp);
    }

    @Override
    protected void onStop() {
        super.onStop();
        M_MerchantProfileApiCalls m_merchantProfileApiCalls = new M_MerchantProfileApiCalls(this);
        m_merchantProfileApiCalls.uploadIntellisense(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),
                new JsonProfessionalProfilePersonal().setDataDictionary(LaunchActivity.getLaunchActivity().getSuggestionsPrefs()));
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
        viewPager.setCurrentItem(pos);
        // saveAllData();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                STORAGE_PERMISSION_PERMS,
                STORAGE_PERMISSION_CODE);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating data...");
    }

    protected void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
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

                    Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
                    archiver.extract(temp, destination);
                    String path = Environment.getExternalStorageDirectory() + "/UnZipped";
                    Log.d("Files", "Path: " + path);
                    File directory = new File(path);
                    directory.deleteOnExit();

                    File[] files = directory.listFiles();
                    Log.d("Files", "Size: " + files.length);
                    masterData.clear();
                    for (File file : files) {
                        String fileName = file.getName();
                        Log.d("Files", "FileName:" + fileName);
                        if (fileName.endsWith(".csv")) {
                            int lineCount = 0;
                            try {
                                // PreferredStoreDB.deletePreferredStore(fileName.substring(0, fileName.lastIndexOf("_")));
                                BufferedReader buffer = new BufferedReader(new FileReader(file.getAbsolutePath()));
                                String line;
                                while ((line = buffer.readLine()) != null) {
                                    lineCount++;
                                    // PreferredStoreDB.insertPreferredStore(line);
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

                                }
                            } catch (Exception e) {
                                Log.e("Loading file=" + fileName + " line=" + lineCount + " reason={}", e.getLocalizedMessage(), e);
                                throw new RuntimeException("Loading file=" + fileName + " line=" + lineCount);
                            }
                        }
                    }
                    preferenceXrayFragment.setData(masterDataXray);
                    preferenceSonoFragment.setData(masterDataSono);
                    preferenceScanFragment.setData(masterDataScan);
                    preferenceMriFragment.setData(masterDataMri);
                    preferencePathFragment.setData(masterDataPath);
                    preferenceSpecFragment.setData(masterDataSpec);
                    for (File file : files) {
                        new File(path, file.getName()).delete();
                    }
                    directory.delete();
                } catch (Exception e) {
                    Log.e("Failed file loading {}", e.getLocalizedMessage(), e);
                    //TODO make sure to increase the date as not to fetch again
                }
            }
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
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
}
