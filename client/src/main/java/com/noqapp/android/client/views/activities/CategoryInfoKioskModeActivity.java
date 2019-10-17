package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.cache.Cache;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CategoryGridAdapter;
import com.noqapp.android.client.views.adapters.LevelUpQueueAdapter;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.cache.CacheBuilder.newBuilder;

public class CategoryInfoKioskModeActivity extends BaseActivity implements QueuePresenter,
        LevelUpQueueAdapter.OnItemClickListener, CategoryGridAdapter.OnItemClickListener {

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
    private RecyclerView rv_categories;
    private ImageView iv_category_banner;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private String title = "";
    private View view_loader;
    private CategoryGridAdapter.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info_kiosk);
        tv_store_name = findViewById(R.id.tv_store_name);
        rv_categories = findViewById(R.id.rv_categories);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        view_loader = findViewById(R.id.view_loader);
        initActionsViews(false);
        actionbarBack.setOnClickListener(null);
        listener = this;
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (null != bundle) {
            codeQR = bundle.getString(IBConstant.KEY_CODE_QR);
            BizStoreElastic bizStoreElastic = (BizStoreElastic) bundle.getSerializable("BizStoreElastic");
            if (null != bizStoreElastic) {
                if (bizStoreElastic.getBusinessType() == BusinessTypeEnum.DO || bizStoreElastic.getBusinessType() == BusinessTypeEnum.BK) {
                    setProgressMessage("Loading " + bizStoreElastic.getBusinessName() + "...");
                } else {
                    setProgressMessage("Loading ...");
                }
            } else {
                setProgressMessage("Loading ...");
            }
            if (NetworkUtils.isConnectingToInternet(this)) {
                showProgress();
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
    }


    @Override
    public void onBackPressed() {
        return;
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
    public void queueResponse(JsonQueue jsonQueue) {
        dismissProgress();
    }


    @Override
    public void queueResponse(BizStoreElasticList bizStoreElasticList) {
        if (!bizStoreElasticList.getBizStoreElastics().isEmpty()) {
            view_loader.setVisibility(View.GONE);
            populateAndSortedCache(bizStoreElasticList);
            bizStoreElastic = bizStoreElasticList.getBizStoreElastics().get(0);
            dismissProgress();
            tv_store_name.setText(bizStoreElastic.getBusinessName());
            codeQR = bizStoreElastic.getCodeQR();

            Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
                    .placeholder(ImageUtils.getBannerPlaceholder(this))
                    .error(ImageUtils.getBannerErrorPlaceholder(this))
                    .into(iv_category_banner);
            if (bizStoreElastic.getBizServiceImages().size() > 0) {
                // load first image default
                Picasso.get()
                        .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getBizServiceImages().get(0)))
                        .placeholder(ImageUtils.getBannerPlaceholder(this))
                        .error(ImageUtils.getBannerErrorPlaceholder(this))
                        .into(iv_category_banner);
            }

            switch (bizStoreElastic.getBusinessType()) {
                case DO:
                    tv_toolbar_title.setText("Medical");
                    title = "Select a Doctor";
                    break;
                case BK:
                    tv_toolbar_title.setText("Bank");
                    title = "Select a Service";
                    break;
                case HS:
                    tv_toolbar_title.setText("Health Service");
                    title = "Select a Service";
                    break;
                default:
                    tv_toolbar_title.setText("Departments");
                    title = "Select a Queue";
            }

            rv_categories.setLayoutManager(new GridLayoutManager(this, 2));
            Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);
            CategoryGridAdapter recyclerView_Adapter
                    = new CategoryGridAdapter(this,
                    getCategoryThatArePopulated(),
                    queueMap, listener);
            rv_categories.setAdapter(recyclerView_Adapter);

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
    public void onCategoryItemClick(BizStoreElastic item) {
        Intent in = null;
        Bundle b = new Bundle();
        switch (item.getBusinessType()) {
            case DO:
            case BK:
                // open hospital profile
                in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                in.putExtra(IBConstant.KEY_IMAGE_URL, AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, item.getDisplayImage()));
                startActivity(in);
                break;
            case HS:
            case PH: {
                // open order screen
                in = new Intent(this, StoreDetailActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
            break;
            default: {
                // open order screen
                in = new Intent(this, StoreWithMenuActivity.class);
                b.putSerializable("BizStoreElastic", item);
                in.putExtras(b);
                startActivity(in);
            }
        }
    }

    @Override
    public void onCategoryItemClick(JsonCategory jsonCategory, int pos) {
        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent(CATEGORY);
        Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);
        switch (bizStoreElastic.getBusinessType()) {
            case BK: {
                Intent in = new Intent(this, BeforeJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, queueMap.get(jsonCategory.getBizCategoryId()).get(0).getCodeQR());
                in.putExtra(IBConstant.KEY_FROM_LIST, false);
                in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
                startActivity(in);
            }
            break;
            default:
                Intent in = new Intent(this, QueueListActivity.class);
                in.putExtra("list", (Serializable) getCategoryThatArePopulated());
                in.putExtra("hashmap", (Serializable) cacheQueue.getIfPresent("queue"));
                in.putExtra("title", title);
                in.putExtra("position", pos);
                startActivity(in);
        }

    }
}
