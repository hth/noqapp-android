package com.noqapp.android.merchant.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.noqapp.android.common.beans.JsonResponse;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.ActionTypeEnum;
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
import com.noqapp.android.merchant.views.adapters.StoreProductMenuAdapter;
import com.noqapp.android.merchant.views.interfaces.ActionOnProductPresenter;
import com.noqapp.android.merchant.views.interfaces.StoreProductPresenter;
import com.noqapp.android.merchant.views.model.StoreProductApiCalls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProductListActivity extends BaseActivity implements
        StoreProductPresenter, ActionOnProductPresenter, MenuHeaderAdapter.OnItemClickListener,
        StoreProductMenuAdapter.MenuItemUpdate {

    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuHeaderAdapter;
    private TextView tv_name;
    private String codeQR = "";
    private ArrayList<JsonStoreCategory> jsonStoreCategories = new ArrayList<>();
    private ExpandableListView expandableListView;
    private List<Integer> headerPosition = new ArrayList<>();

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
        expandableListView = findViewById(R.id.expandableListView);
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
            jsonStoreCategories.addAll(CommonHelper.populateWithAllCategories(LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()));

            ArrayList<JsonStoreProduct> jsonStoreProducts = (ArrayList<JsonStoreProduct>) jsonStore.getJsonStoreProducts();
            final HashMap<String, List<StoreCartItem>> storeCartItems = new HashMap<>();
            for (int l = 0; l < jsonStoreCategories.size(); l++) {
                storeCartItems.put(jsonStoreCategories.get(l).getCategoryId(), new ArrayList<>());
            }
            for (int k = 0; k < jsonStoreProducts.size(); k++) {
                if (jsonStoreProducts.get(k).getStoreCategoryId() != null) {
                    if (jsonStoreProducts.get(k).isActive()) {
                        if (storeCartItems.containsKey(jsonStoreProducts.get(k).getStoreCategoryId())) {
                            storeCartItems.get(jsonStoreProducts.get(k).getStoreCategoryId()).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                        } else {
                            if (null == storeCartItems.get(defaultCategory)) {
                                storeCartItems.put(defaultCategory, new ArrayList<>());
                            }
                            if (jsonStoreProducts.get(k).isActive()) {
                                storeCartItems.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                            }
                        }
                    }
                } else {
                    //TODO(hth) when product without category else it will drop
                    if (null == storeCartItems.get(defaultCategory)) {
                        storeCartItems.put(defaultCategory, new ArrayList<>());
                    }
                    if (jsonStoreProducts.get(k).isActive()) {
                        storeCartItems.get(defaultCategory).add(new StoreCartItem(0, jsonStoreProducts.get(k)));
                    }
                }
            }

            if (null != storeCartItems.get(defaultCategory)) {
                jsonStoreCategories.add(new JsonStoreCategory().setCategoryName(defaultCategory).setCategoryId(defaultCategory));
            }

            List<JsonStoreCategory> tempHeaderList = jsonStoreCategories;
            HashMap<String, List<StoreCartItem>> expandableListDetail = storeCartItems;

            ArrayList<Integer> removeEmptyData = new ArrayList<>();
            int headerTracker = 0;
            for (int i = 0; i < tempHeaderList.size(); i++) {
                if (expandableListDetail.get(tempHeaderList.get(i).getCategoryId()).size() > 0) {
                    headerPosition.add(headerTracker);
                    headerTracker += expandableListDetail.get(tempHeaderList.get(i).getCategoryId()).size();

                } else
                    removeEmptyData.add(i);
            }
            // Remove the categories which having zero items
            for (int j = removeEmptyData.size() - 1; j >= 0; j--) {
                tempHeaderList.remove((int) removeEmptyData.get(j));
            }

            // fill the category items on basis of header which having items
            HashMap<String, List<StoreCartItem>> tempListDetails = new HashMap<>();
            for (int i = 0; i < tempHeaderList.size(); i++) {
                tempListDetails.put(tempHeaderList.get(i).getCategoryId(), expandableListDetail.get(tempHeaderList.get(i).getCategoryId()));

                // add  the count in category for header list
                int itemSize = expandableListDetail.get(tempHeaderList.get(i).getCategoryId()).size();
                tempHeaderList.set(i, tempHeaderList.get(i).setCategoryName(tempHeaderList.get(i).getCategoryName()+" ("+itemSize+") "));
            }
            rcv_header = findViewById(R.id.rcv_header);
            rcv_header.setHasFixedSize(true);
            rcv_header.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            rcv_header.setItemAnimator(new DefaultItemAnimator());

            menuHeaderAdapter = new MenuHeaderAdapter(tempHeaderList, this, this);
            rcv_header.setAdapter(menuHeaderAdapter);
            menuHeaderAdapter.notifyDataSetChanged();

            StoreProductMenuAdapter expandableListAdapter = new StoreProductMenuAdapter(
                    this,
                    tempHeaderList,
                    tempListDetails,
                    this);
            expandableListView.setAdapter(expandableListAdapter);
            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
            }
            expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> true);
            expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    int position = new AppUtils().getFirstVisibleGroup(expandableListView);
                    if (position < menuHeaderAdapter.getItemCount()) {
                        rcv_header.smoothScrollToPosition(position);
                        menuHeaderAdapter.setSelectedPosition(position);
                        menuHeaderAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
            if (jsonStoreCategories.size() > 0) {
                tv_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                tv_name.setText("Add Product");
                tv_name.setOnClickListener(v -> addOrEditProduct(null, ActionTypeEnum.ADD));
            }

        }
    }

    @Override
    public void menuHeaderClick(int pos) {
        try {
            expandableListView.setSelectedGroup(pos);
            menuHeaderAdapter.setSelectedPosition(pos);
            menuHeaderAdapter.notifyDataSetChanged();
            // expandableListView.scrollTo(0, -200);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        AlertDialog.Builder builder;
        if (LaunchActivity.isTablet) {
            builder = new AlertDialog.Builder(this);
        } else {
            builder = new AlertDialog.Builder(this, R.style.FullScreenDialogTheme);
        }
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

        List<String> prodTypes = ProductTypeEnum.populateWithProductType(LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType());
        // sort the list alphabetically
        Collections.sort(prodTypes);
        prodTypes.add(0, "Select product type");
        List<String> prodUnits = UnitOfMeasurementEnum.asListOfDescription();
        // sort the list alphabetically
        Collections.sort(prodUnits);
        prodUnits.add(0, "Select product unit");

        ArrayList<JsonStoreCategory> tempJsonStoreCategories = new ArrayList<>();
        tempJsonStoreCategories.addAll(jsonStoreCategories);
        tempJsonStoreCategories.addAll(CommonHelper.populateWithAllCategories(LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType()));
        // sort the list alphabetically
        Collections.sort(tempJsonStoreCategories, (JsonStoreCategory jsc1, JsonStoreCategory jsc2) -> jsc1.getCategoryName().compareTo(jsc2.getCategoryName()));
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < tempJsonStoreCategories.size(); i++) {
            categories.add(tempJsonStoreCategories.get(i).getCategoryName());
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
            edt_prod_unit_value.setText(String.valueOf(CommonHelper.divideByHundred(jsonStoreProduct.getUnitValue())));
            sp_category_type.setSelection(getCategoryItemPosition(jsonStoreProduct.getStoreCategoryId(), tempJsonStoreCategories));
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
                    jsonStoreProduct.setStoreCategoryId(getCategoryID(sp_category_type.getSelectedItem().toString(), tempJsonStoreCategories));
                    jsonStoreProduct.setPackageSize(Integer.parseInt(edt_prod_pack_size.getText().toString()));
                    jsonStoreProduct.setUnitValue(Integer.parseInt(edt_prod_unit_value.getText().toString()) * 100);
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

    private String getCategoryID(String category, ArrayList<JsonStoreCategory> tempJsonStoreCategories) {
        for (int i = 0; i < tempJsonStoreCategories.size(); i++) {
            if (category.equals(tempJsonStoreCategories.get(i).getCategoryName())) {
                return tempJsonStoreCategories.get(i).getCategoryId();
            }
        }
        return "";
    }

    private int getCategoryItemPosition(String category, ArrayList<JsonStoreCategory> tempJsonStoreCategories) {
        for (int i = 0; i < tempJsonStoreCategories.size(); i++) {
            if (category.equals(tempJsonStoreCategories.get(i).getCategoryId())) {
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
