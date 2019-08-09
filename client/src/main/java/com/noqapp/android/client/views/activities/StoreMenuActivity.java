package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreMenuActivity extends BaseActivity implements
        MenuHeaderAdapter.OnItemClickListener, StoreProductMenuAdapter.CartOrderUpdate {
    private Button tv_place_order;
    private RecyclerView rcv_header;
    private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuHeaderAdapter;
    private HashMap<String, StoreCartItem> orders = new HashMap<>();
    private String currencySymbol;
    private List<Integer> headerPosition = new ArrayList<>();
    private RecyclerView rcv_menu;
    private LinearLayoutManager menuLayoutManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        initActionsViews(true);
        tv_toolbar_title.setText("Menu");
        rcv_header = findViewById(R.id.rcv_header);
        rcv_menu = findViewById(R.id.rcv_menu);
        tv_place_order = findViewById(R.id.tv_place_order);
        jsonQueue = (JsonQueue) getIntent().getSerializableExtra("jsonQueue");
        currencySymbol = AppUtilities.getCurrencySymbol(jsonQueue.getCountryShortName());
        List<JsonStoreCategory> headerList = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        HashMap<String, List<StoreCartItem>> expandableListDetail = (HashMap<String, List<StoreCartItem>>) getIntent().getExtras().getSerializable("listDataChild");

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


        rcv_menu.setHasFixedSize(true);
        menuLayoutManger = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv_menu.setLayoutManager(menuLayoutManger);
        rcv_menu.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        StoreProductMenuAdapter storeProductMenuAdapter = new StoreProductMenuAdapter(childData, this, this, currencySymbol);
        rcv_menu.setAdapter(storeProductMenuAdapter);

        rcv_menu.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        System.out.println("The RecyclerView is not scrolling");
                        int k = menuLayoutManger.findFirstVisibleItemPosition();
                        for (int i = 0; i < headerPosition.size(); i++) {
                            if (k <= headerPosition.get(i)) {
                                menuHeaderAdapter.setSelectedPosition(k);
                                menuHeaderAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        System.out.println("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;

                }
            }
        });

        orders.clear();
        tv_place_order.setOnClickListener((View v) -> {
            if (UserUtils.isLogin()) {
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    HashMap<String, StoreCartItem> getOrder = getOrders();

                    List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                    int price = 0;
                    for (StoreCartItem value : getOrder.values()) {
                        ll.add(new JsonPurchaseOrderProduct()
                                .setProductId(value.getJsonStoreProduct().getProductId())
                                .setProductPrice(value.getFinalDiscountedPrice() * 100)
                                .setProductQuantity(value.getChildInput())
                                .setProductName(value.getJsonStoreProduct().getProductName())
                                .setPackageSize(value.getJsonStoreProduct().getPackageSize())
                                .setUnitValue(value.getJsonStoreProduct().getUnitValue())
                                .setUnitOfMeasurement(value.getJsonStoreProduct().getUnitOfMeasurement())
                                .setProductType(value.getJsonStoreProduct().getProductType()));
                        price += value.getChildInput() * value.getFinalDiscountedPrice() * 100;
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
                        bundle.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, jsonQueue.getStoreAddress());
                        bundle.putString("GeoHash", jsonQueue.getGeoHash());
                        bundle.putInt("deliveryRange", jsonQueue.getDeliveryRange());
                        bundle.putString("topic", jsonQueue.getTopic());
                        bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
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
        rcv_menu.smoothScrollToPosition(headerPosition.get(pos));
        menuHeaderAdapter.setSelectedPosition(pos);
        menuHeaderAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCartOrderInfo(int amountString) {
        if (amountString > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is: " + amountString);
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }

    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }

}