package com.noqapp.android.client.views.fragments;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.presenter.QueuePresenter;
import com.noqapp.android.client.presenter.beans.JsonCategory;
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.presenter.beans.JsonQueueList;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;
import com.noqapp.android.client.utils.PhoneFormatterUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.adapters.CategoryListPagerAdapter;
import com.noqapp.android.client.views.adapters.CategoryPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.noqapp.android.client.utils.AppUtilities.getSystemHourMinutes;

public class CategoryInfoFragment extends NoQueueBaseFragment implements QueuePresenter, CategoryPagerAdapter.CategoryPagerClick {
    private final String TAG = CategoryInfoFragment.class.getSimpleName();

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

    @BindView(R.id.viewPager)
    protected ViewPager viewPager;

    @BindView(R.id.ll_slide_view)
    protected LinearLayout ll_slide_view;

    @BindView(R.id.list_pager)
    protected ViewPager list_pager;

    private String codeQR;

    private String frtag;

    private Animation animShow, animHide;
    private JsonQueueList jsonQueueList;
    private boolean isSliderOpen = false;

    //Set cache parameters
    private final Cache<String, Map<String, JsonCategory>> cacheCategory = CacheBuilder.newBuilder()
            .maximumSize(1)
            .build();

    private final Cache<String, Map<String, ArrayList<JsonQueue>>> cacheQueue = CacheBuilder.newBuilder()
            .maximumSize(1)
            .build();

    private CategoryListPagerAdapter mFragmentCardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_info, container, false);
        ButterKnife.bind(this, view);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        AppUtilities.setRatingBarColor(stars, getActivity());

        animShow = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_show);
        animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.popup_hide);
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

        Bundle bundle = getArguments();
        if (null != bundle) {
            codeQR = bundle.getString(KEY_CODE_QR);
            boolean callingFromHistory = getArguments().getBoolean(KEY_IS_HISTORY, false);
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                LaunchActivity.getLaunchActivity().progressDialog.show();
//                if (UserUtils.isLogin()) {
//                    QueueApiModel.queuePresenter = this;
//                    if (callingFromHistory) {
//                        QueueApiModel.remoteScanQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
//                    } else {
//                        QueueApiModel.getQueueState(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), codeQR);
//                    }
//                } else {
                QueueModel.queuePresenter = this;
                QueueModel.getAllQueueState(UserUtils.getDeviceId(), codeQR);
                // }
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
            if (bundle.getBoolean(KEY_FROM_LIST, false)) {
                frtag = LaunchActivity.tabList;
            } else {
                frtag = LaunchActivity.tabHome;
            }
            if (bundle.getBoolean(KEY_IS_REJOIN, false)) {

                frtag = LaunchActivity.getLaunchActivity().getCurrentSelectedTabTag();
            }
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Log.i(tag, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    //   Log.i(tag, "onKey Back listener is working!!!");
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getBoolean(KEY_FROM_LIST, false)) {
            if (getArguments().getBoolean(KEY_IS_HISTORY, false)) {
                LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.remotejoin));
            } else {
                LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_qscan_detail));
            }
        } else {
            LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_qscan_detail));
        }

        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

    @Override
    public void queueError() {
        Log.d(TAG, "Queue=Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void authenticationFailure(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        if (errorCode == Constants.INVALID_CREDENTIAL) {
            NoQueueBaseActivity.clearPreferences();
            ShowAlertInformation.showAuthenticErrorDialog(getActivity());
        }
        if (errorCode == Constants.INVALID_BAR_CODE) {
            ShowAlertInformation.showBarcodeErrorDialog(LaunchActivity.getLaunchActivity());

        }
    }

    @Override
    public void queueResponse(JsonQueue jsonQueue) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Queue=" + jsonQueue.toString());
        tv_store_name.setText(jsonQueue.getBusinessName());
        tv_queue_name.setText(jsonQueue.getDisplayName());
        tv_address.setText(Formatter.getFormattedAddress(jsonQueue.getStoreAddress()));
        tv_mobile.setText(PhoneFormatterUtil.formatNumber(jsonQueue.getCountryShortName(), jsonQueue.getStorePhone()));
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
        ratingBar.setRating(jsonQueue.getRating());
        // tv_rating.setText(String.valueOf(Math.round(jsonQueue.getRating())));
        tv_rating_review.setText(String.valueOf(jsonQueue.getRatingCount() == 0 ? "No" : jsonQueue.getRatingCount()) + " Reviews");

        codeQR = jsonQueue.getCodeQR();

    }

    @Override
    public void queueResponse(JsonQueueList jsonQueueList) {
        this.jsonQueueList = jsonQueueList;
        if (!jsonQueueList.getQueues().isEmpty()) {
            queueResponse(jsonQueueList.getQueues().get(0));
            populateAndSortedCache(jsonQueueList);
        } else {
            //TODO(chandra) when its empty do something nice
        }

        Map<String, ArrayList<JsonQueue>> queueMap = cacheQueue.getIfPresent("queue");
        CategoryPagerAdapter mCardAdapter = new CategoryPagerAdapter(
                getActivity(),
                getCategoryThatArePopulated(),
                queueMap,
                this);
        viewPager.setAdapter(mCardAdapter);
        viewPager.setOffscreenPageLimit(3);
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
        categoryMap.put("", new JsonCategory().setBizCategoryId("").setCategoryName("My Name"));
        cacheCategory.put("category", categoryMap);

        int systemHourMinutes = getSystemHourMinutes();
        Map<String, ArrayList<JsonQueue>> queueMap = new HashMap<>();
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
        }
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
    public void pageClicked(int position) {
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
                mFragments.add(CategoryListFragment.newInstance(queueMap.get(key), categoryMap.get(key).getCategoryName(), color));
            } else {
                Log.w(TAG, "Skipped empty category " + key);
            }
        }


//TODO(chandra) why this logic is relatively bad then new logic. Of course have to delete this
//        for (int j = 0; j < jsonQueueList.getCategories().size(); j++) {
//
//            ArrayList<JsonQueue> temp = new ArrayList<>();
//            for (int i = 0; i < jsonQueueList.getQueues().size(); i++) {
//                if (null != jsonQueueList.getQueues().get(i).getBizCategoryId() &&
//                        jsonQueueList.getQueues().get(i).getBizCategoryId().equals(jsonQueueList.getCategories().get(j).getBizCategoryId()))
//                    temp.add(jsonQueueList.getQueues().get(i));
//            }
//            String color = colorCodes[j % colorCodes.length];
//            mFragments.add(CategoryListFragment.newInstance(temp, jsonQueueList.getCategories().get(j).getCategoryName(), color));
//
//        }
        mFragmentCardAdapter = new CategoryListPagerAdapter(
                getActivity().getSupportFragmentManager(),
                mFragments);
        list_pager.setAdapter(null);
        list_pager.setAdapter(mFragmentCardAdapter);
        list_pager.setOffscreenPageLimit(3);
        list_pager.setCurrentItem(position);
        isSliderOpen = true;

    }
}
