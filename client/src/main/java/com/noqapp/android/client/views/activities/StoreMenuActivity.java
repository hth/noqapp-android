package com.noqapp.android.client.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CustomExpandableListAdapter;
import com.noqapp.android.client.views.toremove.ChildData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
// Scrollview issue  https://stackoverflow.com/questions/37605545/android-nestedscrollview-which-contains-expandablelistview-doesnt-scroll-when?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

public class StoreMenuActivity extends AppCompatActivity implements PurchaseOrderPresenter {
    private static final String TAG = StoreMenuActivity.class.getSimpleName();

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_store_address)
    protected TextView tv_store_address;
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
        tv_store_address.setText(jsonQueue.getStoreAddress());
        tv_store_name.setText(jsonQueue.getDisplayName());
        expandableListTitle = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        expandableListDetail = (HashMap<String, List<ChildData>>) getIntent().getExtras().getSerializable("listDataChild");
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        PurchaseApiModel.purchaseOrderPresenter = this;
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, ChildData> getOrder = expandableListAdapter.getOrders();


                List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                int price = 0;
                for (ChildData value : getOrder.values()) {
                    ll.add(new JsonPurchaseOrderProduct()
                            .setProductId(value.getJsonStoreProduct().getProductId())
                            .setProductPrice(value.getJsonStoreProduct().getProductPrice())
                            .setProductQuantity(value.getChildInput()));
                    price += value.getChildInput() * value.getJsonStoreProduct().getProductPrice();
                }
                JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                        .setBizStoreId(jsonQueue.getBizStoreId())
                        .setBusinessType(jsonQueue.getBusinessType())
                        .setQueueUserId("100000000021")
                        // jsonPurchaseOrder.setCustomerName(jsonQueue.);
                        .setDeliveryType(jsonQueue.getDeliveryTypes().get(0))
                        .setOrderPrice(String.valueOf(price))
                        .setPaymentType(jsonQueue.getPaymentTypes().get(1));
                jsonPurchaseOrder.setPurchaseOrderProducts(ll);

                Log.i(TAG, "order Place " + jsonPurchaseOrder.asJson());
                PurchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
            }
        });

    }

    @Override
    public void purchaseOrderResponse(JsonResponse response) {
        if (null != response) {
            if (response.getResponse() == 1) {
                Toast.makeText(this, "Order placed successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Order failed.", Toast.LENGTH_LONG).show();
            }
        } else {
            //Show error
        }
    }

    @Override
    public void purchaseOrderError() {

    }

    @Override
    public void authenticationFailure(int errorCode) {

    }
}