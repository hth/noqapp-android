package com.noqapp.android.merchant.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.ProductTypeEnum;
import com.noqapp.android.common.model.types.order.UnitOfMeasurementEnum;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.store.JsonStore;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.adapters.EnumAdapter;
import com.noqapp.android.merchant.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.merchant.views.adapters.StoreMenuAdapter;
import com.noqapp.android.merchant.views.adapters.TabViewPagerAdapter;
import com.noqapp.android.merchant.views.fragments.FragmentDummy;
import com.noqapp.android.merchant.views.interfaces.ActionOnProductPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.StoreProductApiCalls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductListActivity extends BaseActivity implements
        StoreProductPresenter, ActionOnProductPresenter, MenuHeaderAdapter.OnItemClickListener,
        StoreMenuAdapter.MenuItemUpdate {

    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuAdapter;
    private ViewPager viewPager;
    private TextView tv_name;
    private String codeQR = "";
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_list);
        setProgressMessage("Fetching data...");
        codeQR = getIntent().getStringExtra("codeQR");
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_name = findViewById(R.id.tv_name);
        ImageView actionbarBack = findViewById(R.id.actionbarBack);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setOnClickListener(v -> finish());
        tv_toolbar_title.setText(getString(R.string.screen_product_list));
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            StoreProductApiCalls storeProductApiCalls = new StoreProductApiCalls();
            storeProductApiCalls.setStoreProductPresenter(this);
            storeProductApiCalls.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void storeProductResponse(JsonStore jsonStore) {
        dismissProgress();
        if (null != jsonStore) {
            String defaultCategory = "Un-Categorized";
            jsonStoreCategories.clear();
            jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
            jsonStoreCategories.addAll(CommonHelper.populateWithAllCategories(BusinessTypeEnum.GS));

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

            rcv_header = findViewById(R.id.rcv_header);
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

            if (jsonStoreCategories.size() > 0) {
                tv_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                tv_name.setText("Add Product");
                tv_name.setOnClickListener(v -> addOrEditProduct(null, ActionTypeEnum.ADD));
            }
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
            setProgressMessage("Updating data...");
            showProgress();
            setProgressCancel(false);
            StoreProductApiCalls storeProductApiCalls = new StoreProductApiCalls();
            storeProductApiCalls.setActionOnProductPresenter(this);
            storeProductApiCalls.actionOnProduct(UserUtils.getEmail(), UserUtils.getAuth(), codeQR, actionTypeEnum, jsonStoreProduct);
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void addOrEditProduct(final JsonStoreProduct temp, final ActionTypeEnum actionTypeEnum) {
        final JsonStoreProduct jsonStoreProduct = null != temp ? temp : new JsonStoreProduct();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_edit_prod_list, null, false);

        ImageView actionbarBack = customDialogView.findViewById(R.id.actionbarBack);
        TextView tv_toolbar_title = customDialogView.findViewById(R.id.tv_toolbar_title);
        TextView tv_offline_title = customDialogView.findViewById(R.id.tv_offline_title);
        final TextView tv_online = customDialogView.findViewById(R.id.tv_online);
        final TextView tv_offline = customDialogView.findViewById(R.id.tv_offline);
        if (actionTypeEnum == ActionTypeEnum.ADD) {
            tv_toolbar_title.setText("Add Product");
            tv_online.setVisibility(View.GONE);
            tv_offline.setVisibility(View.GONE);
            tv_offline_title.setVisibility(View.GONE);
        }

        tv_online.setOnClickListener(v -> {
            tv_offline.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_online.setBackgroundResource(R.drawable.button_drawable_red_square);
            tv_online.setText("Online");
            tv_online.setTextColor(Color.WHITE);
            tv_offline.setTextColor(Color.BLACK);
            jsonStoreProduct.setActive(true);
            menuItemUpdate(jsonStoreProduct, ActionTypeEnum.ACTIVE);
            // mAlertDialog.dismiss();

        });

        tv_offline.setOnClickListener(v -> {
            tv_offline.setBackgroundResource(R.drawable.button_drawable_red_square);
            tv_online.setBackgroundResource(R.drawable.square_white_bg_drawable);
            tv_online.setTextColor(Color.BLACK);
            tv_offline.setTextColor(Color.WHITE);
            tv_offline.setText("Offline");
            jsonStoreProduct.setActive(false);
            menuItemUpdate(jsonStoreProduct, ActionTypeEnum.INACTIVE);
            // mAlertDialog.dismiss();

        });
        final Spinner sp_category_type = customDialogView.findViewById(R.id.sp_category_type);
        final Spinner sp_product_type = customDialogView.findViewById(R.id.sp_product_type);
        final Spinner sp_unit = customDialogView.findViewById(R.id.sp_unit);
        final TextInputEditText edt_prod_name = customDialogView.findViewById(R.id.edt_prod_name);
        final TextInputEditText edt_prod_price = customDialogView.findViewById(R.id.edt_prod_price);
        final TextInputEditText edt_prod_limit = customDialogView.findViewById(R.id.edt_prod_limit);
        final TextInputEditText edt_prod_description = customDialogView.findViewById(R.id.edt_prod_description);
        final TextInputEditText edt_prod_discount = customDialogView.findViewById(R.id.edt_prod_discount);
        final TextInputEditText edt_prod_unit_value = customDialogView.findViewById(R.id.edt_prod_unit_value);
        final TextInputEditText edt_prod_pack_size = customDialogView.findViewById(R.id.edt_prod_pack_size);

        List<String> prodTypes = ProductTypeEnum.asListOfDescription();
        prodTypes.add(0, "Select product type");
        List<String> prodUnits = UnitOfMeasurementEnum.asListOfDescription();
        prodUnits.add(0, "Select product unit");

        List<String> categories = new ArrayList<>();
        for (int i = 0; i < jsonStoreCategories.size(); i++) {
            categories.add(jsonStoreCategories.get(i).getCategoryName());
        }
        categories.add(0, "Select product category");
        sp_category_type.setAdapter(new EnumAdapter(this, categories));
        sp_product_type.setAdapter(new EnumAdapter(this, prodTypes));
        sp_unit.setAdapter(new EnumAdapter(this, prodUnits));

        if (null != temp) {
            edt_prod_name.setText(jsonStoreProduct.getProductName());
            edt_prod_price.setText(jsonStoreProduct.getDisplayPrice());
            edt_prod_description.setText(jsonStoreProduct.getProductInfo());
            edt_prod_discount.setText(jsonStoreProduct.getDisplayDiscount());
            edt_prod_limit.setText(String.valueOf(jsonStoreProduct.getInventoryLimit()));
            edt_prod_pack_size.setText(String.valueOf(jsonStoreProduct.getPackageSize()));
            edt_prod_unit_value.setText(String.valueOf(jsonStoreProduct.getUnitValue()));
            sp_category_type.setSelection(getCategoryItemPosition(jsonStoreProduct.getStoreCategoryId()));
            sp_unit.setSelection(getItemPosition(prodUnits, jsonStoreProduct.getUnitOfMeasurement().getDescription()));
            sp_product_type.setSelection(getItemPosition(prodTypes, jsonStoreProduct.getProductType().getDescription()));
            if (jsonStoreProduct.isActive()) {
                tv_offline.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_online.setBackgroundResource(R.drawable.button_drawable_red_square);
                tv_online.setText("Online");
                tv_online.setTextColor(Color.WHITE);
                tv_offline.setTextColor(Color.BLACK);
            } else {
                tv_offline.setBackgroundResource(R.drawable.button_drawable_red_square);
                tv_online.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_online.setTextColor(Color.BLACK);
                tv_offline.setTextColor(Color.WHITE);
                tv_offline.setText("Offline");
            }
        }

        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_create_token = customDialogView.findViewById(R.id.btn_create_token);
        btn_create_token.setOnClickListener(v -> {
            if (sp_category_type.getSelectedItemPosition() == 0) {
                new CustomToast().showToast(ProductListActivity.this, "Please select product category");
            } else if (sp_product_type.getSelectedItemPosition() == 0) {
                new CustomToast().showToast(ProductListActivity.this, "Please select product type");
            } else if (sp_unit.getSelectedItemPosition() == 0) {
                new CustomToast().showToast(ProductListActivity.this, "Please select product unit");
            } else {
                if (validate(edt_prod_name, edt_prod_price, edt_prod_description, edt_prod_discount, edt_prod_unit_value, edt_prod_pack_size)) {
                    jsonStoreProduct.setProductName(edt_prod_name.getText().toString());
                    jsonStoreProduct.setProductInfo(edt_prod_description.getText().toString());
                    jsonStoreProduct.setProductPrice((int) (Float.parseFloat(edt_prod_price.getText().toString()) * 100));
                    jsonStoreProduct.setProductDiscount((int) (Float.parseFloat(edt_prod_discount.getText().toString()) * 100));
                    jsonStoreProduct.setProductType(ProductTypeEnum.getEnum(sp_product_type.getSelectedItem().toString()));
                    jsonStoreProduct.setUnitOfMeasurement(UnitOfMeasurementEnum.getEnum(sp_unit.getSelectedItem().toString()));
                    jsonStoreProduct.setStoreCategoryId(getCategoryID(sp_category_type.getSelectedItem().toString()));
                    jsonStoreProduct.setPackageSize(Integer.parseInt(edt_prod_pack_size.getText().toString()));
                    jsonStoreProduct.setUnitValue(Integer.parseInt(edt_prod_unit_value.getText().toString()));
                    jsonStoreProduct.setInventoryLimit(Integer.parseInt(edt_prod_limit.getText().toString()));
                    menuItemUpdate(jsonStoreProduct, actionTypeEnum);
                    mAlertDialog.dismiss();
                }
            }
        });

        actionbarBack.setOnClickListener(v -> mAlertDialog.dismiss());
        mAlertDialog.show();
    }


    @Override
    public void actionOnProductResponse(JsonResponse jsonResponse) {
        dismissProgress();
        if (Constants.SUCCESS == jsonResponse.getResponse()) {
            new CustomToast().showToast(this, "Action perform successfully");
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                showProgress();
                StoreProductApiCalls storeProductApiCalls = new StoreProductApiCalls();
                storeProductApiCalls.setStoreProductPresenter(this);
                storeProductApiCalls.storeProduct(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        } else {
            new CustomToast().showToast(this, "Failed to perform action");
        }
    }


    private boolean validate(TextInputEditText... views) {
        boolean isValid = true;
        for (TextInputEditText v : views) {
            v.setError(null);
        }
        AppUtils.hideKeyBoard(this);
        String errorMsg = "";
        for (TextInputEditText v : views) {
            if (TextUtils.isEmpty(v.getText().toString())) {
                v.setError(getString(R.string.error_field_required));
                if (isValid) {
                    isValid = false;
                    errorMsg = getString(R.string.error_all_field_required);
                }
            }
        }
        if (!TextUtils.isEmpty(errorMsg))
            new CustomToast().showToast(this, errorMsg);
        return isValid;
    }

    private String getCategoryID(String category) {
        for (int i = 0; i < jsonStoreCategories.size(); i++) {
            if (category.equals(jsonStoreCategories.get(i).getCategoryName())) {
                return jsonStoreCategories.get(i).getCategoryId();
            }
        }
        return "";
    }

    private int getCategoryItemPosition(String category) {
        for (int i = 0; i < jsonStoreCategories.size(); i++) {
            if (category.equals(jsonStoreCategories.get(i).getCategoryId())) {
                return i + 1;
            }
        }
        return 0;
    }

    private int getItemPosition(List<String> data, String value) {
        for (int i = 0; i < data.size(); i++) {
            if (value.equals(data.get(i))) {
                return i;
            }
        }
        return 0;
    }
}
