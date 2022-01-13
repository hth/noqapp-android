package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.api.ClientPreferenceApiImpl;
import com.noqapp.android.client.model.api.ClientProfileApiImpl;
import com.noqapp.android.client.model.api.PurchaseOrderApiImpl;
import com.noqapp.android.client.presenter.ClientPreferencePresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.ResponsePresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.StoreProductFinalOrderAdapter;
import com.noqapp.android.client.views.customviews.FixedHeightListView;
import com.noqapp.android.client.views.version_2.viewmodels.AfterJoinOrderViewModel;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserPreference;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.DiscountTypeEnum;
import com.noqapp.android.common.model.types.SupportedDeliveryEnum;
import com.noqapp.android.common.model.types.SupportedPaymentEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentMethodEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CashFreeNotifyPresenter;
import com.noqapp.android.common.utils.CommonHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter, ProfilePresenter,
        ResponsePresenter, CFClientInterface, CashFreeNotifyPresenter,
        StoreProductFinalOrderAdapter.CartOrderUpdate, ClientPreferencePresenter {
    private TextView tv_address;
    private EditText edt_optional;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private ClientProfileApiImpl clientProfileApiImpl;
    private PurchaseOrderApiImpl purchaseOrderApiImpl;
    private long mLastClickTime = 0;
    private String currencySymbol;
    private JsonPurchaseOrder jsonPurchaseOrderServer;
    private Button tv_place_order;
    private AppCompatRadioButton acrb_cash, acrb_online;
    private AppCompatRadioButton acrb_home_delivery, acrb_take_away;
    private boolean isProductWithoutPrice = false;
    private JsonUserAddress jsonUserAddress;
    private RelativeLayout rl_apply_coupon, rl_coupon_applied;
    private TextView tv_coupon_amount;
    private TextView tv_coupon_name;
    private JsonCoupon jsonCoupon;
    private TextView tv_grand_total_amt;
    private TextView tv_coupon_discount_amt;
    private TextView tv_coupon_discount_label;
    private TextView tv_total_order_amt;
    private TextView tv_due_amt;
    private TextView tv_final_amount;
    private LinearLayout ll_address;
    private RadioGroup rg_delivery;
    private JsonQueue jsonQueue;
    private AfterJoinOrderViewModel afterJoinOrderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        afterJoinOrderViewModel = new ViewModelProvider(this).get(AfterJoinOrderViewModel.class);
        tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        tv_due_amt = findViewById(R.id.tv_due_amt);
        tv_address = findViewById(R.id.tv_address);
        ll_address = findViewById(R.id.ll_address);
        FixedHeightListView lv_product = findViewById(R.id.lv_product);
        rg_delivery = findViewById(R.id.rg_delivery);
        acrb_cash = findViewById(R.id.acrb_cash);
        acrb_online = findViewById(R.id.acrb_online);
        acrb_home_delivery = findViewById(R.id.acrb_home_delivery);
        acrb_take_away = findViewById(R.id.acrb_take_away);
        rg_delivery.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.acrb_home_delivery) {
                ll_address.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.acrb_take_away) {
                ll_address.setVisibility(View.GONE);
            }
        });

        jsonQueue = (JsonQueue) getIntent().getExtras().getSerializable(IBConstant.KEY_JSON_QUEUE);
        Map<String, String> enabledPaymentOption = SupportedPaymentEnum.asMapWithNameAsKey(jsonQueue.getAcceptedPayments());
        acrb_cash.setEnabled(!TextUtils.isEmpty(enabledPaymentOption.get(SupportedPaymentEnum.COD.getName())));
        acrb_online.setEnabled(!TextUtils.isEmpty(enabledPaymentOption.get(SupportedPaymentEnum.ONP.getName())));
        if (TextUtils.isEmpty(enabledPaymentOption.get(SupportedPaymentEnum.COD.getName()))) {
            acrb_cash.setChecked(false);
        }
        if (TextUtils.isEmpty(enabledPaymentOption.get(SupportedPaymentEnum.ONP.getName()))) {
            acrb_online.setChecked(false);
        }

        AppCompatRadioButton acrb_home_delivery = (AppCompatRadioButton) rg_delivery.getChildAt(0);
        AppCompatRadioButton acrb_take_away = (AppCompatRadioButton) rg_delivery.getChildAt(1);
        Map<String, String> enabledDeliveryOptions = SupportedDeliveryEnum.asMapWithNameAsKey(jsonQueue.getAcceptedDeliveries());
        acrb_home_delivery.setEnabled(!TextUtils.isEmpty(enabledDeliveryOptions.get(SupportedDeliveryEnum.HOM.getName())));
        acrb_take_away.setEnabled(!TextUtils.isEmpty(enabledDeliveryOptions.get(SupportedDeliveryEnum.PIK.getName())));

        if (TextUtils.isEmpty(enabledDeliveryOptions.get(SupportedDeliveryEnum.HOM.getName()))) {
            acrb_home_delivery.setChecked(false);
        }
        if (TextUtils.isEmpty(enabledDeliveryOptions.get(SupportedDeliveryEnum.PIK.getName()))) {
            acrb_take_away.setChecked(false);
        }

        JsonUserPreference jsonUserPreference = NoQueueClientApplication.getUserProfile().getJsonUserPreference();
        if (jsonUserPreference != null)
            if (jsonUserPreference.getDeliveryMode() == DeliveryModeEnum.HD) {
                acrb_home_delivery.setChecked(true);
                acrb_take_away.setChecked(false);
                ll_address.setVisibility(View.VISIBLE);
            } else {
                acrb_home_delivery.setChecked(false);
                acrb_take_away.setChecked(true);
                ll_address.setVisibility(View.GONE);
            }

        if (jsonUserPreference != null)
            if (jsonUserPreference.getPaymentMethod() == PaymentMethodEnum.CA) {
                acrb_cash.setChecked(true);
                acrb_online.setChecked(false);
            } else {
                acrb_cash.setChecked(false);
                acrb_online.setChecked(true);
            }

        if (null != NoQueueClientApplication.getUserProfile() && null != NoQueueClientApplication.getUserProfile().getJsonUserAddresses()) {
            List<JsonUserAddress> jsonUserAddressList = NoQueueClientApplication.getUserProfile().getJsonUserAddresses();
            for (int i = 0; i < jsonUserAddressList.size(); i++) {
                if (jsonUserAddressList.get(i).isPrimaryAddress()) {
                    jsonUserAddress = jsonUserAddressList.get(i);
                    tv_address.setText(jsonUserAddress.getAddress());
                    NoQueueClientApplication.setSelectedAddressId(jsonUserAddress.getId());
                    break;
                }
            }
        }
        TextView tv_change_address = findViewById(R.id.tv_change_address);
        tv_change_address.setOnClickListener((View v) -> {
            Intent in = new Intent(OrderActivity.this, AddressListActivity.class);
            startActivityForResult(in, Constants.REQUEST_CODE_SELECT_ADDRESS);
        });
        edt_optional = findViewById(R.id.edt_optional);
        edt_optional.setOnTouchListener((view, event) -> {
            if (view.getId() == R.id.edt_optional) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });
        tv_place_order = findViewById(R.id.tv_place_order);
        tv_coupon_amount = findViewById(R.id.tv_coupon_amount);
        tv_coupon_name = findViewById(R.id.tv_coupon_name);
        rl_apply_coupon = findViewById(R.id.rl_apply_coupon);
        rl_coupon_applied = findViewById(R.id.rl_coupon_applied);
        tv_coupon_discount_amt = findViewById(R.id.tv_coupon_discount_amt);
        tv_coupon_discount_label = findViewById(R.id.tv_coupon_discount_label);
        tv_grand_total_amt = findViewById(R.id.tv_grand_total_amt);
        tv_final_amount = findViewById(R.id.tv_final_amount);
        rl_apply_coupon.setOnClickListener((View v) -> {
            if (null != jsonPurchaseOrder && jsonPurchaseOrder.isDiscountedPurchase()) {
                new CustomToast().showToast(OrderActivity.this, getString(R.string.discount_error));
            } else {
                Intent in = new Intent(OrderActivity.this, CouponsActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, jsonPurchaseOrder.getCodeQR());
                startActivityForResult(in, Constants.ACTIVITY_RESULT_BACK);
            }
        });
        TextView tv_remove_coupon = findViewById(R.id.tv_remove_coupon);
        tv_remove_coupon.setOnClickListener((View v) -> {
            ShowCustomDialog showDialog = new ShowCustomDialog(OrderActivity.this, true);
            showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick() {
                    jsonCoupon = null;
                    rl_apply_coupon.setVisibility(View.VISIBLE);
                    rl_coupon_applied.setVisibility(View.GONE);
                    tv_coupon_amount.setText("");
                    tv_coupon_name.setText("");
                    jsonPurchaseOrder.setStoreDiscount(0);
                    jsonPurchaseOrder.setCouponId("");
                    tv_coupon_discount_label.setText(getString(R.string.discount));
                    updateDiscountUI();
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showDialog.displayDialog("Remove coupon", "Do you want to remove the coupon?");
        });
        initActionsViews(true);
        purchaseOrderApiImpl = new PurchaseOrderApiImpl(this);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        currencySymbol = getIntent().getExtras().getString(AppUtils.CURRENCY_SYMBOL);
        tv_toolbar_title.setText(getString(R.string.screen_order));
        //tv_address.setText(NoQueueBaseActivity.getAddress());
        tv_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        clientProfileApiImpl = new ClientProfileApiImpl();
        clientProfileApiImpl.setProfilePresenter(this);
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_tax_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getTax()));
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.total()));
        tv_grand_total_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.grandTotal()));
        tv_final_amount.setText("Grand Total \n" + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.grandTotal()));
        // tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        if (jsonPurchaseOrder.getStoreDiscount() > 0) {
            String couponName = jsonPurchaseOrder.getJsonCoupon().getDiscountName();
            tv_coupon_discount_label.setText(getString(R.string.discount_with_coupon, couponName));
        }
        tv_coupon_discount_amt.setText(Constants.DASH + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        StoreProductFinalOrderAdapter storeProductFinalOrderAdapter = new StoreProductFinalOrderAdapter(
                this,
                jsonPurchaseOrder.getPurchaseOrderProducts(),
                this,
                currencySymbol);

        lv_product.setAdapter(storeProductFinalOrderAdapter);
        checkProductWithZeroPrice();

        tv_place_order.setOnClickListener((View v) -> {
            if (NoQueueClientApplication.getUserProfile().isAccountValidated()) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showProgress();
                setProgressCancel(false);
                setProgressMessage("Order placing in progress...");
                if (validateForm()) {
                    if (isProductWithoutPrice) {
                        new CustomToast().showToast(OrderActivity.this, "Cannot process as merchant has not set product price");
                    } else {
                        if (isOnline()) {
                            if (acrb_home_delivery.isChecked()) {
                                if (null == jsonUserAddress) {
                                    ShowAlertInformation.showInfoDisplayDialog(this, "Address Required", "Please select delivery address.");
                                }
                            }

                            showProgress();
                            setProgressMessage("Order placing in progress...");
                            jsonPurchaseOrder
                                    .setUserAddressId(acrb_home_delivery.isChecked() ? jsonUserAddress.getId() : null)
                                    .setDeliveryMode(acrb_home_delivery.isChecked() ? DeliveryModeEnum.HD : DeliveryModeEnum.TO)
                                    .setPaymentMode(null) //not required here
                                    .setCustomerPhone(NoQueueClientApplication.getPhoneNo())
                                    .setAdditionalNote(StringUtils.isBlank(edt_optional.getText().toString()) ? null : edt_optional.getText().toString());
                            purchaseOrderApiImpl.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                            enableDisableOrderButton(false);
                        } else {
                            ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                            dismissProgress();
                        }
                    }
                } else {
                    dismissProgress();
                }
            } else {
                new CustomToast().showToast(OrderActivity.this, "Please verify email address. Go to profile to verify.");
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_SELECT_ADDRESS && data != null) {
            if (null != data.getExtras()) {
                jsonUserAddress = (JsonUserAddress) data.getSerializableExtra(Constants.JSON_USER_ADDRESS);
                if (null != jsonUserAddress) {
                    tv_address.setText(jsonUserAddress.getAddress());
                }
            }
        }
        if (requestCode == Constants.ACTIVITY_RESULT_BACK) {
            if (resultCode == RESULT_OK) {
                jsonCoupon = (JsonCoupon) data.getSerializableExtra(IBConstant.KEY_DATA_OBJECT);
                Log.e("data receive", jsonCoupon.toString());
                rl_coupon_applied.setVisibility(View.VISIBLE);
                rl_apply_coupon.setVisibility(View.GONE);
                if (DiscountTypeEnum.F == jsonCoupon.getDiscountType()) {
                    tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonCoupon.getDiscountAmount()));
                    jsonPurchaseOrder.setStoreDiscount(jsonCoupon.getDiscountAmount());
                } else {
                    int computedDiscount = Integer.parseInt(jsonPurchaseOrder.getOrderPrice()) * jsonCoupon.getDiscountAmount() / 100;
                    tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(computedDiscount));
                    jsonPurchaseOrder.setStoreDiscount(computedDiscount);
                }
                tv_coupon_name.setText(jsonCoupon.getDiscountName());
                jsonPurchaseOrder.setCouponId(jsonCoupon.getCouponId());
                updateDiscountUI();
                if (jsonPurchaseOrder.getStoreDiscount() > 0) {
                    tv_coupon_discount_label.setText(getString(R.string.discount_with_coupon, jsonCoupon.getDiscountName()));
                }
            }
        }
    }

    private void updateDiscountUI() {
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_grand_total_amt.setText(currencySymbol + jsonPurchaseOrder.computeFinalAmountWithDiscountOffline());
        tv_final_amount.setText("Grand Total \n" + currencySymbol + jsonPurchaseOrder.computeFinalAmountWithDiscountOffline());
        // tv_coupon_amount.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        tv_coupon_discount_amt.setText(Constants.DASH + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getStoreDiscount()));
        tv_due_amt.setText(currencySymbol + jsonPurchaseOrder.computeFinalAmountWithDiscountOffline());
    }

    public void checkProductWithZeroPrice() {
        isProductWithoutPrice = false;
        if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getPurchaseOrderProducts() && jsonPurchaseOrder.getPurchaseOrderProducts().size() > 0) {
            for (JsonPurchaseOrderProduct jpop : jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
            }
        }
    }

    private boolean isAddressRequired() {
        return acrb_home_delivery.isChecked();
    }

    private boolean validateForm() {
        boolean isValid = true;
        tv_address.setError(null);
        if (!NoQueueClientApplication.isEmailVerified()) {
            ShowAlertInformation.showInfoDisplayDialog(this, "Email Required", "To pay, email is mandatory. In your profile add and verify email");
            isValid = false;
        }
        if (isAddressRequired()) {
            if (tv_address.getText().toString().equals("")) {
                ShowAlertInformation.showInfoDisplayDialog(this, "Address Required", "Please enter delivery address.");
                isValid = false;
            } else {
                String storeGeoHash = getIntent().getExtras().getString("GeoHash");
                if (!TextUtils.isEmpty(storeGeoHash)) {
                    if (null == jsonUserAddress || TextUtils.isEmpty(jsonUserAddress.getGeoHash())) {
                        ShowAlertInformation.showInfoDisplayDialog(this, "Address not valid", "Please select a valid address");
                        isValid = false;
                    } else {
                        float lat_s = (float) GeoHashUtils.decodeLatitude(storeGeoHash);
                        float long_s = (float) GeoHashUtils.decodeLongitude(storeGeoHash);
                        float lat_d = (float) GeoHashUtils.decodeLatitude(jsonUserAddress.getGeoHash());
                        float long_d = (float) GeoHashUtils.decodeLongitude(jsonUserAddress.getGeoHash());
                        float distance = (float) AppUtils.calculateDistance(lat_s, long_s, lat_d, long_d);
                        switch (jsonPurchaseOrder.getBusinessType()) {
                            case RS:
                            case FT:
                                if (distance > getIntent().getExtras().getInt("deliveryRange")) {
                                    ShowAlertInformation.showInfoDisplayDialog(this, "Address too far", "Please change the address. This address is very far from the store");
                                    isValid = false;
                                }
                                break;
                            default:
                                if (distance > 150) { // Set for washing car stores
                                    ShowAlertInformation.showInfoDisplayDialog(this, "Address too far", "Please change the address. This address is very far from the store");
                                    isValid = false;
                                }
                        }
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.VB) {
                jsonPurchaseOrderServer = jsonPurchaseOrder;
                if (acrb_online.isChecked()) {
                    triggerOnlinePayment();
                } else {
                    triggerCashPayment();
                }
                clientProfileApiImpl.setProfilePresenter(this);
                if (TextUtils.isEmpty(NoQueueClientApplication.getAddress())) {
                    String address = tv_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setFirstName(NoQueueClientApplication.getUserName());
                    updateProfile.setBirthday(NoQueueClientApplication.getUserDOB());
                    updateProfile.setGender(NoQueueClientApplication.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(NoQueueClientApplication.getUserProfile().getQueueUserId());
                    clientProfileApiImpl.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
            } else {
                new CustomToast().showToast(this, "Order failed.");
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void payCashResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
            new CustomToast().showToast(this, "Order placed successfully. Pay the amount in cash at counter");
            Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", jsonPurchaseOrder);
            bundle.putBoolean(IBConstant.KEY_FROM_LIST, true);
            bundle.putString("displayCurrentServing", jsonPurchaseOrder.getDisplayServingNumber());
            bundle.putSerializable(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            bundle.putInt("token", jsonPurchaseOrder.getToken());
            bundle.putSerializable("oldData", this.jsonPurchaseOrder);
            bundle.putString("GeoHash", getIntent().getExtras().getString("GeoHash"));
            bundle.putString(IBConstant.KEY_STORE_NAME, getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
            bundle.putString(IBConstant.KEY_STORE_ADDRESS, getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));
            bundle.putString(AppUtils.CURRENCY_SYMBOL, currencySymbol);
            bundle.putString(IBConstant.KEY_CODE_QR, getIntent().getExtras().getString(IBConstant.KEY_CODE_QR));
            in.putExtras(bundle);
            startActivity(in);
            FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic") + "_A");
            callAddressPreference();
        } else {
            new CustomToast().showToast(this, jsonPurchaseOrder.getTransactionMessage());
        }
    }


    @Override
    public void purchaseOrderCancelResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        // implementation not required here
        dismissProgress();
    }

    @Override
    public void purchaseOrderActivateResponse(JsonPurchaseOrderHistorical jsonPurchaseOrderHistorical) {
        // implementation not required here
        dismissProgress();
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueClientApplication.commitProfile(profile, email, auth);
    }

    @Override
    public void profileError() {
        dismissProgress();
    }


    @Override
    public void onSuccess(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        for (Map.Entry entry : map.entrySet()) {
            Log.e("Payment success", entry.getKey() + " " + entry.getValue());
        }
        purchaseOrderApiImpl.setCashFreeNotifyPresenter(this);
        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
        jsonCashfreeNotification.setTxMsg(map.get("txMsg"));
        jsonCashfreeNotification.setTxTime(map.get("txTime"));
        jsonCashfreeNotification.setReferenceId(map.get("referenceId"));
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount"));
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));
        jsonCashfreeNotification.setOrderId(map.get("orderId"));
        purchaseOrderApiImpl.cashFreeNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        new CustomToast().showToast(this, "Transaction failed");
        enableDisableOrderButton(false);
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        enableDisableOrderButton(false);
        if (isOnline()) {
            showProgress();
            purchaseOrderApiImpl.setResponsePresenter(this);
            purchaseOrderApiImpl.cancelPayBeforeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrderServer);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void responsePresenterResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == Constants.SUCCESS) {
                new CustomToast().showToast(this, getString(R.string.cancel_order));
            } else {
                new CustomToast().showToast(this, getString(R.string.fail_to_cancel_order));
            }
        } else {
            new CustomToast().showToast(this, getString(R.string.fail_to_cancel_order));
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(getIntent().getExtras().getString("topic") + "_A");
        if (null != jsonPurchaseOrderServer) {
            afterJoinOrderViewModel.deleteTokenAndQueue(jsonPurchaseOrderServer.getCodeQR(), String.valueOf(jsonPurchaseOrderServer.getToken()));
        }
        onBackPressed();
        dismissProgress();
    }

    @Override
    public void responsePresenterError() {
        Log.d("", "responsePresenterError");
        dismissProgress();
    }

    private void enableDisableOrderButton(boolean isEnable) {
        tv_place_order.setEnabled(isEnable);
        tv_place_order.setClickable(isEnable);
        tv_place_order.setBackground(ContextCompat.getDrawable(this, isEnable ? R.drawable.btn_bg_enable : R.drawable.btn_bg_inactive));
        tv_place_order.setTextColor(ContextCompat.getColor(this, isEnable ? R.color.white : R.color.btn_color));
    }

    private void triggerOnlinePayment() {
        String token = jsonPurchaseOrderServer.getJsonResponseWithCFToken().getCftoken();
        String stage = BuildConfig.CASHFREE_STAGE;
        String appId = BuildConfig.CASHFREE_APP_ID;
        String orderId = jsonPurchaseOrderServer.getTransactionId();
        String orderAmount = jsonPurchaseOrderServer.getJsonResponseWithCFToken().getOrderAmount();
        String orderNote = "Test Order";
        String customerName = NoQueueClientApplication.getCustomerNameWithQid(NoQueueClientApplication.getUserName(), NoQueueClientApplication.getUserProfile().getQueueUserId());
        String customerPhone = NoQueueClientApplication.getOfficePhoneNo();
        String customerEmail = NoQueueClientApplication.getOfficeMail();

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_APP_ID, appId);
        params.put(PARAM_ORDER_ID, orderId);
        params.put(PARAM_ORDER_AMOUNT, orderAmount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, customerName);
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);
        // params.put(PARAM_PAYMENT_MODES,"CC");
//        for (Map.Entry entry : params.entrySet()) {
////            Log.d("CFSKDSample", entry.getKey() + " " + entry.getValue());
////        }

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);
        cfPaymentService.setConfirmOnExit(true);
        cfPaymentService.doPayment(this, params, token, this, stage);

    }

    private void triggerCashPayment() {
        jsonPurchaseOrderServer.setPaymentMode(PaymentModeEnum.CA);
        purchaseOrderApiImpl.payCash(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrderServer);
    }

    @Override
    public void cashFreeNotifyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            new CustomToast().showToast(this, "Order placed successfully.");
            Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", jsonPurchaseOrder);
            bundle.putSerializable("oldData", this.jsonPurchaseOrder);
            bundle.putBoolean(IBConstant.KEY_FROM_LIST, true);
            bundle.putString("displayCurrentServing", jsonPurchaseOrder.getDisplayServingNumber());
            bundle.putInt("token", jsonPurchaseOrder.getToken());
            bundle.putSerializable(IBConstant.KEY_JSON_TOKEN_QUEUE, jsonQueue.getJsonTokenAndQueue());
            bundle.putString("GeoHash", getIntent().getExtras().getString("GeoHash"));
            bundle.putString(IBConstant.KEY_STORE_NAME, getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
            bundle.putString(IBConstant.KEY_STORE_ADDRESS, getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));
            bundle.putString(AppUtils.CURRENCY_SYMBOL, currencySymbol);
            bundle.putString(IBConstant.KEY_CODE_QR, getIntent().getExtras().getString(IBConstant.KEY_CODE_QR));
            in.putExtras(bundle);
            startActivity(in);
            FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic") + "_A");
            callAddressPreference();
        } else {
            new CustomToast().showToast(this, jsonPurchaseOrder.getTransactionMessage());
        }

    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        super.responseErrorPresenter(errorCode);
        enableDisableOrderButton(true);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        super.responseErrorPresenter(eej);
        enableDisableOrderButton(true);
    }

    @Override
    public void updateCartOrderInfo(List<JsonPurchaseOrderProduct> list, String cartAmount) {
        jsonPurchaseOrder.setPurchaseOrderProducts(list);
        jsonPurchaseOrder.setOrderPrice(cartAmount.replace(".", ""));
        updateDiscountUI();
    }

    private void callAddressPreference() {
        ClientPreferenceApiImpl clientProfileApiCall = new ClientPreferenceApiImpl();
        clientProfileApiCall.setClientPreferencePresenter(this);
        JsonUserPreference jsonUserPreference = NoQueueClientApplication.getUserProfile().getJsonUserPreference();
        jsonUserPreference.setDeliveryMode(acrb_home_delivery.isChecked() ? DeliveryModeEnum.HD : DeliveryModeEnum.TO);
        jsonUserPreference.setPaymentMethod(acrb_cash.isChecked() ? PaymentMethodEnum.CA : PaymentMethodEnum.EL);
        if (null != jsonUserAddress) {
            jsonUserPreference.setUserAddressId(jsonUserAddress.getId());
        }
        clientProfileApiCall.order(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonUserPreference);
    }

    @Override
    public void clientPreferencePresenterResponse(JsonUserPreference jsonUserPreference) {
        if (null != jsonUserPreference) {
            JsonProfile jp = NoQueueClientApplication.getUserProfile();
            jp.setJsonUserPreference(jsonUserPreference);
            NoQueueClientApplication.setUserProfile(jp);
        }
    }
}
