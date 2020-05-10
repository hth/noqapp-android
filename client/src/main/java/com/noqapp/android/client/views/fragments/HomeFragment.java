package com.noqapp.android.client.views.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.AdvertisementApiCalls;
import com.noqapp.android.client.model.FeedApiCall;
import com.noqapp.android.client.model.QueueApiAuthenticCall;
import com.noqapp.android.client.model.QueueApiUnAuthenticCall;
import com.noqapp.android.client.model.SearchBusinessStoreApiCalls;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.FeedPresenter;
import com.noqapp.android.client.presenter.NoQueueDBPresenter;
import com.noqapp.android.client.presenter.SearchBusinessStorePresenter;
import com.noqapp.android.client.presenter.TokenAndQueuePresenter;
import com.noqapp.android.client.presenter.beans.BizStoreElastic;
import com.noqapp.android.client.presenter.beans.BizStoreElasticList;
import com.noqapp.android.client.presenter.beans.JsonFeed;
import com.noqapp.android.client.presenter.beans.JsonFeedList;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueueList;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.presenter.beans.body.Location;
import com.noqapp.android.client.presenter.beans.body.SearchStoreQuery;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.RateTheAppManager;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.SortPlaces;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.activities.AfterJoinActivity;
import com.noqapp.android.client.views.activities.AllEventsActivity;
import com.noqapp.android.client.views.activities.AllFeedsActivity;
import com.noqapp.android.client.views.activities.AppointmentDetailActivity;
import com.noqapp.android.client.views.activities.BeforeJoinActivity;
import com.noqapp.android.client.views.activities.BeforeJoinOrderQueueActivity;
import com.noqapp.android.client.views.activities.BlinkerActivity;
import com.noqapp.android.client.views.activities.CategoryInfoActivity;
import com.noqapp.android.client.views.activities.EventsDetailActivity;
import com.noqapp.android.client.views.activities.FeedActivity;
import com.noqapp.android.client.views.activities.ImageViewerActivity;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.activities.OrderConfirmActivity;
import com.noqapp.android.client.views.activities.SearchActivity;
import com.noqapp.android.client.views.activities.StoreDetailActivity;
import com.noqapp.android.client.views.activities.StoreWithMenuActivity;
import com.noqapp.android.client.views.activities.ViewAllListActivity;
import com.noqapp.android.client.views.adapters.CurrentActivityAdapter;
import com.noqapp.android.client.views.adapters.EventsAdapter;
import com.noqapp.android.client.views.adapters.FeedAdapter;
import com.noqapp.android.client.views.adapters.StoreInfoAdapter;
import com.noqapp.android.client.views.customviews.CirclePagerIndicatorDecoration;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonAdvertisement;
import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.beans.JsonSchedule;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.BusinessSupportEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueOrderTypeEnum;
import com.noqapp.android.common.presenter.AdvertisementPresenter;
import com.noqapp.android.common.utils.CommonHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeFragment extends ScannerFragment implements View.OnClickListener,
        FeedAdapter.OnItemClickListener, EventsAdapter.OnItemClickListener,
        CurrentActivityAdapter.OnItemClickListener, SearchBusinessStorePresenter,
        StoreInfoAdapter.OnItemClickListener, TokenAndQueuePresenter, TokenQueueViewInterface,
        FeedPresenter, AdvertisementPresenter {

    private final String TAG = HomeFragment.class.getSimpleName();
    private RelativeLayout rl_scan;
    private RecyclerView rv_health_care;
    private RecyclerView rv_current_activity;
    private RecyclerView rv_feed;
    private TextView tv_current_title;
    private TextView tv_deviceId;
    private RecyclerView rv_merchant_around_you, rv_events;
    private TextView tv_health_care_view_all;
    private TextView tv_near_view_all;
    private TextView tv_feed_view_all;
    private TextView tv_events_view_all;
    private ProgressBar pb_current;
    private ProgressBar pb_health_care;
    private ProgressBar pb_near;
    private ProgressBar pb_feed;
    private ProgressBar pb_events;
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
    private double lat, lng;
    private String city = "";
    private boolean isFirstTimeUpdate = true;
    private static final String SHOWCASE_ID = "screen helper";
    private boolean isProgressFirstTime = true;
    private boolean isRateUsFirstTime = true;
    private static final int MSG_CURRENT_QUEUE = 0;
    private static final int MSG_HISTORY_QUEUE = 1;
    private static TokenQueueViewInterface tokenQueueViewInterface;
    private static QueueHandler mHandler;
    private List<JsonFeed> jsonFeeds = new ArrayList<>();
    private List<JsonAdvertisement> jsonAdvertisements = new ArrayList<>();
    private List<JsonSchedule> jsonSchedules = new ArrayList<>();


    public HomeFragment() {
        // default constructor required
    }

    public void updateUIWithNewLocation(final double latitude, final double longitude, final String cityName) {
        if (latitude != 0.0 && latitude != Constants.DEFAULT_LATITUDE && Double.compare(lat, latitude) != 0 && !cityName.equals(city)) {
            if (isFirstTimeUpdate) {
                getNearMeInfo(cityName, String.valueOf(latitude), String.valueOf(longitude));
                lat = latitude;
                lng = longitude;
                city = cityName;
                isFirstTimeUpdate = false;
                LaunchActivity.getLaunchActivity().tv_location.setText(city);
            } else {
                // LaunchActivity.getLaunchActivity().tv_location.setText(city);
                cv_update_location.setVisibility(View.VISIBLE);
                tv_update.setOnClickListener((View v) -> {
                    getNearMeInfo(cityName, String.valueOf(latitude), String.valueOf(longitude));
                    lat = latitude;
                    lng = longitude;
                    city = cityName;
                    LaunchActivity.getLaunchActivity().tv_location.setText(cityName);
                    cv_update_location.setVisibility(View.GONE);
                });
                tv_no_thanks.setOnClickListener((View v) -> {
                    cv_update_location.setVisibility(View.GONE);
                });
            }
        } else {
            cv_update_location.setVisibility(View.GONE);
        }
        Log.d("Loc Data: ", "latitude: " + lat + " longitude: " + lng + " city: " + city);
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
        rv_events = view.findViewById(R.id.rv_events);

        tv_health_care_view_all = view.findViewById(R.id.tv_health_care_view_all);
        tv_near_view_all = view.findViewById(R.id.tv_near_view_all);
        tv_feed_view_all = view.findViewById(R.id.tv_feed_view_all);
        tv_events_view_all = view.findViewById(R.id.tv_events_view_all);
        pb_current = view.findViewById(R.id.pb_current);
        pb_health_care = view.findViewById(R.id.pb_health_care);
        pb_near = view.findViewById(R.id.pb_near);
        pb_feed = view.findViewById(R.id.pb_feed);
        pb_events = view.findViewById(R.id.pb_events);
        cv_update_location = view.findViewById(R.id.cv_update_location);
        rl_current_activity = view.findViewById(R.id.rl_current_activity);
        tv_no_thanks = view.findViewById(R.id.tv_no_thanks);
        tv_update = view.findViewById(R.id.tv_update);

        rl_scan.setOnClickListener(this);
        tv_health_care_view_all.setOnClickListener(this);
        tv_near_view_all.setOnClickListener(this);
        tv_feed_view_all.setOnClickListener(this);
        tv_events_view_all.setOnClickListener(this);

        rv_feed = view.findViewById(R.id.rv_feed);
        rv_feed.setHasFixedSize(true);
        rv_feed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_feed.setItemAnimator(new DefaultItemAnimator());

        rv_events.setHasFixedSize(true);
        rv_events.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_events.setItemAnimator(new DefaultItemAnimator());
        if (!LaunchActivity.getLaunchActivity().isCountryIndia()) {
            if (AppUtils.isRelease()) {
                LinearLayout rl_health_care = view.findViewById(R.id.rl_health_care);
                rl_health_care.setVisibility(View.GONE);
            }

            TextView tv_merchant_title = view.findViewById(R.id.tv_merchant_title);
            tv_merchant_title.setText("Merchant Around You");

            LinearLayout rl_feed = view.findViewById(R.id.rl_feed);
            rl_feed.setVisibility(View.GONE);
        }
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
        rv_merchant_around_you.setItemAnimator(new DefaultItemAnimator());

        if (LaunchActivity.getLaunchActivity().isOnline()) {
            callCurrentAndHistoryQueue();
            FeedApiCall feedApiCall = new FeedApiCall(this);
            feedApiCall.activeFeed(UserUtils.getDeviceId());
            pb_feed.setVisibility(View.VISIBLE);

            AdvertisementApiCalls advertisementApiCalls = new AdvertisementApiCalls();
            advertisementApiCalls.setAdvertisementPresenter(this);
            Location location = new Location();
            location.setCityName(Constants.DEFAULT_CITY);
            location.setLatitude(String.valueOf(Constants.DEFAULT_LATITUDE));
            location.setLongitude(String.valueOf(Constants.DEFAULT_LONGITUDE));
            advertisementApiCalls.getAdvertisementsByLocation(UserUtils.getDeviceId(), location);
            pb_events.setVisibility(View.VISIBLE);

        } else {
            if (isAdded()) {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }

        if (TextUtils.isEmpty(LaunchActivity.getLaunchActivity().cityName)) {
            lat = Constants.DEFAULT_LATITUDE;
            lng = Constants.DEFAULT_LONGITUDE;
            city = Constants.DEFAULT_CITY;
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(lng));
        } else {
            lat = LaunchActivity.getLaunchActivity().latitude;
            lng = LaunchActivity.getLaunchActivity().longitude;
            city = LaunchActivity.getLaunchActivity().cityName;
            getNearMeInfo(city, String.valueOf(lat), String.valueOf(lng));
        }
//        Log.e("Did","Auth "+UserUtils.getAuth()+" \n Email ID "+UserUtils.getEmail()+"\n DID "+UserUtils.getDeviceId());
//        Log.e("quserid",LaunchActivity.getUserProfile().getQueueUserId());
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
            tv_deviceId.setText(UserUtils.getDeviceId() + "\n" + NoQueueBaseActivity.getTokenFCM());
            // tv_deviceId.setVisibility(BuildConfig.BUILD_TYPE.equals("debug") ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, TAG, "On Resume " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void callCurrentAndHistoryQueue() {
        if (UserUtils.isLogin()) { // Call secure API if user is loggedIn else normal API
            //Call the current queue
            QueueApiAuthenticCall queueApiModel = new QueueApiAuthenticCall();
            queueApiModel.setTokenAndQueuePresenter(this);
            //Log.e("DEVICE ID NULL", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            queueApiModel.getAllJoinedQueues(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth());

            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken(), Constants.appVersion(), CommonHelper.getLocation(lat, lng));
            queueApiModel.allHistoricalJoinedQueue(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
        } else {
            //Call the current queue
            QueueApiUnAuthenticCall queueModel = new QueueApiUnAuthenticCall();
            queueModel.setTokenAndQueuePresenter(this);
            queueModel.getAllJoinedQueue(UserUtils.getDeviceId());
            //Log.e("DEVICE ID NULL Un", "DID: " + UserUtils.getDeviceId() + " Email: " + UserUtils.getEmail() + " Auth: " + UserUtils.getAuth());
            //Call the history queue
            DeviceToken deviceToken = new DeviceToken(FirebaseInstanceId.getInstance().getToken(), Constants.appVersion(), CommonHelper.getLocation(lat, lng));
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
            b.putString(IBConstant.KEY_CODE_QR, codeQR);
            b.putBoolean(IBConstant.KEY_FROM_LIST, fromList);
            in.putExtra("bundle", b);
            getActivity().startActivity(in);
        } else {
            Intent in = new Intent(getActivity(), BeforeJoinActivity.class);
            in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
            in.putExtra(IBConstant.KEY_FROM_LIST, false);
            in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
            startActivity(in);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void getNearMeInfo(String city, String lat, String longitute) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            SearchStoreQuery searchStoreQuery = new SearchStoreQuery();
            searchStoreQuery.setCityName(city);
            searchStoreQuery.setLatitude(lat);
            searchStoreQuery.setLongitude(longitute);
            searchStoreQuery.setFilters("xyz");
            searchStoreQuery.setScrollId("");
            if (isProgressFirstTime) {
                pb_near.setVisibility(View.VISIBLE);
                pb_health_care.setVisibility(View.VISIBLE);
            }
            SearchBusinessStoreApiCalls searchBusinessStoreApiCalls = new SearchBusinessStoreApiCalls(this);
            searchBusinessStoreApiCalls.otherMerchant(UserUtils.getDeviceId(), searchStoreQuery);
            searchBusinessStoreApiCalls.healthCare(UserUtils.getDeviceId(), searchStoreQuery);
        } else {
            if (isAdded()) {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }
    }

    @Override
    public void nearMeResponse(BizStoreElasticList bizStoreElasticList) {
        nearMeData = new ArrayList<>();
        for (int i = 0; i < bizStoreElasticList.getBizStoreElastics().size(); i++) {
            if (bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType() != BusinessTypeEnum.DO) {
                nearMeData.add(bizStoreElasticList.getBizStoreElastics().get(i));
            }
        }
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeData, new SortPlaces(new LatLng(lat, lng)));
        StoreInfoAdapter storeInfoAdapter = new StoreInfoAdapter(nearMeData, getActivity(), storeListener, lat, lng);
        rv_merchant_around_you.setAdapter(storeInfoAdapter);
        Log.v("NearMe", bizStoreElasticList.toString());
        scrollId = bizStoreElasticList.getScrollId();
        pb_near.setVisibility(View.GONE);
        tv_near_view_all.setVisibility(nearMeData.size() == 0 ? View.GONE : View.VISIBLE);
        isProgressFirstTime = false;
        if (isAdded()) {
            if (NoQueueBaseActivity.getShowHelper()) {
                if (AppUtils.isRelease()) {
                    presentShowcaseSequence();
                }
                NoQueueBaseActivity.setShowHelper(false);
            } else {
                if (isRateUsFirstTime && null != getActivity()) {
                    new RateTheAppManager().appLaunched(getActivity());
                    isRateUsFirstTime = false;
                }
            }
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
            if (BusinessTypeEnum.DO == bizStoreElasticList.getBizStoreElastics().get(i).getBusinessType()) {
                nearMeHospital.add(bizStoreElasticList.getBizStoreElastics().get(i));
            }
        }
        //sort the list, give the Comparator the current location
        Collections.sort(nearMeHospital, new SortPlaces(new LatLng(lat, lng)));
        StoreInfoAdapter storeInfoAdapter = new StoreInfoAdapter(nearMeHospital, getActivity(), storeListener, lat, lng);
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
    public void onStoreItemClick(BizStoreElastic bizStoreElastic) {
        Intent in;
        Bundle b = new Bundle();
        switch (bizStoreElastic.getBusinessType()) {
            //Level up
            case DO:
            case BK:
            case HS:
                // open hospital/Bank profile
                b.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                b.putBoolean(IBConstant.KEY_FROM_LIST, fromList);
                b.putBoolean(IBConstant.KEY_CALL_CATEGORY, true);
                b.putBoolean(IBConstant.KEY_IS_CATEGORY, false);
                b.putSerializable("BizStoreElastic", bizStoreElastic);
                in = new Intent(getActivity(), CategoryInfoActivity.class);
                in.putExtra("bundle", b);
                startActivity(in);
                break;
            case PH:
                // open order screen
                in = new Intent(getActivity(), StoreDetailActivity.class);
                b.putSerializable("BizStoreElastic", bizStoreElastic);
                in.putExtras(b);
                startActivity(in);
                break;
            case RSQ:
            case GSQ:
            case BAQ:
            case CFQ:
            case FTQ:
            case STQ:
                //@TODO Modification done due to corona crisis, Re-check all the functionality
                //proper testing required
                if (BusinessSupportEnum.OQ == bizStoreElastic.getBusinessType().getBusinessSupport()) {
                    in = new Intent(getActivity(), BeforeJoinOrderQueueActivity.class);
                    b.putString(IBConstant.KEY_CODE_QR, bizStoreElastic.getCodeQR());
                    b.putBoolean(IBConstant.KEY_FROM_LIST, false);
                    b.putBoolean(IBConstant.KEY_IS_CATEGORY, false);
                    b.putSerializable("BizStoreElastic", bizStoreElastic);
                    in.putExtras(b);
                    startActivity(in);
                } else {
                    Log.d(TAG, "Reached un-supported condition");
                    Crashlytics.log(Log.ERROR, TAG, "Reached un-supported condition " + bizStoreElastic.getBusinessType());
                }
                break;
            default:
                // open order screen
                in = new Intent(getActivity(), StoreWithMenuActivity.class);
                b.putSerializable("BizStoreElastic", bizStoreElastic);
                in.putExtras(b);
                startActivity(in);
        }
    }

    @Override
    public void currentQorOrderItemClick(JsonTokenAndQueue item) {
        if (null != item) {
            if (item.getBusinessType().getQueueOrderType() == QueueOrderTypeEnum.Q) {
                Intent in = new Intent(getActivity(), AfterJoinActivity.class);
                in.putExtra(IBConstant.KEY_CODE_QR, item.getCodeQR());
                in.putExtra("qUserId", item.getQueueUserId());
                in.putExtra(IBConstant.KEY_FROM_LIST, true);
                in.putExtra(IBConstant.KEY_JSON_TOKEN_QUEUE, item);
                startActivity(in);
            } else {
                Intent in = new Intent(getActivity(), OrderConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IBConstant.KEY_FROM_LIST, true);
                bundle.putString(IBConstant.KEY_CODE_QR, item.getCodeQR());
                bundle.putInt("token", item.getToken());
                bundle.putInt("currentServing", item.getServingNumber());
                bundle.putString("GeoHash", item.getGeoHash());
                bundle.putString(IBConstant.KEY_STORE_NAME, item.getDisplayName());
                bundle.putString(IBConstant.KEY_STORE_ADDRESS, item.getStoreAddress());
                bundle.putString(AppUtils.CURRENCY_SYMBOL, AppUtils.getCurrencySymbol(item.getCountryShortName()));
                in.putExtras(bundle);
                startActivity(in);
            }
        }
    }

    @Override
    public void currentAppointmentClick(JsonSchedule jsonSchedule) {
        Intent intent = new Intent(getActivity(), AppointmentDetailActivity.class);
        intent.putExtra(IBConstant.KEY_DATA_OBJECT, jsonSchedule);
        intent.putExtra(IBConstant.KEY_FROM_LIST, true);
        startActivity(intent);
    }


    @Override
    public void onFeedItemClick(JsonFeed item) {
        Intent in = new Intent(getActivity(), FeedActivity.class);
        in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
        startActivity(in);
    }

    private void nearClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeData);
        intent.putExtra("scrollId", scrollId);
        intent.putExtra("lat", "" + lat);
        intent.putExtra("lng", "" + lng);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    private void allFeedsClick() {
        Intent intent = new Intent(getActivity(), AllFeedsActivity.class);
        intent.putExtra("list", (Serializable) jsonFeeds);
        startActivity(intent);
    }

    private void allEventsClick() {
        Intent intent = new Intent(getActivity(), AllEventsActivity.class);
        intent.putExtra("list", (Serializable) jsonAdvertisements);
        startActivity(intent);
    }

    private void healthCareClick() {
        Intent intent = new Intent(getActivity(), ViewAllListActivity.class);
        intent.putExtra("list", (Serializable) nearMeHospital);
        intent.putExtra("scrollId", scrollId);
        intent.putExtra("lat", "" + lat);
        intent.putExtra("lng", "" + lng);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    @Override
    public void currentQueueResponse(JsonTokenAndQueueList jsonTokenAndQueueList) {
        NoQueueDBPresenter dbPresenter = new NoQueueDBPresenter();
        dbPresenter.tokenQueueViewInterface = this;
        dbPresenter.saveCurrentTokenQueue(jsonTokenAndQueueList.getTokenAndQueues());
        jsonSchedules = jsonTokenAndQueueList.getJsonScheduleList().getJsonSchedules();
        Collections.sort(jsonSchedules, (o1, o2) -> {
            try {
                String two = o2.getScheduleDate() + " " + AppUtils.getTimeFourDigitWithColon(o2.getStartTime());
                String one = o1.getScheduleDate() + " " + AppUtils.getTimeFourDigitWithColon(o1.getStartTime());
                return CommonHelper.SDF_YYYY_MM_DD_KK_MM.parse(one).compareTo(CommonHelper.SDF_YYYY_MM_DD_KK_MM.parse(two));
            } catch (Exception e) {
                Crashlytics.log(Log.ERROR, TAG, "Failed on currentQueueResponse " + e.getLocalizedMessage());
                e.printStackTrace();
                return 0;
            }
        });


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
        dismissProgress();
        passMsgToHandler(false);
    }

    @Override
    public void currentQueueError() {
        Log.d(TAG, "Current queue Error");
        dismissProgress();
        passMsgToHandler(true);
        pb_current.setVisibility(View.GONE);
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing(getActivity());
        pb_current.setVisibility(View.GONE);
        pb_health_care.setVisibility(View.GONE);
        pb_near.setVisibility(View.GONE);
        pb_feed.setVisibility(View.GONE);
        pb_events.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        if (isAdded()) {
            new ErrorResponseHandler().processFailureResponseCode(getActivity(), errorCode);
        }
        pb_feed.setVisibility(View.GONE);
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (isAdded()) {
                new ErrorResponseHandler().processError(getActivity(), eej);
            }
        }
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
        dismissProgress();
        Log.d(TAG, "Current Queue Count : " + currentQueueList.size());
        if (null != getActivity() && isAdded()) {
            Collections.sort(currentQueueList, (o1, o2) -> {
                try {
                    return CommonHelper.SDF_ISO8601_FMT.parse(o2.getCreateDate()).compareTo(CommonHelper.SDF_ISO8601_FMT.parse(o1.getCreateDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            List<Object> temp = new ArrayList<>();
            temp.addAll(currentQueueList);
            temp.addAll(jsonSchedules);

            CurrentActivityAdapter currentActivityAdapter = new CurrentActivityAdapter(temp, getActivity(), currentClickListner);
            rv_current_activity.setAdapter(currentActivityAdapter);
            tv_current_title.setText(getString(R.string.active_queue) + " (" + currentQueueList.size() + ")");
            currentActivityAdapter.notifyDataSetChanged();

            if (currentQueueList.size() > 0) {
                for (JsonTokenAndQueue jtq : currentQueueList) {
                    NoQueueMessagingService.subscribeTopics(jtq.getTopic());
                }
            }
        }
    }

    @Override
    public void tokenHistoryQueueList(List<JsonTokenAndQueue> historyQueueList) {
        dismissProgress();
        Log.d(TAG, "History Queue Count: " + historyQueueList.size());
    }

    public void updateListFromNotification(JsonTokenAndQueue jq, List<JsonTextToSpeech> jsonTextToSpeeches, String msgId) {
        boolean isUpdated = TokenAndQueueDB.updateCurrentListQueueObject(jq.getCodeQR(), "" + jq.getServingNumber(), "" + jq.getToken());
        boolean isUserTurn = jq.afterHowLong() == 0;
        if (isUserTurn && isUpdated) {
            boolean showBuzzer;
            ReviewData reviewData = ReviewDB.getValue(jq.getCodeQR(), "" + jq.getToken());
            if (null != reviewData) {
                if (reviewData.getIsBuzzerShow().equals("1")) {
                    showBuzzer = false;
                } else {
                    showBuzzer = true;
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
                    blinker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().getApplicationContext().startActivity(blinker);
                    if (LaunchActivity.isMsgAnnouncementEnable()) {
                        LaunchActivity.getLaunchActivity().makeAnnouncement(jsonTextToSpeeches, msgId);
                    }
                } else {
                    switch (jq.getPurchaseOrderState()) {
                        case RP:
                        case RD:
                            ContentValues cv = new ContentValues();
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                            ReviewDB.updateReviewRecord(jq.getCodeQR(), String.valueOf(jq.getToken()), cv);
                            Intent blinker = new Intent(getActivity(), BlinkerActivity.class);
                            blinker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().getApplicationContext().startActivity(blinker);
                            if (LaunchActivity.isMsgAnnouncementEnable()) {
                                LaunchActivity.getLaunchActivity().makeAnnouncement(jsonTextToSpeeches, msgId);
                            }
                            break;
                        case CO:
                            //  ShowAlertInformation.showInfoDisplayDialog(getActivity(), title , body);
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
        in_search.putExtra("lng", "" + lng);
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
            case R.id.tv_feed_view_all:
                allFeedsClick();
                break;
            case R.id.tv_events_view_all:
                allEventsClick();
                break;
//            case R.id.iv_event:
//                Intent in = new Intent(getActivity(), ImageViewerActivity.class);
//                startActivity(in);
//                break;

        }
    }

    @Override
    public void allActiveFeedResponse(JsonFeedList jsonFeedList) {
        if (null != jsonFeedList && jsonFeedList.getJsonFeeds().size() > 0) {
            jsonFeeds = jsonFeedList.getJsonFeeds();
            FeedAdapter feedAdapter = new FeedAdapter(jsonFeedList.getJsonFeeds(), getActivity(), this);
            rv_feed.setAdapter(feedAdapter);
            tv_feed_view_all.setVisibility(jsonFeedList.getJsonFeeds().size() == 0 ? View.GONE : View.VISIBLE);
        }
        pb_feed.setVisibility(View.GONE);
    }


    @Override
    public void advertisementResponse(JsonAdvertisementList jsonAdvertisementList) {
        if (null != jsonAdvertisementList && jsonAdvertisementList.getJsonAdvertisements().size() > 0) {
            jsonAdvertisements = jsonAdvertisementList.getJsonAdvertisements();
            EventsAdapter eventsAdapter = new EventsAdapter(jsonAdvertisements, this);
            rv_events.setAdapter(eventsAdapter);
            tv_events_view_all.setVisibility(jsonAdvertisementList.getJsonAdvertisements().size() == 0 ? View.GONE : View.VISIBLE);
        }
        pb_events.setVisibility(View.GONE);
    }

    @Override
    public void onEventItemClick(JsonAdvertisement item) {
        switch (item.getAdvertisementViewerType()) {
            case JBA: {
                Intent in = new Intent(getActivity(), ImageViewerActivity.class);
                in.putExtra(IBConstant.KEY_URL, item.createAdvertisementImageURL().isEmpty() ? null : AppUtils.getImageUrls(BuildConfig.ADVERTISEMENT_BUCKET, item.createAdvertisementImageURL()));
                startActivity(in);
                break;
            }
            default: {
                Intent in = new Intent(getActivity(), EventsDetailActivity.class);
                in.putExtra(IBConstant.KEY_DATA_OBJECT, item);
                startActivity(in);
            }
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
                            .setTarget(LaunchActivity.getLaunchActivity().tv_location)
                            .setDismissText("GOT IT")
                            .setContentText("Search your preferred location")
                            .withRectangleShape(true)
                            .build()
            );
//            sequence.addSequenceItem(
//                    new MaterialShowcaseView.Builder(getActivity())
//                            .setTarget(rl_scan)
//                            .setDismissText("GOT IT")
//                            .setContentText("Click here to scan store QRCode to join their queue or place order")
//                            .withRectangleShape(true)
//                            .build()
//            );
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
            Crashlytics.log(Log.ERROR, TAG, "Failed to present showcase sequence " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


}
