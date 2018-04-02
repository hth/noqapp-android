package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.presenter.beans.body.PurchaseOrderParam;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CustomExpandableListAdapter;
import com.noqapp.android.client.views.toremove.ChildData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreMenuActivity extends AppCompatActivity implements PurchaseOrderPresenter{
    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<JsonStoreCategory> expandableListTitle;
    private HashMap<String, List<ChildData>> expandableListDetail;
    private JsonQueue jsonQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        ButterKnife.bind(this);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Menu");
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        jsonQueue = (JsonQueue) getIntent().getSerializableExtra("jsonQueue");
        expandableListTitle =  (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");;
        expandableListDetail = (HashMap<String, List<ChildData>>) getIntent().getExtras().getSerializable("listDataChild");
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        PurchaseApiModel.purchaseOrderPresenter = this;
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PurchaseOrderParam purchaseOrderParam = new PurchaseOrderParam();
                JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
                jsonPurchaseOrder.setBizStoreId(jsonQueue.getBizCategoryId());
                jsonPurchaseOrder.setBusinessType(jsonQueue.getBusinessType());
                jsonPurchaseOrder.setQueueUserId("100000000021");
               // jsonPurchaseOrder.setCustomerName(jsonQueue.);
                jsonPurchaseOrder.setDeliveryType(jsonQueue.getDeliveryTypes().get(0));
                jsonPurchaseOrder.setOrderPrice("100");
                jsonPurchaseOrder.setPaymentType(jsonQueue.getPaymentTypes().get(0));

                List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                ll.add(new JsonPurchaseOrderProduct().setProductId("5ac1c36cb85cb7763e9ea9fc").setProductPrice(10000).setProductQuantity(5));
                jsonPurchaseOrder.setPurchaseOrderProducts(ll);
                purchaseOrderParam.setJsonPurchaseOrder(AppUtilities.parseJson(jsonPurchaseOrder));
                PurchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(),purchaseOrderParam);

            }
        });

    }

    @Override
    public void purchaseOrderResponse(JsonResponse jsonResponse) {

    }

    @Override
    public void purchaseOrderError() {

    }

    @Override
    public void authenticationFailure(int errorCode) {

    }
}