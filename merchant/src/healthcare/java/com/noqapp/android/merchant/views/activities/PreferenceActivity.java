package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.interfaces.FilePresenter;
import com.noqapp.android.merchant.interfaces.MasterLabPresenter;
import com.noqapp.android.merchant.model.MasterLabModel;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MultiSelectListAdapter;
import com.noqapp.android.merchant.views.pojos.DataObj;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class PreferenceActivity extends AppCompatActivity implements MasterLabPresenter, FilePresenter {
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ListView lv_tests, lv_all_tests;
    private AutoCompleteTextView actv_search;
    private EditText edt_add;
    private ArrayAdapter<String> listAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<JsonMasterLab> masterData = new ArrayList<>();
    private ArrayList<String> masterDataString = new ArrayList<>();
    private ArrayList<DataObj> checkList = new ArrayList<>();
    private MultiSelectListAdapter multiSelectListAdapter;
    private ArrayAdapter<String> actvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        initProgress();
        edt_add = findViewById(R.id.edt_add);
        lv_tests = findViewById(R.id.lv_tests);
        lv_all_tests = findViewById(R.id.lv_all_tests);
        actv_search = findViewById(R.id.actv_search);
        checkList = LaunchActivity.getSelectedTest();
        if(null == checkList)
            checkList = new ArrayList<>();
        actv_search.setThreshold(1);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) parent.getItemAtPosition(position);
                DataObj dataObj = new DataObj();
                dataObj.setName(value);
                dataObj.setSelect(false);
                if (!checkList.contains(dataObj)) {
                    checkList.add(dataObj);
                    multiSelectListAdapter.notifyDataSetChanged();
                    actv_search.setText("");
                } else {
                    Toast.makeText(PreferenceActivity.this, "Already selected", Toast.LENGTH_LONG).show();
                }
                new AppUtils().hideKeyBoard(PreferenceActivity.this);
            }
        });
        actv_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (actv_search.getRight() - actv_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        actv_search.setText("");
                        return true;
                    }
                    if (event.getRawX() <= (20 + actv_search.getLeft() + actv_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        //performSearch();
                        return true;
                    }
                }
                return false;
            }
        });

        multiSelectListAdapter = new MultiSelectListAdapter(this, checkList);
        lv_tests.setAdapter(multiSelectListAdapter);
        lv_all_tests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataObj dataObj = new DataObj();
                dataObj.setName(masterDataString.get(position));
                dataObj.setSelect(false);
                if (!checkList.contains(dataObj)) {
                    checkList.add(dataObj);
                    multiSelectListAdapter.notifyDataSetChanged();
                }
            }
        });

        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                MasterLabModel masterLabModel = new MasterLabModel();
                masterLabModel.setMasterLabPresenter(PreferenceActivity.this);
                masterLabModel.add(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), new JsonMasterLab().
                        setProductName(edt_add.getText().toString()).setProductShortName(edt_add.getText().toString()).setHealthCareService(HealthCareServiceEnum.SONO));
            }
        });
        if (isStoragePermissionAllowed()) {
            progressDialog.show();
            MasterLabModel masterLabModel = new MasterLabModel();
            masterLabModel.setFilePresenter(PreferenceActivity.this);
            masterLabModel.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            requestStoragePermission();
        }

    }

    @Override
    public void masterLabUploadResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            masterDataString.add(edt_add.getText().toString());

            listAdapter.notifyDataSetChanged();
            actvAdapter.notifyDataSetChanged();
            DataObj dataObj = new DataObj();
            dataObj.setName(edt_add.getText().toString());
            dataObj.setSelect(false);
            checkList.add(dataObj);
            multiSelectListAdapter.notifyDataSetChanged();
            edt_add.setText("");
            Toast.makeText(this, "Test updated Successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to update test", Toast.LENGTH_LONG).show();
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
                                    masterData.add(new JsonMasterLab().
                                            setProductName(strArray[0]).setProductShortName(strArray[1]).setHealthCareService(HealthCareServiceEnum.valueOf(strArray[2])));
                                    masterDataString.add(strArray[0]);

                                }
                            } catch (Exception e) {
                                Log.e("Loading file=" + fileName + " line=" + lineCount + " reason={}", e.getLocalizedMessage(), e);
                                throw new RuntimeException("Loading file=" + fileName + " line=" + lineCount);
                            }
                        }
                    }
                    listAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, masterDataString);
                    lv_all_tests.setAdapter(listAdapter);
                    actvAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, masterDataString);
                    actv_search.setAdapter(actvAdapter);
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
    public void fileError() {
        dismissProgress();
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



    @Override
    public void onBackPressed() {
        LaunchActivity.setSelectedTest(multiSelectListAdapter.getAllSelectedList());
        super.onBackPressed();
    }
}
