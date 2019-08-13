package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.StoreMenuGridAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.StoreProductApiCalls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductMenuFragment extends BaseFragment implements StoreProductPresenter,
        MenuHeaderAdapter.OnItemClickListener, StoreMenuGridAdapter.CartOrderUpdate {
    private Button btn_view_order;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();


    private String codeQR = "";
    private boolean isTablet;
    private TabViewPagerAdapter adapter;

    public ProductMenuFragment setProductMenuProcess(ProductMenuProcess productMenuProcess) {
        this.productMenuProcess = productMenuProcess;
        return this;
    }

    public ProductMenuProcess productMenuProcess;

    public interface ProductMenuProcess {
        void updateOrderList();

        void viewList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_product_menu, container, false);
        rcv_header = view.findViewById(R.id.rcv_header);
        btn_view_order = view.findViewById(R.id.btn_view_order);
        rcv_header = view.findViewById(R.id.rcv_header);
        viewPager = view.findViewById(R.id.pager);
        codeQR = getArguments().getString("codeQR");
        isTablet = getArguments().getBoolean("isTablet", false);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            StoreProductApiCalls storeProductApiCalls = new StoreProductApiCalls();
            storeProductApiCalls.setStoreProductPresenter(this);
            storeProductApiCalls.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
        btn_view_order.setOnClickListener(v -> {
            if (null != productMenuProcess)
                productMenuProcess.viewList();
        });

        return view;
    }


    @Override
    public void storeProductResponse(JsonStore jsonStore) {
        dismissProgress();
        if (null != jsonStore) {
            String defaultCategory = "Un-Categorized";
            jsonStoreCategories.clear();
            jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<StoreCartItem>> listDataChild = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                listDataChild.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<StoreCartItem>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    listDataChild.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                } else {
                    //TODO(hth) when product without category else it will drop
                    if (null == listDataChild.get(defaultCategory)) {
                        listDataChild.put(defaultCategory, new ArrayList<StoreCartItem>());
                    }
                    listDataChild.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                }
            }

            if (null != listDataChild.get(defaultCategory)) {
                jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
            }


            adapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager());
            ArrayList<Integer> removeEmptyData = new ArrayList<>();
            for (int i = 0; i < jsonStoreCategories.size(); i++) {
                if (listDataChild.get(jsonStoreCategories.get(i).getCategoryId()).size() > 0)
                    adapter.addFragment(new FragmentDummyMenu(listDataChild.get(jsonStoreCategories.get(i).getCategoryId()), StoreMenuActivity.storeMenuActivity, this), "FRAG" + i);
                else
                    removeEmptyData.add(i);
            }
            // Remove the categories which having zero items
            for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
                jsonStoreCategories.remove((int) removeEmptyData.get(j));
            }
            rcv_header.setHasFixedSize(true);
            rcv_header.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rcv_header.setItemAnimator(new DefaultItemAnimator());

            menuAdapter = new MenuHeaderAdapter(jsonStoreCategories, getActivity(), this);
            rcv_header.setAdapter(menuAdapter);
            menuAdapter.notifyDataSetChanged();
            viewPager.setAdapter(null);
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        viewPager.setCurrentItem(pos);
    }


    @Override
    public void cartStatusChanged() {
        new CustomToast().showToast(getActivity(),"Item added to cart",true);
        if (isTablet) {
            // update side order list
            if (null != productMenuProcess)
                productMenuProcess.updateOrderList();
        } else {
            if (StoreMenuActivity.storeMenuActivity.getOrders().size() > 0) {
                btn_view_order.setVisibility(View.VISIBLE);
                btn_view_order.setText("View Order ("+StoreMenuActivity.storeMenuActivity.getOrders().size()+")");
            } else {
                btn_view_order.setVisibility(View.GONE);
                btn_view_order.setText("");
            }
        }
    }

}
