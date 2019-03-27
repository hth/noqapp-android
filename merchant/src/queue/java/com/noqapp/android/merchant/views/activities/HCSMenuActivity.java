package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BaseMasterLabApiCalls;
import com.noqapp.android.merchant.model.FindCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.AutoCompleteHCSMenuAdapter;
import com.noqapp.android.merchant.views.adapters.HCSMenuAdapter;
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.interfaces.FilePresenter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;
import com.noqapp.android.merchant.views.pojos.HCSMenuObject;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;

import com.hbb20.CountryCodePicker;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Health Care Service Menu Screen
public class HCSMenuActivity extends AppCompatActivity implements FilePresenter, AutoCompleteHCSMenuAdapter.SearchByPos, HCSMenuAdapter.StaggeredClick,
        FindCustomerPresenter, PurchaseOrderPresenter, RegistrationActivity.RegisterCallBack, LoginActivity.LoginCallBack {
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
    private View view_test;
    private Button btn_place_order;
    private EditText edt_mobile;
    private Spinner sp_patient_list;
    private TextView tv_select_patient;
    private Button btn_create_order, btn_create_token;
    private String codeQR = "";
    private PurchaseOrderApiCalls purchaseOrderApiCalls;
    private FindCustomerApiCalls findCustomerApiCalls;
    private LinearLayout ll_order_list;
    private TextView tv_order_list;
    private RelativeLayout rl_total;
    private LinearLayout ll_mobile;
    private int price = 0;
    private String currencySymbol;
    public static UpdateWholeList updateWholeList;
    private PreferenceObjects preferenceObjects;
    private CountryCodePicker ccp;
    private String countryCode = "";
    private String countryShortName = "";
    public interface UpdateWholeList {
        void updateWholeList();
    }

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
                if (null != updateWholeList) {
                    updateWholeList.updateWholeList();
                }
            }
        });
        view_test = findViewById(R.id.view_test);
        rcv_menu = findViewById(R.id.rcv_menu);
        rcv_menu.setLayoutManager(getFlexBoxLayoutManager(this));
        rcv_menu_select = findViewById(R.id.rcv_menu_select);
        rcv_menu_select.setLayoutManager(getFlexBoxLayoutManager(this));
        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTokenDialogWithMobile(HCSMenuActivity.this, codeQR);
            }
        });

        currencySymbol = BaseLaunchActivity.getCurrencySymbol();
        jsonTopic = (JsonTopic) getIntent().getSerializableExtra("jsonTopic");

        codeQR = jsonTopic.getCodeQR();
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
        if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().getSuggestionsProductPrefs())) {
            callFileApi();
        } else {
            try {
                preferenceObjects = new Gson().fromJson(LaunchActivity.getLaunchActivity().getSuggestionsProductPrefs(), PreferenceObjects.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == preferenceObjects) {
                callFileApi();
            } else {
                if (preferenceObjects.isLastUpdateTimeExceed()) {
                    callFileApi();
                } else {
                    masterDataMri = preferenceObjects.getMriList();
                    masterDataPath = preferenceObjects.getPathologyList();
                    masterDataScan = preferenceObjects.getScanList();
                    masterDataSono = preferenceObjects.getSonoList();
                    masterDataXray = preferenceObjects.getXrayList();
                    masterDataSpec = preferenceObjects.getSpecList();
                    bindAdapterData();
                }
            }
        }
        purchaseOrderApiCalls = new PurchaseOrderApiCalls();
        findCustomerApiCalls = new FindCustomerApiCalls();
        findCustomerApiCalls.setFindCustomerPresenter(this);
        purchaseOrderApiCalls.setPurchaseOrderPresenter(this);
    }

    public FlexboxLayoutManager getFlexBoxLayoutManager(Context context) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    private void callFileApi() {
        if (isStoragePermissionAllowed()) {
            progressDialog.show();
            BaseMasterLabApiCalls baseMasterLabApiCalls = new BaseMasterLabApiCalls();
            baseMasterLabApiCalls.setFilePresenter(this);
            baseMasterLabApiCalls.fetchFile(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());
        } else {
            requestStoragePermission();
        }
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
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
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

                                bindAdapterData();
                                PreferenceObjects preferenceObjects = new PreferenceObjects();
                                preferenceObjects.setMriList(preferenceObjects.clearListSelection(masterDataMri));
                                preferenceObjects.setScanList(preferenceObjects.clearListSelection(masterDataScan));
                                preferenceObjects.setSonoList(preferenceObjects.clearListSelection(masterDataSono));
                                preferenceObjects.setXrayList(preferenceObjects.clearListSelection(masterDataXray));
                                preferenceObjects.setPathologyList(preferenceObjects.clearListSelection(masterDataPath));
                                preferenceObjects.setSpecList(preferenceObjects.clearListSelection(masterDataSpec));
                                preferenceObjects.setLastUpdateDate(CommonHelper.SDF_YYYY_MM_DD.format(new Date()));
                                LaunchActivity.getLaunchActivity().setSuggestionsProductsPrefs(preferenceObjects);
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

    private void bindAdapterData() {

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
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej && eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            btn_create_token.setClickable(true);
            Toast.makeText(this, eej.getReason(), Toast.LENGTH_LONG).show();
            Intent in = new Intent(this, LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            in.putExtra("countryCode",countryCode);
            in.putExtra("countryShortName",countryShortName);
            startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else {
            new ErrorResponseHandler().processError(this, eej);
        }
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
        staggeredClick(false, menuObject, 0);


    }

    @Override
    public void staggeredClick(boolean isRemove, HCSMenuObject hcsMenuObject, int pos) {
        {
            new AppUtils().hideKeyBoard(this);
            if (isRemove) {
                menuSelectData.remove(pos);
                view_test.setVisibility(menuSelectData.size() > 0 ? View.VISIBLE : View.GONE);
                hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
            } else {
                if (isItemExist(hcsMenuObject.getSortName())) {
                    Toast.makeText(this, "Test Already added in list", Toast.LENGTH_LONG).show();
                } else {
                    menuSelectData.add(hcsMenuObject);
                    view_test.setVisibility(menuSelectData.size() > 0 ? View.VISIBLE : View.GONE);
                    hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                    rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
                }
            }

            if (menuSelectData.size() > 0) {
                btn_place_order.setVisibility(View.VISIBLE);
            } else {
                btn_place_order.setVisibility(View.GONE);
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

    private double calculateTotalPrice() {
        int amount = 0;
        for (int i = 0; i < menuSelectData.size(); i++) {
            amount += 1 * menuSelectData.get(i).getPrice();
        }
        return amount;
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_create_order_with_mobile, null, false);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        TextView tv_create_token = view.findViewById(R.id.tvtitle);
        TextView tv_cost = view.findViewById(R.id.tv_cost);
        TextView tv_toolbar_title = view.findViewById(R.id.tv_toolbar_title);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);
        tv_order_list = view.findViewById(R.id.tv_order_list);
        ll_order_list = view.findViewById(R.id.ll_order_list);
        ll_mobile = view.findViewById(R.id.ll_mobile);
        rl_total = view.findViewById(R.id.rl_total);
        ll_order_list.removeAllViews();
        if (menuSelectData.size() > 0) {
            price = 0;
            for (int i = 0; i < menuSelectData.size(); i++) {
                final int pos = i;
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View v = layoutInflater.inflate(R.layout.list_item_order_add, null);
                TextView tv_title = v.findViewById(R.id.tv_title);
                TextView tv_amount = v.findViewById(R.id.tv_amount);
                tv_title.setText(menuSelectData.get(i).getJsonMasterLab().getProductShortName());
                tv_amount.setText(currencySymbol + " " + String.valueOf(menuSelectData.get(i).getPrice()));
                tv_amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        builder.setTitle(null);
                        View prideDialog = inflater.inflate(R.layout.dialog_modify_price, null, false);
                        final EditText edt_prod_price = prideDialog.findViewById(R.id.edt_prod_price);
                        builder.setView(prideDialog);
                        final AlertDialog mAlertDialog = builder.create();
                        mAlertDialog.setCanceledOnTouchOutside(false);
                        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        Button btn_update = prideDialog.findViewById(R.id.btn_update);
                        Button btn_cancel = prideDialog.findViewById(R.id.btn_cancel);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlertDialog.dismiss();
                            }
                        });
                        btn_update.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                edt_prod_price.setError(null);
                                if (edt_prod_price.getText().toString().equals("")) {
                                    edt_prod_price.setError(mContext.getString(R.string.empty_price));
                                } else {
//                                    new AppUtils().hideKeyBoard(HCSMenuActivity.this);
//                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(edt_prod_price.getWindowToken(), 0);
                                    menuSelectData.get(pos).setPrice(Double.parseDouble(edt_prod_price.getText().toString()));
                                    tv_amount.setText(currencySymbol + " " + String.valueOf(menuSelectData.get(pos).getPrice()));
                                    tv_cost.setText(currencySymbol + " " + String.valueOf(calculateTotalPrice()));
                                    mAlertDialog.dismiss();
                                }
                            }
                        });
                        mAlertDialog.show();
                    }
                });
                ll_order_list.addView(v);
                price += 1 * menuSelectData.get(pos).getPrice();
            }
            tv_cost.setText(currencySymbol + " " + String.valueOf(price));
        }

        tv_toolbar_title.setText("Create order");
        tv_create_token.setText("Click button to create order");
        edt_mobile = view.findViewById(R.id.edt_mobile);
        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_mobile) {
                    ll_mobile.setVisibility(View.VISIBLE);
                    edt_id.setVisibility(View.GONE);
                    edt_id.setText("");
                } else {
                    edt_id.setVisibility(View.VISIBLE);
                    ll_mobile.setVisibility(View.GONE);
                    edt_mobile.setText("");
                }
            }
        });
        ccp = view.findViewById(R.id.ccp);
        String c_codeValue = LaunchActivity.getLaunchActivity().getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token.setText("Search Patient");
        btn_create_token.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean isValid = true;
                edt_mobile.setError(null);
                edt_id.setError(null);
                new AppUtils().hideKeyBoard(HCSMenuActivity.this);
                int selectedId = rg_user_id.getCheckedRadioButtonId();
                if (selectedId == R.id.rb_mobile) {
                    if (TextUtils.isEmpty(edt_mobile.getText())) {
                        edt_mobile.setError(getString(R.string.error_mobile_blank));
                        isValid = false;
                    }
                } else {
                    if (TextUtils.isEmpty(edt_id.getText())) {
                        edt_id.setError(getString(R.string.error_customer_id));
                        isValid = false;
                    }
                }
                if (isValid) {
                    String phone = "";
                    String cid = "";
                    if (rb_mobile.isChecked()) {
                        edt_id.setText("");
                        countryCode = ccp.getSelectedCountryCode();
                        countryShortName = ccp.getDefaultCountryName().toUpperCase();
                        phone = countryCode+ edt_mobile.getText().toString();
                    } else {
                        cid = edt_id.getText().toString();
                        edt_mobile.setText("");// set blank so that wrong phone no. not pass to login screen
                    }
                    progressDialog.show();


                    findCustomerApiCalls.findCustomer(
                            BaseLaunchActivity.getDeviceID(),
                            LaunchActivity.getLaunchActivity().getEmail(),
                            LaunchActivity.getLaunchActivity().getAuth(),
                            new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
                    btn_create_token.setClickable(false);
                    // mAlertDialog.dismiss();

                }
            }
        });

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    @Override
    public void passPhoneNo(JsonProfile jsonProfile) {
        // coming from login or registration activity
        findCustomerResponse(jsonProfile);
    }


    @Override
    public void findCustomerResponse(final JsonProfile jsonProfile) {
        dismissProgress();
        if (null != jsonProfile) {
            List<JsonProfile> jsonProfileList = new ArrayList<>();
            jsonProfileList.add(jsonProfile);
            if (jsonProfile.getDependents().size() > 0) {
                jsonProfileList.addAll(jsonProfile.getDependents());
            }
            JsonProfileAdapter adapter = new JsonProfileAdapter(this, jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setVisibility(View.VISIBLE);
            edt_mobile.setEnabled(false);
            tv_select_patient.setVisibility(View.VISIBLE);
            tv_order_list.setVisibility(View.VISIBLE);
            ll_order_list.setVisibility(View.VISIBLE);
            rl_total.setVisibility(View.VISIBLE);
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.setMessage("Placing order....");
                        progressDialog.show();
                        btn_create_order.setEnabled(false);
                        List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                        int price = 0;
                        for (HCSMenuObject value : menuSelectData) {
                            ll.add(new JsonPurchaseOrderProduct()
                                    .setProductId("")
                                    .setProductPrice((int) value.getPrice() * 100)
                                    .setProductQuantity(1)
                                    .setProductName(value.getJsonMasterLab().getProductName()));
                            price += 1 * value.getPrice() * 100;
                        }
                        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                                .setCodeQR(codeQR)
                                .setQueueUserId(jsonProfileList.get(sp_patient_list.getSelectedItemPosition()).getQueueUserId())
                                .setOrderPrice(String.valueOf(price));
                        jsonPurchaseOrder.setPurchaseOrderProducts(ll);


                        jsonPurchaseOrder.setDeliveryAddress("");
                        jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.HD);
                        jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA);
                        jsonPurchaseOrder.setCustomerPhone(jsonProfile.getPhoneRaw());
                        jsonPurchaseOrder.setAdditionalNote("");
                        jsonPurchaseOrder.setCustomized(true);
                        purchaseOrderApiCalls.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                        // Toast.makeText(HCSMenuActivity.this,"Waiting for procedure...",Toast.LENGTH_LONG).show();
                    } else {
                        ShowAlertInformation.showNetworkDialog(HCSMenuActivity.this);
                    }
                }
            });
        }

    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        }
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }
}