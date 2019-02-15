package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;

import com.noqapp.android.merchant.model.BaseMasterLabModel;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.AutoCompleteHCSMenuAdapter;
import com.noqapp.android.merchant.views.adapters.HCSMenuAdapter;
import com.noqapp.android.merchant.views.interfaces.FilePresenter;
import com.noqapp.android.merchant.views.pojos.HCSMenuObject;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

// Health Care Service Menu Screen
public class HCSMenuActivity extends AppCompatActivity implements FilePresenter, AutoCompleteHCSMenuAdapter.SearchByPos, HCSMenuAdapter.StaggeredClick {
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private RecyclerView rcv_menu, rcv_menu_select;
    private ProgressDialog progressDialog;
    private long lastPress;
    private Toast backPressToast;
    private ArrayList<HCSMenuObject> masterDataXray = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataSono = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataScan = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataMri = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataPath = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataSpec = new ArrayList<>();

    private ArrayList<HCSMenuObject> menuSelectData = new ArrayList<>();
    private JsonTopic jsonTopic;
    private AutoCompleteTextView actv_search;
    private HCSMenuAdapter hcsMenuAdapter = null;
    private HCSMenuAdapter hcsMenuSelectAdapter = null;
    View view_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_menu);
        initProgress();
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Test List");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view_test = findViewById(R.id.view_test);
        rcv_menu = findViewById(R.id.rcv_menu);
        rcv_menu.setLayoutManager(getFlexBoxLayoutManager(this));
        rcv_menu_select = findViewById(R.id.rcv_menu_select);
        rcv_menu_select.setLayoutManager(getFlexBoxLayoutManager(this));


        jsonTopic = (JsonTopic) getIntent().getSerializableExtra("jsonTopic");
        actv_search = findViewById(R.id.actv_search);
        actv_search.setThreshold(1);
        ImageView iv_clear_actv = findViewById(R.id.iv_clear_actv);
        iv_clear_actv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv_search.setText("");
                new AppUtils().hideKeyBoard(HCSMenuActivity.this);
            }
        });
        if (isStoragePermissionAllowed()) {
            callFileApi();
        } else {
            requestStoragePermission();
        }

    }

    public FlexboxLayoutManager getFlexBoxLayoutManager(Context context) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    private void callFileApi() {
        progressDialog.show();
        BaseMasterLabModel masterLabModel = new BaseMasterLabModel();
        masterLabModel.setFilePresenter(this);
        masterLabModel.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_LONG);
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
        }
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
                                    if (strArray[2].equals(HealthCareServiceEnum.SCAN.getName())) {
                                        masterDataScan.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.SONO.getName())) {
                                        masterDataSono.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.MRI.getName())) {
                                        masterDataMri.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.XRAY.getName())) {
                                        masterDataXray.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.PATH.getName())) {
                                        masterDataPath.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    if (strArray[2].equals(HealthCareServiceEnum.SPEC.getName())) {
                                        masterDataSpec.add(new HCSMenuObject().setJsonMasterLab(new JsonMasterLab().
                                                setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2]))));
                                    }
                                    Log.e("data is :", line);
                                }
                                HealthCareServiceEnum hcse = HealthCareServiceEnum.valueOf(jsonTopic.getBizCategoryId());

                                AutoCompleteHCSMenuAdapter adapterSearch;
                                switch (hcse) {
                                    case SPEC:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataSpec, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataSpec, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    case SONO:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataSono, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataSono, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    case MRI:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataMri, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataMri, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    case XRAY:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataXray, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataXray, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    case PATH:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataPath, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataPath, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    case SCAN:
                                        hcsMenuAdapter = new HCSMenuAdapter(this, masterDataScan, this, false);
                                        rcv_menu.setAdapter(hcsMenuAdapter);
                                        adapterSearch = new AutoCompleteHCSMenuAdapter(this, masterDataScan, null, this);
                                        actv_search.setAdapter(adapterSearch);
                                        break;
                                    default:
                                }
                                hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                                rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
                            } catch (Exception e) {
                                Log.e("Loading file=" + fileName + " line=" + lineCount + " reason={}", e.getLocalizedMessage(), e);
                                throw new RuntimeException("Loading file=" + fileName + " line=" + lineCount);
                            }
                        }
                    }
                    // preferenceXrayFragment.setData(masterDataXray);
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

    @Override
    public void searchByPos(HCSMenuObject menuObject) {
        new AppUtils().hideKeyBoard(this);
        actv_search.setText("");
        staggeredClick(false,  menuObject, 0);


    }

    @Override
    public void staggeredClick(boolean isRemove, HCSMenuObject hcsMenuObject, int pos) {
        {
            new AppUtils().hideKeyBoard(this);
            if(isRemove){
                menuSelectData.remove(pos);
                view_test.setVisibility(menuSelectData.size() > 0 ? View.VISIBLE : View.GONE);
                hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
            }else {
                if (isItemExist(hcsMenuObject.getSortName())) {
                    Toast.makeText(this, "Test Already added in list", Toast.LENGTH_LONG).show();
                } else {
                    menuSelectData.add(hcsMenuObject);
                    view_test.setVisibility(menuSelectData.size() > 0 ? View.VISIBLE : View.GONE);
                    hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                    rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
                }
            }
        }
    }

    private boolean isItemExist(String name) {
        for (int i = 0; i < menuSelectData.size(); i++) {
            if (menuSelectData.get(i).getSortName().equals(name))
                return true;
        }
        return false;
    }
}
