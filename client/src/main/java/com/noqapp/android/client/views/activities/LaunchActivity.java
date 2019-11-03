package com.noqapp.android.client.views.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceApiCall;
import com.noqapp.android.client.model.database.DatabaseHelper;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.fcm.JsonClientTokenAndQueueData;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.FabricEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter;
import com.noqapp.android.client.views.fragments.ChangeLocationFragment;
import com.noqapp.android.client.views.fragments.HomeFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicAppointmentData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.pojos.MenuDrawer;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PermissionUtils;
import com.noqapp.android.common.views.activities.AppUpdateActivity;

import com.google.android.gms.maps.MapsInitializer;

import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class LaunchActivity
        extends NoQueueBaseActivity
        implements OnClickListener, DeviceRegisterPresenter, AppBlacklistPresenter, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();
    public static DatabaseHelper dbHandler;
    public static Locale locale;
    public static SharedPreferences languagePref;
    public static String language;
    private static LaunchActivity launchActivity;
    public TextView tv_location;
    public NetworkUtil networkUtil;
    public ActivityCommunicator activityCommunicator;
    public double latitude = 0;
    public double longitude = 0;
    public String cityName = "";
    protected ExpandableListView expandable_drawer_listView;
    private TextView tv_badge;
    private long lastPress;
    private Toast backPressToast;
    private FcmNotificationReceiver fcmNotificationReceiver;
    private ImageView iv_profile;
    private TextView tv_name, tv_email, tv_version;
    private HomeFragment homeFragment;
    private DrawerLayout drawer;
    private List<MenuDrawer> menuDrawerItems = new ArrayList<>();
    public static String COUNTRY_CODE = Constants.DEFAULT_COUNTRY_CODE;
    public static String DISTANCE_UNIT = "km";
    public static boolean isLockMode = false;

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        JodaTimeAndroid.init(this);
        //https://stackoverflow.com/questions/26178212/first-launch-of-activity-with-google-maps-is-very-slow
        MapsInitializer.initialize(this);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        setContentView(R.layout.activity_launch);
        isLockMode = NoQueueBaseActivity.getKioskModeInfo().isKioskModeEnable();
        tv_badge = findViewById(R.id.tv_badge);
        tv_location = findViewById(R.id.tv_location);
        ImageView iv_search = findViewById(R.id.iv_search);
        ImageView iv_notification = findViewById(R.id.iv_notification);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        launchActivity = this;
        COUNTRY_CODE = getCountryCode();
        Log.i(TAG, "Country Code: " + COUNTRY_CODE);

        if (!isCountryIndia()) {
            Constants.DEFAULT_LATITUDE = 37.7749;
            Constants.DEFAULT_LONGITUDE = 122.4194;
            Constants.DEFAULT_CITY = "San Francisco";
            Constants.DEFAULT_COUNTRY_CODE = "US";
            DISTANCE_UNIT = "mi";
        }

        //NoQueueBaseActivity.saveMailAuth("","");
        if (null != getIntent().getExtras()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("fcmToken"))) {
                NoQueueBaseActivity.setFCMToken(getIntent().getStringExtra("fcmToken"));
            }

            if (!TextUtils.isEmpty(getIntent().getStringExtra("deviceId"))) {
                NoQueueBaseActivity.setDeviceID(getIntent().getStringExtra("deviceId"));
            }
        }
        Log.v("Device id check", getDeviceID());
        setReviewShown(false);//Reset the flag when app is killed
        networkUtil = new NetworkUtil(this);
        fcmNotificationReceiver = new FcmNotificationReceiver();
        fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
        //Language setup
        languagePref = PreferenceManager.getDefaultSharedPreferences(this);
        languagePref.registerOnSharedPreferenceChangeListener(this);
        language = languagePref.getString("pref_language", "");

        if (!language.equals("")) {
            if (language.equals("hi")) {
                language = "hi";
                locale = new Locale("hi");
            } else {
                locale = Locale.ENGLISH;
                language = "en_US";
            }
        } else {
            locale = Locale.ENGLISH;
            language = "en_US";
        }
        callLocationManager();
        ((MyApplication) getApplication()).setLocale(this);
        iv_search.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        fl_notification.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.VISIBLE);
        //initProgress();
        homeFragment = new HomeFragment();
        replaceFragmentWithoutBackStack(R.id.frame_layout, homeFragment);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        expandable_drawer_listView = findViewById(R.id.expandable_drawer_listView);
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.nav_header_main, expandable_drawer_listView, false);
        expandable_drawer_listView.addHeaderView(headerView);
        iv_profile = headerView.findViewById(R.id.iv_profile);
        tv_name = headerView.findViewById(R.id.tv_name);
        tv_version = findViewById(R.id.tv_version);
        tv_email = headerView.findViewById(R.id.tv_email);
        tv_email.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        tv_version.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_version)).setText(
                AppUtils.isRelease()
                        ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                        : getString(R.string.version_no, "Not for release"));
        setUpExpandableList(UserUtils.isLogin());


        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceApiCall deviceModel = new DeviceApiCall();
            deviceModel.setAppBlacklistPresenter(this);
            deviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }
        if (null != getIntent().getExtras()) {
            try {
                latitude = getIntent().getDoubleExtra("latitude", Constants.DEFAULT_LATITUDE);
                longitude = getIntent().getDoubleExtra("longitude", Constants.DEFAULT_LONGITUDE);
                getAddress(latitude, longitude);
                //updateLocationUI();
                tv_location.setText(cityName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setKioskMode();
    }

    private void setKioskMode() {
        if (isLockMode) {
            if (NoQueueBaseActivity.getKioskModeInfo().isLevelUp()) {
                if (NoQueueBaseActivity.getKioskModeInfo().isFeedbackScreen()) {
                    Intent in = new Intent(LaunchActivity.this, SurveyKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                } else {
                    clearPreferences();
                    Intent in = new Intent(LaunchActivity.this, CategoryInfoKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                }
            } else {
                if (NoQueueBaseActivity.getKioskModeInfo().isFeedbackScreen()) {
                    Intent in = new Intent(LaunchActivity.this, SurveyKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                } else {
                    clearPreferences();
                    Intent in = new Intent(LaunchActivity.this, StoreWithMenuKioskActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, NoQueueBaseActivity.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                }
            }
        }
    }

    public void updateLocationUI() {
        if (null != homeFragment) {
            homeFragment.updateUIWithNewLocation(latitude, longitude, cityName);
            //tv_location.setText(cityName);
        }
    }

    public void updateLocationInfo(double lat, double lng, String city) {
        replaceFragmentWithoutBackStack(R.id.frame_layout, homeFragment);
        getSupportActionBar().show();
        latitude = lat;
        longitude = lng;
        cityName = city;
        tv_location.setText(cityName);
        updateLocationUI();
    }

    private void callLocationManager() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, new String[]{PermissionUtils.LOCATION_PERMISSION}, PermissionUtils.PERMISSION_REQUEST_LOCATION);
            return;
        }

        long mLocTrackingInterval = 1000 * 60 * 2; // 5 sec
        float trackingDistance = 1;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(this)
                .location()
                .continuous()
                .config(builder.build())
                .start(location -> {
                    if (null != location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.e("Location found: ", "Location detected: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                        getAddress(latitude, longitude);
                        updateLocationUI();
                    }
                });
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            cityName = addresses.get(0).getAddressLine(0);
            if (!TextUtils.isEmpty(obj.getLocality()) && !TextUtils.isEmpty(obj.getSubLocality())) {
                cityName = obj.getSubLocality() + ", " + obj.getLocality();
            } else {
                if (!TextUtils.isEmpty(obj.getSubLocality())) {
                    cityName = obj.getSubLocality();
                } else if (!TextUtils.isEmpty(obj.getLocality())) {
                    cityName = obj.getLocality();
                } else {
                    cityName = addresses.get(0).getAddressLine(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.QRCODE) && extras.containsKey(Constants.ISREVIEW) && extras.containsKey(Constants.TOKEN)) {
                String codeQR = extras.getString(Constants.QRCODE);
                String token = extras.getString(Constants.TOKEN);
                String qid = extras.getString(Constants.QID);
                boolean isReview = extras.getBoolean(Constants.ISREVIEW, false);
                if (isReview) {
                    callReviewActivity(codeQR, token);
                } else {
                    callSkipScreen(codeQR, token, qid);
                }
            }
        }
        //Toast.makeText(launchActivity, "New Intent called", Toast.LENGTH_SHORT).show();
        setKioskMode();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.actionbarBack:
                onBackPressed();
                break;
            case R.id.tv_location:
                replaceFragmentWithoutBackStack(R.id.frame_layout, new ChangeLocationFragment());
                break;
            case R.id.iv_search:
                homeFragment.callSearch();
                break;
            case R.id.iv_notification:
                Intent in = new Intent(launchActivity, NotificationActivity.class);
                startActivity(in);
                break;
            case R.id.iv_profile:
                if (UserUtils.isLogin()) {
                    Intent intent = new Intent(launchActivity, UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to view profile");
                    Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                    loginIntent.putExtra("fromLogin", true);
                    startActivity(loginIntent);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_version:
                break;
            case R.id.tv_email:
                if (UserUtils.isLogin()) {
                    Intent intent = new Intent(launchActivity, UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                    loginIntent.putExtra("fromLogin", true);
                    startActivity(loginIntent);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.requestCodeJoinQActivity) {
            if (resultCode == RESULT_OK) {
                String intent_qrCode = data.getExtras().getString(Constants.QRCODE);
                String token = data.getExtras().getString(Constants.TOKEN);
                if (activityCommunicator != null) {
                    activityCommunicator.requestProcessed(intent_qrCode, token);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_STORAGE) {
            try {
                //both remaining permission allowed
                if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    AppUtils.shareTheApp(launchActivity);
                }
                //one remaining permission allowed
                else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppUtils.shareTheApp(launchActivity);
                }
                //No permission allowed
                else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //Do nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                if (ContextCompat.checkSelfPermission(this, PermissionUtils.LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    callLocationManager();
                }
            } else {
                // Permission denied, Disable the functionality that depends on this permission.
                new CustomToast().showToast(this, "permission denied");
            }
            return;
        }
    }

    public boolean isOnline() {
        return networkUtil.isOnline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        languagePref.registerOnSharedPreferenceChangeListener(this);
        updateNotificationBadgeCount();
        setUpExpandableList(UserUtils.isLogin());
        updateDrawerUI();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        if (null != fcmNotificationReceiver) {
            fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
        }

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());

        ReviewData reviewData = ReviewDB.getPendingReview();
        // shown only one time if the review is canceled
        if (StringUtils.isNotBlank(reviewData.getCodeQR()) && !isReviewShown() && !NoQueueBaseActivity.getShowHelper()) {
            callReviewActivity(reviewData.getCodeQR(), reviewData.getToken());
        }

        ReviewData reviewDataSkip = ReviewDB.getSkippedQueue();
        // shown only one time if it is skipped
        if (StringUtils.isNotBlank(reviewDataSkip.getCodeQR())) {
            ReviewDB.deleteReview(reviewData.getCodeQR(), reviewData.getToken());
            new CustomToast().showToast(launchActivity, "You were skipped");
        }
    }

    public void updateDrawerUI() {
        if (UserUtils.isLogin()) {
            tv_email.setText(NoQueueBaseActivity.getActualMail());
            tv_name.setText(NoQueueBaseActivity.getUserName());
        } else {
            tv_email.setText("Please login");
            tv_name.setText("Guest User");
        }
        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                Picasso.get()
                        .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, NoQueueBaseActivity.getUserProfileUri()))
                        .placeholder(ImageUtils.getProfilePlaceholder(this))
                        .error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNotificationBadgeCount() {
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if (notify_count > 0) {
            tv_badge.setVisibility(View.VISIBLE);
        } else {
            tv_badge.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
//        if(null != fcmNotificationReceiver)
//            fcmNotificationReceiver.unregister(this);
        super.onPause();
        languagePref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fcmNotificationReceiver) {
            fcmNotificationReceiver.unregister(this);
        }
    }


    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (f instanceof ChangeLocationFragment) {
            updateLocationInfo(latitude, longitude, cityName);
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = new CustomToast().getToast(launchActivity, getString(R.string.exit_app));
            backPressToast.show();
            lastPress = currentTime;
        } else {
            if (backPressToast != null) {
                backPressToast.cancel();
            }
            //super.onBackPressed();
            finish();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    private void callReviewActivity(String codeQR, String token) {
        JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR, token);
        if (null == jtk) {
            jtk = TokenAndQueueDB.getHistoryQueueObject(codeQR, token);
        }

        if (null != jtk) {
            Intent in = new Intent(launchActivity, ReviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IBConstant.KEY_DATA_OBJECT, jtk);
            in.putExtras(bundle);
            startActivityForResult(in, Constants.requestCodeJoinQActivity);
            Log.v("Review screen call: ", jtk.toString());
            ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
            if (jsonTokenAndQueueArrayList.size() == 1) {
                //un subscribe the topic
                NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
            }
        } else {
            ReviewDB.deleteReview(codeQR, token);
        }
    }

    private void callSkipScreen(String codeQR, String token, String qid) {
        ReviewData reviewData = ReviewDB.getValue(codeQR, token);
        if (null != reviewData) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseTable.Review.KEY_SKIP, -1);
            ReviewDB.updateReviewRecord(codeQR, token, cv);
            // update
        } else {
            //insert
            ContentValues cv = new ContentValues();
            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
            cv.put(DatabaseTable.Review.CODE_QR, codeQR);
            cv.put(DatabaseTable.Review.TOKEN, token);
            cv.put(DatabaseTable.Review.QID, qid);
            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
            cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
            cv.put(DatabaseTable.Review.KEY_GOTO, "");
            ReviewDB.insert(cv);
        }
        new CustomToast().showToast(launchActivity, "You were skipped");
        // Clear all activity from stack then launch skip(Join) Screen
        Intent in1 = new Intent(this, LaunchActivity.class);
        in1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in1);
        Intent in = new Intent(this, BeforeJoinActivity.class);
        in.putExtra(IBConstant.KEY_CODE_QR, codeQR);
        in.putExtra(IBConstant.KEY_FROM_LIST, false);
        in.putExtra(IBConstant.KEY_IS_REJOIN, true);
        in.putExtra(IBConstant.KEY_IS_CATEGORY, false);
        startActivity(in);
    }

    @Override
    public void appBlacklistError(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (MobileSystemErrorCodeEnum.valueOf(eej.getSystemError()) == MobileSystemErrorCodeEnum.MOBILE_UPGRADE) {
                Intent in = new Intent(launchActivity, AppUpdateActivity.class);
                startActivity(in);
                finish();
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
    }


    @Override
    public void appBlacklistResponse(JsonLatestAppVersion jsonLatestAppVersion) {
        if (null != jsonLatestAppVersion && !TextUtils.isEmpty(jsonLatestAppVersion.getLatestAppVersion())) {
            if (AppUtils.isRelease()) {
                try {
                    String currentVersion = Constants.appVersion();
                    if (Integer.parseInt(currentVersion.replace(".", "")) < Integer.parseInt(jsonLatestAppVersion.getLatestAppVersion().replace(".", ""))) {
                        ShowAlertInformation.showThemePlayStoreDialog(
                                this,
                                getString(R.string.playstore_update_title),
                                getString(R.string.playstore_update_msg),
                                true);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Compare version check reason=" + e.getLocalizedMessage(), e);
                }
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_language")) {
            ((MyApplication) getApplication()).setLocale(this);
            this.recreate();
        }
    }

    public void showChangeLangDialog() {
        final Dialog dialog = new Dialog(launchActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        final LinearLayout ll_hindi = dialog.findViewById(R.id.ll_hindi);
        final LinearLayout ll_english = dialog.findViewById(R.id.ll_english);
        final RadioButton rb_hi = dialog.findViewById(R.id.rb_hi);
        final RadioButton rb_en = dialog.findViewById(R.id.rb_en);

        if (language.equals("hi")) {
            rb_hi.setChecked(true);
            rb_en.setChecked(false);
        } else {
            rb_en.setChecked(true);
            rb_hi.setChecked(false);
        }
        ll_hindi.setOnClickListener((View v) -> {
            AppUtils.changeLanguage("hi");
            dialog.dismiss();
            if (AppUtils.isRelease()) {
                Answers.getInstance()
                        .logCustom(new CustomEvent(FabricEvents.EVENT_CHANGE_LANGUAGE)
                                .putCustomAttribute("Language", "HINDI"));
            }
        });
        ll_english.setOnClickListener((View v) -> {
            AppUtils.changeLanguage("en");
            dialog.dismiss();
            if (AppUtils.isRelease()) {
                Answers.getInstance()
                        .logCustom(new CustomEvent(FabricEvents.EVENT_CHANGE_LANGUAGE)
                                .putCustomAttribute("Language", "ENGLISH"));
            }
        });
        dialog.show();
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing(this);
    }

    private void updateNotification(Object object, String codeQR) {
        String go_to = "";
        String messageOrigin = "";
        String current_serving = "";
        String title = "";
        String body = "";
        PurchaseOrderStateEnum purchaseOrderStateEnum = PurchaseOrderStateEnum.IN;
        if (object instanceof JsonTopicQueueData) {
            current_serving = String.valueOf(((JsonTopicQueueData) object).getCurrentlyServing());//intent.getStringExtra(Constants.CurrentlyServing);
            go_to = ((JsonTopicQueueData) object).getGoTo();//intent.getStringExtra(Constants.GoTo_Counter);
            messageOrigin = ((JsonTopicQueueData) object).getMessageOrigin().name();//intent.getStringExtra(Constants.MESSAGE_ORIGIN);
            title = ((JsonTopicQueueData) object).getTitle();
            body = ((JsonTopicQueueData) object).getBody();
        } else if (object instanceof JsonTopicOrderData) {
            current_serving = String.valueOf(((JsonTopicOrderData) object).getCurrentlyServing());//intent.getStringExtra(Constants.CurrentlyServing);
            go_to = ((JsonTopicOrderData) object).getGoTo();//intent.getStringExtra(Constants.GoTo_Counter);
            messageOrigin = ((JsonTopicOrderData) object).getMessageOrigin().name();//intent.getStringExtra(Constants.MESSAGE_ORIGIN);
            purchaseOrderStateEnum = ((JsonTopicOrderData) object).getPurchaseOrderState();
            title = ((JsonTopicOrderData) object).getTitle();
            body = ((JsonTopicOrderData) object).getBody();
        }
        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
            if (null != jtk) {
                //update DB & after join screen
                if (Integer.parseInt(current_serving) < jtk.getServingNumber()) {
                    // Do nothing - In Case of getting service no less than what the object have
                } else {
                    jtk.setServingNumber(Integer.parseInt(current_serving));
                    TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                }

                if (object instanceof JsonTopicOrderData && jtk.getToken() - Integer.parseInt(current_serving) <= 0) {
                    jtk.setPurchaseOrderState(purchaseOrderStateEnum);
                }
                /*
                 * Save codeQR of goto & show it in after join screen on app
                 * Review DB for review key && current serving == token no.
                 */
                if (Integer.parseInt(current_serving) == jtk.getToken()) {
                    // if (Integer.parseInt(current_serving) == jtk.getToken() && isReview) {
                    ReviewData reviewData = ReviewDB.getValue(codeQR, current_serving);
                    if (null != reviewData) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseTable.Review.KEY_GOTO, go_to);
                        ReviewDB.updateReviewRecord(codeQR, current_serving, cv);
                        // update
                    } else {
                        //insert
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                        cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                        cv.put(DatabaseTable.Review.TOKEN, current_serving);
                        cv.put(DatabaseTable.Review.QID, jtk.getQueueUserId());
                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                        cv.put(DatabaseTable.Review.KEY_GOTO, go_to);
                        ReviewDB.insert(cv);
                    }
                }

                if (jtk.isTokenExpired() && jsonTokenAndQueueArrayList.size() == 1) {
                    //un subscribe the topic
                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                }

                if (activityCommunicator != null) {
                    boolean isUpdated = activityCommunicator.updateUI(codeQR, jtk, go_to);

                    if (isUpdated) {
                        ReviewData reviewData = ReviewDB.getValue(codeQR, current_serving);
                        if (null != reviewData) {
                            if (!reviewData.getIsBuzzerShow().equals("1")) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                                ReviewDB.updateReviewRecord(codeQR, current_serving, cv);
                                Intent blinker = new Intent(LaunchActivity.this, BlinkerActivity.class);
                                startActivity(blinker);
                            } else {
                                //Blinker already shown
                            }
                            // update
                        } else {
                            //insert
                            ContentValues cv = new ContentValues();
                            cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                            cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                            cv.put(DatabaseTable.Review.TOKEN, current_serving);
                            cv.put(DatabaseTable.Review.QID, jtk.getQueueUserId());
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                            cv.put(DatabaseTable.Review.KEY_GOTO, "");
                            ReviewDB.insert(cv);
                            Intent blinker = new Intent(LaunchActivity.this, BlinkerActivity.class);
                            startActivity(blinker);
                        }
                    }
                }
                try {
                    // In case of order update the order status
                    if (object instanceof JsonTopicOrderData) {
                        if (messageOrigin.equalsIgnoreCase(MessageOriginEnum.O.name()) && Integer.parseInt(current_serving) == jtk.getToken()) {
                            jtk.setPurchaseOrderState(((JsonTopicOrderData) object).getPurchaseOrderState());
                            TokenAndQueueDB.updateCurrentListOrderObject(codeQR, jtk.getPurchaseOrderState().getName(), String.valueOf(jtk.getToken()));
                        }
                    }
                    homeFragment.updateListFromNotification(jtk, go_to, title, body);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "codeQR=" + codeQR + " current_serving=" + current_serving + " goTo=" + go_to);
            }
        }
    }

    public void reCreateDeviceID() {
        if (new NetworkUtil(this).isOnline()) {
            String deviceId = UUID.randomUUID().toString().toUpperCase();
            Log.d(TAG, "Re-Created deviceId=" + deviceId);
            NoQueueBaseActivity.setDeviceID(deviceId);
            DeviceApiCall deviceModel = new DeviceApiCall();
            deviceModel.setDeviceRegisterPresenter(this);
            deviceModel.register(deviceId, new DeviceToken(NoQueueBaseActivity.getFCMToken(), Constants.appVersion()));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
            TextView tvTitle = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            tvTitle.setText(getString(R.string.networkerror));
            tv_msg.setText(getString(R.string.offline));
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(v -> {
                mAlertDialog.dismiss();
                finish();
            });
            mAlertDialog.show();
            Log.w(TAG, "No network found");
        }
    }

    @Override
    public void deviceRegisterError() {

    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        //dismissProgress(); no progress bar silent call here
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        //dismissProgress(); no progress bar silent call here
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if (deviceRegistered.getRegistered() == 1) {
            Log.e("Device register", "deviceRegister Success");
        } else {
            Log.e("Device register error: ", deviceRegistered.toString());
            new CustomToast().showToast(this, "Device register error: ");
        }
    }

    private void setUpExpandableList(boolean isLogin) {
        // Fill menu items
        menuDrawerItems.clear();

        if (isCountryIndia()) {
            List<MenuDrawer> healthList = new ArrayList<>();
            healthList.add(new MenuDrawer(getString(R.string.medical_profiles), false, false, R.drawable.medical_profile));
            healthList.add(new MenuDrawer(getString(R.string.medical_history), false, false, R.drawable.medical_history));
            healthList.add(new MenuDrawer(getString(R.string.my_appointments), false, false, R.drawable.appointment));

            menuDrawerItems.add(new MenuDrawer(getString(R.string.health_care), true, true, R.drawable.health_care, healthList));
        }
        menuDrawerItems.add(new MenuDrawer(getString(R.string.order_history), true, false, R.drawable.purchase_order));
        menuDrawerItems.add(new MenuDrawer(getString(R.string.merchant_account), true, false, R.drawable.merchant_account));
        menuDrawerItems.add(new MenuDrawer(getString(R.string.offers), true, false, R.drawable.offers));

        List<MenuDrawer> settingList = new ArrayList<>();
        settingList.add(new MenuDrawer(getString(R.string.share), false, false, R.drawable.ic_menu_share));
        settingList.add(new MenuDrawer(getString(R.string.invite), false, false, R.drawable.invite));
        settingList.add(new MenuDrawer(getString(R.string.legal), false, false, R.drawable.legal));
        settingList.add(new MenuDrawer(getString(R.string.ratetheapp), false, false, R.drawable.ic_star));
        settingList.add(new MenuDrawer(getString(R.string.language_setting), false, false, R.drawable.language));
        if (isLogin) {
            settingList.add(new MenuDrawer(getString(R.string.notification_setting), false, false, R.drawable.ic_notification));
        }
        menuDrawerItems.add(new MenuDrawer(getString(R.string.action_settings), true, true, R.drawable.settings_square, settingList));
        menuDrawerItems.add(new MenuDrawer(getString(R.string.title_activity_contact_us), true, false, R.drawable.contact_us));
        if (isLogin) {
            menuDrawerItems.add(new MenuDrawer(getString(R.string.logout), true, false, R.drawable.ic_logout));
        }

        DrawerExpandableListAdapter expandableListAdapter = new DrawerExpandableListAdapter(this, menuDrawerItems);
        expandable_drawer_listView.setAdapter(expandableListAdapter);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        expandable_drawer_listView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (menuDrawerItems.get(groupPosition).isGroup()) {
                if (!menuDrawerItems.get(groupPosition).isHasChildren()) {
                    int drawableId = menuDrawerItems.get(groupPosition).getIcon();
                    menuClick(drawableId);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
            return false;
        });

        expandable_drawer_listView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (menuDrawerItems.get(groupPosition) != null) {
                MenuDrawer model = menuDrawerItems.get(groupPosition).getChildList().get(childPosition);
                int drawableId = model.getIcon();
                menuClick(drawableId);
                drawer.closeDrawer(GravityCompat.START);
            }
            return false;
        });
    }

    private void menuClick(int drawable) {
        switch (drawable) {
            case R.drawable.merchant_account:
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    Intent in = new Intent(LaunchActivity.this, WebViewActivity.class);
                    in.putExtra(IBConstant.KEY_URL, UserUtils.isLogin() ? Constants.URL_MERCHANT_LOGIN : Constants.URL_MERCHANT_REGISTER);
                    startActivity(in);
                } else {
                    ShowAlertInformation.showNetworkDialog(LaunchActivity.this);
                }
                break;
            case R.drawable.purchase_order: {
                Intent in = new Intent(LaunchActivity.this, OrderQueueHistoryActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_app_setting: {
                Intent in = new Intent(launchActivity, SettingsActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.offers: {
                if (UserUtils.isLogin()) {
                    Intent in = new Intent(launchActivity, CouponsActivity.class);
                    startActivity(in);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to see the details");
                }
                break;
            }
            case R.drawable.ic_notification: {
                Intent in = new Intent(launchActivity, NotificationSettings.class);
                startActivity(in);
                break;
            }
            case R.id.nav_transaction:
                new CustomToast().showToast(launchActivity, "Coming soon... ");
                break;
            case R.drawable.ic_logout:
                ShowCustomDialog showDialog = new ShowCustomDialog(launchActivity, true);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        NoQueueBaseActivity.clearPreferences();
                        Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                        startActivity(loginIntent);
                    }

                    @Override
                    public void btnNegativeClick() {
                        //Do nothing
                    }
                });
                showDialog.displayDialog(getString(R.string.logout), getString(R.string.logout_msg));
                break;
            case R.drawable.medical_history: {
                if (UserUtils.isLogin()) {
                    Intent in = new Intent(launchActivity, MedicalHistoryActivity.class);
                    startActivity(in);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to see the details");
                }
                break;
            }
            case R.drawable.medical_profile: {
                if (UserUtils.isLogin()) {
                    Intent in = new Intent(launchActivity, AllUsersProfileActivity.class);
                    startActivity(in);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to see the details");
                }
                break;
            }
            case R.drawable.appointment: {
                if (UserUtils.isLogin()) {
                    Intent in = new Intent(launchActivity, MyAppointmentsActivity.class);
                    startActivity(in);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to see the details");
                }
                break;
            }
            case R.drawable.language:
                showChangeLangDialog();
                break;
            case R.drawable.contact_us: {
                Intent in = new Intent(LaunchActivity.this, ContactUsActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.ic_star:
                AppUtils.openPlayStore(launchActivity);
                break;
            case R.drawable.ic_menu_share:
                if (PermissionUtils.isExternalStoragePermissionAllowed(launchActivity)) {
                    AppUtils.shareTheApp(launchActivity);
                } else {
                    PermissionUtils.requestStoragePermission(launchActivity);
                }
                break;
            case R.drawable.legal: {
                Intent in = new Intent(LaunchActivity.this, PrivacyActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.invite: {
                Intent in = new Intent(LaunchActivity.this, InviteActivity.class);
                startActivity(in);
                break;
            }
        }
    }

    public class FcmNotificationReceiver extends BroadcastReceiver {
        public boolean isRegistered;

        public void register(Context context, IntentFilter filter) {
            try {
                if (!isRegistered) {
                    LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
                    Log.e("FCM Receiver: ", "register");
                    isRegistered = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void unregister(Context context) {
            if (isRegistered) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                Log.e("FCM Receiver: ", "unregister");
                isRegistered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                // new push notification is received
                String payload = intent.getStringExtra(Constants.Firebase_Type);
                String codeQR = intent.getStringExtra(Constants.CodeQR);
                Log.d(TAG, "payload=" + payload + " codeQR=" + codeQR);
                Object object = intent.getSerializableExtra("object");
                if (object instanceof JsonTopicQueueData) {
                    Log.e("onReceiveJsonTopicQdata", ((JsonTopicQueueData) object).toString());
                } else if (object instanceof JsonClientData) {
                    Log.e("onReceiveJsonClientData", ((JsonClientData) object).toString());
                } else if (object instanceof JsonAlertData) {
                    Log.e("onReceiveJsonAlertData", ((JsonAlertData) object).toString());
                } else if (object instanceof JsonTopicOrderData) {
                    Log.e("onReceiveJsonTopicOdata", ((JsonTopicOrderData) object).toString());
                } else if (object instanceof JsonClientTokenAndQueueData) {
                    Log.e("JsonClientTokenAndQData", ((JsonClientTokenAndQueueData) object).toString());
                } else if (object instanceof JsonClientOrderData) {
                    Log.e("JsonClientOrderData", ((JsonClientOrderData) object).toString());
                } else if (object instanceof JsonTopicAppointmentData) {
                    Log.e("JsonTopicAppointData", ((JsonTopicAppointmentData) object).toString());
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            "", ((JsonTopicAppointmentData) object).getBody(),
                            ((JsonTopicAppointmentData) object).getTitle(), BusinessTypeEnum.PA.getName(), ((JsonTopicAppointmentData) object).getImageURL());
                } else if (object instanceof JsonMedicalFollowUp) {
                    Log.e("JsonMedicalFollowUp", ((JsonMedicalFollowUp) object).toString());
                    NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            ((JsonMedicalFollowUp) object).getCodeQR(), ((JsonMedicalFollowUp) object).getBody(),
                            ((JsonMedicalFollowUp) object).getTitle(), BusinessTypeEnum.PA.getName(), ((JsonMedicalFollowUp) object).getImageURL());
                }

                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    if (object instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonAlertData) object).getCodeQR(),
                                ((JsonAlertData) object).getBody(),
                                ((JsonAlertData) object).getTitle(),
                                ((JsonAlertData) object).getBusinessType() == null ? BusinessTypeEnum.PA.getName() :
                                        ((JsonAlertData) object).getBusinessType().getName(), ((JsonAlertData) object).getImageURL());
                        //Show some meaningful msg to the end user
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, ((JsonAlertData) object).getTitle(), ((JsonAlertData) object).getBody());
                        updateNotificationBadgeCount();
                    } else if (object instanceof JsonClientData) {
                        String token = String.valueOf(((JsonClientData) object).getToken());
                        String qid = ((JsonClientData) object).getQueueUserId();
                        if (((JsonClientData) object).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                // update
                            } else {
                                //insert
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                cv.put(DatabaseTable.Review.TOKEN, token);
                                cv.put(DatabaseTable.Review.QID, qid);
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                cv.put(DatabaseTable.Review.KEY_GOTO, "");
                                ReviewDB.insert(cv);
                            }
                            callReviewActivity(codeQR, token);
                            // this code is added to close the join & after join screen if the request is processed
                            if (activityCommunicator != null) {
                                activityCommunicator.requestProcessed(codeQR, token);
                            }
                        } else if (((JsonClientData) object).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_SKIP, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                // update
                            } else {
                                //insert
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, -1);
                                cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                cv.put(DatabaseTable.Review.TOKEN, token);
                                cv.put(DatabaseTable.Review.QID, qid);
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                cv.put(DatabaseTable.Review.KEY_GOTO, "");
                                ReviewDB.insert(cv);
                            }
                            callSkipScreen(codeQR, token, qid);
                        }
                    } else if (object instanceof JsonClientOrderData) {
                        String token = String.valueOf(((JsonClientOrderData) object).getOrderNumber());
                        String qid = ((JsonClientOrderData) object).getQueueUserId();
                        if (((JsonClientOrderData) object).getPurchaseOrderState().getName().equalsIgnoreCase(PurchaseOrderStateEnum.OD.getName())) {
                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                // update
                            } else {
                                //insert
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                cv.put(DatabaseTable.Review.CODE_QR, codeQR);
                                cv.put(DatabaseTable.Review.TOKEN, token);
                                cv.put(DatabaseTable.Review.QID, qid);
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                                cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                                cv.put(DatabaseTable.Review.KEY_GOTO, "");
                                ReviewDB.insert(cv);
                            }
                            callReviewActivity(codeQR, token);
                            // this code is added to close the join & after join screen if the request is processed
                            // Update the order screen/ Join Screen if open
                            if (activityCommunicator != null) {
                                activityCommunicator.requestProcessed(codeQR, token);
                            }
                        }
                    } else if (object instanceof JsonTopicOrderData) {
                        updateNotification(object, codeQR);
                    } else if (object instanceof JsonTopicQueueData) {
                        updateNotification(object, codeQR);
                    } else if (object instanceof JsonClientTokenAndQueueData) {
                        List<JsonTokenAndQueue> jsonTokenAndQueueList = ((JsonClientTokenAndQueueData) object).getTokenAndQueues();
                        if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size() > 0) {
                            TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList);
                        }
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonClientTokenAndQueueData) object).getCodeQR(), ((JsonClientTokenAndQueueData) object).getBody(),
                                ((JsonClientTokenAndQueueData) object).getTitle(), BusinessTypeEnum.PA.getName(), ((JsonClientTokenAndQueueData) object).getImageURL());

                        for (int i = 0; i < jsonTokenAndQueueList.size(); i++) {
                            NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList.get(i).getTopic());
                        }
                        updateNotificationBadgeCount();
                        if (null != homeFragment)
                            homeFragment.fetchCurrentAndHistoryList();
                    }
                } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                    if (object instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonAlertData) object).getCodeQR(),
                                ((JsonAlertData) object).getBody(),
                                ((JsonAlertData) object).getTitle(),
                                ((JsonAlertData) object).getBusinessType() == null ? BusinessTypeEnum.PA.getName() :
                                        ((JsonAlertData) object).getBusinessType().getName(), ((JsonAlertData) object).getImageURL());
                        //Show some meaningful msg to the end user
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, ((JsonAlertData) object).getTitle(), ((JsonAlertData) object).getBody());
                        updateNotificationBadgeCount();
                    } else {
                        updateNotification(object, codeQR);
                    }
                } else {
                    new CustomToast().showToast(launchActivity, "UnSupported Notification reached: " + payload);
                }
            }
        }
    }

    public static String getCountryCode() {
        if (UserUtils.isLogin()) {
            return getCountryShortName();
        } else {
            try {
                final TelephonyManager tm = (TelephonyManager) launchActivity.getSystemService(Context.TELEPHONY_SERVICE);
                final String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                    return simCountry.toLowerCase(Locale.getDefault());
                } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                        return networkCountry.toLowerCase(Locale.getDefault());
                    } else {
                        return getCountry(launchActivity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getCountry(launchActivity);
        }
    }

    public static String getCountry(Context context) {
        try {
            LocationManager locationManager = (LocationManager) launchActivity.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        return addresses.get(0).getCountryCode();
                    } else {
                        return Constants.DEFAULT_COUNTRY_CODE;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Constants.DEFAULT_COUNTRY_CODE;
                }

            } else {
                return Constants.DEFAULT_COUNTRY_CODE;
            }
        } catch (SecurityException e) {
            return Constants.DEFAULT_COUNTRY_CODE;
        }
    }

    public boolean isCountryIndia() {
        return (COUNTRY_CODE.equalsIgnoreCase("India") || COUNTRY_CODE.equalsIgnoreCase("IN"));
    }
}
