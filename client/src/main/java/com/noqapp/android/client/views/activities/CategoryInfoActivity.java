package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.cache.Cache;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.RecyclerViewGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.noqapp.android.client.utils.AppUtilities.getTimeIn24HourFormat;

public class CategoryInfoActivity extends BaseActivity implements QueuePresenter, RecyclerViewGridAdapter.OnItemClickListener {


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

    private String codeQR;
    private  BizStoreElastic bizStoreElastic;
    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = newBuilder()
            .maximumSize(1)
            .build();

    private final Cache<String, Map<String, ArrayList<BizStoreElastic>>> cacheQueue = newBuilder()
            .maximumSize(1)
            .build();

    /* Compute Rating and Rating Count at runtime. */
    private float rating = 0;
    private int ratingCount = 0;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerViewGridAdapter.OnItemClickListener listener;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        ButterKnife.bind(this);
        initActionsViews(false);
        tv_toolbar_title.setText("Departments");
        listener = this;
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
        listener = this;
        tv_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.makeCall(LaunchActivity.getLaunchActivity(), tv_mobile.getText().toString());
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openAddressInMap(LaunchActivity.getLaunchActivity(), tv_address.getText().toString());
            }
        });

        bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            codeQR = bundle.getString(NoQueueBaseFragment.KEY_CODE_QR);
            boolean callingFromHistory = bundle.getBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                progressDialog.show();
                QueueModel.queuePresenter = this;
                if (bundle.getBoolean("CallCategory", false)) {
                    QueueModel.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
                } else {
                    QueueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
                }
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
        recyclerViewLayoutManager = new GridLayoutManager(this, 2);
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
    public void authenticationFailure(int errorCode) {
        dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(this);
        }
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(LaunchActivity.getLaunchActivity());

        }
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
            String address = "";
            if (!TextUtils.isEmpty(bizStoreElastic.getTown())) {
                address = bizStoreElastic.getTown();
            }
            if (!TextUtils.isEmpty(bizStoreElastic.getArea())) {
                address = bizStoreElastic.getArea() + "," + address;
            }
            tv_address.setText(address);
            tv_complete_address.setText(bizStoreElastic.getAddress());
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(bizStoreElastic.getCountryShortName(), bizStoreElastic.getPhone()));
            ratingBar.setRating(rating);
            tv_rating.setText(String.valueOf(Math.round(bizStoreElastic.getRating())));
            tv_rating_review.setText(String.valueOf(ratingCount == 0 ? "No" : ratingCount) + " Reviews");
            tv_amenities.setText("Amenities: Parking, Free Wi-Fi"); //Ambulance Service,Emergency Service 24/7,Ac/non-ac beds
            codeQR = bizStoreElastic.getCodeQR();
            AppUtilities.setStoreDrawable(this,iv_business_icon,bizStoreElastic.getBusinessType(),tv_rating);
            Picasso.with(this)
                    .load(bizStoreElastic.getDisplayImage())
                    .into(iv_category_banner);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv_thumb_images.setHasFixedSize(true);
            rv_thumb_images.setLayoutManager(horizontalLayoutManager);
            ArrayList<String> storeServiceImages = new ArrayList<String>();
            storeServiceImages.add("https://www.ssdhospital.com/wp-content/uploads/2016/11/dscn0794-1024x590.jpg");
            storeServiceImages.add("http://www.ssdhospital.in/wp-content/uploads/2016/11/dscn0783.jpg");
            storeServiceImages.add("https://static.lybrate.com/imgs/ps/cl/b21f1285cf4e0de0ef3ec8f9c01a5d65/4444185b9b85355188138aa90c3890d7/Sai-Snehdeep-Hospital-Navi-Mumbai-a8c3fc.jpg");
            storeServiceImages.add("http://medicaltreatmentcost.com/oc-content/uploads/0/119.jpg");
            storeServiceImages.add("https://content1.jdmagicbox.com/comp/mumbai/b1/022pxx22.xx22.130622101306.r7b1/catalogue/sai-snehdeep-hospital-kopar-khairane-mumbai-dermatologists-2ov44bu.jpg");
            storeServiceImages.add("https://content3.jdmagicbox.com/comp/mumbai/b1/022pxx22.xx22.130622101306.r7b1/catalogue/sai-snehdeep-hospital-kopar-khairane-mumbai-dermatologists-3zj5ots.jpg");
                    //(ArrayList<String>) bizStoreElastic.getStoreServiceImages();
            ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
            rv_thumb_images.setAdapter(adapter);

        } else {
            //TODO(chandra) when its empty do something nice
        }

        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent("queue");
        RecyclerViewGridAdapter recyclerView_Adapter
                = new RecyclerViewGridAdapter(this,
                getCategoryThatArePopulated(),
                queueMap, listener);

        rv_categories.setAdapter(recyclerView_Adapter);
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
        cacheCategory.put("category", categoryMap);

        int systemHourMinutes = getTimeIn24HourFormat();
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

        cacheQueue.put("queue", queueMap);
    }

    public List<JsonCategory> getCategoryThatArePopulated() {
        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent("category");
        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent("queue");

        Set<String> categoryKey = categoryMap.keySet();
        Set<String> queueKey = queueMap.keySet();

        categoryKey.retainAll(queueKey);
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public void onCategoryItemClick(int pos, JsonCategory jsonCategory) {
        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent("category");
        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent("queue");
        switch ( bizStoreElastic .getBusinessType()) {

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
}
