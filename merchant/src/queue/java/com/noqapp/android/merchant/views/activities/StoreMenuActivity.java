package com.noqapp.android.merchant.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.PaymentPermissionEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.utils.IBConstant;
import com.noqapp.android.merchant.views.fragments.ProductMenuFragment;
import com.noqapp.android.merchant.views.fragments.ProductMenuListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreMenuActivity extends BaseActivity implements ProductMenuFragment.ProductMenuProcess {
    public static UpdateWholeList updateWholeList;
    private long lastPress;
    private Toast backPressToast;
    private HashMap<String, StoreCartItem> orders = new HashMap<>();
    public static StoreMenuActivity storeMenuActivity;
    private ProductMenuListFragment productMenuListFragment;
    private ProductMenuFragment productMenuFragment;
    public JsonTopic jsonTopic;
    private String codeQR;

    public interface UpdateWholeList {
        void updateWholeList();
    }

    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        setProgressMessage("Fetching data...");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        storeMenuActivity = this;
        tv_toolbar_title.setText("Menu");
        jsonTopic = (JsonTopic) getIntent().getSerializableExtra("jsonTopic");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> {
            updateList();
        });
        codeQR = jsonTopic.getCodeQR();
        Bundle b = new Bundle();
        b.putString("codeQR", codeQR);
        b.putBoolean("isTablet", LaunchActivity.isTablet);
        productMenuFragment = new ProductMenuFragment();
        productMenuFragment.setProductMenuProcess(this);
        productMenuFragment.setArguments(b);
        productMenuListFragment = new ProductMenuListFragment(getCartList(), storeMenuActivity);
        productMenuListFragment.setArguments(b);
        if (LaunchActivity.isTablet) {
            replaceFragmentWithBackStack(R.id.fl_product_menu, productMenuFragment, "ProductMenu");
            replaceFragmentWithBackStack(R.id.fl_product_list, productMenuListFragment, "ProductList");
        } else {
            replaceFragmentWithBackStack(R.id.fl_product_menu, productMenuFragment, "ProductList");
        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                backPressToast = new CustomToast().getToast(this, getString(R.string.exit_the_app));
                backPressToast.show();
                lastPress = currentTime;
            } else {
                if (backPressToast != null) {
                    backPressToast.cancel();
                }
                //super.onBackPressed();
                updateList();
            }
        } else {
            super.onBackPressed();
        }
    }

    public static void updateList() {
        storeMenuActivity.finish();
        if (null != updateWholeList) {
            updateWholeList.updateWholeList();
        }
    }

    public void updateAndCallPayment(JsonPurchaseOrder jsonPurchaseOrder) {
        try {
            Intent in = new Intent(this, OrderDetailActivity.class);
            in.putExtra("jsonPurchaseOrder", jsonPurchaseOrder);
            if (PaymentPermissionEnum.A == jsonTopic.getJsonPaymentPermission().getPaymentPermissions().get(LaunchActivity.getLaunchActivity().getUserLevel().name())) {
                in.putExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, false);
            } else {
                in.putExtra(IBConstant.KEY_IS_PAYMENT_NOT_ALLOWED, true);
            }
            in.putExtra(IBConstant.KEY_IS_PAYMENT_PARTIAL_ALLOWED, jsonTopic.getBusinessType() == BusinessTypeEnum.HS);
            startActivity(in);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "StoreMenuActivity", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderList() {
        if (null != productMenuListFragment) {
            productMenuListFragment.updateCartOrderViaMenu();
        }
    }

    @Override
    public void viewList() {
        Bundle b = new Bundle();
        b.putString("codeQR", codeQR);
        productMenuListFragment = new ProductMenuListFragment(getCartList(), storeMenuActivity);
        productMenuListFragment.setArguments(b);
        replaceFragmentWithBackStack(R.id.fl_product_menu, productMenuListFragment, "ProductList");
    }

    public ArrayList<StoreCartItem> getCartList() {
        ArrayList<StoreCartItem> list = new ArrayList<>();
        for (Map.Entry<String, StoreCartItem> entry : orders.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }
}