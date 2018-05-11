package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.ProfileModel;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.model.types.DeliveryTypeEnum;
import com.noqapp.android.client.model.types.PaymentTypeEnum;
import com.noqapp.android.client.model.types.PurchaseOrderStateEnum;
import com.noqapp.android.client.presenter.ProfilePresenter;
import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.body.UpdateProfile;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter ,ProfilePresenter {

    @BindView(R.id.tv_user_name)
    protected TextView tv_user_name;
    @BindView(R.id.tv_total_order_amt)
    protected TextView tv_total_order_amt;
    @BindView(R.id.tv_tax_amt)
    protected TextView tv_tax_amt;
    @BindView(R.id.tv_due_amt)
    protected TextView tv_due_amt;


    @BindView(R.id.edt_address)
    protected EditText edt_address;
    @BindView(R.id.edt_phone)
    protected EditText edt_phone;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
    @BindView(R.id.ll_order_details)
    protected LinearLayout ll_order_details;
    private JsonPurchaseOrder jsonPurchaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initActionsViews(false);
        jsonPurchaseOrder = (JsonPurchaseOrder) getIntent().getExtras().getSerializable("data");
        tv_toolbar_title.setText(getString(R.string.screen_order));
        tv_user_name.setText(NoQueueBaseActivity.getUserName());
        edt_phone.setText(NoQueueBaseActivity.getPhoneNo());
        edt_address.setText(NoQueueBaseActivity.getAddress());
        PurchaseApiModel.purchaseOrderPresenter = this;
        tv_tax_amt.setText(getString(R.string.rupee)+""+"0.0");
        tv_due_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        tv_total_order_amt.setText(getString(R.string.rupee)+""+Double.parseDouble(jsonPurchaseOrder.getOrderPrice())/100);
        for (int i =0; i< jsonPurchaseOrder.getPurchaseOrderProducts().size();i++){
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = jsonPurchaseOrder.getPurchaseOrderProducts().get(i);
            LayoutInflater inflater = LayoutInflater.from(this);
            View inflatedLayout= inflater.inflate(R.layout.order_summary_item, null, false);
            TextView tv_title = (TextView)inflatedLayout.findViewById(R.id.tv_title);
            TextView tv_qty = (TextView)inflatedLayout.findViewById(R.id.tv_qty);
            TextView tv_price = (TextView)inflatedLayout.findViewById(R.id.tv_price);
            TextView tv_total_price = (TextView)inflatedLayout.findViewById(R.id.tv_total_price);
            tv_title.setText(jsonPurchaseOrderProduct.getJsonStoreProduct().getProductName());
            tv_qty.setText("Quantity: "+jsonPurchaseOrderProduct.getProductQuantity());
            tv_price.setText(getString(R.string.rupee)+""+jsonPurchaseOrderProduct.getProductPrice()/100);
            tv_total_price.setText(getString(R.string.rupee)+""+jsonPurchaseOrderProduct.getProductPrice()*jsonPurchaseOrderProduct.getProductQuantity()/100);
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
                        PurchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                    } else {
                        ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                    }
                }
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        edt_phone.setError(null);
        edt_address.setError(null);
        if(edt_phone.getText().toString().equals("")){
            edt_phone.setError("Please enter mobile no.");
            isValid = false;
        }
        if(!edt_phone.getText().toString().equals("") && edt_phone.getText().length()<10){
            edt_phone.setError("Please enter valid mobile no.");
            isValid = false;
        }
        if(edt_address.getText().toString().equals("")){
            edt_address.setError("Please enter delivery address.");
            isValid = false;
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


                if (TextUtils.isEmpty(NoQueueBaseActivity.getAddress())) {
                    ProfileModel.profilePresenter = this;
                    String address = edt_address.getText().toString();
                    UpdateProfile updateProfile = new UpdateProfile();
                    updateProfile.setAddress(address);
                    updateProfile.setFirstName(NoQueueBaseActivity.getUserName());
                    updateProfile.setBirthday(NoQueueBaseActivity.getUserDOB());
                    updateProfile.setGender(NoQueueBaseActivity.getGender());
                    updateProfile.setTimeZoneId(TimeZone.getDefault().getID());
                    ProfileModel.updateProfile(UserUtils.getEmail(), UserUtils.getAuth(), updateProfile);
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
    public void queueError() {
        dismissProgress();
    }


}
