package com.noqapp.android.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.CategoryListPagerAdapter;
import com.noqapp.android.client.views.adapters.RecyclerViewGridAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoViewAllAdapter;
import com.noqapp.android.client.views.fragments.CategoryListFragment;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.cache.CacheBuilder.*;
import static com.noqapp.android.client.utils.AppUtilities.getTimeIn24HourFormat;

public class CategoryInfoActivity extends AppCompatActivity implements QueuePresenter, RecyclerViewGridAdapter.OnItemClickListener {


    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;

    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;

    @BindView(R.id.tv_address)
    protected TextView tv_address;

    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;

    @BindView(R.id.tv_hour_saved)
    protected TextView tv_hour_saved;

    @BindView(R.id.tv_rating_review)
    protected TextView tv_rating_review;

    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;

    @BindView(R.id.ll_slide_view)
    protected LinearLayout ll_slide_view;

    @BindView(R.id.list_pager)
    protected ViewPager list_pager;

    @BindView(R.id.rv_categories)
    protected RecyclerView rv_categories;

    @BindView(R.id.iv_category_banner)
    protected ImageView iv_category_banner;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;
    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.ll_cat_info)
    protected LinearLayout ll_cat_info;

    private String codeQR;

    private String frtag;

    private Animation animShow, animHide;
    private boolean isSliderOpen = false;

    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = newBuilder()
            .maximumSize(1)
            .build();

    private final Cache<String, Map<String, ArrayList<JsonQueue>>> cacheQueue = newBuilder()
            .maximumSize(1)
            .build();

    /* Compute Rating and Rating Count at runtime. */
    private float rating = 0;
    private int ratingCount = 0;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private CategoryListPagerAdapter mFragmentCardAdapter;
    private RecyclerViewGridAdapter.OnItemClickListener listener;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        ButterKnife.bind(this);
        fl_notification.setVisibility(View.INVISIBLE);
        actionbarBack.setVisibility(View.VISIBLE);
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_toolbar_title.setText("Categories");
        listener = this;

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, this);
        listener = this;
        animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);
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

        bundle = getIntent().getBundleExtra("b");
        if (null != bundle) {
            codeQR = bundle.getString(NoQueueBaseFragment.KEY_CODE_QR);
            boolean callingFromHistory = bundle.getBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
                QueueModel.queuePresenter = this;
                QueueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
            if (bundle.getBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false)) {
                frtag = LaunchActivity.tabList;
            } else {
                frtag = LaunchActivity.tabHome;
            }
            if (bundle.getBoolean(NoQueueBaseFragment.KEY_IS_REJOIN, false)) {

                frtag = LaunchActivity.getLaunchActivity().getCurrentSelectedTabTag();
            }
        }
        ll_cat_info.setFocusableInTouchMode(true);
        ll_cat_info.requestFocus();
        ll_cat_info.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isSliderOpen) {
                        ll_slide_view.startAnimation(animHide);
                        ll_slide_view.setVisibility(View.GONE);
                        isSliderOpen = false;
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });


        recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rv_categories.setLayoutManager(recyclerViewLayoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getArguments().getBoolean(KEY_FROM_LIST, false)) {
//            if (getArguments().getBoolean(KEY_IS_HISTORY, false)) {
//                LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_remote_qscan_detail));
//            } else {
//                LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_qscan_detail));
//            }
//        } else {
//            LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_qscan_detail));
//        }
//
//        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

    @Override
    public void queueError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
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


            LaunchActivity.getLaunchActivity().dismissProgress();
            tv_store_name.setText(jsonQueue.getBusinessName());
            tv_queue_name.setText(jsonQueue.getDisplayName());
            tv_queue_name.setVisibility(View.GONE);
            tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
            tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
            tv_mobile.setVisibility(View.GONE);
            if (jsonQueue.isDayClosed()) {
                tv_hour_saved.setText(getString(R.string.store_closed));
            } else {
                tv_hour_saved.setText(
                        getString(R.string.store_hour)
                                + " "
                                + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                                + " - "
                                + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getEndHour()));
            }
            tv_hour_saved.setVisibility(View.GONE);
            ratingBar.setRating(rating);
            // tv_rating.setText(String.valueOf(Math.round(jsonQueue.getRating())));
            tv_rating_review.setText(String.valueOf(ratingCount == 0 ? "No" : ratingCount) + " Reviews");
            codeQR = jsonQueue.getCodeQR();

            Picasso.with(this)
                    .load(jsonQueue.getDisplayImage())
                    .into(iv_category_banner);

    }

    @Override
    public void queueResponse(JsonQueueList jsonQueueList) {
        if (!jsonQueueList.getQueues().isEmpty()) {
            populateAndSortedCache(jsonQueueList);
            queueResponse(jsonQueueList.getQueues().get(0));
        } else {
            //TODO(chandra) when its empty do something nice
        }

        Map<String, ArrayList<JsonQueue>> queueMap = cacheQueue.getIfPresent("queue");
        RecyclerViewGridAdapter recyclerView_Adapter = new RecyclerViewGridAdapter( this,
                getCategoryThatArePopulated(),
                queueMap,listener);

        rv_categories.setAdapter(recyclerView_Adapter);
    }

    /**
     * Populated cache and sorted based on current time.
     *
     * @param jsonQueueList
     */
    private void populateAndSortedCache(JsonQueueList jsonQueueList) {
        Map<String, JsonCategory> categoryMap = new LinkedHashMap<>();
        for (JsonCategory jsonCategory : jsonQueueList.getCategories()) {
            categoryMap.put(jsonCategory.getBizCategoryId(), jsonCategory);
        }
        categoryMap.put("", new JsonCategory().setBizCategoryId("").setCategoryName(jsonQueueList.getQueues().get(0).getBusinessName()));
        cacheCategory.put("category", categoryMap);

        int systemHourMinutes = getTimeIn24HourFormat();
        Map<String, ArrayList<JsonQueue>> queueMap = new HashMap<>();
        float ratingQueue = 0;
        int ratingCountQueue = 0, queueWithRating = 0;
        for (JsonQueue jsonQueue : jsonQueueList.getQueues()) {

            //Likely hood of blank bizCategoryId
            String categoryId = jsonQueue.getBizCategoryId() == null ? "" : jsonQueue.getBizCategoryId();
            if (!queueMap.containsKey(categoryId)) {
                ArrayList<JsonQueue> jsonQueues = new ArrayList<>();
                jsonQueues.add(jsonQueue);
                queueMap.put(categoryId, jsonQueues);
            } else {
                ArrayList<JsonQueue> jsonQueues = queueMap.get(categoryId);
                jsonQueues.add(jsonQueue);
                AppUtilities.sortJsonQueues(systemHourMinutes, jsonQueues);
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
        Map<String, ArrayList<JsonQueue>> queueMap = cacheQueue.getIfPresent("queue");

        Set<String> categoryKey = categoryMap.keySet();
        Set<String> queueKey = queueMap.keySet();

        categoryKey.retainAll(queueKey);
        return new ArrayList<>(categoryMap.values());
    }

    @Override
    public void onCategoryItemClick(int pos) {
        ll_slide_view.setVisibility(View.VISIBLE);
        ll_slide_view.startAnimation(animShow);
        ArrayList<CategoryListFragment> mFragments = new ArrayList<>();

        Map<String, JsonCategory> categoryMap = cacheCategory.getIfPresent("category");
        Map<String, ArrayList<JsonQueue>> queueMap = cacheQueue.getIfPresent("queue");

        int count = 0;
        for (String key : categoryMap.keySet()) {
            String color = Constants.colorCodes[count % Constants.colorCodes.length];
            count++;

            if (queueMap.containsKey(key)) {
                mFragments.add(CategoryListFragment.newInstance(queueMap.get(key), categoryMap.get(key).getCategoryName(), color,
                        bundle.getBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false),
                        bundle.getBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false)));
            } else {
                Log.w("", "Skipped empty category " + key);
            }
        }

        mFragmentCardAdapter = new CategoryListPagerAdapter(
                getSupportFragmentManager(),
                mFragments);
        list_pager.setAdapter(null);
        list_pager.setAdapter(mFragmentCardAdapter);
        list_pager.setOffscreenPageLimit(3);
        list_pager.setCurrentItem(pos);
        isSliderOpen = true;
    }
}
