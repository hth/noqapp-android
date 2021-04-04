package com.noqapp.android.merchant.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.category.HealthCareServiceEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BaseMasterLabApiCalls;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
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
import java.util.Date;
import java.util.List;

/**
 * Health Care Service Menu Screen
 */
public class HCSMenuActivity extends BaseActivity implements FilePresenter,
        AutoCompleteHCSMenuAdapter.SearchByPos, HCSMenuAdapter.StaggeredClick,
        FindCustomerPresenter, PurchaseOrderPresenter, RegistrationActivity.RegisterCallBack,
        LoginActivity.LoginCallBack {
    private final int STORAGE_PERMISSION_CODE = 102;
    private final String[] STORAGE_PERMISSION_PERMS = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private RecyclerView rcv_menu, rcv_menu_select;
    private long lastPress;
    private Toast backPressToast;
    private ArrayList<HCSMenuObject> masterDataXray = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataSono = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataScan = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataMri = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataPath = new ArrayList<>();
    private ArrayList<HCSMenuObject> masterDataSpec = new ArrayList<>();

    private final ArrayList<HCSMenuObject> menuSelectData = new ArrayList<>();
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
    private BusinessCustomerApiCalls businessCustomerApiCalls;
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
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_menu);
        setProgressMessage("Updating data...");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText("Test List");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> {
            finish();
            if (null != updateWholeList) {
                updateWholeList.updateWholeList();
            }
        });
        view_test = findViewById(R.id.view_test);
        rcv_menu = findViewById(R.id.rcv_menu);
        rcv_menu.setLayoutManager(getFlexBoxLayoutManager(this));
        rcv_menu_select = findViewById(R.id.rcv_menu_select);
        rcv_menu_select.setLayoutManager(getFlexBoxLayoutManager(this));
        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(v -> showCreateTokenDialogWithMobile(HCSMenuActivity.this, codeQR));

        currencySymbol = AppInitialize.getCurrencySymbol();
        jsonTopic = (JsonTopic) getIntent().getSerializableExtra("jsonTopic");

        codeQR = jsonTopic.getCodeQR();
        actv_search = findViewById(R.id.actv_search);
        actv_search.setThreshold(1);
        ImageView iv_clear_actv = findViewById(R.id.iv_clear_actv);
        iv_clear_actv.setOnClickListener(v -> {
            actv_search.setText("");
            AppUtils.hideKeyBoard(HCSMenuActivity.this);
        });
        if (TextUtils.isEmpty(AppInitialize.getSuggestionsProductPrefs())) {
            callFileApi();
        } else {
            try {
                preferenceObjects = new Gson().fromJson(AppInitialize.getSuggestionsProductPrefs(), PreferenceObjects.class);
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
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setFindCustomerPresenter(this);
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
            showProgress();
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
            backPressToast = new CustomToast().getToast(this, "Press back to exit");
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
        return result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED;
        //If permission is not granted returning false
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

    public void uncompressTarGZ(File tarFile, File dest) {
        try {
            dest.mkdir();
            TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarFile))));
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
            if (tarFile.exists()) {
                Log.e("File exist:", tarFile.getAbsolutePath());
                tarFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        AppInitialize.setSuggestionsProductsPrefs(preferenceObjects);
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
            new CustomToast().showToast(this, eej.getReason());
            Intent in = new Intent(this, LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            in.putExtra("countryCode", countryCode);
            in.putExtra("countryShortName", countryShortName);
            startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else {
            new ErrorResponseHandler().processError(this, eej);
        }
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
        AppUtils.hideKeyBoard(this);
        actv_search.setText("");
        staggeredClick(false, menuObject, 0);
    }

    @Override
    public void staggeredClick(boolean isRemove, HCSMenuObject hcsMenuObject, int pos) {
        {
            AppUtils.hideKeyBoard(this);
            if (isRemove) {
                menuSelectData.remove(pos);
                view_test.setVisibility(menuSelectData.size() > 0 ? View.VISIBLE : View.GONE);
                hcsMenuSelectAdapter = new HCSMenuAdapter(this, menuSelectData, this, true);
                rcv_menu_select.setAdapter(hcsMenuSelectAdapter);
            } else {
                if (isItemExist(hcsMenuObject.getSortName())) {
                    new CustomToast().showToast(this, "Test Already added in list");
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
                tv_amount.setText(currencySymbol + menuSelectData.get(i).getPrice());
                tv_amount.setOnClickListener(v1 -> {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater1 = LayoutInflater.from(mContext);
                    builder1.setTitle(null);
                    View prideDialog = inflater1.inflate(R.layout.dialog_modify_price, null, false);
                    final EditText edt_prod_price = prideDialog.findViewById(R.id.edt_prod_price);
                    builder1.setView(prideDialog);
                    final AlertDialog mAlertDialog = builder1.create();
                    mAlertDialog.setCanceledOnTouchOutside(false);
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    Button btn_update = prideDialog.findViewById(R.id.btn_update);
                    Button btn_cancel = prideDialog.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(v11 -> mAlertDialog.dismiss());
                    btn_update.setOnClickListener(v112 -> {
                        edt_prod_price.setError(null);
                        if (edt_prod_price.getText().toString().equals("")) {
                            edt_prod_price.setError(mContext.getString(R.string.empty_price));
                        } else {
//                                    new AppUtils().hideKeyBoard(HCSMenuActivity.this);
//                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edt_prod_price.getWindowToken(), 0);
                            menuSelectData.get(pos).setPrice(Double.parseDouble(edt_prod_price.getText().toString()));
                            tv_amount.setText(currencySymbol + menuSelectData.get(pos).getPrice());
                            tv_cost.setText(currencySymbol + calculateTotalPrice());
                            mAlertDialog.dismiss();
                        }
                    });
                    mAlertDialog.show();
                });
                ll_order_list.addView(v);
                price += 1 * menuSelectData.get(pos).getPrice();
            }
            tv_cost.setText(currencySymbol + price);
        }

        tv_toolbar_title.setText("Create Order");
        tv_create_token.setText("Click button to create order");
        edt_mobile = view.findViewById(R.id.edt_mobile);
        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        builder.setView(view);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mobile) {
                ll_mobile.setVisibility(View.VISIBLE);
                edt_id.setVisibility(View.GONE);
                edt_id.setText("");
            } else {
                edt_id.setVisibility(View.VISIBLE);
                ll_mobile.setVisibility(View.GONE);
                edt_mobile.setText("");
            }
        });
        ccp = view.findViewById(R.id.ccp);
        String c_codeValue = AppInitialize.getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token.setText("Search Patient");
        btn_create_token.setOnClickListener(v -> {
            boolean isValid = true;
            edt_mobile.setError(null);
            edt_id.setError(null);
            AppUtils.hideKeyBoard(HCSMenuActivity.this);
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
                    phone = countryCode + edt_mobile.getText().toString();
                } else {
                    cid = edt_id.getText().toString();
                    edt_mobile.setText("");// set blank so that wrong phone no. not pass to login screen
                }
                showProgress();
                setProgressMessage("Searching patient...");
                setProgressCancel(false);
                businessCustomerApiCalls.findCustomer(
                    AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(),
                    new JsonBusinessCustomerLookup()
                        .setCodeQR(codeQR)
                        .setCustomerPhone(phone)
                        .setBusinessCustomerId(cid)
                );
                btn_create_token.setClickable(false);
                // mAlertDialog.dismiss();
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }

    @Override
    public void userFound(JsonProfile jsonProfile) {
        // coming from login activity
        findCustomerResponse(jsonProfile);
    }

    @Override
    public void userRegistered(JsonProfile jsonProfile) {
        // coming from registration activity
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
            btn_create_order.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new NetworkUtil(HCSMenuActivity.this).isOnline()) {
                        setProgressMessage("Placing order....");
                        showProgress();
                        setProgressCancel(false);
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
                        jsonPurchaseOrder.setUserAddressId(jsonProfile.findPrimaryOrAnyExistingAddressId());
                        jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.HD);
                        jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA);
                        jsonPurchaseOrder.setCustomerPhone(jsonProfile.getPhoneRaw());
                        jsonPurchaseOrder.setAdditionalNote("");
                        jsonPurchaseOrder.setCustomized(true);

                        purchaseOrderApiCalls.medicalPurchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(HCSMenuActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void purchaseOrderListResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        dismissProgress();
        // do nothing
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        dismissProgress();
        if (null != jsonPurchaseOrder) {
            Log.v("order data:", jsonPurchaseOrder.toString());
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