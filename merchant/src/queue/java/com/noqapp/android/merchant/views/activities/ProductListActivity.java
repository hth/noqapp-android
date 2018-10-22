package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.model.StoreProductModel;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.interfaces.ActionOnProductPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.MenuAdapter;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.FragmentDummy;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements StoreProductPresenter, ActionOnProductPresenter, MenuHeaderAdapter.OnItemClickListener, MenuAdapter.MenuItemUpdate {

    private ProgressDialog progressDialog;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private String codeQR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);
        initProgress();
        codeQR = getIntent().getStringExtra("codeQR");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText(getString(R.string.screen_product_list));
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.show();
            StoreProductModel storeProductModel = new StoreProductModel();
            storeProductModel.setStoreProductPresenter(this);
            storeProductModel.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej)
            new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching data...");
    }

    private void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void storeProductResponse(JsonStore jsonStore) {
        dismissProgress();
        if (null != jsonStore) {
            String defaultCategory = "Un-Categorized";
            final ArrayList<JsonStoreCategory> jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<ChildData>> listDataChild = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                listDataChild.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<ChildData>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    listDataChild.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new ChildData(0, jsonStoreProducts.get(k)));
                } else {
                    //TODO(hth) when product without category else it will drop
                    if (null == listDataChild.get(defaultCategory)) {
                        listDataChild.put(defaultCategory, new ArrayList<ChildData>());
                    }
                    listDataChild.get(defaultCategory).add(new ChildData(0, jsonStoreProducts.get(k)));
                }
            }

            if (null != listDataChild.get(defaultCategory)) {
                jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
            }

            rcv_header = findViewById(R.id.rcv_header);
            // List<JsonStoreCategory> expandableListTitle = jsonStoreCategories;
            //HashMap<String, List<ChildData>> expandableListDetail = listDataChild;
            viewPager = findViewById(R.id.pager);
            TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
            ArrayList<Integer> removeEmptyData = new ArrayList<>();
            for (int i = 0; i < jsonStoreCategories.size(); i++) {
                if (listDataChild.get(jsonStoreCategories.get(i).getCategoryId()).size() > 0)
                    adapter.addFragment(new FragmentDummy(listDataChild.get(jsonStoreCategories.get(i).getCategoryId()), this), "FRAG" + i);
                else
                    removeEmptyData.add(i);
            }
            // Remove the categories which having zero items
            for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
                jsonStoreCategories.remove((int) removeEmptyData.get(j));
            }
            rcv_header.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rcv_header.setLayoutManager(horizontalLayoutManagaer);
            rcv_header.setItemAnimator(new DefaultItemAnimator());

            menuAdapter = new MenuHeaderAdapter(jsonStoreCategories, this, this);
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
    public void menuItemUpdate(JsonStoreProduct jsonStoreProduct, ActionTypeEnum actionTypeEnum) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            progressDialog.setMessage("Updating data...");
            progressDialog.show();
            StoreProductModel storeProductModel = new StoreProductModel();
            storeProductModel.setActionOnProductPresenter(this);
            storeProductModel.actionOnProduct(UserUtils.getEmail(), UserUtils.getAuth(), codeQR, actionTypeEnum, jsonStoreProduct);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void actionOnProductResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            Toast.makeText(this, "Action perform successfully", Toast.LENGTH_LONG).show();
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                StoreProductModel storeProductModel = new StoreProductModel();
                storeProductModel.setStoreProductPresenter(this);
                storeProductModel.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        } else {
            Toast.makeText(this, "Failed to perform action", Toast.LENGTH_LONG).show();
        }
    }
}
