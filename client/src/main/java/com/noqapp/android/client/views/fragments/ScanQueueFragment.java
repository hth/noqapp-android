package com.noqapp.android.client.views.fragments;

import static com.noqapp.android.common.utils.Formatter.formatRFC822;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
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
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.presenter.beans.body.StoreInfoParam;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AfterJoinActivity;
import com.noqapp.android.client.views.activities.BlinkerActivity;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.JoinActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.OrderConfirmActivity;
import com.noqapp.android.client.views.activities.SearchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.ViewAllListActivity;
import com.noqapp.android.client.views.adapters.CurrentActivityAdapter;
import com.noqapp.android.client.views.adapters.GooglePlacesAutocompleteAdapter;
import com.noqapp.android.client.views.adapters.RecentActivityAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.customviews.CirclePagerIndicatorDecoration;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.body.DeviceToken;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import java.util.Comparator;
import java.util.List;

public class ScanQueueFragment extends Scanner implements View.OnClickListener, CurrentActivityAdapter.OnItemClickListener, RecentActivityAdapter.OnItemClickListener, NearMePresenter, StoreInfoAdapter.OnItemClickListener, TokenAndQueuePresenter, TokenQueueViewInterface {

    private final String TAG = ScanQueueFragment.class.getSimpleName();
    protected RelativeLayout rl_scan;
    protected RecyclerView rv_recent_activity;
    protected RecyclerView rv_current_activity;
    protected TextView tv_current_title;
    protected TextView tv_auto;
    protected TextView tv_deviceId;
    protected RecyclerView rv_merchant_around_you;
    protected TextView tv_recent_view_all;
    protected TextView tv_near_view_all;
    protected ProgressBar pb_current;
    protected ProgressBar pb_recent;
    protected ProgressBar pb_near;
    protected AutoCompleteTextView autoCompleteTextView;
    protected CardView cv_update_location;
    protected LinearLayout rl_current_activity;
    protected TextView tv_no_thanks;
    protected TextView tv_update;

