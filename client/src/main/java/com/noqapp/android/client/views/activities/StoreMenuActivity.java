package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.PurchaseApiModel;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.interfaces.PurchaseOrderPresenter;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CustomExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
// Scrollview issue  https://stackoverflow.com/questions/37605545/android-nestedscrollview-which-contains-expandablelistview-doesnt-scroll-when?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

public class StoreMenuActivity extends BaseActivity implements  CustomExpandableListAdapter.CartUpdate {
    private static final String TAG = StoreMenuActivity.class.getSimpleName();

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
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
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail, this);
        expandableListView.setAdapter(expandableListAdapter);
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.isLogin()) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
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
                        if (price / 100 >= jsonQueue.getMinimumDeliveryOrder()) {
                            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                                    .setBizStoreId(jsonQueue.getBizStoreId())
                                    .setBusinessType(jsonQueue.getBusinessType())
                                    .setQueueUserId("100000000021")
                                    // jsonPurchaseOrder.setCustomerName(jsonQueue.);
                                    .setDeliveryType(jsonQueue.getDeliveryTypes().get(0)) // need to change dynamic
                                    .setOrderPrice(String.valueOf(price))
                                    .setPaymentType(jsonQueue.getPaymentTypes().get(1)); // need to change dynamic
                            jsonPurchaseOrder.setPurchaseOrderProducts(ll);

                          //  progressDialog.show();
                            // PurchaseApiModel.placeOrder(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), jsonPurchaseOrder);
                            Intent intent = new Intent(StoreMenuActivity.this, OrderActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data",jsonPurchaseOrder);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(StoreMenuActivity.this, "Minimum cart amount is " + jsonQueue.getMinimumDeliveryOrder(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ShowAlertInformation.showNetworkDialog(StoreMenuActivity.this);
                    }
                } else {
                    // Navigate to login screen
                    Intent loginIntent = new Intent(StoreMenuActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

    }

    @Override
    public void updateCartInfo(int amountString) {
        if (amountString > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is: " + amountString);
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }
}