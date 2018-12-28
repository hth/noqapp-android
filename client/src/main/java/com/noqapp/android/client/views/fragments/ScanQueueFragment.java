package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.FeedModel;
import com.noqapp.android.client.model.NearMeModel;
import com.noqapp.android.client.model.QueueApiModel;
import com.noqapp.android.client.model.QueueModel;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.NearMePresenter;
import com.noqapp.android.client.presenter.NoQueueDBPresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.FeedPresenter;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.noqapp.android.client.presenter.beans.JsonFeedList;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.RateTheAppManager;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AfterJoinActivity;
import com.noqapp.android.client.views.activities.BlinkerActivity;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.FeedActivity;
import com.noqapp.android.client.views.activities.JoinActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.OrderConfirmActivity;
import com.noqapp.android.client.views.activities.SearchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.ViewAllListActivity;
import com.noqapp.android.client.views.adapters.CurrentActivityAdapter;
import com.noqapp.android.client.views.adapters.FeedAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.customviews.CirclePagerIndicatorDecoration;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanQueueFragment extends Scanner implements View.OnClickListener, FeedAdapter.OnItemClickListener, CurrentActivityAdapter.OnItemClickListener, NearMePresenter, StoreInfoAdapter.OnItemClickListener, TokenAndQueuePresenter, TokenQueueViewInterface, FeedPresenter {

    private final String TAG = ScanQueueFragment.class.getSimpleName();
    private RelativeLayout rl_scan;
    private RecyclerView rv_health_care;
    private RecyclerView rv_current_activity;
    private RecyclerView rv_feed;
    private TextView tv_current_title;
    private TextView tv_deviceId;
    private RecyclerView rv_merchant_around_you;
    private TextView tv_health_care_view_all;
    private TextView tv_near_view_all;
    private ProgressBar pb_current;
    private ProgressBar pb_health_care;
    private ProgressBar pb_near;
    private ProgressBar pb_feed;
    private CardView cv_update_location;
    private LinearLayout rl_current_activity;
    private TextView tv_no_thanks;
    private TextView tv_update;

    private boolean fromList = false;
    private ArrayList<BizStoreElastic> nearMeData;
    private ArrayList<BizStoreElastic> nearMeHospital;
    private CurrentActivityAdapter.OnItemClickListener currentClickListner;
    private StoreInfoAdapter.OnItemClickListener storeListener;
    private String scrollId = "";
    private double lat, log;
    private String city = "";
    private boolean isFirstTimeUpdate = true;
    private static final String SHOWCASE_ID = "screen helper";
    private boolean isProgressFirstTime = true;
    private static final int MSG_CURRENT_QUEUE = 0;
    private static final int MSG_HISTORY_QUEUE = 1;
    private static TokenQueueViewInterface tokenQueueViewInterface;
    private static QueueHandler mHandler;

    public ScanQueueFragment() {

    }

    public void updateUIWithNewLocation(final double latitude, final double longitude, final String cityName) {
        if (latitude != 0.0 && latitude != LaunchActivity.getLaunchActivity().getDefaultLatitude() && Double.compare(lat, latitude) != 0 && !cityName.equals(city)) {
            if (isFirstTimeUpdate) {
                getNearMeInfo(cityName, "" + latitude, "" + longitude);
                lat = latitude;
                log = longitude;
                city = cityName;
                isFirstTimeUpdate = false;
            } else {
                cv_update_location.setVisibility(View.VISIBLE);
                tv_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNearMeInfo(cityName, "" + latitude, "" + longitude);
                        lat = latitude;
                        log = longitude;
                        city = cityName;
                        cv_update_location.setVisibility(View.GONE);
                    }
                });
                tv_no_thanks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cv_update_location.setVisibility(View.GONE);
                    }
                });
            }
        } else {
            cv_update_location.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scan_queue, container, false);

        rl_scan = view.findViewById(R.id.rl_scan);

        rv_health_care = view.findViewById(R.id.rv_health_care);
        rv_current_activity = view.findViewById(R.id.rv_current_activity);
        tv_current_title = view.findViewById(R.id.tv_current_title);
        tv_deviceId = view.findViewById(R.id.tv_deviceId);
        rv_merchant_around_you = view.findViewById(R.id.rv_merchant_around_you);

        tv_health_care_view_all = view.findViewById(R.id.tv_health_care_view_all);
        tv_near_view_all = view.findViewById(R.id.tv_near_view_all);
        pb_current = view.findViewById(R.id.pb_current);
        pb_health_care = view.findViewById(R.id.pb_health_care);
        pb_near = view.findViewById(R.id.pb_near);
        pb_feed = view.findViewById(R.id.pb_feed);
        cv_update_location = view.findViewById(R.id.cv_update_location);
        rl_current_activity = view.findViewById(R.id.rl_current_activity);
        tv_no_thanks = view.findViewById(R.id.tv_no_thanks);
        tv_update = view.findViewById(R.id.tv_update);
        rl_scan.setOnClickListener(this);
        tv_health_care_view_all.setOnClickListener(this);
        tv_near_view_all.setOnClickListener(this);

        rv_feed = view.findViewById(R.id.rv_feed);
        rv_feed.setHasFixedSize(true);
        rv_feed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_feed.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tokenQueueViewInterface = this;
        currentClickListner = this;
        storeListener = this;
        mHandler = new QueueHandler();

        rv_health_care.setHasFixedSize(true);
        rv_health_care.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_health_care.setItemAnimator(new DefaultItemAnimator());

        //
        rv_current_activity.setHasFixedSize(true);
        rv_current_activity.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_current_activity.setItemAnimator(new DefaultItemAnimator());
        rv_current_activity.addItemDecoration(new CirclePagerIndicatorDecoration());

        //
        rv_merchant_around_you.setHasFixedSize(true);
        rv_merchant_around_you.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        // rv_merchant_around_you.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            callCurrentAndHistoryQueue();
            FeedModel feedModel = new FeedModel(this);
            feedModel.activeFeed(UserUtils.getDeviceId());
            pb_feed.setVisibility(View.VISIBLE);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }

        if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
            lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
            log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
            city = LaunchActivity.getLaunchActivity().getDefaultCity();
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
        } else {
            lat = LaunchActivity.getLaunchActivity().latitute;
            log = LaunchActivity.getLaunchActivity().longitute;
            city = LaunchActivity.getLaunchActivity().cityName;
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity() && isAdded()) {
            /* Update the current Queue & history queue so that user get the latest queue status & get reflected in DB. */
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                callCurrentAndHistoryQueue();
            }
        }
        try {
            tv_deviceId.setText(UserUtils.getDeviceId() + "\n" + NoQueueBaseActivity.getFCMToken());
            // tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callCurrentAndHistoryQueue() {
        if (UserUtils.isLogin()) { // Call secure API if user is loggedIn else normal API
            //Call the current queue
            QueueApiModel queueApiModel = new QueueApiModel();
            queueApiModel.setTokenAndQueuePresenter(this);
            //Log.e("DEVICE ID NULL", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            queueApiModel.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());

            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken(), Constants.appVersion());
            queueApiModel.allHistoricalJoinedQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
        } else {
            //Call the current queue
            QueueModel queueModel = new QueueModel();
            queueModel.setTokenAndQueuePresenter(this);
            queueModel.getAllJoinedQueue(UserUtils.getDeviceId());
            //Log.e("DEVICE ID NULL Un", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken(), Constants.appVersion());
            queueModel.getAllHistoricalJoinedQueue(UserUtils.getDeviceId(), deviceToken);
        }
        if (isProgressFirstTime) {
            pb_current.setVisibility(View.VISIBLE);
            pb_health_care.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void barcodeResult(String codeQR, boolean isCategoryData) {
        if (isCategoryData) {
            Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
            Bundle b = new Bundle();
            b.putString(KEY_CODE_QR, codeQR);
            b.putBoolean(KEY_FROM_LIST, fromList);
            in.putExtra("bundle", b);
            getActivity().startActivity(in);
        } else {
            Intent in = new Intent(getActivity(), JoinActivity.class);
            in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
            in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
            in.putExtra("isCategoryData", false);
            startActivity(in);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void getNearMeInfo(String city, String lat, String longitute) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            StoreInfoParam storeInfoParam = new StoreInfoParam();
            storeInfoParam.setCityName(city);
            storeInfoParam.setLatitude(lat);
            storeInfoParam.setLongitude(longitute);
            storeInfoParam.setFilters("xyz");
            storeInfoParam.setScrollId("");
            if (isProgressFirstTime) {
                pb_near.setVisibility(View.VISIBLE);
                pb_health_care.setVisibility(View.VISIBLE);
            }
            NearMeModel nearMeModel = new NearMeModel(this);
            nearMeModel.otherMerchant(UserUtils.getDeviceId(), storeInfoParam);
            // nearMeModel.healthCare(UserUtils.getDeviceId(), storeInfoParam);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {
        nearMeData = new ArrayList<>();
        nearMeData.addAll(bizStoreElasticList.getBizStoreElastics());
//        for (int i = 0; i < bizStoreElasticList.getBizStoreElastics().size(); i++) {
//            if (bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType() != BusinessTypeEnum.PH &&
//                    bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType() != BusinessTypeEnum.DO) {
//                nearMeData.add(bizStoreElasticList.getBizStoreElastics().get(i));
//            }
//        }
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(lat, log)));
        StoreInfoAdapter storeInfoAdapter = new StoreInfoAdapter(nearMeData, getActivity(), storeListener, lat, log);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe", bizStoreElasticList.toString());
        scrollId = bizStoreElasticList.getScrollId();
        pb_near.setVisibility(View.GONE);
        tv_near_view_all.setVisibility(nearMeData.size() == 0 ? View.GONE : View.VISIBLE);
        isProgressFirstTime = false;
        if (NoQueueBaseActivity.getShowHelper() && isAdded()) {
            presentShowcaseSequence();
            NoQueueBaseActivity.setShowHelper(false);
        }else {
            new RateTheAppManager().app_launched(getActivity());
        }
    }

    @Override
    public void nearMeError() {
        pb_near.setVisibility(View.GONE);
    }

    @Override
    public void nearMeHospitalResponse(BizStoreElasticList bizStoreElasticList) {
        nearMeHospital = new ArrayList<>();
        for (int i = 0; i < bizStoreElasticList.getBizStoreElastics().size(); i++) {
            if (bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType() == BusinessTypeEnum.PH ||
                    bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType() == BusinessTypeEnum.DO) {
                nearMeHospital.add(bizStoreElasticList.getBizStoreElastics().get(i));
            }
        }
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeHospital, new SortPlaces(new LatLng(lat, log)));
        StoreInfoAdapter storeInfoAdapter = new StoreInfoAdapter(nearMeHospital, getActivity(), storeListener, lat, log);
        rv_health_care.setAdapter(storeInfoAdapter);
        Log.v("NearMe Hospital", bizStoreElasticList.toString());
        scrollId = bizStoreElasticList.getScrollId();
        pb_health_care.setVisibility(View.GONE);
        tv_health_care_view_all.setVisibility(nearMeHospital.size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void nearMeHospitalError() {
        pb_health_care.setVisibility(View.GONE);
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
            case HS:
                // open hospital/Bank profile
                Bundle b = new Bundle();
                b.putString(KEY_CODE_QR, item.getCodeQR());
                b.putBoolean(KEY_FROM_LIST, fromList);
                b.putBoolean("CallCategory", true);
                b.putBoolean("isCategoryData", false);
                b.putSerializable("BizStoreElastic", item);
                Intent in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            default:
                // open order screen
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", item);
                intent.putExtras(bundle);
                startActivity(intent);
        }
    }

    @Override
    public void currentItemClick(JsonTokenAndQueue item, View view, int pos) {
        if (null != item) {
            if (item.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.Q) {
                Intent in = new Intent(getActivity(), AfterJoinActivity.class);
                in.putExtra(KEY_CODE_QR, item.getCodeQR());
                in.putExtra(KEY_FROM_LIST, true);
                in.putExtra(KEY_JSON_TOKEN_QUEUE, item);
                startActivity(in);
            } else {
                Intent in = new Intent(getActivity(), OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(KEY_FROM_LIST, true);
                bundle.putString(KEY_CODE_QR, item.getCodeQR());
                bundle.putInt("token", item.getToken());
                bundle.putInt("currentServing", item.getServingNumber());
                bundle.putString("storeName", item.getDisplayName());
                bundle.putString("storeAddress", item.getStoreAddress());
                bundle.putString(AppUtilities.CURRENCY_SYMBOL, AppUtilities.getCurrencySymbol(item.getCountryShortName()));
                in.putExtras(bundle);
                startActivity(in);
            }
        }
    }

    @Override
    public void onFeedItemClick(JsonFeed item, View view, int pos) {
        Intent in = new Intent(getActivity(), FeedActivity.class);
        in.putExtra("object", item);
        startActivity(in);
    }

    private void nearClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeData);
        intent.putExtra("scrollId", scrollId);
        intent.putExtra("lat", "" + lat);
        intent.putExtra("long", "" + log);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    private void healthCareClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeHospital);
        intent.putExtra("scrollId", scrollId);
        intent.putExtra("lat", "" + lat);
        intent.putExtra("long", "" + log);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    @Override
    public void currentQueueResponse(List<JsonTokenAndQueue> tokenAndQueues) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter();
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveCurrentTokenQueue(tokenAndQueues);
        pb_current.setVisibility(View.GONE);
    }

    @Override
    public void historyQueueResponse(List<JsonTokenAndQueue> tokenAndQueues, boolean sinceBeginning) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter();
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveHistoryTokenQueue(tokenAndQueues, sinceBeginning);
    }

    @Override
    public void historyQueueError() {
        Log.d(TAG, "History queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(false);
    }

    @Override
    public void currentQueueError() {
        Log.d(TAG, "Current queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(true);
        pb_current.setVisibility(View.GONE);
    }

    @Override
    public void authenticationFailure() {
        LaunchActivity.getLaunchActivity().dismissProgress();
        AppUtilities.authenticationProcessing(getActivity());
        pb_current.setVisibility(View.GONE);
        pb_health_care.setVisibility(View.GONE);
        pb_near.setVisibility(View.GONE);
        pb_feed.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
        pb_feed.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej)
            new ErrorResponseHandler().processError(getActivity(), eej);
        pb_feed.setVisibility(View.GONE);
    }

    @Override
    public void currentQueueSaved() {
        passMsgToHandler(true);
    }

    @Override
    public void historyQueueSaved() {
        passMsgToHandler(false);
    }

    private void passMsgToHandler(boolean isCurrentQueue) {
        if (null != getActivity() && isAdded()) {
            // pass msg to handler to load the data from DB
            if (isCurrentQueue) {
                Message msg = new Message();
                msg.what = MSG_CURRENT_QUEUE;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = MSG_HISTORY_QUEUE;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void tokenCurrentQueueList(List<JsonTokenAndQueue> currentQueueList) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, "Current Queue Count : " + String.valueOf(currentQueueList.size()));
        if (null != getActivity() && isAdded()) {
            CurrentActivityAdapter currentActivityAdapter = new CurrentActivityAdapter(currentQueueList, getActivity(), currentClickListner);
            rv_current_activity.setAdapter(currentActivityAdapter);
            tv_current_title.setText(getString(R.string.active_queue) + " (" + String.valueOf(currentQueueList.size()) + ")");
            currentActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void tokenHistoryQueueList(List<JsonTokenAndQueue> historyQueueList) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        Log.d(TAG, ":History Queue Count:" + String.valueOf(historyQueueList.size()));
    }

    public void updateListFromNotification(JsonTokenAndQueue jq, String go_to) {
        boolean isUpdated = TokenAndQueueDB.updateCurrentListQueueObject(jq.getCodeQR(), "" + jq.getServingNumber(), "" + jq.getToken());
        boolean isUserTurn = jq.afterHowLong() == 0;
        if (isUserTurn && isUpdated) {
            boolean showBuzzer;
            ReviewData reviewData = ReviewDB.getValue(jq.getCodeQR(), "" + jq.getToken());
            if (null != reviewData) {
                if (!reviewData.getIsBuzzerShow().equals("1")) {
                    showBuzzer = true;
                } else {
                    showBuzzer = false;
                }
                // update
            } else {
                //insert
                ContentValues cv = new ContentValues();
                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                cv.put(DatabaseTable.Review.CODE_QR, jq.getCodeQR());
                cv.put(DatabaseTable.Review.TOKEN, "" + jq.getToken());
                cv.put(DatabaseTable.Review.QID, jq.getQueueUserId());
                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                cv.put(DatabaseTable.Review.KEY_GOTO, "");
                ReviewDB.insert(cv);
                showBuzzer = true;
            }
            if (showBuzzer) {
                if (QueueOrderTypeEnum.Q == jq.getBusinessType().getQueueOrderType()) {
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                    ReviewDB.updateReviewRecord(jq.getCodeQR(), String.valueOf(jq.getToken()), cv);
                    Intent blinker = new Intent(getActivity(), BlinkerActivity.class);
                    startActivity(blinker);
                } else {
                    switch (jq.getPurchaseOrderState()) {
                        case RP:
                        case RD:
                            ContentValues cv = new ContentValues();
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                            ReviewDB.updateReviewRecord(jq.getCodeQR(), String.valueOf(jq.getToken()), cv);
                            Intent blinker = new Intent(getActivity(), BlinkerActivity.class);
                            startActivity(blinker);
                            break;
                        case CO:
                            ShowAlertInformation.showInfoDisplayDialog(getActivity(), jq.getBusinessName() + " has cancelled your order. Sorry for your inconvenience. :(");
                            break;
                        default:
                            //Do Nothing
                    }
                }
            }
        }
        //fetch the
        fetchCurrentAndHistoryList();
    }

    public void fetchCurrentAndHistoryList() {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter();
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.getCurrentTokenQueueListFromDB();
        dbPresenter.getHistoryTokenQueueListFromDB();
    }

    public void callSearch() {
        Intent in_search = new Intent(getActivity(), SearchActivity.class);
        in_search.putExtra("scrollId", "");
        in_search.putExtra("lat", "" + lat);
        in_search.putExtra("long", "" + log);
        in_search.putExtra("city", city);
        startActivity(in_search);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_scan:
                startScanningBarcode();
                break;
            case R.id.tv_health_care_view_all:
                healthCareClick();
                break;
            case R.id.tv_near_view_all:
                nearClick();
                break;

        }
    }

    @Override
    public void allActiveFeedResponse(JsonFeedList jsonFeedList) {
        if (null != jsonFeedList && jsonFeedList.getJsonFeeds().size() > 0) {
            FeedAdapter feedAdapter = new FeedAdapter(jsonFeedList.getJsonFeeds(), getActivity(), this);
            rv_feed.setAdapter(feedAdapter);
        }
        pb_feed.setVisibility(View.GONE);
    }

    private static class QueueHandler extends Handler {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter();

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CURRENT_QUEUE:
                    dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                    dbPresenter.getCurrentTokenQueueListFromDB();
                    break;

                case MSG_HISTORY_QUEUE:
                    dbPresenter.tokenQueueViewInterface = tokenQueueViewInterface;
                    dbPresenter.getHistoryTokenQueueListFromDB();
                    break;
                default:
                    break;
            }
        }
    }

    private void presentShowcaseSequence() {
        try {
            MaterialShowcaseView.resetSingleUse(getActivity(), SHOWCASE_ID);
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);
            sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
                @Override
                public void onShow(MaterialShowcaseView itemView, int position) {
                    // Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
                }
            });
            sequence.setConfig(config);
            sequence.addSequenceItem(
                    //autoCompleteTextView, "Click here to scan the store QRCode to join their queue", "GOT IT"
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(LaunchActivity.getLaunchActivity().tv_location)
                            .setDismissText("GOT IT")
                            .setContentText("Search your preferred location")
                            .withRectangleShape(true)
                            .build()
            );
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(rl_scan)
                            .setDismissText("GOT IT")
                            .setContentText("Click here to scan store QRCode to join their queue or place order")
                            .withRectangleShape(true)
                            .build()
            );
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(rl_current_activity)
                            .setDismissText("DONE")
                            .setContentText("Your current join queue or order will be visible here")
                            .withRectangleShape(true)
                            .build()
            );
            sequence.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
