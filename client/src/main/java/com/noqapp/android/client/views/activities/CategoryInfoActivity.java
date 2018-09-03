package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import static com.google.common.cache.CacheBuilder.newBuilder;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.FacilityEnum;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkChangeReceiver;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.RecyclerViewGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.google.common.cache.Cache;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryInfoActivity extends BaseActivity implements QueuePresenter, RecyclerViewGridAdapter.OnItemClickListener {


    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = newBuilder()
            .maximumSize(1)
            .build();
    private final Cache<String, Map<String, ArrayList<BizStoreElastic>>> cacheQueue = newBuilder()
            .maximumSize(1)
            .build();
    private final String QUEUE = "queue";
    private final String CATEGORY = "category";
    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_amenities)
    protected TextView tv_amenities;
    @BindView(R.id.tv_address)
    protected TextView tv_address;
    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;
    @BindView(R.id.tv_complete_address)
    protected TextView tv_complete_address;
    @BindView(R.id.tv_address_title)
    protected TextView tv_address_title;
    @BindView(R.id.tv_rating_review)
    protected TextView tv_rating_review;
    @BindView(R.id.tv_rating)
    protected TextView tv_rating;
    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;
    @BindView(R.id.iv_business_icon)
    protected ImageView iv_business_icon;
    @BindView(R.id.rv_categories)
    protected RecyclerView rv_categories;
    @BindView(R.id.rv_thumb_images)
    protected RecyclerView rv_thumb_images;
    @BindView(R.id.iv_category_banner)
    protected ImageView iv_category_banner;
    @BindView(R.id.btn_join_queues)
    protected Button btn_join_queues;
    @BindView(R.id.sc_amenities)
    protected SegmentedControl sc_amenities;
    @BindView(R.id.sc_facility)
    protected SegmentedControl sc_facility;
    @BindView(R.id.ll_cat_info)
    protected LinearLayout ll_cat_info;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private boolean isFuture = false;
    private float rating = 0;
    private int ratingCount = 0;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerViewGridAdapter.OnItemClickListener listener;
    private Bundle bundle;
    private String title = "";
    private NetworkChangeReceiver myReceiver = new NetworkChangeReceiver();
    private EventBus bus = EventBus.getDefault();
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        ButterKnife.bind(this);
        initActionsViews(false);
        listener = this;
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
            }
        });
        snackbar = Snackbar
                .make(ll_cat_info, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(myReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }

        bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            codeQR = bundle.getString(NoQueueBaseFragment.KEY_CODE_QR);
            BizStoreElastic bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            if(null != bizStoreElastic)
              progressDialog.setMessage("Loading "+bizStoreElastic.getBusinessName()+"...");
            else
                progressDialog.setMessage("Loading ...");
//            if (NetworkUtils.isConnectingToInternet(this)) {
//                showSnackBar(true);
//                progressDialog.show();
//                QueueModel queueModel = new QueueModel();
//                queueModel.setQueuePresenter(this);
//                if (bundle.getBoolean("CallCategory", false)) {
//                    queueModel.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
//                } else {
//                    queueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
//                }
//            } else {
//                showSnackBar(false);
//                //ShowAlertInformation.showNetworkDialog(this);
//            }
        }
        recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rv_categories.setLayoutManager(recyclerViewLayoutManager);

    }

    @Subscribe
    public void onEvent(Boolean name) {
        Log.e("name value: ", String.valueOf(name));
        if (NetworkUtils.isConnectingToInternet(this)) {
            showSnackBar(true);
            if (null == bizStoreElastic) {
                progressDialog.show();
                QueueModel queueModel = new QueueModel();
                queueModel.setQueuePresenter(this);
                if (bundle.getBoolean("CallCategory", false)) {
                    queueModel.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
                } else {
                    queueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
                }
            }
        } else {
            showSnackBar(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            unregisterReceiver(myReceiver);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void queueError() {
        dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        AppUtilities.authenticationProcessing(this,errorCode);
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        dismissProgress();
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        if (!bizStoreElasticList.getBizStoreElastics().isEmpty()) {
            populateAndSortedCache(bizStoreElasticList);
            bizStoreElastic = bizStoreElasticList.getBizStoreElastics().get(0);
            LaunchActivity.getLaunchActivity().dismissProgress();
            tv_store_name.setText(bizStoreElastic.getBusinessName());
            tv_address.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(),bizStoreElastic.getArea()));
            tv_complete_address.setText(bizStoreElastic.getAddress());
            tv_complete_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_complete_address.getText().toString());
                }
            });
            tv_address_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_complete_address.getText().toString());
                }
            });
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            ratingBar.setRating(rating);
            tv_rating.setText(String.valueOf(AppUtilities.round(bizStoreElastic.getRating())));
            if(tv_rating.getText().toString().equals("0.0"))
                tv_rating.setVisibility(View.INVISIBLE);
            else
                tv_rating.setVisibility(View.VISIBLE);
            tv_rating_review.setText(String.valueOf(ratingCount == 0 ? "No" : ratingCount) + " Reviews");
            codeQR = bizStoreElastic.getCodeQR();
            AppUtilities.setStoreDrawable(this, iv_business_icon, bizStoreElastic.getBusinessType(), tv_rating);

            List<AmenityEnum> amenities = bizStoreElastic.getAmenities();
            ArrayList<String> amenitiesdata = new ArrayList<>();
            for (int j = 0; j < amenities.size(); j++) {
                amenitiesdata.add(amenities.get(j).getDescription());
            }
            sc_amenities.addSegments(amenitiesdata);
            List<FacilityEnum> faclities = bizStoreElastic.getFacilities();
            ArrayList<String> data = new ArrayList<>();
            for (int j = 0; j < faclities.size(); j++) {
                data.add(faclities.get(j).getDescription());
            }
            sc_facility.addSegments(data);
            Picasso.with(this)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv_thumb_images.setHasFixedSize(true);
            rv_thumb_images.setLayoutManager(horizontalLayoutManager);
            ArrayList<String> storeServiceImages = new ArrayList<>();
            // initialize list if we are receiving urls from server
            if (bizStoreElastic.getBizServiceImages().size() > 0) {
                storeServiceImages = (ArrayList<String>) bizStoreElastic.getBizServiceImages();
                // load first image default
                Picasso.with(this)
                        .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getBizServiceImages().get(0)))
                        .placeholder(ImageUtils.getBannerPlaceholder(this))
                        .error(ImageUtils.getBannerErrorPlaceholder(this))
                        .into(iv_category_banner);
            }

            ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
            rv_thumb_images.setAdapter(adapter);
            Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);

            if (isFuture) {
                RecyclerViewGridAdapter recyclerView_Adapter
                        = new RecyclerViewGridAdapter(this,
                        getCategoryThatArePopulated(),
                        queueMap, listener);
                rv_categories.setAdapter(recyclerView_Adapter);
            }

            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                    btn_join_queues.setText("Find a Doctor");
                    tv_toolbar_title.setText("Medical");
                    title = "Select a Doctor";
                    break;
                case BK:
                    btn_join_queues.setText("View Services");
                    tv_toolbar_title.setText("Bank");
                    title = "Select a Service";
                    break;
                default:
                    btn_join_queues.setText("Join a Queue");
                    tv_toolbar_title.setText("Departments");
                    title = "Select a Queue";
            }
        } else {
            //TODO(chandra) when its empty do something nice
        }


        dismissProgress();
    }

    /**
     * Populated cache and sorted based on current time.
     * <p>
     * //@param jsonQueueList
     */
    private void populateAndSortedCache(BizStoreElasticList bizStoreElasticList) {
        Map<String, JsonCategory> categoryMap = new LinkedHashMap<>();
        for (JsonCategory jsonCategory : bizStoreElasticList.getJsonCategories()) {
            categoryMap.put(jsonCategory.getBizCategoryId(), jsonCategory);
        }
        categoryMap.put("", new JsonCategory().setBizCategoryId("").setCategoryName(bizStoreElasticList.getBizStoreElastics().get(0).getBusinessName()));
        cacheCategory.put(CATEGORY, categoryMap);

        Map<String, ArrayList<BizStoreElastic>> queueMap = new HashMap<>();
        float ratingQueue = 0;
        int ratingCountQueue = 0, queueWithRating = 0;
        for (BizStoreElastic jsonQueue : bizStoreElasticList.getBizStoreElastics()) {

            //Likely hood of blank bizCategoryId
            String bizCategoryId = jsonQueue.getBizCategoryId() == null ? "" : jsonQueue.getBizCategoryId();
            if (!queueMap.containsKey(bizCategoryId)) {
                ArrayList<BizStoreElastic> jsonQueues = new ArrayList<>();
                jsonQueues.add(jsonQueue);
                queueMap.put(bizCategoryId, jsonQueues);
            } else {
                ArrayList<BizStoreElastic> jsonQueues = queueMap.get(bizCategoryId);
                jsonQueues.add(jsonQueue);
                // AppUtilities.sortJsonQueues(systemHourMinutes, jsonQueues);
            }

            if (jsonQueue.getRatingCount() != 0) {
                ratingQueue += jsonQueue.getRating();
                ratingCountQueue += jsonQueue.getRatingCount();
                queueWithRating++;
            }
        }
        rating = ratingQueue / queueWithRating;
        ratingCount = ratingCountQueue;
        cacheQueue.put(QUEUE, queueMap);
    }

    public List<JsonCategory> getCategoryThatArePopulated() {
        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent(CATEGORY);
        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);

        Set<String> categoryKey = categoryMap.keySet();
        Set<String> queueKey = queueMap.keySet();

        categoryKey.retainAll(queueKey);
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public void onCategoryItemClick(int pos, JsonCategory jsonCategory) {
        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent(CATEGORY);
        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);
        switch (bizStoreElastic.getBusinessType()) {
            case BK:
                Intent in = new Intent(this, JoinActivity.class);
                in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, queueMap.get(jsonCategory.getBizCategoryId()).get(0).getCodeQR());
                in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
                in.putExtra(NoQueueBaseFragment.KEY_IS_HISTORY, false);
                in.putExtra("isCategoryData", false);
                startActivity(in);
                break;
            default:
                Intent intent = new Intent(this, CategoryListActivity.class);
                intent.putExtra("categoryName", categoryMap.get(jsonCategory.getBizCategoryId()).getCategoryName());
                intent.putExtra("list", (Serializable) queueMap.get(jsonCategory.getBizCategoryId()));
                startActivity(intent);
        }

    }

    @OnClick(R.id.btn_join_queues)
    public void joinClick() {
        if (null != getCategoryThatArePopulated() && null != cacheQueue.getIfPresent("queue")) {
            Intent in = new Intent(this, CategoryPagerActivity.class);
            in.putExtra("list", (Serializable) getCategoryThatArePopulated());
            in.putExtra("hashmap", (Serializable) cacheQueue.getIfPresent("queue"));
            in.putExtra("title", title);
            startActivity(in);
        }
    }
    private void showSnackBar(boolean isHide) {
        if (isHide)
            snackbar.dismiss();
        else
            snackbar.show();
    }
}
