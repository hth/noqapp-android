package com.noqapp.android.merchant.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

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
    private int sc_product_type_index = -1;
    private JsonStore jsonStore = null;
    private boolean isViewHidden = true;
    private int selectionPos = -1;
    private String selectedCategory = "";
    private Map<String, Integer> mapIndex;

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
            this.jsonStore = jsonStore;
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

            List<JsonStoreCategory> tempHeaderList = new ArrayList<>();
            tempHeaderList.addAll(jsonStoreCategories);
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
            List<JsonStoreCategory> finalHeaderList = new ArrayList<>();
            for (int i = 0; i < tempHeaderList.size(); i++) {
                tempListDetails.put(tempHeaderList.get(i).getCategoryId(), expandableListDetail.get(tempHeaderList.get(i).getCategoryId()));
                // add  the count in category for header list
                int itemSize = expandableListDetail.get(tempHeaderList.get(i).getCategoryId()).size();
                JsonStoreCategory jsc = new JsonStoreCategory().setCategoryId(tempHeaderList.get(i).getCategoryId())
                        .setCategoryName(tempHeaderList.get(i).getCategoryName() + " (" + itemSize + ") ");
                finalHeaderList.add(jsc);
            }
            rcv_header = findViewById(R.id.rcv_header);
            rcv_header.setHasFixedSize(true);
            rcv_header.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            rcv_header.setItemAnimator(new DefaultItemAnimator());

            menuHeaderAdapter = new MenuHeaderAdapter(finalHeaderList, this, this);
            rcv_header.setAdapter(menuHeaderAdapter);
            menuHeaderAdapter.notifyDataSetChanged();

            StoreProductMenuAdapter expandableListAdapter = new StoreProductMenuAdapter(
                    this,
                    finalHeaderList,
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
        Button btn_add_update = customDialogView.findViewById(R.id.btn_add_update);
        Button btn_view = customDialogView.findViewById(R.id.btn_view);
        View productview = customDialogView.findViewById(R.id.productview);
        isViewHidden = true;
        btn_view.setOnClickListener(v -> {
            isViewHidden = !isViewHidden;
            btn_view.setText(isViewHidden ? "Show client preview" : "Hide client preview");
            productview.setVisibility(isViewHidden ? View.GONE : View.VISIBLE);
        });

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

        TextView tv_name = customDialogView.findViewById(R.id.tv_name);
        TextView tv_price = customDialogView.findViewById(R.id.tv_price);
        TextView tv_discounted_price = customDialogView.findViewById(R.id.tv_discounted_price);
        TextView tv_description = customDialogView.findViewById(R.id.tv_description);
        TextView tv_unit = customDialogView.findViewById(R.id.tv_unit);
        TextView tv_measure = customDialogView.findViewById(R.id.tv_measure);

        ///
        final TextView tv_category_type = customDialogView.findViewById(R.id.tv_category_type);
        final Spinner sp_unit = customDialogView.findViewById(R.id.sp_unit);
        final EditText edt_prod_name = customDialogView.findViewById(R.id.edt_prod_name);
        final EditText edt_prod_price = customDialogView.findViewById(R.id.edt_prod_price);
        final EditText edt_prod_limit = customDialogView.findViewById(R.id.edt_prod_limit);
        final EditText edt_prod_description = customDialogView.findViewById(R.id.edt_prod_description);
        final EditText edt_prod_discount = customDialogView.findViewById(R.id.edt_prod_discount);
        final EditText edt_prod_unit_value = customDialogView.findViewById(R.id.edt_prod_unit_value);
        final EditText edt_prod_pack_size = customDialogView.findViewById(R.id.edt_prod_pack_size);
        final SegmentedControl sc_product_type = customDialogView.findViewById(R.id.sc_product_type);
        edt_prod_name.addTextChangedListener(new CustomTextWatcher(tv_name, "Name", true, edt_prod_name));
        edt_prod_description.addTextChangedListener(new CustomTextWatcher(tv_description, "Description"));
        edt_prod_price.addTextChangedListener(new CustomTextWatcher(tv_price, LaunchActivity.getCurrencySymbol() + " Price", true));
        edt_prod_unit_value.addTextChangedListener(new CustomTextWatcher(tv_unit, "Quantity"));

        formatText(tv_name, "Name", "");
        formatText(tv_description, "Description", "");
        formatText(tv_price, LaunchActivity.getCurrencySymbol() + " Price ", "");
        formatText(tv_unit, "Quantity", "");
        formatText(tv_measure, "Unit", "");

        if (actionTypeEnum == ActionTypeEnum.ADD) {
            tv_toolbar_title.setText("Add Product");
            tv_online.setVisibility(View.GONE);
            tv_offline.setVisibility(View.GONE);
            tv_offline_title.setVisibility(View.GONE);
            btn_add_update.setText("Add");
            edt_prod_discount.setText("0");
            edt_prod_limit.setText("0");
            edt_prod_unit_value.setText("1");
            edt_prod_pack_size.setText("1");
        }

        sp_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                formatText(tv_measure, "Unit", sp_unit.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sc_product_type_index = -1;
        List<String> prodTypesSegment = ProductTypeEnum.populateWithProductType(LaunchActivity.getLaunchActivity().getUserProfile().getBusinessType());
        // sort the list alphabetically
        Collections.sort(prodTypesSegment);
        List<String> prodUnits = UnitOfMeasurementEnum.asListOfDescription();

        // sort the list alphabetically
        Collections.sort(prodUnits);
        final ArrayList<JsonStoreCategory> categoryList = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
        // sort the list alphabetically
        Collections.sort(categoryList, (JsonStoreCategory jsc1, JsonStoreCategory jsc2) -> jsc1.getCategoryName().compareTo(jsc2.getCategoryName()));
        List<String> categories = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            categories.add(categoryList.get(i).getCategoryName());
        }
        tv_category_type.setOnClickListener(v -> {
            if (selectionPos != -1) {
                selectCategoryDialog(categories, tv_category_type, selectionPos);
            } else if (null != temp) {
                selectCategoryDialog(categories, tv_category_type, getCategoryItemPosition(jsonStoreProduct.getStoreCategoryId(), categoryList));
            } else {
                selectCategoryDialog(categories, tv_category_type, -1);
            }
        });
        sp_unit.setAdapter(new EnumAdapter(this, prodUnits));
        sc_product_type.addSegments(prodTypesSegment);

        sc_product_type.addOnSegmentSelectListener((segmentViewHolder, isSelected, isReselected) -> {
            if (isSelected) {
                sc_product_type_index = segmentViewHolder.getAbsolutePosition();
            }
            if (isReselected) {
                sc_product_type_index = -1;
                sc_product_type.clearSelection();
            }
        });
        if (null != temp) {
            selectionPos = -1;
            edt_prod_name.setText(jsonStoreProduct.getProductName());
            edt_prod_price.setText(jsonStoreProduct.getDisplayPrice());
            edt_prod_description.setText(jsonStoreProduct.getProductInfo());
            edt_prod_discount.setText(jsonStoreProduct.getDisplayDiscount());
            edt_prod_limit.setText(String.valueOf(jsonStoreProduct.getInventoryLimit()));
            edt_prod_pack_size.setText(String.valueOf(jsonStoreProduct.getPackageSize()));
            edt_prod_unit_value.setText(String.valueOf(jsonStoreProduct.getDisplayUnitValue()));
            tv_category_type.setText(categories.get(getCategoryItemPosition(jsonStoreProduct.getStoreCategoryId(), categoryList)));
            selectionPos = getCategoryItemPosition(jsonStoreProduct.getStoreCategoryId(), categoryList);
            sp_unit.setSelection(getItemPosition(prodUnits, jsonStoreProduct.getUnitOfMeasurement().getFriendlyDescription()));
            sc_product_type.setSelectedSegment(getItemPosition(prodTypesSegment, jsonStoreProduct.getProductType().getDescription()));
            if (jsonStoreProduct.isActive()) {
                tv_offline.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_online.setBackgroundResource(R.drawable.bg_nogradient_square);
                tv_online.setText("Online");
                tv_online.setTextColor(Color.WHITE);
                tv_offline.setTextColor(Color.BLACK);
            } else {
                tv_offline.setBackgroundResource(R.drawable.bg_nogradient_square);
                tv_online.setBackgroundResource(R.drawable.square_white_bg_drawable);
                tv_online.setTextColor(Color.BLACK);
                tv_offline.setTextColor(Color.WHITE);
                tv_offline.setText("Offline");
            }
        }

        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        btn_add_update.setOnClickListener(v -> {
            if (sc_product_type_index == -1) {
                new CustomToast().showToast(ProductListActivity.this, "Please select product type");
            } else {
                if (validate(edt_prod_name, edt_prod_price, edt_prod_unit_value, edt_prod_pack_size)) {
                    jsonStoreProduct.setProductName(edt_prod_name.getText().toString());
                    jsonStoreProduct.setProductInfo(edt_prod_description.getText().toString());
                    jsonStoreProduct.setProductPrice((int) (Float.parseFloat(edt_prod_price.getText().toString()) * 100));
                    jsonStoreProduct.setProductDiscount((int) (convertStringToFloat(edt_prod_discount.getText().toString()) * 100));
                    jsonStoreProduct.setProductType(ProductTypeEnum.getEnum(prodTypesSegment.get(sc_product_type_index)));
                    jsonStoreProduct.setUnitOfMeasurement(UnitOfMeasurementEnum.getEnum(sp_unit.getSelectedItem().toString()));
                    jsonStoreProduct.setStoreCategoryId(getCategoryID(categories.get(selectionPos), categoryList));
                    jsonStoreProduct.setPackageSize(convertStringToInt(edt_prod_pack_size.getText().toString()));
                    jsonStoreProduct.setUnitValue(convertStringToInt(edt_prod_unit_value.getText().toString()) * 100);
                    jsonStoreProduct.setInventoryLimit(convertStringToInt(edt_prod_limit.getText().toString()));
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
            selectionPos = -1;
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


    private boolean validate(EditText... views) {
        boolean isValid = true;
        for (EditText v : views) {
            v.setError(null);
        }
        AppUtils.hideKeyBoard(this);
        String errorMsg = "";
        for (EditText v : views) {
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
                return i;
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

    private class CustomTextWatcher implements TextWatcher {
        private TextView view;
        private EditText sourceTextView;
        private String prefix = "";
        private boolean isCap;
        private boolean isCurrency;

        private CustomTextWatcher(TextView view, String prefix) {
            this.view = view;
            this.prefix = prefix;
        }

        private CustomTextWatcher(TextView view, String prefix, boolean isCurrency) {
            this.view = view;
            this.prefix = prefix;
            this.isCurrency = isCurrency;
        }

        private CustomTextWatcher(TextView view, String prefix, boolean isCap, EditText sourceTextView) {
            this.view = view;
            this.prefix = prefix;
            this.isCap = isCap;
            this.sourceTextView = sourceTextView;
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void afterTextChanged(Editable editable) {
            String text = isCap ? CommonHelper.capitalizeEachWordFirstLetter(editable.toString()) : editable.toString();
            if (isCurrency)
                text = LaunchActivity.getCurrencySymbol() + " " + text;
            formatText(view, prefix, text);
            if (editable.length() != 0 && isCap) {
                sourceTextView.removeTextChangedListener(this);
                sourceTextView.setText(CommonHelper.capitalizeEachWordFirstLetter(editable.toString()));
                sourceTextView.setSelection(sourceTextView.getText().length());
                sourceTextView.addTextChangedListener(this);
            }
        }
    }

    private void formatText(TextView tv, String title, String value) {
        tv.setText(TextUtils.isEmpty(value) ? title : value);
    }

    private int convertStringToInt(String input) {
        if (TextUtils.isEmpty(input)) {
            return 1;
        } else {
            return Integer.parseInt(input);
        }
    }

    private Float convertStringToFloat(String input) {
        if (TextUtils.isEmpty(input)) {
            return 0f;
        } else {
            return Float.parseFloat(input);
        }
    }


    private void selectCategoryDialog(List<String> categories, TextView textView, int autoSelect) {
        selectionPos = -1;
        selectedCategory = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customDialogView = inflater.inflate(R.layout.select_category, null, false);
        TextView tvtitle = customDialogView.findViewById(R.id.tvtitle);
        ListView listView = customDialogView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, categories));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectionPos = i;
            textView.setText(categories.get(selectionPos));
        });
        initIndexList(categories);
        displayIndex(customDialogView, listView);
        if (-1 != autoSelect) {
            listView.setItemChecked(autoSelect, true);
        }
        tvtitle.setText("Select Category");
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ImageView iv_close = customDialogView.findViewById(R.id.iv_close);
        Button btn_done = customDialogView.findViewById(R.id.btn_done);
        iv_close.setOnClickListener(v -> mAlertDialog.dismiss());
        btn_done.setOnClickListener(v -> {
            if (selectionPos == -1) {
                new CustomToast().showToast(this, "please select a category");
            } else {
                selectedCategory = categories.get(selectionPos);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    private void initIndexList(List<String> categories) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            String index = category.substring(0, 1);
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex(View view, ListView listView) {
        LinearLayout indexLayout = view.findViewById(R.id.side_index);
        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(v -> {
                TextView selectedIndex = (TextView) v;
                listView.setSelection(mapIndex.get(selectedIndex.getText()));
            });
            indexLayout.addView(textView);
        }
    }
}