    private boolean fromList = false;
    private RecentActivityAdapter recentActivityAdapter;
    private ArrayList<BizStoreElastic> nearMeData;
    private CurrentActivityAdapter.OnItemClickListener currentClickListner;
    private RecentActivityAdapter.OnItemClickListener recentClickListner;
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
                AppUtilities.setAutoCompleteText(autoCompleteTextView, cityName);
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
                        AppUtilities.setAutoCompleteText(autoCompleteTextView, cityName);
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
        rv_recent_activity = view.findViewById(R.id.rv_recent_activity);
        rv_current_activity = view.findViewById(R.id.rv_current_activity);
        tv_current_title = view.findViewById(R.id.tv_current_title);
        tv_auto = view.findViewById(R.id.tv_auto);
        tv_deviceId = view.findViewById(R.id.tv_deviceId);
        rv_merchant_around_you = view.findViewById(R.id.rv_merchant_around_you);
        tv_recent_view_all = view.findViewById(R.id.tv_recent_view_all);
        tv_near_view_all = view.findViewById(R.id.tv_near_view_all);
        pb_current = view.findViewById(R.id.pb_current);
        pb_recent = view.findViewById(R.id.pb_recent);
        pb_near = view.findViewById(R.id.pb_near);
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        cv_update_location = view.findViewById(R.id.cv_update_location);
        rl_current_activity = view.findViewById(R.id.rl_current_activity);
        tv_no_thanks = view.findViewById(R.id.tv_no_thanks);
        tv_update = view.findViewById(R.id.tv_update);
        rl_scan.setOnClickListener(this);
        tv_recent_view_all.setOnClickListener(this);
        tv_near_view_all.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String city_name = (String) parent.getItemAtPosition(position);
                    LatLng latLng = AppUtilities.getLocationFromAddress(getActivity(), city_name);
                    if (null != latLng) {
                        lat = latLng.latitude;
                        log = latLng.longitude;
                    } else {
                        lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                        log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                    }
                    city = city_name;
                    getNearMeInfo(city_name, String.valueOf(lat), String.valueOf(log));
                    new AppUtilities().hideKeyBoard(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        autoCompleteTextView.setThreshold(3);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        autoCompleteTextView.setText("");
                        return true;
                    }
//                    if (event.getRawX() <= (10+autoCompleteTextView.getLeft() + autoCompleteTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                        // your action here
//                        lat = LaunchActivity.getLaunchActivity().latitute;
//                        log = LaunchActivity.getLaunchActivity().longitute;
//                        city = LaunchActivity.getLaunchActivity().cityName;
//                        autoCompleteTextView.setText(city);
//                        getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
//                        new AppUtilities().hideKeyBoard(getActivity());
//                        return true;
//                    }
                }
                return false;
            }
        });
        tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
                    lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
                    log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
                    city = LaunchActivity.getLaunchActivity().getDefaultCity();
                    AppUtilities.setAutoCompleteText(autoCompleteTextView, city);
                    getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
                } else {
                    lat = LaunchActivity.getLaunchActivity().latitute;
                    log = LaunchActivity.getLaunchActivity().longitute;
                    city = LaunchActivity.getLaunchActivity().cityName;
                    AppUtilities.setAutoCompleteText(autoCompleteTextView, city);
                    getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
                    new AppUtilities().hideKeyBoard(getActivity());
                }
            }
        });
        tokenQueueViewInterface = this;
        currentClickListner = this;
        recentClickListner = this;
        storeListener = this;
        mHandler = new QueueHandler();
        rv_recent_activity.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recent_activity.setLayoutManager(horizontalLayoutManager);
        rv_recent_activity.setItemAnimator(new DefaultItemAnimator());

        //
        rv_current_activity.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_current_activity.setLayoutManager(horizontalLayoutManager2);
        rv_current_activity.setItemAnimator(new DefaultItemAnimator());
        rv_current_activity.addItemDecoration(new CirclePagerIndicatorDecoration());

        //
        rv_merchant_around_you.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_merchant_around_you.setLayoutManager(horizontalLayoutManager1);
        // rv_merchant_around_you.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            callCurrentAndRecentQueue();
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }

        if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
            lat = LaunchActivity.getLaunchActivity().getDefaultLatitude();
            log = LaunchActivity.getLaunchActivity().getDefaultLongitude();
            city = LaunchActivity.getLaunchActivity().getDefaultCity();
            AppUtilities.setAutoCompleteText(autoCompleteTextView, city);
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
        } else {
            lat = LaunchActivity.getLaunchActivity().latitute;
            log = LaunchActivity.getLaunchActivity().longitute;
            city = LaunchActivity.getLaunchActivity().cityName;
            AppUtilities.setAutoCompleteText(autoCompleteTextView, city);
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(log));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity() && isAdded()) {
            /* Update the current Queue & history queue so that user get the latest queue status & get reflected in DB. */
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                callCurrentAndRecentQueue();
            }
        }
        try {
            tv_deviceId.setText(UserUtils.getDeviceId() + "\n" + NoQueueBaseActivity.getFCMToken());
           // tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callCurrentAndRecentQueue() {
        if (UserUtils.isLogin()) { // Call secure API if user is loggedIn else normal API
            //Call the current queue
            QueueApiModel queueApiModel = new QueueApiModel();
            queueApiModel.setTokenAndQueuePresenter(this);
            //Log.e("DEVICE ID NULL", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            queueApiModel.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());

            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
            queueApiModel.allHistoricalJoinedQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
        } else {
            //Call the current queue
            QueueModel queueModel = new QueueModel();
            queueModel.setTokenAndQueuePresenter(this);
            queueModel.getAllJoinedQueue(UserUtils.getDeviceId());
            //Log.e("DEVICE ID NULL Un", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken());
            queueModel.getAllHistoricalJoinedQueue(UserUtils.getDeviceId(), deviceToken);
        }
        if (isProgressFirstTime) {
            pb_current.setVisibility(View.VISIBLE);
            pb_recent.setVisibility(View.VISIBLE);
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
            }
            new NearMeModel(this).nearMeStore(UserUtils.getDeviceId(), storeInfoParam);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {
        nearMeData = new ArrayList<>();
        nearMeData.addAll(bizStoreElasticList.getBizStoreElastics());
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(lat, log)));
        StoreInfoAdapter storeInfoAdapter = new StoreInfoAdapter(nearMeData, getActivity(), storeListener, lat, log);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe", bizStoreElasticList.toString());
        scrollId = bizStoreElasticList.getScrollId();
        pb_near.setVisibility(View.GONE);
        if (null != recentActivityAdapter) {
            recentActivityAdapter.updateLatLong(lat, log);
            recentActivityAdapter.notifyDataSetChanged();
        }
        tv_near_view_all.setVisibility(nearMeData.size() == 0 ? View.GONE : View.VISIBLE);
        isProgressFirstTime = false;
        if (NoQueueBaseActivity.getShowHelper() && isAdded()) {
            presentShowcaseSequence();
            NoQueueBaseActivity.setShowHelper(false);
        }
    }

    @Override
    public void nearMeError() {
        pb_near.setVisibility(View.GONE);
    }

    @Override
    public void onStoreItemClick(BizStoreElastic item, View view, int pos) {
        switch (item.getBusinessType()) {
            case DO:
            case BK:
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
                bundle.putString("storeName", item.getDisplayName());
                bundle.putString("storeAddress", item.getStoreAddress());
                in.putExtras(bundle);
                startActivity(in);
            }
        }
    }

    @Override
    public void recentItemClick(JsonTokenAndQueue item, View view, int pos) {
        if (null != item) {
            if (item.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.Q) {
                Intent in = new Intent(getActivity(), JoinActivity.class);
                in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, item.getCodeQR());
                in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, true);
                in.putExtra("isCategoryData", false);
                startActivity(in);
            } else {
                BizStoreElastic bizStoreElastic = new BizStoreElastic();
                bizStoreElastic.setRating(item.getRatingCount());
                bizStoreElastic.setDisplayImage(item.getDisplayImage());
                bizStoreElastic.setBusinessName(item.getBusinessName());
                bizStoreElastic.setCodeQR(item.getCodeQR());
                bizStoreElastic.setBusinessType(item.getBusinessType());
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BizStoreElastic", bizStoreElastic);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
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

    private void recentClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        Bundle bundle = new Bundle();
        // bundle.putSerializable("data", data1);
        intent.putExtras(bundle);
        // startActivity(intent);
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
        pb_recent.setVisibility(View.GONE);
    }

    @Override
    public void historyQueueError() {
        Log.d(TAG, "History queue Error");
        LaunchActivity.getLaunchActivity().dismissProgress();
        passMsgToHandler(false);
        pb_recent.setVisibility(View.GONE);
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
        pb_recent.setVisibility(View.GONE);
        pb_near.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        LaunchActivity.getLaunchActivity().dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        new ErrorResponseHandler().processError(getActivity(), eej);
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
        Collections.sort(historyQueueList, new Comparator<JsonTokenAndQueue>() {
            @Override
            public int compare(JsonTokenAndQueue o1, JsonTokenAndQueue o2) {
                try {
                    return formatRFC822.parse(o2.getServiceEndTime()).compareTo(formatRFC822.parse(o1.getServiceEndTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        if (null != getActivity() && isAdded()) {
            recentActivityAdapter = new RecentActivityAdapter(historyQueueList, getActivity(), recentClickListner, lat, log);
            rv_recent_activity.setAdapter(recentActivityAdapter);
            recentActivityAdapter.notifyDataSetChanged();
        }
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
            case R.id.tv_recent_view_all:
                recentClick();
                break;
            case R.id.tv_near_view_all:
                nearClick();
                break;

        }
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
                            .setTarget(autoCompleteTextView)
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
