package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.model.types.DeliveryTypeEnum;
import com.noqapp.android.client.model.types.PaymentTypeEnum;
import com.noqapp.android.client.model.types.PurchaseOrderStateEnum;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonUserAddress;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.SpinAdapter;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.body.UpdateProfile;

import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter, ProfilePresenter {

    @BindView(R.id.tv_user_name)
    protected TextView tv_user_name;
    @BindView(R.id.tv_total_order_amt)
    protected TextView tv_total_order_amt;
    @BindView(R.id.tv_tax_amt)
    protected TextView tv_tax_amt;
    @BindView(R.id.tv_due_amt)
    protected TextView tv_due_amt;
    @BindView(R.id.spinner)
    protected AppCompatSpinner spinner;
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
        profileModel = new ProfileModel();
        tv_tax_amt.setText(getString(R.string.rupee)+""+"0.0");
        tv_due_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        tv_total_order_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        for (int i =0; i< jsonPurchaseOrder.getPurchaseOrderProducts().size();i++){
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout = inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = (TextView) inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_qty = (TextView) inflatedLayout.findViewById(R.id.tv_qty);
            TextView tv_price = (TextView) inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_total_price = (TextView) inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getJsonStoreProduct().getProductName());
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
            profileModel.setProfilePresenter(this);
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
            if (null != latLng_d) {
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

                profileModel.setProfilePresenter(this);
                if (TextUtils.isEmpty(NoQueueBaseActivity.getAddress())) {
                    String address = edt_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(NoQueueBaseActivity.getUserName());
                    updateProfile.setBirthday(NoQueueBaseActivity.getUserDOB());
                    updateProfile.setGender(NoQueueBaseActivity.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
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
        Toast.makeText(this, "Error code : " + "" + errorCode, Toast.LENGTH_LONG).show();
        dismissProgress();
    }

    @Override
    public void queueResponse(JsonProfile profile, String email, String auth) {
        Log.v("JsonProfile", profile.toString());
        NoQueueBaseActivity.commitProfile(profile, email, auth);
    }

    @Override
    public void profileAddressResponse(JsonUserAddressList jsonUserAddressList) {
        Toast.makeText(this, "" + jsonUserAddressList.getJsonUserAddresses().size(), Toast.LENGTH_LONG).show();
        final List<JsonUserAddress> notificationsList = jsonUserAddressList.getJsonUserAddresses();


        ArrayAdapter adapter = new SpinAdapter(OrderActivity.this,
                android.R.layout.simple_spinner_item,
                notificationsList);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String addressId = notificationsList.get(i).getId();
                String addressName = notificationsList.get(i).getAddress();
                Toast.makeText(OrderActivity.this, "Address Name: " + addressName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void queueError(String error) {

    }

}
