package com.noqapp.android.client.views.activities;

import static com.google.common.cache.CacheBuilder.newBuilder;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.FacilityEnum;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.StoreHourElastic;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.AccreditionAdapter;
import com.noqapp.android.client.views.adapters.CategoryListAdapter;
import com.noqapp.android.client.views.adapters.RecyclerViewGridAdapter;
import com.noqapp.android.client.views.adapters.StaggeredGridAdapter;
import com.noqapp.android.client.views.adapters.ThumbnailGalleryAdapter;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.common.utils.PhoneFormatterUtil;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.common.cache.Cache;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public class CategoryInfoActivity extends BaseActivity implements QueuePresenter, RecyclerViewGridAdapter.OnItemClickListener,CategoryListAdapter.OnItemClickListener {

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
    private RecyclerView rcv_amenities;
    private RecyclerView rcv_facility;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private float rating = 0;
    private int reviewCount = 0;
    private RecyclerViewGridAdapter.OnItemClickListener listener;
    private String title = "";
    private View view_loader;
    private RecyclerView rcv_accreditation,rcv_single_queue;
    private LinearLayout ll_top_header;

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
        rcv_amenities = findViewById(R.id.rcv_amenities);
        rcv_facility = findViewById(R.id.rcv_facility);
        rcv_accreditation = findViewById(R.id.rcv_accreditation);
        rcv_single_queue = findViewById(R.id.rcv_single_queue);
        ll_top_header = findViewById(R.id.ll_top_header);
        view_loader = findViewById(R.id.view_loader);
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
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
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
                QueueApiUnAuthenticCall queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
                queueApiUnAuthenticCall.setQueuePresenter(this);
                if (bundle.getBoolean(IBConstant.KEY_CALL_CATEGORY, false)) {
                    queueApiUnAuthenticCall.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
                } else {
                    queueApiUnAuthenticCall.getAllQueueState(UserUtils.getDeviceId(), codeQR);
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
            view_loader.setVisibility(View.GONE);
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
            tv_rating.setText(String.valueOf(AppUtilities.round(rating)) + " -");
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
                        bundle.putString(IBConstant.KEY_CODE_QR, codeQR);
                        bundle.putString(IBConstant.KEY_STORE_NAME, bizStoreElastic.getBusinessName());
                        bundle.putString(IBConstant.KEY_STORE_ADDRESS, tv_address.getText().toString());
                        bundle.putBoolean("isLevelUp", true);
                        in.putExtras(bundle);
                        startActivity(in);
                    }
                }
            });
            codeQR = bizStoreElastic.getCodeQR();

            List<AmenityEnum> amenityEnums = bizStoreElastic.getAmenities();
            List<String> amenities = new ArrayList<>();
            for (int j = 0; j < amenityEnums.size(); j++) {
                amenities.add(amenityEnums.get(j).getDescription());
            }
            rcv_amenities.setLayoutManager(getFlexBoxLayoutManager());
            rcv_amenities.setAdapter(new StaggeredGridAdapter(amenities));
            List<FacilityEnum> facilityEnums = bizStoreElastic.getFacilities();
            List<String> facilities = new ArrayList<>();
            for (int j = 0; j < facilityEnums.size(); j++) {
                facilities.add(facilityEnums.get(j).getDescription());
            }
            rcv_facility.setLayoutManager(getFlexBoxLayoutManager());
            rcv_facility.setAdapter(new StaggeredGridAdapter(facilities));
            Picasso.get()
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
            rv_thumb_images.setHasFixedSize(true);
            rv_thumb_images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            List<String> storeServiceImages = new ArrayList<>();
            // initialize list if we are receiving urls from server
            if (bizStoreElastic.getBizServiceImages().size() > 0) {
                storeServiceImages = (ArrayList<String>) bizStoreElastic.getBizServiceImages();
                // load first image default
                Picasso.get()
                        .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getBizServiceImages().get(0)))
                        .placeholder(ImageUtils.getBannerPlaceholder(this))
                        .error(ImageUtils.getBannerErrorPlaceholder(this))
                        .into(iv_category_banner);
            }

            ThumbnailGalleryAdapter adapter = new ThumbnailGalleryAdapter(this, storeServiceImages);
            rv_thumb_images.setAdapter(adapter);
            rcv_accreditation.setHasFixedSize(true);
            rcv_accreditation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            List<String> accreditationImages = new ArrayList<>();
            if (bizStoreElastic.getAccreditation().size() > 0) {
                for (int i = 0; i < bizStoreElastic.getAccreditation().size(); i++) {
                    accreditationImages.add(bizStoreElastic.getAccreditation().get(i).getFilename());
                }
            }
            AccreditionAdapter accreditionAdapter = new AccreditionAdapter(this, accreditationImages);
            rcv_accreditation.setAdapter(accreditionAdapter);
            if (null != storeServiceImages && storeServiceImages.size() > 0) {
                iv_category_banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryInfoActivity.this, SliderActivity.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("imageurls", (ArrayList<String>) bizStoreElastic.getBizServiceImages());
                        startActivity(intent);
                    }
                });
            }
            Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);
            boolean isFuture = false; // for future
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
                case HS:
                    btn_join_queues.setText("View Services");
                    tv_toolbar_title.setText("Health Service");
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
        checkForSingleEntry();
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
        for (BizStoreElastic bizStoreElastic : bizStoreElasticList.getBizStoreElastics()) {

            //Likely hood of blank bizCategoryId
            String bizCategoryId = bizStoreElastic.getBizCategoryId() == null ? "" : bizStoreElastic.getBizCategoryId();

            /* When level up, check if queue is offline. */
            if (bizStoreElastic.isActive()) {
                if (!queueMap.containsKey(bizCategoryId)) {
                    ArrayList<BizStoreElastic> bizStoreElasticArrayList = new ArrayList<>();
                    bizStoreElasticArrayList.add(bizStoreElastic);
                    queueMap.put(bizCategoryId, bizStoreElasticArrayList);
                } else {
                    ArrayList<BizStoreElastic> jsonQueues = queueMap.get(bizCategoryId);
                    jsonQueues.add(bizStoreElastic);
                    // AppUtilities.sortJsonQueues(systemHourMinutes, jsonQueues);
                }

                if (bizStoreElastic.getReviewCount() != 0) {
                    ratingQueue += bizStoreElastic.getRating();
                    ratingCountQueue += bizStoreElastic.getReviewCount();
                    queueWithRating++;
                }
            }
        }
        rating = ratingQueue / queueWithRating;
        reviewCount = ratingCountQueue;
        cacheQueue.put(QUEUE, queueMap);
    }

    public List<JsonCategory> getCategoryThatArePopulated() {
        Map<String, JsonCategory> categoryMap = new HashMap<>();
        Map<String, ArrayList<BizStoreElastic>> queueMap = new HashMap<>();
        if (null != cacheCategory.getIfPresent(CATEGORY)) {
            categoryMap = cacheCategory.getIfPresent(CATEGORY);
        }
        if (null != cacheQueue.getIfPresent(QUEUE)) {
            queueMap = cacheQueue.getIfPresent(QUEUE);
        }

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
                in.putExtra(IBConstant.KEY_CODE_QR, queueMap.get(jsonCategory.getBizCategoryId()).get(0).getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                startActivity(in);
                break;
            default:
                Intent intent = new Intent(this, CategoryListActivity.class);
                intent.putExtra("categoryName", categoryMap.get(jsonCategory.getBizCategoryId()).getCategoryName());
                intent.putExtra("list", (Serializable) queueMap.get(jsonCategory.getBizCategoryId()));
                intent.putExtra("title", title);
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

    private void checkForSingleEntry(){
        rcv_single_queue.setVisibility(View.GONE);
        ll_top_header.setVisibility(View.VISIBLE);

        if ((null != getCategoryThatArePopulated() && getCategoryThatArePopulated().size()==1) &&
                (null != cacheQueue.getIfPresent("queue") && cacheQueue.getIfPresent("queue").size()==1)){

            Map.Entry<String, ArrayList<BizStoreElastic>> entry = cacheQueue.getIfPresent("queue").entrySet().iterator().next();
            ArrayList<BizStoreElastic> bizStoreElastics = entry.getValue();
            if(bizStoreElastics.size() == 1){
                if(!AppUtilities.isRelease())
                Toast.makeText(this, "One item found", Toast.LENGTH_SHORT).show();
                CategoryListAdapter categoryListAdapter = new CategoryListAdapter(bizStoreElastics, this, this,true);
                rcv_single_queue.setHasFixedSize(true);
                rcv_single_queue.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                rcv_single_queue.setItemAnimator(new DefaultItemAnimator());
                rcv_single_queue.setAdapter(categoryListAdapter);
                rcv_single_queue.setVisibility(View.VISIBLE);
                btn_join_queues.setVisibility(View.GONE);
                ll_top_header.setVisibility(View.GONE);
            }else{
                if(!AppUtilities.isRelease())
                Toast.makeText(this, bizStoreElastics.size()+" item found", Toast.LENGTH_SHORT).show();
            }
        }else {
            if(!AppUtilities.isRelease())
            Toast.makeText(this, "Other Case", Toast.LENGTH_SHORT).show();
        }

    }
   public FlexboxLayoutManager getFlexBoxLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        return layoutManager;
    }

    @Override
    public void onCategoryItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                // open hospital profile
                Intent in = new Intent(this, JoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                startActivity(in);
                break;
            default:
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                in.putExtras(bundle);
                startActivity(in);
        }
    }
}
