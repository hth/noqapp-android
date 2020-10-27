package com.noqapp.android.merchant.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbb20.CountryCodePicker;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderList;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PhoneFormatterUtil;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.BusinessCustomerApiCalls;
import com.noqapp.android.merchant.presenter.beans.JsonBusinessCustomerLookup;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.AppInitialize;
import com.noqapp.android.merchant.views.activities.BaseLaunchActivity;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.LoginActivity;
import com.noqapp.android.merchant.views.activities.RegistrationActivity;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.adapters.JsonProfileAdapter;
import com.noqapp.android.merchant.views.adapters.StoreMenuOrderAdapter;
import com.noqapp.android.merchant.views.interfaces.FindCustomerPresenter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderApiCalls;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductMenuListFragment
    extends BaseFragment
    implements StoreMenuOrderAdapter.CartOrderUpdate, FindCustomerPresenter, PurchaseOrderPresenter, RegistrationActivity.RegisterCallBack, LoginActivity.LoginCallBack {
    private RecyclerView rcv_order_list;
    private List<StoreCartItem> childData;
    private StoreMenuActivity storeMenuActivity;
    private Button btn_place_order;
    private EditText edt_mobile;
    private Spinner sp_patient_list;
    private TextView tv_select_patient;
    private Button btn_create_order, btn_create_token;
    private PurchaseOrderApiCalls purchaseOrderApiCalls;
    private BusinessCustomerApiCalls businessCustomerApiCalls;
    private String codeQR = "";
    private StoreMenuOrderAdapter storeMenuOrderAdapter;
    private AlertDialog mAlertDialog;
    private String cid = "";
    private CountryCodePicker ccp;
    private String countryCode = "";
    private String countryShortName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_product_menu_list, container, false);
        btn_place_order = view.findViewById(R.id.btn_place_order);
        rcv_order_list = view.findViewById(R.id.rcv_order_list);
        rcv_order_list.setHasFixedSize(true);
        rcv_order_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rcv_order_list.setItemAnimator(new DefaultItemAnimator());
        rcv_order_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        storeMenuOrderAdapter = new StoreMenuOrderAdapter(childData, storeMenuActivity, this, AppInitialize.getCurrencySymbol());
        rcv_order_list.setAdapter(storeMenuOrderAdapter);
        purchaseOrderApiCalls = new PurchaseOrderApiCalls();
        businessCustomerApiCalls = new BusinessCustomerApiCalls();
        businessCustomerApiCalls.setFindCustomerPresenter(this);
        purchaseOrderApiCalls.setPurchaseOrderPresenter(this);
        updateCartOrderInfo(storeMenuOrderAdapter.showCartAmount());
        codeQR = getArguments().getString("codeQR");
        btn_place_order.setOnClickListener(v -> showCreateTokenDialogWithMobile(getActivity(), codeQR));
        return view;
    }

    public ProductMenuListFragment(List<StoreCartItem> childData, StoreMenuActivity storeMenuActivity) {
        this.childData = childData;
        this.storeMenuActivity = storeMenuActivity;
    }

    @Override
    public void updateCartOrderInfo(BigDecimal amountString) {
        if (amountString.compareTo(new BigDecimal(0)) > 0) {
            btn_place_order.setVisibility(View.VISIBLE);
            btn_place_order.setText("Your cart amount is: " + amountString);
        } else {
            btn_place_order.setVisibility(View.GONE);
            btn_place_order.setText("");
        }
    }

    public void updateCartOrderViaMenu() {
        storeMenuOrderAdapter.setMenuItemsList(StoreMenuActivity.storeMenuActivity.getCartList());
        updateCartOrderInfo(storeMenuOrderAdapter.showCartAmount());
    }

    private void showCreateTokenDialogWithMobile(final Context mContext, final String codeQR) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        builder.setTitle(null);
        View view = inflater.inflate(R.layout.dialog_create_order_with_mobile, null, false);
        ImageView actionbarBack = view.findViewById(R.id.actionbarBack);
        TextView tv_create_token = view.findViewById(R.id.tvtitle);
        TextView tv_toolbar_title = view.findViewById(R.id.tv_toolbar_title);
        sp_patient_list = view.findViewById(R.id.sp_patient_list);
        tv_select_patient = view.findViewById(R.id.tv_select_patient);
        tv_toolbar_title.setText("Create Order");
        tv_create_token.setText("Click button to create order");
        edt_mobile = view.findViewById(R.id.edt_mobile);
        final EditText edt_id = view.findViewById(R.id.edt_id);
        final RadioGroup rg_user_id = view.findViewById(R.id.rg_user_id);
        final RadioButton rb_mobile = view.findViewById(R.id.rb_mobile);
        final RadioButton rb_customer_id = view.findViewById(R.id.rb_customer_id);
        builder.setView(view);
        mAlertDialog = null;
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        rg_user_id.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mobile) {
                edt_mobile.setVisibility(View.VISIBLE);
                edt_id.setVisibility(View.GONE);
                edt_id.setText("");
            } else {
                edt_id.setVisibility(View.VISIBLE);
                edt_mobile.setVisibility(View.GONE);
                edt_mobile.setText("");
            }
        });
        cid = "";
        ccp = view.findViewById(R.id.ccp);
        String c_codeValue = AppInitialize.getUserProfile().getCountryShortName();
        int c_code = PhoneFormatterUtil.getCountryCodeFromRegion(c_codeValue.toUpperCase());
        ccp.setDefaultCountryUsingNameCode(String.valueOf(c_code));
        rb_customer_id.setVisibility(View.GONE);
        btn_create_token = view.findViewById(R.id.btn_create_token);
        btn_create_order = view.findViewById(R.id.btn_create_order);
        btn_create_token.setText("Search Customer");
        btn_create_token.setOnClickListener(v -> {
            boolean isValid = true;
            edt_mobile.setError(null);
            edt_id.setError(null);
            AppUtils.hideKeyBoard(getActivity());
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
                cid = "";
                if (rb_mobile.isChecked()) {
                    edt_id.setText("");
                    countryCode = ccp.getSelectedCountryCode();
                    countryShortName = ccp.getDefaultCountryName().toUpperCase();
                    phone = countryCode + edt_mobile.getText().toString();
                    cid = "";
                } else {
                    cid = edt_id.getText().toString();
                    edt_mobile.setText("");// set blank so that wrong phone no not pass to login screen
                }
                showProgress();
                setProgressMessage("Searching user...");
                setProgressCancel(false);
                businessCustomerApiCalls.findCustomer(
                    AppInitialize.getDeviceID(),
                    AppInitialize.getEmail(),
                    AppInitialize.getAuth(),
                    new JsonBusinessCustomerLookup().setCodeQR(codeQR).setCustomerPhone(phone).setBusinessCustomerId(cid));
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
            JsonProfileAdapter adapter = new JsonProfileAdapter(getActivity(), jsonProfileList);
            sp_patient_list.setAdapter(adapter);
            sp_patient_list.setEnabled(false);
            sp_patient_list.setVisibility(View.VISIBLE);
            edt_mobile.setEnabled(false);
            //tv_select_patient.setVisibility(View.VISIBLE);
            btn_create_order.setVisibility(View.VISIBLE);
            btn_create_token.setVisibility(View.GONE);
            btn_create_order.setOnClickListener(v -> {
                if (new NetworkUtil(getActivity()).isOnline()) {
                    setProgressMessage("Placing order....");
                    showProgress();
                    setProgressCancel(false);
                    HashMap<String, StoreCartItem> getOrder = StoreMenuActivity.storeMenuActivity.getOrders();
                    List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                    int price = 0;
                    for (StoreCartItem value : getOrder.values()) {
                        ll.add(new JsonPurchaseOrderProduct()
                            .setProductId(value.getJsonStoreProduct().getProductId())
                            .setProductPrice(value.getFinalDiscountedPrice().movePointRight(2).intValue())
                            .setProductQuantity(value.getChildInput())
                            .setProductName(value.getJsonStoreProduct().getProductName()));
                        price += value.getChildInput() * value.getFinalDiscountedPrice().movePointRight(2).intValue();
                    }
                    JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                        .setCodeQR(codeQR)
                        .setQueueUserId(jsonProfile.getQueueUserId())
                        .setOrderPrice(String.valueOf(price));
                    jsonPurchaseOrder.setCustomerName(jsonProfile.getName());
                    jsonPurchaseOrder.setPurchaseOrderProducts(ll);
                    jsonPurchaseOrder.setDeliveryAddress(jsonProfile.getAddress());
                    jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.TO);
                    jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA);
                    jsonPurchaseOrder.setCustomerPhone(jsonProfile.getPhoneRaw());
                    jsonPurchaseOrder.setAdditionalNote("");
                    purchaseOrderApiCalls.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                    if (null != mAlertDialog && mAlertDialog.isShowing()) {
                        mAlertDialog.dismiss();
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(getActivity());
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
            // Navigate to order detail screen
            try {
                StoreMenuActivity.storeMenuActivity.updateAndCallPayment(jsonPurchaseOrder);
            }catch (Exception e){
                e.printStackTrace();
            }
            StoreMenuActivity.updateList();
        }
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej && eej.getSystemErrorCode().equalsIgnoreCase(MobileSystemErrorCodeEnum.USER_NOT_FOUND.getCode())) {
            btn_create_token.setClickable(true);
            new CustomToast().showToast(getActivity(), eej.getReason());
            Intent in = new Intent(getActivity(), LoginActivity.class);
            in.putExtra("phone_no", edt_mobile.getText().toString());
            in.putExtra("countryCode", countryCode);
            in.putExtra("countryShortName", countryShortName);
            startActivity(in);
            RegistrationActivity.registerCallBack = this;
            LoginActivity.loginCallBack = this;
        } else {
            new ErrorResponseHandler().processError(getActivity(), eej);
        }
    }
}
