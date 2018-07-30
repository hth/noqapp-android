package com.noqapp.android.merchant.views.fragments;


import com.noqapp.android.common.beans.order.JsonPurchaseOrder;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.order.JsonPurchaseOrderList;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.PeopleInQOrderAdapter;
import com.noqapp.android.merchant.views.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.merchant.views.model.PurchaseOrderModel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerchantDetailFragment extends BaseMerchantDetailFragment implements PurchaseOrderPresenter {

    private PeopleInQOrderAdapter peopleInQOrderAdapter;
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void getAllPeopleInQ(JsonTopic jsonTopic) {
        PurchaseOrderModel purchaseOrderModel = new PurchaseOrderModel(this);
        purchaseOrderModel.fetch(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonTopic.getCodeQR());
    }

    @Override
    public void purchaseOrderResponse(JsonPurchaseOrderList jsonPurchaseOrderList) {
        if (null != jsonPurchaseOrderList) {
            Log.v("order data:", jsonPurchaseOrderList.toString());
            purchaseOrders = jsonPurchaseOrderList.getPurchaseOrders();
            Collections.sort(
                    purchaseOrders,
                    new Comparator<JsonPurchaseOrder>() {
                        public int compare(JsonPurchaseOrder lhs, JsonPurchaseOrder rhs) {
                            return Integer.compare(lhs.getToken(), rhs.getToken());
                        }
                    }
            );
            peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR());
            rv_queue_people.setAdapter(peopleInQOrderAdapter);
        }
        dismissProgress();
    }

    @Override
    public void purchaseOrderError() {
        dismissProgress();
    }

    @Override
    protected void resetList() {
        // super.resetList();

        purchaseOrders = new ArrayList<>();
        peopleInQOrderAdapter = new PeopleInQOrderAdapter(purchaseOrders, context, jsonTopic.getCodeQR());
        rv_queue_people.setAdapter(peopleInQOrderAdapter);
    }
}
