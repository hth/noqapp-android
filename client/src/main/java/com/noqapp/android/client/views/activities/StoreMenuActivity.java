package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.ChildData;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrder;
import com.noqapp.android.client.presenter.beans.JsonPurchaseOrderProduct;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStoreCategory;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CustomExpandableListAdapter;
import com.noqapp.android.client.views.adapters.MenuAdapter;
import com.noqapp.android.client.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.client.views.fragments.FragmentDummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
// Scrollview issue  https://stackoverflow.com/questions/37605545/android-nestedscrollview-which-contains-expandablelistview-doesnt-scroll-when?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

public class StoreMenuActivity extends BaseActivity implements CustomExpandableListAdapter.CartUpdate, MenuHeaderAdapter.OnItemClickListener, MenuAdapter.CartOrderUpdate {
    private static final String TAG = StoreMenuActivity.class.getSimpleName();

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;
    @BindView(R.id.tv_place_order)
    protected TextView tv_place_order;
    @BindView(R.id.rcv_header)
    protected RecyclerView rcv_header;
    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<JsonStoreCategory> expandableListTitle;
    private HashMap<String, List<ChildData>> expandableListDetail;
    private JsonQueue jsonQueue;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private HashMap<String, ChildData> orders = new HashMap<>();

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
        expandableListTitle = (ArrayList<JsonStoreCategory>) getIntent().getExtras().getSerializable("jsonStoreCategories");
        expandableListDetail = (HashMap<String, List<ChildData>>) getIntent().getExtras().getSerializable("listDataChild");
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail, this);
        expandableListView.setAdapter(expandableListAdapter);

        viewPager = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ArrayList<Integer> removeEmptyData = new ArrayList<>();
        for (int i = 0; i < expandableListTitle.size(); i++) {
            if (expandableListDetail.get(expandableListTitle.get(i).getCategoryId()).size() > 0)
                adapter.addFragment(new FragmentDummy(expandableListDetail.get(expandableListTitle.get(i).getCategoryId()), this, this), "FRAG" + i);
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
                                    .setProductPrice(value.getJsonStoreProduct().getProductPrice())
                                    .setProductQuantity(value.getChildInput())
                                    .setJsonStoreProduct(value.getJsonStoreProduct()));
                            price += value.getChildInput() * value.getJsonStoreProduct().getProductPrice();
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}