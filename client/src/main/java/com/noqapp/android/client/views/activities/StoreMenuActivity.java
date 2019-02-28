package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CustomExpandableListAdapter;
import com.noqapp.android.client.views.adapters.MenuAdapter;
import com.noqapp.android.client.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.client.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.client.views.fragments.FragmentDummy;
import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.beans.store.JsonStoreCategory;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// Scrollview issue  https://stackoverflow.com/questions/37605545/android-nestedscrollview-which-contains-expandablelistview-doesnt-scroll-when?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

public class StoreMenuActivity extends BaseActivity implements CustomExpandableListAdapter.CartUpdate, MenuHeaderAdapter.OnItemClickListener, MenuAdapter.CartOrderUpdate {
    private Button tv_place_order;
    private RecyclerView rcv_header;
    private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private HashMap<String, ChildData> orders = new HashMap<>();
    private String currencySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        initActionsViews(true);
        tv_toolbar_title.setText("Menu");
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        rcv_header = findViewById(R.id.rcv_header);
        tv_place_order = findViewById(R.id.tv_place_order);
        jsonQueue = (JsonQueue) getIntent().getSerializableExtra("jsonQueue");
        currencySymbol = AppUtilities.getCurrencySymbol(jsonQueue.getCountryShortName());
        List<JsonStoreCategory> expandableListTitle = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        HashMap<String, List<ChildData>> expandableListDetail = (HashMap<String, List<ChildData>>) getIntent().getExtras().getSerializable("listDataChild");
        CustomExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail, this, currencySymbol);
        expandableListView.setAdapter(expandableListAdapter);

        viewPager = findViewById(R.id.pager);
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        ArrayList<Integer> removeEmptyData = new ArrayList<>();
        for (int i = 0; i < expandableListTitle.size(); i++) {
            if (expandableListDetail.get(expandableListTitle.get(i).getCategoryId()).size() > 0)
                adapter.addFragment(new FragmentDummy(expandableListDetail.get(expandableListTitle.get(i).getCategoryId()), this, this, currencySymbol), "FRAG" + i);
            else
                removeEmptyData.add(i);
        }
        // Remove the categories which having zero items
        for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
            expandableListTitle.remove((int) removeEmptyData.get(j));
        }
        rcv_header.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_header.setLayoutManager(horizontalLayoutManagaer);
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        menuAdapter = new MenuHeaderAdapter(expandableListTitle, this, this);
        rcv_header.setAdapter(menuAdapter);
        viewPager.setAdapter(adapter);
        orders.clear();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                rcv_header.smoothScrollToPosition(position);
                menuAdapter.setSelected_pos(position);
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtils.isLogin()) {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        //HashMap<String, ChildData> getOrder = expandableListAdapter.getOrders();  old one
                        HashMap<String, ChildData> getOrder = getOrders();

                        List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                        int price = 0;
                        for (ChildData value : getOrder.values()) {
                            ll.add(new JsonPurchaseOrderProduct()
                                    .setProductId(value.getJsonStoreProduct().getProductId())
                                    .setProductPrice(value.getFinalDiscountedPrice() * 100)
                                    .setProductQuantity(value.getChildInput())
                                    .setProductName(value.getJsonStoreProduct().getProductName()));
                            price += value.getChildInput() * value.getFinalDiscountedPrice() * 100;
                        }
                        if (price / 100 >= jsonQueue.getMinimumDeliveryOrder()) {
                            JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                                    .setBizStoreId(jsonQueue.getBizStoreId())
                                    .setBusinessType(jsonQueue.getBusinessType())
                                    .setOrderPrice(String.valueOf(price));
                            jsonPurchaseOrder.setPurchaseOrderProducts(ll);

                            Intent intent = new Intent(StoreMenuActivity.this, OrderActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", jsonPurchaseOrder);
                            bundle.putString("storeName", jsonQueue.getDisplayName());
                            bundle.putString("storeAddress", jsonQueue.getStoreAddress());
                            bundle.putInt("deliveryRange", jsonQueue.getDeliveryRange());
                            bundle.putString("topic", jsonQueue.getTopic());
                            bundle.putString(AppUtilities.CURRENCY_SYMBOL, currencySymbol);
                            bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, jsonQueue.getCodeQR());
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
        updateCartOrderInfo(amountString);
    }

    @Override
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
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

    public HashMap<String, ChildData> getOrders() {
        return orders;
    }

}