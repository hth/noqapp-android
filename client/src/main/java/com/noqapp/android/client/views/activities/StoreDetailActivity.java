package com.noqapp.android.client.views.activities;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.StoreDetailApiCalls;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.presenter.StorePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonStore;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.common.beans.store.JsonStoreCategory;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.pojos.StoreCartItem;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import com.noqapp.android.common.utils.CommonHelper;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreDetailActivity extends BaseActivity implements StorePresenter {

    private JsonStore jsonStore = null;
    private JsonQueue jsonQueue = null;
    private TextView tv_contact_no, tv_address, tv_address_title, tv_known_for, tv_store_name, tv_store_address, tv_store_timings, tv_header_menu, tv_header_famous;
    private BizStoreElastic bizStoreElastic;
    private RecyclerView rv_thumb_images, rv_photos;
    private boolean canAddItem = true;
    private TextView tv_rating, tv_rating_review;
    private RecyclerView rcv_payment_mode;
    private RecyclerView rcv_amenities;
    private RecyclerView rcv_delivery_types;
    private Button tv_menu;
    private ImageView iv_category_banner;
    private View view_loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(AppInitialize.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        tv_address = findViewById(R.id.tv_address);
        tv_address_title = findViewById(R.id.tv_address_title);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_store_address = findViewById(R.id.tv_store_address);
        tv_contact_no = findViewById(R.id.tv_contact_no);
        tv_known_for = findViewById(R.id.tv_known_for);
        tv_store_timings = findViewById(R.id.tv_store_timings);
        tv_header_menu = findViewById(R.id.tv_header_menu);
        tv_header_famous = findViewById(R.id.tv_header_famous);
        tv_menu = findViewById(R.id.tv_menu);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        tv_rating = findViewById(R.id.tv_rating);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        rcv_payment_mode = findViewById(R.id.rcv_payment_mode);
        rcv_amenities = findViewById(R.id.rcv_amenities);
        rcv_delivery_types = findViewById(R.id.rcv_delivery_types);
        view_loader = findViewById(R.id.view_loader);
        initActionsViews(false);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
        rv_thumb_images = findViewById(R.id.rv_thumb_images);
        rv_thumb_images.setHasFixedSize(true);
        rv_thumb_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rv_photos = findViewById(R.id.rv_photos);
        rv_photos.setHasFixedSize(true);
        rv_photos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_rating_review.setOnClickListener(v -> {
            if (null != jsonQueue && jsonQueue.getReviewCount() > 0) {
                Intent in = new Intent(StoreDetailActivity.this, AllReviewsActivity.class);
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
            new StoreDetailApiCalls(this).getStoreDetail(UserUtils.getDeviceId(), bizStoreElastic.getCodeQR());
        } else {
            ShowAlertInformation.showNetworkDialog(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_favourite:
                if (canAddItem) {
                    item.setIcon(R.drawable.heart_fill);
                    new CustomToast().showToast(this, "added to favourite");
                    canAddItem = false;
                } else {
                    item.setIcon(R.drawable.ic_heart);
                    new CustomToast().showToast(this, "remove from favourite");
                    canAddItem = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        //@TODO Chandra enable when the feature add on server
        menu.findItem(R.id.menu_favourite).setVisible(false);
        return true;
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
        tv_toolbar_title.setText(jsonQueue.getDisplayName());
        tv_contact_no.setText(jsonQueue.getStorePhone());
        tv_address.setText(jsonQueue.getStoreAddress());
        tv_address.setOnClickListener(v -> AppUtils.openAddressInMap(this, tv_address.getText().toString()));
        tv_address_title.setOnClickListener(v -> AppUtils.openAddressInMap(this, tv_address.getText().toString()));
        tv_store_address.setText(AppUtils.getStoreAddress(jsonQueue.getTown(), jsonQueue.getArea()));
        tv_store_name.setText(jsonQueue.getDisplayName());
        tv_known_for.setText(jsonQueue.getFamousFor());
        if (TextUtils.isEmpty(jsonQueue.getFamousFor())) {
            tv_header_famous.setVisibility(View.GONE);
        }
        List<PaymentModeEnum> temp = jsonQueue.getPaymentModes();
        ArrayList<String> payment_data = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            payment_data.add(temp.get(i).getDescription());
        }
        rcv_payment_mode.setLayoutManager(getFlexBoxLayoutManager());
        rcv_payment_mode.setAdapter(new StaggeredGridAdapter(payment_data));

        tv_rating.setText(String.valueOf(AppUtils.round(jsonQueue.getRating())));
        if (tv_rating.getText().toString().equals("0.0")) {
            tv_rating.setVisibility(View.INVISIBLE);
        } else {
            tv_rating.setVisibility(View.VISIBLE);
        }
        if (jsonQueue.getReviewCount() == 0) {
            tv_rating_review.setText("No Review");
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        } else if (jsonQueue.getReviewCount() == 1) {
            tv_rating_review.setText("1 Review");
        } else {
            tv_rating_review.setText(jsonQueue.getReviewCount() + " Reviews");
        }
        if (!TextUtils.isEmpty(bizStoreElastic.getDisplayImage()))
            Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
        else {
            Picasso.get().load(ImageUtils.getBannerPlaceholder()).into(iv_category_banner);
        }
        //
        List<AmenityEnum> amenities = jsonQueue.getAmenities();
        ArrayList<String> amenitiesdata = new ArrayList<>();
        for (int j = 0; j < amenities.size(); j++) {
            amenitiesdata.add(amenities.get(j).getDescription());
        }
        rcv_amenities.setLayoutManager(getFlexBoxLayoutManager());
        rcv_amenities.setAdapter(new StaggeredGridAdapter(amenitiesdata));

        List<DeliveryModeEnum> deliveryModes = jsonQueue.getDeliveryModes();
        ArrayList<String> deliveryModesdata = new ArrayList<>();
        for (int j = 0; j < deliveryModes.size(); j++) {
            deliveryModesdata.add(deliveryModes.get(j).getDescription());
        }
        rcv_delivery_types.setLayoutManager(getFlexBoxLayoutManager());
        rcv_delivery_types.setAdapter(new StaggeredGridAdapter(deliveryModesdata));


        //
        ArrayList<String> storeServiceImages = new ArrayList<>(jsonQueue.getStoreServiceImages());
        for (int i = 0; i < storeServiceImages.size(); i++) {
            storeServiceImages.set(i, jsonQueue.getCodeQR() + "/" + storeServiceImages.get(i));
        }
        ThumbnailGalleryAdapter serviceAdapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
        rv_thumb_images.setAdapter(serviceAdapter);
        //
        final ArrayList<String> storeInteriorImages = new ArrayList<>(jsonQueue.getStoreInteriorImages());
        for (int i = 0; i < storeInteriorImages.size(); i++) {
            storeInteriorImages.set(i, jsonQueue.getCodeQR() + "/" + storeInteriorImages.get(i));
        }
        ThumbnailGalleryAdapter interiorAdapter = new ThumbnailGalleryAdapter(this, storeInteriorImages);
        rv_photos.setAdapter(interiorAdapter);
        if (null != storeInteriorImages && storeInteriorImages.size() > 0) {
            iv_category_banner.setOnClickListener((View v) -> {
                Intent intent = new Intent(StoreDetailActivity.this, SliderActivity.class);
                intent.putExtra("pos", 0);
                intent.putExtra("imageurls", storeInteriorImages);
                startActivity(intent);
            });
        }
        //
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

        tv_menu.setOnClickListener((View v) -> {
            if (isOrderNow()) {
                Intent in = new Intent(StoreDetailActivity.this, StoreMenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("jsonStoreCategories", jsonStoreCategories);
                bundle.putSerializable("listDataChild", storeCartItems);
                bundle.putSerializable("jsonQueue", jsonQueue);
                bundle.putBoolean("isStoreOpen", AppUtils.isStoreOpenToday(jsonStore));
                in.putExtras(bundle);
                startActivity(in);
            } else {
                //Do nothing
                new CustomToast().showToast(StoreDetailActivity.this, "Please visit store to purchase.");
            }
        });
        if (AppUtils.isStoreOpenToday(jsonStore)) {
            tv_menu.setClickable(true);
            if (isOrderNow()) {
                tv_menu.setText("Order Now");
            } else {
                tv_menu.setText("Visit Store");
                tv_header_menu.setVisibility(View.GONE);
            }
        } else {
            tv_menu.setClickable(false);
            tv_menu.setText("Closed");
        }
        tv_store_timings.setText(Html.fromHtml(new AppUtils().formatWeeklyTimings(this, jsonStore.getJsonHours())));
    }

    private boolean isOrderNow() {
        switch (jsonQueue.getBusinessType()) {
            case PH:
            case HS:
                return false;
            default:
                return true;
        }
    }

    public FlexboxLayoutManager getFlexBoxLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }
}
