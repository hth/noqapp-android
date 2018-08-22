package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.ProfileAddressPresenter;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonUserAddress;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.body.UpdateProfile;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.common.beans.order.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.order.DeliveryTypeEnum;
import com.noqapp.android.common.model.types.order.PaymentTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;
import java.util.TimeZone;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter,ProfilePresenter, ProfileAddressPresenter {

    @BindView(R.id.tv_user_name)
    protected TextView tv_user_name;
    @BindView(R.id.tv_total_order_amt)
    protected TextView tv_total_order_amt;
    @BindView(R.id.tv_tax_amt)
    protected TextView tv_tax_amt;
    @BindView(R.id.tv_due_amt)
    protected TextView tv_due_amt;
    @BindView(R.id.rg_address)
    protected RadioGroup rg_address;
    @BindView(R.id.edt_address)
    protected EditText edt_address;
    @BindView(R.id.edt_phone)
    protected EditText edt_phone;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
    @BindView(R.id.ll_order_details)
    protected LinearLayout ll_order_details;

    private JsonPurchaseOrder jsonPurchaseOrder;
    private ProfileModel profileModel;
    private PurchaseApiModel purchaseApiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initActionsViews(false);
        purchaseApiModel = new PurchaseApiModel(this);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
        tv_toolbar_title.setText(getString(R.string.screen_order));
        tv_user_name.setText(NoQueueBaseActivity.getUserName());
        edt_phone.setText(NoQueueBaseActivity.getPhoneNo());
        edt_address.setText(NoQueueBaseActivity.getAddress());
        edt_address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        profileModel = new ProfileModel();
        profileModel.setProfilePresenter(this);
        profileModel.setProfileAddressPresenter(this);
        tv_tax_amt.setText(getString(R.string.rupee)+""+"0.0");
        tv_due_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        tv_total_order_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        for (int i =0; i< jsonPurchaseOrder.getPurchaseOrderProducts().size();i++){
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_qty = inflatedLayout.findViewById(R.id.tv_qty);
            TextView tv_price = inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_total_price = inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getProductName());
            tv_qty.setText("Quantity: " + jsonPurchaseOrderProduct.getProductQuantity());
            tv_price.setText(getString(R.string.rupee) + "" + jsonPurchaseOrderProduct.getProductPrice() / 100);
            tv_total_price.setText(getString(R.string.rupee) + "" + jsonPurchaseOrderProduct.getProductPrice() * jsonPurchaseOrderProduct.getProductQuantity() / 100);
            ll_order_details.addView(inflatedLayout);
        }
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        progressDialog.show();
                        progressDialog.setMessage("Order placing in progress..");
                        jsonPurchaseOrder.setDeliveryAddress(edt_address.getText().toString());
                        jsonPurchaseOrder.setDeliveryType(DeliveryTypeEnum.HD);
                        jsonPurchaseOrder.setPaymentType(PaymentTypeEnum.CA);
                        jsonPurchaseOrder.setCustomerPhone(edt_phone.getText().toString());

                        purchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                    }
                }
            }
        });
        if (LaunchActivity.getLaunchActivity().isOnline() ){//&& !NoQueueBaseActivity.getAddress().equals(edt_address.getText().toString())) {
            profileModel.getProfileAllAddress(UserUtils.getEmail(), UserUtils.getAuth());
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        edt_phone.setError(null);
        edt_address.setError(null);
        if (edt_phone.getText().toString().equals("")) {
            edt_phone.setError("Please enter mobile no.");
            isValid = false;
        }
        if (!edt_phone.getText().toString().equals("") && edt_phone.getText().length() < 10) {
            edt_phone.setError("Please enter valid mobile no.");
            isValid = false;
        }
        if (edt_address.getText().toString().equals("")) {
            edt_address.setError("Please enter delivery address.");
            isValid = false;
        } else {
            LatLng latLng_d = AppUtilities.getLocationFromAddress(this, edt_address.getText().toString());
            LatLng latLng_s = AppUtilities.getLocationFromAddress(this, getIntent().getExtras().getString("storeAddress"));
            if (null != latLng_d && null != latLng_s) {
                float distance = (float) AppUtilities.calculateDistance(
                        (float) latLng_s.latitude,
                        (float) latLng_s.longitude,
                        (float) latLng_d.latitude,
                        (float) latLng_d.longitude);
                if (distance > getIntent().getExtras().getInt("deliveryRange")) {
                    edt_address.setError("Please change the address. This address is very far from the store");
                    isValid = false;

                }
                Toast.makeText(this, "distance is:" + distance, Toast.LENGTH_LONG).show();
            }
        }

        return isValid;
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrder jsonPurchaseOrder) {
        if (null != jsonPurchaseOrder) {
            if (jsonPurchaseOrder.getPurchaseOrderState() == PurchaseOrderStateEnum.PO) {
                Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(OrderActivity.this, OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", jsonPurchaseOrder);
                bundle.putSerializable("oldData", this.jsonPurchaseOrder);
                bundle.putString("storeName", getIntent().getExtras().getString("storeName"));
                bundle.putString("storeAddress", getIntent().getExtras().getString("storeAddress"));
                in.putExtras(bundle);
                startActivity(in);
                NoQueueMessagingService.subscribeTopics(getIntent().getExtras().getString("topic"));
                profileModel.setProfilePresenter(this);
                if (TextUtils.isEmpty(NoQueueBaseActivity.getAddress())) {
                    String address = edt_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(NoQueueBaseActivity.getUserName());
                    updateProfile.setBirthday(NoQueueBaseActivity.getUserDOB());
                    updateProfile.setGender(NoQueueBaseActivity.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    updateProfile.setQueueUserId(NoQueueBaseActivity.getUserProfile().getQueueUserId());
                    profileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
                }
                if (LaunchActivity.getLaunchActivity().isOnline() && !NoQueueBaseActivity.getAddress().equals(edt_address.getText().toString())) {
                    profileModel.addProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(),new JsonUserAddress().setAddress(edt_address.getText().toString()).setId(""));
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
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        AppUtilities.authenticationProcessing(this,errorCode);
    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
    }

    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void queueError(String error) {
        dismissProgress();
    }

    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {
        final List<JsonUserAddress> notificationsList = jsonUserAddressList.getJsonUserAddresses();
        Log.e("address list: ",notificationsList.toString());
        rg_address.removeAllViews();
        for (int i = 0; i < notificationsList.size(); i++) {
            final AppCompatRadioButton rdbtn = new AppCompatRadioButton(this);
            rdbtn.setId((i * 2) + i);
            rdbtn.setTag(notificationsList.get(i).getId());
            rdbtn.setText(notificationsList.get(i).getAddress());
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
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (rdbtn.getRight() - rdbtn.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                            tv_msg.setText("Do you want to delete address from address list.");
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
                                        profileModel.deleteProfileAddress(UserUtils.getEmail(), UserUtils.getAuth(),new JsonUserAddress().setAddress(rdbtn.getText().toString()).setId(rdbtn.getTag().toString()));
                                    }else {
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
            rdbtn.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.cancel_grey),null);
            rdbtn.setCompoundDrawablePadding(20);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{

                            Color.DKGRAY
                            , Color.rgb (242,81,112),
                    }
            );
            CompoundButtonCompat.setButtonTintList(rdbtn, colorStateList);
            float paddingDp = 10f;
            // Convert to pixels
            int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp, this.getResources().getDisplayMetrics());
            rdbtn.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            rg_address.addView(rdbtn);
        }
        rg_address.setVisibility(View.VISIBLE);
        dismissProgress();

    }

    @Override
    public void profileAddressError() {
        dismissProgress();
    }

}
