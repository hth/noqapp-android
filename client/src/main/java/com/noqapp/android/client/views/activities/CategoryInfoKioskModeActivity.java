package com.noqapp.android.client.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

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
        CategoryGridAdapter.OnItemClickListener {

    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = newBuilder()
            .maximumSize(1)
            .build();
    private final Cache<String, Map<String, ArrayList<BizStoreElastic>>> cacheQueue = newBuilder()
            .maximumSize(1)
            .build();

    private final String QUEUE = "queue";
    private final String CATEGORY = "category";
    private RecyclerView rv_categories;
    private ImageView iv_category_banner;
    private String codeQR;
    private BizStoreElastic bizStoreElastic;
    private String title = "";
    private View view_loader;
    private CategoryGridAdapter.OnItemClickListener listener;
    private EditText edt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSoftKeys(LaunchActivity.isLockMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info_kiosk);
        rv_categories = findViewById(R.id.rv_categories);
        iv_category_banner = findViewById(R.id.iv_category_banner);
        view_loader = findViewById(R.id.view_loader);
        initActionsViews(true);
        actionbarBack.setVisibility(View.INVISIBLE);
        edt_search = findViewById(R.id.edt_search);
        edt_search.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_LEFT = 0;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edt_search.getRight() - edt_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    AppUtils.hideKeyBoard(CategoryInfoKioskModeActivity.this);
                    edt_search.setText("");
                    return true;
                }
                if (event.getRawX() <= (20 + edt_search.getLeft() + edt_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    performSearch(edt_search.getText().toString());
                    return true;
                }
            }
            return false;
        });
        edt_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(edt_search.getText().toString());
                return true;
            }
            return false;
        });
        listener = this;
        codeQR = getIntent().getStringExtra(IBConstant.KEY_CODE_QR);
        setProgressMessage("Loading ...");
        if (!TextUtils.isEmpty(codeQR)) {
            if (NetworkUtils.isConnectingToInternet(this)) {
                showProgress();
                QueueApiUnAuthenticCall queueApiUnAuthenticCall = new QueueApiUnAuthenticCall();
                queueApiUnAuthenticCall.setQueuePresenter(this);
                queueApiUnAuthenticCall.getAllQueueStateLevelUp(UserUtils.getDeviceId(), codeQR);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        }
    }

    private void performSearch(String searchString) {
        AppUtils.hideKeyBoard(CategoryInfoKioskModeActivity.this);
        Intent in_search = new Intent(CategoryInfoKioskModeActivity.this, SearchActivity.class);
        in_search.putExtra("scrollId", "");
        in_search.putExtra("lat", "" + LaunchActivity.getLaunchActivity().latitude);
        in_search.putExtra("lng", "" + LaunchActivity.getLaunchActivity().longitude);
        in_search.putExtra("city", LaunchActivity.getLaunchActivity().cityName);
        in_search.putExtra("searchString", searchString);
        in_search.putExtra("codeQR", bizStoreElastic.getCodeQR());
        startActivity(in_search);
        edt_search.setText("");
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
            tv_toolbar_title.setText(bizStoreElastic.getBusinessName());
            codeQR = bizStoreElastic.getCodeQR();

//            Picasso.get()
//                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getDisplayImage()))
//                    .placeholder(ImageUtils.getBannerPlaceholder(this))
//                    .error(ImageUtils.getBannerErrorPlaceholder(this))
//                    .into(iv_category_banner);
//            if (bizStoreElastic.getBizServiceImages().size() > 0) {
//                // load first image default
//                Picasso.get()
//                        .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, bizStoreElastic.getBizServiceImages().get(0)))
//                        .placeholder(ImageUtils.getBannerPlaceholder(this))
//                        .error(ImageUtils.getBannerErrorPlaceholder(this))
//                        .into(iv_category_banner);
//            }
            if (AppUtils.isTablet(getApplicationContext())) {
                rv_categories.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                rv_categories.setLayoutManager(new GridLayoutManager(this, 2));
            }

            Map<String, ArrayList<BizStoreElastic>> queueMap = cacheQueue.getIfPresent(QUEUE);
            CategoryGridAdapter recyclerView_Adapter = new CategoryGridAdapter(
                    this,
                    getCategoryThatArePopulated(),
                    queueMap,
                    listener);
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
    public void onCategoryItemClick(JsonCategory jsonCategory, int pos) {
        Intent in = new Intent(this, QueueListActivity.class);
        in.putExtra("list", (Serializable) getCategoryThatArePopulated());
        in.putExtra("hashmap", (Serializable) cacheQueue.getIfPresent("queue"));
        in.putExtra("title", title);
        in.putExtra("position", pos);
        startActivity(in);
    }

}
