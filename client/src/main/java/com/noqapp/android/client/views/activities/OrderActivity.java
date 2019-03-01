package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.PurchaseOrderPresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderHistorical;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.TimeZone;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter, ProfilePresenter, ProfileAddressPresenter {
    private RadioGroup rg_address;
    private TextView tv_address;
    private RelativeLayout rl_address;
    private EditText edt_phone, edt_add_address;
    private Button btn_add_address;
    private EditText edt_optional;
    private JsonPurchaseOrder jsonPurchaseOrder;
    private ProfileModel profileModel;
    private PurchaseApiModel purchaseApiModel;
    private long mLastClickTime = 0;
    private String currencySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TextView tv_user_name = findViewById(R.id.tv_user_name);
        TextView tv_total_order_amt = findViewById(R.id.tv_total_order_amt);
        TextView tv_tax_amt = findViewById(R.id.tv_tax_amt);
        TextView tv_due_amt = findViewById(R.id.tv_due_amt);
        rg_address = findViewById(R.id.rg_address);
        rl_address = findViewById(R.id.rl_address);
        tv_address = findViewById(R.id.tv_address);

        edt_add_address = findViewById(R.id.edt_add_address);
        btn_add_address = findViewById(R.id.btn_add_address);
        TextView tv_add_address = findViewById(R.id.tv_add_address);
        TextView tv_change_address = findViewById(R.id.tv_change_address);
        TextView tv_cancel = findViewById(R.id.tv_cancel);
        tv_change_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_address.setVisibility(View.VISIBLE);
            }
        });
        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_add_address.setError(null);
                if (TextUtils.isEmpty(edt_add_address.getText().toString())) {
                    edt_add_address.setError(getString(R.string.error_field_required));
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.show();
                        progressDialog.setMessage("Adding address in progress..");
                        profileModel.addProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), new JsonUserAddress().setAddress(edt_add_address.getText().toString()).setId(""));
                    }
                }
            }
        });
        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_add_address.setVisibility(View.VISIBLE);
                btn_add_address.setVisibility(View.VISIBLE);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_address.setVisibility(View.GONE);
            }
        });
        edt_phone = findViewById(R.id.edt_phone);
        edt_optional = findViewById(R.id.edt_optional);
        final Button tv_place_order = findViewById(R.id.tv_place_order);
        LinearLayout ll_order_details = findViewById(R.id.ll_order_details);
        initActionsViews(true);
        purchaseApiModel = new PurchaseApiModel(this);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
        currencySymbol = getIntent().getExtras().getString(AppUtilities.CURRENCY_SYMBOL);
        tv_toolbar_title.setText(getString(R.string.screen_order));
        tv_user_name.setText(NoQueueBaseActivity.getUserName());
        edt_phone.setText(NoQueueBaseActivity.getPhoneNo());
        tv_address.setText(NoQueueBaseActivity.getAddress());
        tv_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        profileModel = new ProfileModel();
        profileModel.setProfilePresenter(this);
        profileModel.setProfileAddressPresenter(this);
        tv_tax_amt.setText(currencySymbol + "" + "0.0");
        tv_due_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        tv_total_order_amt.setText(currencySymbol + "" + Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) / 100);
        for (int i = 0; i < jsonPurchaseOrder.getPurchaseOrderProducts().size(); i++) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName() + " " + currencySymbol + "" + (jsonPurchaseOrderProduct.getProductPrice() / 100) + " x " + String.valueOf(jsonPurchaseOrderProduct.getProductQuantity()));
            tv_total_price.setText(currencySymbol + "" + jsonPurchaseOrderProduct.getProductPrice() * jsonPurchaseOrderProduct.getProductQuantity() / 100);
            ll_order_details.addView(inflatedLayout);
        }
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                progressDialog.show();
                progressDialog.setMessage("Order placing in progress..");
                if (validateForm()) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.show();
                        progressDialog.setMessage("Order placing in progress..");
                        jsonPurchaseOrder.setDeliveryAddress(tv_address.getText().toString());
                        jsonPurchaseOrder.setDeliveryMode(DeliveryModeEnum.HD);
                        jsonPurchaseOrder.setPaymentMode(PaymentModeEnum.CA);
                        jsonPurchaseOrder.setCustomerPhone(edt_phone.getText().toString());
                        jsonPurchaseOrder.setAdditionalNote(edt_optional.getText().toString());

                        purchaseApiModel.purchase(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                        tv_place_order.setEnabled(false);
                        tv_place_order.setClickable(false);
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                        dismissProgress();
                    }
                } else {
                    dismissProgress();
                }
            }
        });
        JsonUserAddressList jsonUserAddressList = new JsonUserAddressList();
        jsonUserAddressList.setJsonUserAddresses(LaunchActivity.getUserProfile().getJsonUserAddresses());
        profileAddressResponse(jsonUserAddressList);
        rl_address.setVisibility(View.GONE);
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
        if (tv_address.getText().toString().equals("")) {
            tv_address.setError("Please enter delivery address.");
            isValid = false;
        } else {
            LatLng latLng_d = AppUtilities.getLocationFromAddress(this, tv_address.getText().toString());
            LatLng latLng_s = AppUtilities.getLocationFromAddress(this, getIntent().getExtras().getString("storeAddress"));
            if (null != latLng_d && null != latLng_s) {
                float distance = (float) AppUtilities.calculateDistance(
                        (float) latLng_s.latitude,
                        (float) latLng_s.longitude,
                        (float) latLng_d.latitude,
                        (float) latLng_d.longitude);
                if (jsonPurchaseOrder.getBusinessType() == BusinessTypeEnum.RS) {
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

        return isValid;
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPresentOrderState() == PurchaseOrderStateEnum.PO) {
                Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", jsonPurchaseOrder);
                bundle.putSerializable("oldData", this.jsonPurchaseOrder);
                bundle.putString("storeName", getIntent().getExtras().getString("storeName"));
                bundle.putString("storeAddress", getIntent().getExtras().getString("storeAddress"));
                bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
                bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, getIntent().getExtras().getString(NoQueueBaseActivity.KEY_CODE_QR));
                in.putExtras(bundle);
                startActivity(in);
                NoQueueMessagingService.subscribeTopics(getIntent().getExtras().getString("topic"));
                profileModel.setProfilePresenter(this);
                if (TextUtils.isEmpty(NoQueueBaseActivity.getAddress())) {
                    String address = tv_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(NoQueueBaseActivity.getUserName());
                    updateProfile.setBirthday(NoQueueBaseActivity.getUserDOB());
                    updateProfile.setGender(NoQueueBaseActivity.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(NoQueueBaseActivity.getUserProfile().getQueueUserId());
                    profileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
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
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {
        final List<JsonUserAddress> addressList = jsonUserAddressList.getJsonUserAddresses();
        JsonProfile jp = LaunchActivity.getUserProfile();
        jp.setJsonUserAddresses(addressList);
        LaunchActivity.setUserProfile(jp);
        Log.e("address list: ", addressList.toString());
        rg_address.removeAllViews();
        for (int i = 0; i < addressList.size(); i++) {

            LayoutInflater inflater = LayoutInflater.from(this);
            View radio_view = inflater.inflate(R.layout.list_item_radio, null, false);
            final AppCompatRadioButton rdbtn = radio_view.findViewById(R.id.acrb);
            rdbtn.setId((i * 2) + i);
            rdbtn.setTag(addressList.get(i).getId());
            rdbtn.setText(addressList.get(i).getAddress());
            rdbtn.setPadding(10, 20, 10, 20);
            rdbtn.setLayoutParams(
                    new RadioGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.FILL_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
            rdbtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (rdbtn.getRight() - rdbtn.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(OrderActivity.this);
                            builder.setTitle(null);
                            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
                            builder.setView(customDialogView);
                            final AlertDialog mAlertDialog = builder.create();
                            mAlertDialog.setCanceledOnTouchOutside(false);
                            TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
                            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
                            tvtitle.setText("Delete Address");
                            tv_msg.setText("Do you want to delete address from address list?");
                            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
                            Button btn_no = customDialogView.findViewById(R.id.btn_no);
                            View separator = customDialogView.findViewById(R.id.seperator);
                            btn_no.setVisibility(View.VISIBLE);
                            separator.setVisibility(View.VISIBLE);
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                                        progressDialog.show();
                                        progressDialog.setMessage("Deleting address..");
                                        profileModel.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(), new JsonUserAddress().setAddress(rdbtn.getText().toString()).setId(rdbtn.getTag().toString()));
                                    } else {
                                        ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                            btn_no.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    mAlertDialog.dismiss();
                                }
                            });
                            mAlertDialog.show();
                            return true;
                        }
                    }
                    return false;
                }
            });
            rg_address.addView(rdbtn);
        }
        rg_address.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton radioButtonChosen = group.findViewById(checkedId);
                tv_address.setText(radioButtonChosen.getText());
                rl_address.setVisibility(View.GONE);

            }
        });
        rl_address.setVisibility(View.VISIBLE);
        edt_add_address.setVisibility(View.GONE);
        btn_add_address.setVisibility(View.GONE);
        edt_add_address.setText("");
        dismissProgress();

    }

    @Override
    public void onBackPressed() {
        if (rl_address.getVisibility() == View.VISIBLE) {
            rl_address.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
