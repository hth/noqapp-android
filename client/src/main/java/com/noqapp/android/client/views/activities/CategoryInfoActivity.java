package com.noqapp.android.client.views.activities;

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
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.RecyclerViewGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.google.common.cache.Cache;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chandra on 5/7/17.
 */
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
    private TextView tv_store_name;
    private TextView tv_address;
    private TextView tv_mobile;
    private TextView tv_complete_address;
    private TextView tv_address_title;
    private TextView tv_rating_review;
    private TextView tv_rating;
    private RecyclerView rv_categories;
    private RecyclerView rv_thumb_images;
    private ImageView iv_category_banner;
    private Button btn_join_queues;
    private SegmentedControl sc_amenities;
    private SegmentedControl sc_facility;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private boolean isFuture = false;
    private float rating = 0;
    private int reviewCount = 0;
    private RecyclerViewGridAdapter.OnItemClickListener listener;
    private String title = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_address = findViewById(R.id.tv_address);
        tv_mobile = findViewById(R.id.tv_mobile);
        tv_complete_address = findViewById(R.id.tv_complete_address);
        tv_address_title = findViewById(R.id.tv_address_title);
        tv_rating_review = findViewById(R.id.tv_rating_review);
        tv_rating = findViewById(R.id.tv_rating);
        rv_categories = findViewById(R.id.rv_categories);
        rv_thumb_images = findViewById(R.id.rv_thumb_images);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        btn_join_queues = findViewById(R.id.btn_join_queues);
        sc_amenities = findViewById(R.id.sc_amenities);
        sc_facility = findViewById(R.id.sc_facility);
        initActionsViews(false);
        listener = this;
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
            }
        });
        btn_join_queues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinClick();
            }
        });


        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            codeQR = bundle.getString(NoQueueBaseFragment.KEY_CODE_QR);
            BizStoreElastic bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            if (null != bizStoreElastic) {
                if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO || bizStoreElastic.getBusinessType() == BusinessTypeEnum.BK) {
                    progressDialog.setMessage("Loading " + bizStoreElastic.getBusinessName() + "...");
                } else {
                    progressDialog.setMessage("Loading ...");
                }
            } else {
                progressDialog.setMessage("Loading ...");
            }
            if (NetworkUtils.isConnectingToInternet(this)) {
                progressDialog.show();
                QueueModel queueModel = new QueueModel();
                queueModel.setQueuePresenter(this);
                if (bundle.getBoolean("CallCategory", false)) {
                    queueModel.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
                } else {
                    queueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rv_categories.setLayoutManager(recyclerViewLayoutManager);

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
    public void authenticationFailure() {
        dismissProgress();
        AppUtilities.authenticationProcessing(this);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(this);
        } else {
            new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        }
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        dismissProgress();
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        if (!bizStoreElasticList.getBizStoreElastics().isEmpty()) {
            populateAndSortedCache(bizStoreElasticList);
            bizStoreElastic = bizStoreElasticList.getBizStoreElastics().get(0);
            LaunchActivity.getLaunchActivity().dismissProgress();
            tv_store_name.setText(bizStoreElastic.getBusinessName());
            tv_address.setText(AppUtilities.getStoreAddress(bizStoreElastic.getTown(), bizStoreElastic.getArea()));
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
            tv_rating.setText(String.valueOf(AppUtilities.round(rating))+" -");
            if (tv_rating.getText().toString().equals("0.0")) {
                tv_rating.setVisibility(View.INVISIBLE);
            } else {
                tv_rating.setVisibility(View.VISIBLE);
            }
            tv_rating_review.setText(String.valueOf(reviewCount == 0 ? "No" : reviewCount) + " Reviews");
            tv_rating_review.setPaintFlags(tv_rating_review.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_rating_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != bizStoreElastic && reviewCount > 0) {
                        Intent in = new Intent(CategoryInfoActivity.this, ShowAllReviewsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(NoQueueBaseActivity.KEY_CODE_QR, codeQR);
                        bundle.putString("storeName", bizStoreElastic.getBusinessName());
                        bundle.putString("storeAddress", tv_address.getText().toString());
                        bundle.putBoolean("isLevelUp", true);
                        in.putExtras(bundle);
                        startActivity(in);
                    }
                }
            });
            codeQR = bizStoreElastic.getCodeQR();
            try {
                // clear all the segments before load new
                sc_amenities.removeAllSegments();
                sc_facility.removeAllSegments();
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<AmenityEnum> amenityEnums = bizStoreElastic.getAmenities();
            List<String> amenities = new ArrayList<>();
            for (int j = 0; j < amenityEnums.size(); j++) {
                amenities.add(amenityEnums.get(j).getDescription());
            }
            sc_amenities.addSegments(amenities);
            List<FacilityEnum> facilityEnums = bizStoreElastic.getFacilities();
            List<String> facilities = new ArrayList<>();
            for (int j = 0; j < facilityEnums.size(); j++) {
                facilities.add(facilityEnums.get(j).getDescription());
            }
            sc_facility.addSegments(facilities);
            Picasso.with(this)
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv_thumb_images.setHasFixedSize(true);
            rv_thumb_images.setLayoutManager(horizontalLayoutManager);
            List<String> storeServiceImages = new ArrayList<>();
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
                    btn_join_queues.setText("Find Doctor");
                    tv_toolbar_title.setText("Medical");
                    title = "Select a Doctor";
                    break;
                case BK:
                    btn_join_queues.setText("View Services");
                    tv_toolbar_title.setText("Bank");
                    title = "Select a Service";
                    break;
                default:
                    btn_join_queues.setText("Join Queue");
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

            if (jsonQueue.getReviewCount() != 0) {
                ratingQueue += jsonQueue.getRating();
                ratingCountQueue += jsonQueue.getReviewCount();
                queueWithRating++;
            }
        }
        rating = ratingQueue / queueWithRating;
        reviewCount = ratingCountQueue;
        cacheQueue.put(QUEUE, queueMap);
    }

    public List<JsonCategory> getCategoryThatArePopulated() {
        Map<String, JsonCategory> categoryMap = new HashMap<>();
        Map<String, ArrayList<BizStoreElastic>> queueMap = new HashMap<>();
        if (null != cacheCategory.getIfPresent(CATEGORY))
            categoryMap = cacheCategory.getIfPresent(CATEGORY);
        if (null != cacheQueue.getIfPresent(QUEUE))
            queueMap = cacheQueue.getIfPresent(QUEUE);

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

    private void joinClick() {
        if (null != getCategoryThatArePopulated() && null != cacheQueue.getIfPresent("queue")) {
            Intent in = new Intent(this, CategoryPagerActivity.class);
            in.putExtra("list", (Serializable) getCategoryThatArePopulated());
            in.putExtra("hashmap", (Serializable) cacheQueue.getIfPresent("queue"));
            in.putExtra("title", title);
            startActivity(in);
        }
    }

}
