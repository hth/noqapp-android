package com.noqapp.android.merchant.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
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
    private boolean isTablet = false;

    public interface UpdateWholeList {
        void updateWholeList();
    }
    public HashMap<String, StoreCartItem> getOrders() {
        return orders;
    }
    private String codeQR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isTablet = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isTablet = false;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_menu);
        setProgressMessage("Fetching data...");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        storeMenuActivity = this;
        tv_toolbar_title.setText("Menu");
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> {
            updateList();
        });
        codeQR = getIntent().getStringExtra("codeQR");
        Bundle b = new Bundle();
        b.putString("codeQR", codeQR);
        b.putBoolean("isTablet", isTablet);
        productMenuFragment = new ProductMenuFragment();
        productMenuFragment.setProductMenuProcess(this);
        productMenuFragment.setArguments(b);
        productMenuListFragment = new ProductMenuListFragment(getCartList(), storeMenuActivity);
        productMenuListFragment.setArguments(b);
        if (new AppUtils().isTablet(getApplicationContext())) {
            replaceFragmentWithoutBackStack(R.id.fl_product_menu, productMenuFragment);
            replaceFragmentWithoutBackStack(R.id.fl_product_list, productMenuListFragment);
        } else {
            replaceFragmentWithoutBackStack(R.id.fl_product_menu, productMenuFragment);
        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
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

    @Override
    public void updateOrderList() {
        new CustomToast().showToast(this, "called from tab");
        if(null != productMenuListFragment){
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