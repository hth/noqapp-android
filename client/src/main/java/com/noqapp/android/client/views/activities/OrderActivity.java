package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ClientProfileApiCall;
import com.noqapp.android.client.model.PurchaseOrderApiCall;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.GeoHashUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.beans.payment.cashfree.JsonCashfreeNotification;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.presenter.CashFreeNotifyPresenter;
import com.noqapp.android.common.utils.CommonHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import androidx.appcompat.widget.AppCompatRadioButton;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter, ProfilePresenter, CFClientInterface, CashFreeNotifyPresenter {
    private TextView tv_address;
    private EditText edt_phone;
    private EditText edt_optional;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private ClientProfileApiCall clientProfileApiCall;
    private PurchaseOrderApiCall purchaseOrderApiCall;
    private long mLastClickTime = 0;
    private String currencySymbol;
    private JsonPurchaseOrder jsonPurchaseOrderServer;
    private Button tv_place_order;
    private AppCompatRadioButton acrb_cash, acrb_online;
    private boolean isProductWithoutPrice = false;
    private JsonUserAddress jsonUserAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView tv_user_name = findViewById(R.id.tv_user_name);
        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        tv_address = findViewById(R.id.tv_address);

        acrb_cash = findViewById(R.id.acrb_cash);
        acrb_online = findViewById(R.id.acrb_online);

        TextView tv_add_address = findViewById(R.id.tv_add_address);
        TextView tv_change_address = findViewById(R.id.tv_change_address);
        tv_change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(OrderActivity.this,AddressBookActivity.class);
                startActivityForResult(in,78);
            }
        });
        edt_phone = findViewById(R.id.edt_phone);
        edt_optional = findViewById(R.id.edt_optional);
        tv_place_order = findViewById(R.id.tv_place_order);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);
        initActionsViews(true);
        purchaseOrderApiCall = new PurchaseOrderApiCall(this);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable(IBConstant.KEY_DATA);
        currencySymbol = getIntent().getExtras().getString(AppUtilities.CURRENCY_SYMBOL);
        tv_toolbar_title.setText(getString(R.string.screen_order));
        tv_user_name.setText(NoQueueBaseActivity.getUserName());
        edt_phone.setText(NoQueueBaseActivity.getPhoneNo());
        tv_address.setText(NoQueueBaseActivity.getAddress());
        tv_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        clientProfileApiCall = new ClientProfileApiCall();
        clientProfileApiCall.setProfilePresenter(this);
        tv_tax_amt.setText(currencySymbol + "0.00");
        tv_due_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        tv_total_order_amt.setText(currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice()));
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + currencySymbol + CommonHelper.displayPrice(jsonPurchaseOrderProduct.getProductPrice()) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(currencySymbol + CommonHelper.displayPrice(new BigDecimal(jsonPurchaseOrderProduct.getProductPrice()).multiply(new BigDecimal(jsonPurchaseOrderProduct.getProductQuantity())).toString()));
            ll_order_details.addView(inflatedLayout);
        }
        checkProductWithZeroPrice();
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoQueueBaseActivity.getUserProfile().isAccountValidated()) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    progressDialog.show();
                    progressDialog.setMessage("Order placing in progress..");
                    if (validateForm()) {
                        if (isProductWithoutPrice) {
                            Toast.makeText(OrderActivity.this, "Merchant have not set the price of the product.Hence payment cann't be proceed ", Toast.LENGTH_LONG).show();
                        } else {
                            if (LaunchActivity.getLaunchActivity().isOnline()) {
                                progressDialog.show();
                                progressDialog.setMessage("Order placing in progress..");

                                jsonPurchaseOrder.setDeliveryAddress(tv_address.getText().toString());
                                jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.HD);
                                jsonPurchaseOrder.setPaymentMode(null); //not required here
                                jsonPurchaseOrder.setCustomerPhone(edt_phone.getText().toString());
                                jsonPurchaseOrder.setAdditionalNote(edt_optional.getText().toString());
                                purchaseOrderApiCall.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
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
                    Toast.makeText(OrderActivity.this, "Please add email id to your profile, if not added & verify it", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 78) {
            if (null != data.getExtras()) {
                jsonUserAddress = (JsonUserAddress) data.getExtras().getSerializable("jsonUserAddress");
                if (null != jsonUserAddress) {
                    tv_address.setText(jsonUserAddress.getAddress());
                }
            }
        }
    }

    public void checkProductWithZeroPrice() {
        isProductWithoutPrice = false;
        if (null != jsonPurchaseOrder && null != jsonPurchaseOrder.getPurchaseOrderProducts() && jsonPurchaseOrder.getPurchaseOrderProducts().size() > 0) {
            for (JsonPurchaseOrderProduct jpop :
                    jsonPurchaseOrder.getPurchaseOrderProducts()) {
                if (jpop.getProductPrice() == 0) {
                    isProductWithoutPrice = true;
                    break;
                }
            }
        }
    }


    private boolean validateForm() {
        boolean isValid = true;
        edt_phone.setError(null);
        tv_address.setError(null);
        if (edt_phone.getText().toString().equals("")) {
            edt_phone.setError("Please enter mobile no.");
            isValid = false;
        }
        if (!edt_phone.getText().toString().equals("") && edt_phone.getText().length() < 10) {
            edt_phone.setError("Please enter valid mobile no.");
            isValid = false;
        }
        if (!NoQueueBaseActivity.isEmailVerified()) {
            Toast.makeText(this, "Email is mandatory. Please add and verify it", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (tv_address.getText().toString().equals("")) {
            tv_address.setError("Please enter delivery address.");
            isValid = false;
        } else {
            String storeGeoHash = getIntent().getExtras().getString("GeoHash");
            if (!TextUtils.isEmpty(storeGeoHash)) {
                if (null == jsonUserAddress || TextUtils.isEmpty(jsonUserAddress.getGeoHash())) {
                    tv_address.setError("Please select a valid address");
                    isValid = false;
                } else {
                    float lat_s = (float) GeoHashUtils.decodeLatitude(storeGeoHash);
                    float long_s = (float) GeoHashUtils.decodeLongitude(storeGeoHash);
                    float lat_d = (float) GeoHashUtils.decodeLatitude(jsonUserAddress.getGeoHash());
                    float long_d = (float) GeoHashUtils.decodeLongitude(jsonUserAddress.getGeoHash());
                    float distance = (float) AppUtilities.calculateDistance(lat_s, long_s, lat_d, long_d);
                    if (BusinessTypeEnum.RS == jsonPurchaseOrder.getBusinessType()) {
                        if (distance > getIntent().getExtras().getInt("deliveryRange")) {
                            tv_address.setError("Please change the address. This address is very far from the store");
                            isValid = false;
                        }
                    } else {
                        if (distance > 150) { // Set for washing car stores
                            tv_address.setError("Please change the address. This address is very far from the store");
                            isValid = false;
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
                clientProfileApiCall.setProfilePresenter(this);
                if (TextUtils.isEmpty(NoQueueBaseActivity.getAddress())) {
                    String address = tv_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(NoQueueBaseActivity.getUserName());
                    updateProfile.setBirthday(NoQueueBaseActivity.getUserDOB());
                    updateProfile.setGender(NoQueueBaseActivity.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(NoQueueBaseActivity.getUserProfile().getQueueUserId());
                    clientProfileApiCall.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
            } else {
                Toast.makeText(this, "Order failed.", Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
        dismissProgress();
    }

    @Override
    public void payCashResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (PaymentStatusEnum.PP == jsonPurchaseOrder.getPaymentStatus()) {
            Toast.makeText(this, "Order placed successfully. Pay the amount in cash at counter", Toast.LENGTH_LONG).show();
            Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", jsonPurchaseOrder);
            bundle.putSerializable("oldData", this.jsonPurchaseOrder);
            bundle.putString(IBConstant.KEY_STORE_NAME, getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
            bundle.putString(IBConstant.KEY_STORE_ADDRESS, getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));
            bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
            bundle.putString(IBConstant.KEY_CODE_QR, getIntent().getExtras().getString(IBConstant.KEY_CODE_QR));
            in.putExtras(bundle);
            startActivity(in);
            NoQueueMessagingService.subscribeTopics(getIntent().getExtras().getString("topic"));
        } else {
            Toast.makeText(this, jsonPurchaseOrder.getTransactionMessage(), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Failed to notify server.", Toast.LENGTH_LONG).show();
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
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void profileResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
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
        purchaseOrderApiCall.setCashFreeNotifyPresenter(this);
        JsonCashfreeNotification jsonCashfreeNotification = new JsonCashfreeNotification();
        jsonCashfreeNotification.setTxMsg(map.get("txMsg"));
        jsonCashfreeNotification.setTxTime(map.get("txTime"));
        jsonCashfreeNotification.setReferenceId(map.get("referenceId"));
        jsonCashfreeNotification.setPaymentMode(map.get("paymentMode"));
        jsonCashfreeNotification.setSignature(map.get("signature"));
        jsonCashfreeNotification.setOrderAmount(map.get("orderAmount"));
        jsonCashfreeNotification.setTxStatus(map.get("txStatus"));
        jsonCashfreeNotification.setOrderId(map.get("orderId"));
        purchaseOrderApiCall.cashFreeNotify(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonCashfreeNotification);
    }

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        Toast.makeText(this, "Transaction failed", Toast.LENGTH_LONG).show();
        enableDisableOrderButton(false);
    }

    @Override
    public void onNavigateBack() {
        Log.e("User Navigate Back", "Back without payment");
        Toast.makeText(this, "Cancelled transaction. Please try again.", Toast.LENGTH_LONG).show();
        enableDisableOrderButton(false);
    }

    private void enableDisableOrderButton(boolean enable) {
        tv_place_order.setEnabled(enable);
        tv_place_order.setClickable(enable);
    }

    private void triggerOnlinePayment() {
        String token = jsonPurchaseOrderServer.getJsonResponseWithCFToken().getCftoken();
        String stage = BuildConfig.CASHFREE_STAGE;
        String appId = Constants.appId;
        String orderId = jsonPurchaseOrderServer.getTransactionId();
        String orderAmount = jsonPurchaseOrderServer.getJsonResponseWithCFToken().getOrderAmount();
        String orderNote = "Test Order";
        String customerName = LaunchActivity.getUserName();
        String customerPhone = LaunchActivity.getPhoneNo();
        String customerEmail = LaunchActivity.getActualMail();

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
        //Toast.makeText(this, "Call Cash API", Toast.LENGTH_LONG).show();
        jsonPurchaseOrderServer.setPaymentMode(PaymentModeEnum.CA);
        purchaseOrderApiCall.payCash(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrderServer);
    }

    @Override
    public void cashFreeNotifyResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (PaymentStatusEnum.PA == jsonPurchaseOrder.getPaymentStatus()) {
            Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
            Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", jsonPurchaseOrder);
            bundle.putSerializable("oldData", this.jsonPurchaseOrder);
            bundle.putString(IBConstant.KEY_STORE_NAME, getIntent().getExtras().getString(IBConstant.KEY_STORE_NAME));
            bundle.putString(IBConstant.KEY_STORE_ADDRESS, getIntent().getExtras().getString(IBConstant.KEY_STORE_ADDRESS));
            bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
            bundle.putString(IBConstant.KEY_CODE_QR, getIntent().getExtras().getString(IBConstant.KEY_CODE_QR));
            in.putExtras(bundle);
            startActivity(in);
            NoQueueMessagingService.subscribeTopics(getIntent().getExtras().getString("topic"));
        } else {
            Toast.makeText(this, jsonPurchaseOrder.getTransactionMessage(), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "Failed to notify server.", Toast.LENGTH_LONG).show();
        }

    }
}
