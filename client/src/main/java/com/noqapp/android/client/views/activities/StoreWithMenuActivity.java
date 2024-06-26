package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.open.StoreDetailImpl;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowKioskModeDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.MenuHeaderAdapter;
import com.noqapp.android.client.views.adapters.StoreProductMenuAdapter;
import com.noqapp.android.client.views.pojos.KioskModeInfo;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.common.utils.CommonHelper;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreWithMenuActivity
    extends BaseActivity
    implements StorePresenter, MenuHeaderAdapter.OnItemClickListener, StoreProductMenuAdapter.CartOrderUpdate {
    private JsonStore jsonStore = null;
    private JsonQueue jsonQueue = null;
    private TextView tv_store_name, tv_store_address;
    private BizStoreElastic bizStoreElastic;
    private TextView tv_rating, tv_rating_review;
    private ImageView iv_category_banner;
    private View view_loader;

    private Button tv_place_order;
    private RecyclerView rcv_header;
    private MenuHeaderAdapter menuHeaderAdapter;
    private String currencySymbol;
    private List<Integer> headerPosition = new ArrayList<>();
    private ExpandableListView expandableListView;
    private View stickyViewSpacer;
    private View frame_collapsible;
    private LinearLayout ll_header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(NoQueueClientApplication.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_store_with_menu);
        initActionsViews(false);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_store_address = findViewById(R.id.tv_store_address);
        TextView tv_view_more = findViewById(R.id.tv_view_more);
        frame_collapsible = findViewById(R.id.frame_collapsible);
        //tv_menu = findViewById(R.id.tv_menu);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        tv_rating = findViewById(R.id.tv_rating);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        view_loader = findViewById(R.id.view_loader);
        rcv_header = findViewById(R.id.rcv_header);
        ll_header = findViewById(R.id.ll_header);
        tv_place_order = findViewById(R.id.tv_place_order);
        expandableListView = findViewById(R.id.expandableListView);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.store_list_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);
        expandableListView.addHeaderView(listHeader);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");

        tv_view_more.setOnClickListener(v -> {
            Intent intent1 = new Intent(StoreWithMenuActivity.this, StoreDetailActivity.class);
            Bundle bundle12 = new Bundle();
            bundle12.putSerializable("BizStoreElastic", bizStoreElastic);
            intent1.putExtras(bundle12);
            startActivity(intent1);
        });

        tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_rating_review.setOnClickListener(v -> {
            if (null != jsonQueue && jsonQueue.getReviewCount() > 0) {
                Intent in = new Intent(StoreWithMenuActivity.this, AllReviewsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                bundle1.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                bundle1.putString(IBConstant.KEY_STORE_ADDRESS, tv_store_address.getText().toString());
                in.putExtras(bundle1);
                startActivity(in);
            }
        });
        setProgressMessage("Loading " + bizStoreElastic.getBusinessName() + "...");
        if (NetworkUtils.isConnectingToInternet(this)) {
            showProgress();
            new StoreDetailImpl(this).getStoreDetail(UserUtils.getDeviceId(), bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public void storeResponse(JsonStore jsonStore) {
        this.jsonStore = jsonStore;
        dismissProgress();
        switch (jsonStore.getJsonQueue().getBusinessType()) {
            case DO:
                // open hospital profile
                break;
            default:
                populateStore();
        }
    }

    private void populateStore() {
        view_loader.setVisibility(View.GONE);
        jsonQueue = jsonStore.getJsonQueue();
        currencySymbol = AppUtils.getCurrencySymbol(jsonQueue.getCountryShortName());
        tv_store_address.setText(AppUtils.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
        tv_store_name.setText(jsonQueue.getDisplayName());
        tv_toolbar_title.setText(jsonQueue.getDisplayName());
        tv_rating.setText(String.valueOf(AppUtils.round(jsonQueue.getRating())));
        if (tv_rating.getText().toString().equals("0.0")) {
            tv_rating.setVisibility(View.INVISIBLE);
        } else {
            tv_rating.setVisibility(View.VISIBLE);
        }
        AppUtils.setReviewCountText(jsonQueue.getReviewCount(), tv_rating_review);

        if (AppUtils.showKioskMode(bizStoreElastic)) {
            populateKioskMode();
        } else {
            TextView tv_enable_kiosk = findViewById(R.id.tv_enable_kiosk);
            tv_enable_kiosk.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage())) {
            Picasso.get()
                .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                .placeholder(ImageUtils.getBannerPlaceholder(this))
                .error(ImageUtils.getBannerErrorPlaceholder(this))
                .into(iv_category_banner);
        } else if (!TextUtils.isEmpty(jsonQueue.getDisplayImage())) {
            Picasso.get()
                .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, jsonQueue.getDisplayImage()))
                .placeholder(ImageUtils.getBannerPlaceholder(this))
                .error(ImageUtils.getBannerErrorPlaceholder(this))
                .into(iv_category_banner);
        } else {
            Picasso.get().load(ImageUtils.getBannerPlaceholder()).into(iv_category_banner);
        }

        if (AppUtils.isStoreOpenToday(jsonStore)) {
            tv_place_order.setClickable(true);
            tv_place_order.setEnabled(true);
        } else {
            tv_place_order.setEnabled(false);
            tv_place_order.setClickable(false);
            tv_place_order.setText("Closed");
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setTextColor(Color.parseColor("#333333"));
            tv_place_order.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg_inactive));
            tv_place_order.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        String defaultCategory = "Un-Categorized";
        //  {
        //TODO @Chandra Optimize the loop
        final ArrayList<JsonStoreCategory> jsonStoreCategories = (ArrayList<JsonStoreCategory>) jsonStore.getJsonStoreCategories();
        jsonStoreCategories.addAll(CommonHelper.populateWithAllCategories(jsonQueue.getBusinessType()));

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
        }
        rcv_header.setHasFixedSize(true);
        rcv_header.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcv_header.setItemAnimator(new DefaultItemAnimator());

        menuHeaderAdapter = new MenuHeaderAdapter(tempHeaderList, this, this);
        rcv_header.setAdapter(menuHeaderAdapter);

        StoreProductMenuAdapter expandableListAdapter = new StoreProductMenuAdapter(
            this,
            tempHeaderList,
            tempListDetails,
            this,
            currencySymbol,
            AppUtils.isStoreOpenToday(jsonStore),
            jsonQueue.getBusinessType(),
            jsonQueue.getBizStoreId());
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
                    // +1 added due to header
                    rcv_header.smoothScrollToPosition(position + 1);
                    menuHeaderAdapter.setSelectedPosition(position + 1);
                    menuHeaderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /* Check if the first item is already reached to top.*/
                if (expandableListView.getFirstVisiblePosition() == 0) {
                    View firstChild = expandableListView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int heroTopY = stickyViewSpacer.getTop();
                    ll_header.setY(Math.max(0, heroTopY + topY));
                    ll_header.setVisibility(View.GONE);
                    /* Set the image to scroll half of the amount that of ListView */
                    frame_collapsible.setY(topY * 0.85f);
                } else {
                    ll_header.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_place_order.setOnClickListener((View v) -> {
            if (UserUtils.isLogin()) {
                if (isOnline()) {
                    HashMap<String, StoreCartItem> getOrder = expandableListAdapter.getOrders();
                    List<JsonPurchaseOrderProduct> ll = new ArrayList<>();
                    int price = 0;
                    int tax = 0;
                    for (StoreCartItem value : getOrder.values()) {
                        JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                            .setProductId(value.getJsonStoreProduct().getProductId())
                            .setProductPrice(value.getFinalDiscountedPrice().movePointRight(2).intValue())
                            .setProductQuantity(value.getChildInput())
                            .setProductName(value.getJsonStoreProduct().getProductName())
                            .setPackageSize(value.getJsonStoreProduct().getPackageSize())
                            .setUnitValue(value.getJsonStoreProduct().getUnitValue())
                            .setUnitOfMeasurement(value.getJsonStoreProduct().getUnitOfMeasurement())
                            .setProductDiscount(value.getJsonStoreProduct().getProductDiscount())
                            .setTax(value.getJsonStoreProduct().getTax())
                            .setProductType(value.getJsonStoreProduct().getProductType());

                        price += jsonPurchaseOrderProduct.getProductQuantity() * value.getFinalDiscountedPrice().movePointRight(2).intValue();
                        tax += jsonPurchaseOrderProduct.computeTax();
                        ll.add(jsonPurchaseOrderProduct);
                    }

                    if (price / 100 >= jsonQueue.getMinimumDeliveryOrder()) {
                        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder()
                            .setBizStoreId(jsonQueue.getBizStoreId())
                            .setBusinessType(jsonQueue.getBusinessType())
                            .setCodeQR(jsonQueue.getCodeQR())
                            .setTax(String.valueOf(tax))
                            .setOrderPrice(String.valueOf(price))
                            .setGrandTotal(String.valueOf(price + tax));
                        jsonPurchaseOrder.setPurchaseOrderProducts(ll);

                        Intent intent = new Intent(StoreWithMenuActivity.this, OrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IBConstant.KEY_DATA, jsonPurchaseOrder);
                        bundle.putSerializable(IBConstant.KEY_JSON_QUEUE, jsonQueue);
                        bundle.putString(IBConstant.KEY_STORE_NAME, jsonQueue.getDisplayName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, jsonQueue.getStoreAddress());
                        bundle.putString("GeoHash", jsonQueue.getGeoHash());
                        bundle.putInt("deliveryRange", jsonQueue.getDeliveryRange());
                        bundle.putString("topic", jsonQueue.getTopic());
                        bundle.putString(AppUtils.CURRENCY_SYMBOL, currencySymbol);
                        bundle.putString(IBConstant.KEY_CODE_QR, jsonQueue.getCodeQR());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        new CustomToast().showToast(StoreWithMenuActivity.this, "Minimum cart amount is " + jsonQueue.getMinimumDeliveryOrder());
                    }
                } else {
                    ShowAlertInformation.showNetworkDialog(StoreWithMenuActivity.this);
                }
            } else {
                // Navigate to login screen
                Intent loginIntent = new Intent(StoreWithMenuActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    /**
     * Call when confirmed to show the kiosk mode.
     */
    private void populateKioskMode() {
        TextView tv_enable_kiosk = findViewById(R.id.tv_enable_kiosk);
        tv_enable_kiosk.setVisibility(View.VISIBLE);
        tv_enable_kiosk.setOnClickListener(v -> {
            ShowKioskModeDialog showKioskModeDialog = new ShowKioskModeDialog(StoreWithMenuActivity.this);
            showKioskModeDialog.setDialogClickListener(new ShowKioskModeDialog.DialogClickListener() {
                @Override
                public void btnPositiveClick(boolean isFeedBackScreen) {
                    NoQueueClientApplication.isLockMode = true;
                    KioskModeInfo kioskModeInfo = new KioskModeInfo();
                    kioskModeInfo.setKioskCodeQR(bizStoreElastic.getCodeQR());
                    kioskModeInfo.setKioskModeEnable(true);
                    kioskModeInfo.setLevelUp(false);
                    kioskModeInfo.setBizNameId(bizStoreElastic.getBizNameId());
                    kioskModeInfo.setBizName(bizStoreElastic.getBusinessName());
                    kioskModeInfo.setFeedbackScreen(isFeedBackScreen);
                    NoQueueClientApplication.setKioskModeInfo(kioskModeInfo);

                    if (NoQueueClientApplication.getKioskModeInfo().isFeedbackScreen()) {
                        Intent in = new Intent(StoreWithMenuActivity.this, SurveyKioskModeActivity.class);
                        in.putExtra(IBConstant.KEY_CODE_QR, NoQueueClientApplication.getKioskModeInfo().getKioskCodeQR());
                        startActivity(in);
                    } else {
                        NoQueueClientApplication.clearPreferences();
                        Intent in = new Intent(StoreWithMenuActivity.this, StoreWithMenuKioskActivity.class);
                        in.putExtra(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                        startActivity(in);
                    }
                    finish();
                }

                @Override
                public void btnNegativeClick() {
                    //Do nothing
                }
            });
            showKioskModeDialog.displayDialog(NoQueueClientApplication.getUserProfile().getUserLevel().getDescription());
        });
        // Added temporary due to textview click issue 
        tv_enable_kiosk.performClick();
    }

    @Override
    public void menuHeaderClick(int pos) {
        try {
            expandableListView.setSelectedGroup(pos);
            menuHeaderAdapter.setSelectedPosition(pos);
            menuHeaderAdapter.notifyDataSetChanged();
            expandableListView.scrollTo(0, -200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCartOrderInfo(BigDecimal amountString) {
        if (amountString.compareTo(new BigDecimal(0)) > 0) {
            tv_place_order.setVisibility(View.VISIBLE);
            tv_place_order.setText("Your cart amount is: " + currencySymbol + amountString.toString());
        } else {
            tv_place_order.setVisibility(View.GONE);
            tv_place_order.setText("");
        }
    }
}
