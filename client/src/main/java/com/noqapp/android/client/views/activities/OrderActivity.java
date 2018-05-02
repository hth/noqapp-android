package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements PurchaseOrderPresenter {

    @BindView(R.id.tv_user_name)
    protected TextView tv_user_name;
    @BindView(R.id.tv_user_address)
    protected TextView tv_user_address;

    @BindView(R.id.edt_address)
    protected EditText edt_address;
    @BindView(R.id.edt_locality)
    protected EditText edt_locality;
    @BindView(R.id.edt_city)
    protected EditText edt_city;
    @BindView(R.id.edt_state)
    protected EditText edt_state;
    @BindView(R.id.edt_pin)
    protected EditText edt_pin;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
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
        tv_user_address.setText("No Address");
        PurchaseApiModel.purchaseOrderPresenter = this;
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    progressDialog.show();
                    PurchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                } else {
                    ShowAlertInformation.showNetworkDialog(OrderActivity.this);
                }
            }
        });
    }

    @Override
    public void purchaseOrderResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
                Intent in =new Intent(OrderActivity.this, OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",jsonPurchaseOrder);
                in.putExtras(bundle);
                startActivity(in);
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
}
