package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.client.views.adapters.StoreProductMenuAdapter;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.StoreCartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreMenuActivity extends BaseActivity implements
    MenuHeaderAdapter.OnItemClickListener, StoreProductMenuAdapter.CartOrderUpdate {
    private Button tv_place_order;
    private RecyclerView rcv_header;
    private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuHeaderAdapter;
    private String currencySymbol;
    private List<Integer> headerPosition = new ArrayList<>();
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        initActionsViews(true);
        tv_toolbar_title.setText("Menu");
        rcv_header = findViewById(R.id.rcv_header);
        tv_place_order = findViewById(R.id.tv_place_order);
        expandableListView = findViewById(R.id.expandableListView);
        jsonQueue = (JsonQueue) getIntent().getSerializableExtra("jsonQueue");
        currencySymbol = AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName());
        List<JsonStoreCategory> headerList = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        HashMap<String, List<StoreCartItem>> expandableListDetail = (HashMap<String, List<StoreCartItem>>) getIntent().getExtras().getSerializable("listDataChild");

        List<JsonStoreCategory> expandableListTitle = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        StoreProductMenuAdapter expandableListAdapter = new StoreProductMenuAdapter(this, expandableListTitle, expandableListDetail,
            this, currencySymbol, getIntent().getBooleanExtra("isStoreOpen", true), jsonQueue.getBusinessType(), jsonQueue.getBizStoreId());
        expandableListView.setAdapter(expandableListAdapter);


        ArrayList<Integer> removeEmptyData = new ArrayList<>();
        ArrayList<StoreCartItem> childData = new ArrayList<>();
        int headerTracker = 0;
        for (int i = 0; i < headerList.size(); i++) {
            if (expandableListDetail.get(headerList.get(i).getCategoryId()).size() > 0) {
                childData.addAll(expandableListDetail.get(headerList.get(i).getCategoryId()));
                headerPosition.add(headerTracker);
                headerTracker += expandableListDetail.get(headerList.get(i).getCategoryId()).size();

            } else
                removeEmptyData.add(i);
        }
        // Remove the categories which having zero items
        for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
            headerList.remove((int) removeEmptyData.get(j));
        }
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        menuHeaderAdapter = new MenuHeaderAdapter(headerList, this, this);
        rcv_header.setAdapter(menuHeaderAdapter);
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int position = new AppUtils().getFirstVisibleGroup(expandableListView);
                rcv_header.smoothScrollToPosition(position);
                menuHeaderAdapter.setSelectedPosition(position);
                menuHeaderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

        tv_place_order.setOnClickListener((View v) -> {
            if (UserUtils.isLogin()) {
                if (isOnline()) {
                    HashMap<String, StoreCartItem> getOrder = expandableListAdapter.getOrders();
                    List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                    int price = 0;
                    for (StoreCartItem value : getOrder.values()) {
                        ll.add(new JsonPurchaseOrderProduct()
                            .setProductId(value.getJsonStoreProduct().getProductId())
                            .setProductPrice(value.getFinalDiscountedPrice().movePointRight(2).intValue())
                            .setProductQuantity(value.getChildInput())
                            .setProductName(value.getJsonStoreProduct().getProductName())
                            .setPackageSize(value.getJsonStoreProduct().getPackageSize())
                            .setUnitValue(value.getJsonStoreProduct().getUnitValue())
                            .setUnitOfMeasurement(value.getJsonStoreProduct().getUnitOfMeasurement())
                            .setProductType(value.getJsonStoreProduct().getProductType()));
                        price += value.getChildInput() * value.getFinalDiscountedPrice().movePointRight(2).intValue();
                    }
                    if (price / 100 >= jsonQueue.getMinimumDeliveryOrder()) {
                        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                            .setBizStoreId(jsonQueue.getBizStoreId())
                            .setBusinessType(jsonQueue.getBusinessType())
                            .setCodeQR(jsonQueue.getCodeQR())
                            .setOrderPrice(String.valueOf(price));
                        jsonPurchaseOrder.setPurchaseOrderProducts(ll);

                        Intent intent = new Intent(StoreMenuActivity.this, OrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IBConstant.KEY_DATA, jsonPurchaseOrder);
                        bundle.putSerializable(IBConstant.KEY_JSON_QUEUE, jsonQueue);
                        bundle.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, jsonQueue.getStoreAddress());
                        bundle.putString("GeoHash", jsonQueue.getGeoHash());
                        bundle.putInt("deliveryRange", jsonQueue.getDeliveryRange());
                        bundle.putString("topic", jsonQueue.getTopic());
                        bundle.putString(AppUtils.CURRENCY_SYMBOL, currencySymbol);
                        bundle.putString(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        new CustomToast().showToast(StoreMenuActivity.this, "Minimum cart amount is " + jsonQueue.getMinimumDeliveryOrder());
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(StoreMenuActivity.this);
                }
            } else {
                // Navigate to login screen
                Intent loginIntent = new Intent(StoreMenuActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

    }


    @Override
    public void menuHeaderClick(int pos) {
        expandableListView.setSelectedGroup(pos);
        menuHeaderAdapter.setSelectedPosition(pos);
        menuHeaderAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCartOrderInfo(BigDecimal amountString) {
        if (amountString.compareTo(new BigDecimal(0)) > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is: " + currencySymbol + amountString.toString());
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }
}