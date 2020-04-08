package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.StoreMenuGridAdapter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.StoreProductApiCalls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductMenuFragment extends BaseFragment implements StoreProductPresenter,
        MenuHeaderAdapter.OnItemClickListener, StoreMenuGridAdapter.CartOrderUpdate {
    private Button btn_view_order;
    private RecyclerView rcv_header;
    private RecyclerView rcv_data;
    private MenuHeaderAdapter menuAdapter;
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();
    private String codeQR = "";
    private boolean isTablet;
    private StoreProductApiCalls storeProductApiCalls;
    final Map<String, List<StoreCartItem>> gridData = new HashMap<>();
    private StoreMenuGridAdapter storeMenuGridAdapter;

    public void setProductMenuProcess(ProductMenuProcess productMenuProcess) {
        this.productMenuProcess = productMenuProcess;
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
        codeQR = getArguments().getString("codeQR");
        isTablet = getArguments().getBoolean("isTablet", false);
        rcv_header = view.findViewById(R.id.rcv_header);
        rcv_data = view.findViewById(R.id.rcv_data);
        rcv_data.setLayoutManager(new GridLayoutManager(getActivity(), isTablet ? 5 : 3));
        rcv_data.setItemAnimator(new DefaultItemAnimator());
        btn_view_order = view.findViewById(R.id.btn_view_order);
        rcv_header = view.findViewById(R.id.rcv_header);
        storeProductApiCalls = new StoreProductApiCalls();
        storeProductApiCalls.setStoreProductPresenter(this);
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
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
            gridData.clear();
            jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            jsonStoreCategories.addAll(CommonHelper.populateWithAllCategories(LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()));

            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<StoreCartItem>> listDataChild = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                listDataChild.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    if (listDataChild.containsKey(jsonStoreProducts.get(k).getStoreCategoryId())) {
                        listDataChild.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                    } else {
                        if (null == listDataChild.get(defaultCategory)) {
                            listDataChild.put(defaultCategory, new ArrayList<>());
                        }
                        listDataChild.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                    }
                } else {
                    if (null == listDataChild.get(defaultCategory)) {
                        listDataChild.put(defaultCategory, new ArrayList<>());
                    }
                    listDataChild.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                }
            }

            if (null != listDataChild.get(defaultCategory)) {
                jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
            }
            ArrayList<Integer> removeEmptyData = new ArrayList<>();
            for (int i = 0; i < jsonStoreCategories.size(); i++) {
                if (listDataChild.get(jsonStoreCategories.get(i).getCategoryId()).size() > 0) {
                    gridData.put(jsonStoreCategories.get(i).getCategoryId(), listDataChild.get(jsonStoreCategories.get(i).getCategoryId()));
                } else {
                    removeEmptyData.add(i);
                }
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
            if (jsonStoreCategories.size() > 0) {
                storeMenuGridAdapter = new StoreMenuGridAdapter(gridData.get(jsonStoreCategories.get(0).getCategoryId()), StoreMenuActivity.storeMenuActivity, this);
                rcv_data.setAdapter(storeMenuGridAdapter);
            }
        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        rcv_header.smoothScrollToPosition(pos);
        menuAdapter.setSelected_pos(pos);
        menuAdapter.notifyDataSetChanged();
        if (jsonStoreCategories.size() > 0) {
            if (null != storeMenuGridAdapter) {
                storeMenuGridAdapter.setMenuItemsList(gridData.get(jsonStoreCategories.get(pos).getCategoryId()));
            } else {
                storeMenuGridAdapter = new StoreMenuGridAdapter(gridData.get(jsonStoreCategories.get(pos).getCategoryId()), StoreMenuActivity.storeMenuActivity, this);
                rcv_data.setAdapter(menuAdapter);
            }
        }
    }


    @Override
    public void cartStatusChanged() {
        new CustomToast().showToast(getActivity(), "Item added to cart", true);
        if (isTablet) {
            // update side order list
            if (null != productMenuProcess)
                productMenuProcess.updateOrderList();
        } else {
            if (StoreMenuActivity.storeMenuActivity.getOrders().size() > 0) {
                btn_view_order.setVisibility(View.VISIBLE);
                btn_view_order.setText("View Order (" + StoreMenuActivity.storeMenuActivity.getOrders().size() + ")");
            } else {
                btn_view_order.setVisibility(View.GONE);
                btn_view_order.setText("");
            }
        }
    }

}
