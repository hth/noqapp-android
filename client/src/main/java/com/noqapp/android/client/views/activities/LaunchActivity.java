package com.noqapp.android.client.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.cache.Cache;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceApiCall;
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
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.ShowCustomDialog;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.DrawerExpandableListAdapter;
import com.noqapp.android.client.views.customviews.BadgeDrawable;
import com.noqapp.android.client.views.fragments.ChangeLocationFragment;
import com.noqapp.android.client.views.fragments.HomeFragment;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonChangeServiceTimeData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonData;
import com.noqapp.android.common.fcm.data.JsonMedicalFollowUp;
import com.noqapp.android.common.fcm.data.JsonTopicAppointmentData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.pojos.MenuDrawer;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.utils.PermissionUtils;
import com.noqapp.android.common.utils.TextToSpeechHelper;
import com.noqapp.android.common.views.activities.AppUpdateActivity;
import com.noqapp.android.common.views.activities.AppsLinksActivity;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

import static com.google.common.cache.CacheBuilder.newBuilder;

public class LaunchActivity
    extends ScannerActivity
    implements OnClickListener, AppBlacklistPresenter,
    SharedPreferences.OnSharedPreferenceChangeListener,
    DeviceRegisterPresenter {
    private static final String TAG = LaunchActivity.class.getSimpleName();
    public static Locale locale;
    public static SharedPreferences languagePref;
    public static String language;
    private static LaunchActivity launchActivity;
    public TextView tv_location;
    public NetworkUtil networkUtil;
    protected ExpandableListView expandable_drawer_listView;
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
    private TextToSpeechHelper textToSpeechHelper;
    private final Cache<String, ArrayList<String>> cacheMsgIds = newBuilder().maximumSize(1).build();
    private final String MSG_IDS = "messageIds";
    private DrawerExpandableListAdapter expandableListAdapter;

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        tv_location = findViewById(R.id.tv_location);
        ImageView iv_search = findViewById(R.id.iv_search);
        ImageView iv_barcode = findViewById(R.id.iv_barcode);
        launchActivity = this;
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            COUNTRY_CODE = "IN";
        } else {
            COUNTRY_CODE = "IN";
        }
        Log.d(TAG, "Country Code: " + COUNTRY_CODE);
        textToSpeechHelper = new TextToSpeechHelper(getApplicationContext());
        if (!isCountryIndia()) {
            Constants.DEFAULT_LATITUDE = 37.7749;
            Constants.DEFAULT_LONGITUDE = 122.4194;
            Constants.DEFAULT_CITY = "San Francisco";
            Constants.DEFAULT_COUNTRY_CODE = "US";
            DISTANCE_UNIT = "mi";
        }

        //NoQueueBaseActivity.saveMailAuth("","");
        if (TextUtils.isEmpty(AppInitialize.getDeviceId())) {
            // Log.v("Device id check", MyApplication.getDeviceId());
            AppInitialize.fetchDeviceId();
        }

        if (null != getIntent().getExtras()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra(AppInitialize.TOKEN_FCM))) {
                AppInitialize.setTokenFCM(getIntent().getStringExtra(AppInitialize.TOKEN_FCM));
            }

            if (!TextUtils.isEmpty(getIntent().getStringExtra("deviceId"))) {
                AppInitialize.setDeviceID(getIntent().getStringExtra("deviceId"));
            }
        }
        AppInitialize.setReviewShown(false);//Reset the flag when app is killed
        networkUtil = new NetworkUtil(this);
        fcmNotificationReceiver = new FcmNotificationReceiver();
        fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));

        //Language setup
        languagePref = PreferenceManager.getDefaultSharedPreferences(this);
        languagePref.registerOnSharedPreferenceChangeListener(this);
        language = languagePref.getString("pref_language", "");

        if (StringUtils.isNotBlank(language)) {
            switch (language) {
                case "hi":
                    language = "hi";
                    locale = new Locale("hi");
                    break;
                case "kn":
                    language = "kn";
                    locale = new Locale("kn");
                    break;
                case "fr":
                    language = "fr";
                    locale = new Locale("fr");
                    break;
                default:
                    locale = Locale.ENGLISH;
                    language = "en_US";
                    break;
            }
        } else {
            locale = Locale.ENGLISH;
            language = "en_US";
        }
        // @TODO revert this location changes
        //callLocationManager();
        ((AppInitialize) getApplication()).setLocale(this);
        iv_search.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        iv_barcode.setOnClickListener(this);
        iv_search.setVisibility(View.VISIBLE);
        //initProgress();
        homeFragment = new HomeFragment();
        replaceFragmentWithoutBackStack(R.id.frame_layout, homeFragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        if (isOnline()) {
            DeviceApiCall deviceModel = new DeviceApiCall();
            deviceModel.setAppBlacklistPresenter(this);
            deviceModel.isSupportedAppVersion();
        }
        if (null != getIntent().getExtras()) {
            try {
                AppInitialize.location.setLatitude(getIntent().getDoubleExtra("latitude", Constants.DEFAULT_LATITUDE));
                AppInitialize.location.setLongitude(getIntent().getDoubleExtra("longitude", Constants.DEFAULT_LONGITUDE));
                AppInitialize.cityName = CommonHelper.getAddress(
                    AppInitialize.location.getLatitude(),
                    AppInitialize.location.getLongitude(),
                    this);

                Log.d(TAG, "Launch Activity City Name =" + AppInitialize.cityName);
                //updateLocationUI();
                tv_location.setText(AppInitialize.cityName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setKioskMode();
    }

    private void setKioskMode() {
        if (AppInitialize.isLockMode) {
            if (AppInitialize.getKioskModeInfo().isLevelUp()) {
                if (AppInitialize.getKioskModeInfo().isFeedbackScreen()) {
                    Intent in = new Intent(LaunchActivity.this, SurveyKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, AppInitialize.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                } else {
                    AppInitialize.clearPreferences();
                    Intent in = new Intent(LaunchActivity.this, CategoryInfoKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, AppInitialize.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                }
            } else {
                if (AppInitialize.getKioskModeInfo().isFeedbackScreen()) {
                    Intent in = new Intent(LaunchActivity.this, SurveyKioskModeActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, AppInitialize.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                } else {
                    AppInitialize.clearPreferences();
                    Intent in = new Intent(LaunchActivity.this, StoreWithMenuKioskActivity.class);
                    in.putExtra(IBConstant.KEY_CODE_QR, AppInitialize.getKioskModeInfo().getKioskCodeQR());
                    startActivity(in);
                }
            }
        }
    }

    public void updateLocationUI() {
        if (null != homeFragment) {
            homeFragment.updateUIWithNewLocation(
                AppInitialize.location.getLatitude(),
                AppInitialize.location.getLongitude(),
                AppInitialize.cityName);
            //tv_location.setText(cityName);
        }
    }

    public void updateLocationInfo(double lat, double lng, String city) {
        replaceFragmentWithoutBackStack(R.id.frame_layout, homeFragment);
        getSupportActionBar().show();
        AppInitialize.location.setLatitude(lat);
        AppInitialize.location.setLongitude(lng);
        AppInitialize.cityName = city;
        tv_location.setText(AppInitialize.cityName);
        LocationPref locationPref = AppInitialize.getLocationPreference()
            .setCity(AppInitialize.cityName)
            .setLatitude(lat)
            .setLongitude(lng);
        AppInitialize.setLocationPreference(locationPref);
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
                    AppInitialize.location.setLatitude(location.getLatitude());
                    AppInitialize.location.setLongitude(location.getLongitude());
                    Log.e("Location found: ", "Location detected: Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                    AppInitialize.cityName = CommonHelper.getAddress(
                        AppInitialize.location.getLatitude(),
                        AppInitialize.location.getLongitude(),
                        this);
                    updateLocationUI();
                }
            });
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
            case R.id.iv_barcode: {
                startScanningBarcode();
            }
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
                //Do nothing
                break;
            case R.id.tv_email:
                if (UserUtils.isLogin()) {
                    Intent intent = new Intent(launchActivity, UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                    loginIntent.putExtra("fromLogin", true);
                    startActivity(loginIntent);
                    finish();
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
                if (AppInitialize.activityCommunicator != null) {
                    AppInitialize.activityCommunicator.requestProcessed(intent_qrCode, token);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_STORAGE) {
            try {
                /* both remaining permission allowed */
                if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    AppUtils.shareTheApp(launchActivity);
                }
                /* one remaining permission allowed */
                else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppUtils.shareTheApp(launchActivity);
                }
                /* No permission allowed */
                else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    /* Do nothing */
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /* Permission was granted. */
                if (ContextCompat.checkSelfPermission(this, PermissionUtils.LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    callLocationManager();
                }
            } else {
                /* Permission denied, Disable the functionality that depends on this permission. */
                new CustomToast().showToast(this, "Permission denied");
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

        /* Register new push message receiver by doing this, the activity will be notified each time a new message arrives. */
        if (null != fcmNotificationReceiver) {
            fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
        }

        /* Clear the notification area when the app is opened */
        NoQueueMessagingService.clearNotifications(getApplicationContext());

        ReviewData reviewData = ReviewDB.getPendingReview();
        /* Shown only one time if the review is canceled */
        if (StringUtils.isNotBlank(reviewData.getCodeQR()) && !AppInitialize.isReviewShown() && !AppInitialize.getShowHelper()) {
            callReviewActivity(reviewData.getCodeQR(), reviewData.getToken());
            Log.d("onResume review screen", "review screen called");
        }

        ReviewData reviewDataSkip = ReviewDB.getSkippedQueue();
        /* Shown only one time if it is skipped */
        if (StringUtils.isNotBlank(reviewDataSkip.getCodeQR())) {
            ReviewDB.deleteReview(reviewData.getCodeQR(), reviewData.getToken());
            new CustomToast().showToast(launchActivity, "You were skipped");
        }
    }

    public void updateDrawerUI() {
        if (UserUtils.isLogin()) {
            tv_email.setText(AppInitialize.getActualMail());
            tv_name.setText(AppInitialize.getUserName());
        } else {
            tv_email.setText("Please login");
            tv_name.setText("Guest User");
        }
        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(AppInitialize.getUserProfileUri())) {
                Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.PROFILE_BUCKET, AppInitialize.getUserProfileUri()))
                    .placeholder(ImageUtils.getProfilePlaceholder(this))
                    .error(ImageUtils.getProfileErrorPlaceholder(this))
                    .into(iv_profile);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.e(TAG, "Failed Update Drawer UI", e);
        }
    }

    public void updateNotificationBadgeCount() {
        int notify_count = NotificationDB.getNotificationCount();
        if (null != expandable_drawer_listView && null != expandableListAdapter) {
            expandableListAdapter.notifyDataSetChanged();
        }
        if (null != getSupportActionBar()) {
            getSupportActionBar().setHomeAsUpIndicator(setBadgeCount(this, R.drawable.ic_burger, notify_count));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);   // enable overriding the default toolbar layout
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
//        if(null != fcmNotificationReceiver)
//            fcmNotificationReceiver.unregister(this);
        super.onPause();
        // languagePref.unregisterOnSharedPreferenceChangeListener(this);
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
            updateLocationInfo(
                AppInitialize.location.getLatitude(),
                AppInitialize.location.getLongitude(),
                AppInitialize.cityName);
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
        try {
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
                    /* Un-subscribe the topic. */
                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                }
            } else {
                ReviewDB.deleteReview(codeQR, token);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void callSkipScreen(String codeQR, String token, String qid) {
        ReviewData reviewData = ReviewDB.getValue(codeQR, token);
        if (null != reviewData) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseTable.Review.KEY_SKIP, -1);
            ReviewDB.updateReviewRecord(codeQR, token, cv);
            /* update */
        } else {
            /* insert */
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
        /* Clear all activity from stack then launch skip(Join) Screen */
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
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.e(TAG, "Compare version check reason=" + e.getLocalizedMessage(), e);
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_language")) {
            ((AppInitialize) getApplication()).setLocale(this);
            this.recreate();
        }
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing(this);
    }

    private void updateNotification(JsonData jsonData, String codeQR) {
        try {
            String go_to = "";
            String messageOrigin = "";
            String current_serving = "";
            List<JsonTextToSpeech> jsonTextToSpeeches = null;
            String msgId = "";
            PurchaseOrderStateEnum purchaseOrderStateEnum = PurchaseOrderStateEnum.IN;
            if (jsonData instanceof JsonTopicQueueData) {
                JsonTopicQueueData jsonTopicQueueData = (JsonTopicQueueData) jsonData;
                current_serving = String.valueOf(jsonTopicQueueData.getCurrentlyServing());
                go_to = jsonTopicQueueData.getGoTo();
                messageOrigin = jsonTopicQueueData.getMessageOrigin().name();
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches();
                msgId = jsonTopicQueueData.getMessageId();
            } else if (jsonData instanceof JsonTopicOrderData) {
                JsonTopicOrderData jsonTopicOrderData = (JsonTopicOrderData) jsonData;
                current_serving = String.valueOf(jsonTopicOrderData.getCurrentlyServing());
                go_to = jsonTopicOrderData.getGoTo();
                messageOrigin = jsonTopicOrderData.getMessageOrigin().name();
                purchaseOrderStateEnum = jsonTopicOrderData.getPurchaseOrderState();
                jsonTextToSpeeches = jsonData.getJsonTextToSpeeches();
                msgId = jsonTopicOrderData.getMessageId();
            }

            ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
            for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
                JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
                if (null != jtk) {
                    /* update DB & after join screen */
                    if (Integer.parseInt(current_serving) < jtk.getServingNumber()) {
                        /* Do nothing - In Case of getting service no less than what the object have */
                    } else {
                        jtk.setServingNumber(Integer.parseInt(current_serving));
                        TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                    }

                    if (jsonData instanceof JsonTopicOrderData && jtk.getToken() - Integer.parseInt(current_serving) <= 0) {
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
                            /* update */
                        } else {
                            /* insert */
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
                        /* Un-subscribe the topic */
                        NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                    }

                    if (AppInitialize.activityCommunicator != null) {
                        boolean isUpdated = AppInitialize.activityCommunicator.updateUI(codeQR, jtk, go_to);

                        if (isUpdated || (jtk.getServingNumber() == jtk.getToken())) {
                            ReviewData reviewData = ReviewDB.getValue(codeQR, current_serving);
                            if (null != reviewData) {
                                if (!reviewData.getIsBuzzerShow().equals("1")) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                                    ReviewDB.updateReviewRecord(codeQR, current_serving, cv);
                                    Intent blinker = new Intent(LaunchActivity.this, BlinkerActivity.class);
                                    blinker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(blinker);
                                    if (AppInitialize.isMsgAnnouncementEnable()) {
                                        makeAnnouncement(jsonTextToSpeeches, msgId);
                                    }
                                } else {
                                    /* Blinker already shown */
                                }
                                /* update */
                            } else {
                                /* insert */
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
                                blinker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(blinker);
                                if (AppInitialize.isMsgAnnouncementEnable()) {
                                    makeAnnouncement(jsonTextToSpeeches, msgId);
                                }
                            }
                        }
                    }
                    try {
                        /* In case of order update the order status */
                        if (jsonData instanceof JsonTopicOrderData) {
                            if (messageOrigin.equalsIgnoreCase(MessageOriginEnum.O.name()) && Integer.parseInt(current_serving) == jtk.getToken()) {
                                jtk.setPurchaseOrderState(((JsonTopicOrderData) jsonData).getPurchaseOrderState());
                                TokenAndQueueDB.updateCurrentListOrderObject(codeQR, jtk.getPurchaseOrderState().getName(), String.valueOf(jtk.getToken()));
                            }
                        }
                        homeFragment.updateListFromNotification(jtk, jsonTextToSpeeches, msgId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                } else {
                    Log.e(TAG, "codeQR=" + codeQR + " current_serving=" + current_serving + " goTo=" + go_to);
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Failed on update notification");
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.e(TAG, "Failed on update notification " + e.getLocalizedMessage());
        }
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        /* dismissProgress(); no progress bar silent call here */
        new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        /* dismissProgress(); no progress bar silent call here */
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    private void setUpExpandableList(boolean isLogin) {
        /* Fill menu items */
        menuDrawerItems.clear();

        if (isCountryIndia()) {
            List<MenuDrawer> healthList = new ArrayList<>();
            healthList.add(new MenuDrawer(getString(R.string.medical_profiles), false, false, R.drawable.medical_profile));
            healthList.add(new MenuDrawer(getString(R.string.medical_history), false, false, R.drawable.medical_history));

            menuDrawerItems.add(new MenuDrawer(getString(R.string.health_care), true, true, R.drawable.health_care, healthList));
            menuDrawerItems.add(new MenuDrawer(getString(R.string.appointments), true, false, R.drawable.appointment));
        }
        menuDrawerItems.add(new MenuDrawer(getString(R.string.order_history), true, false, R.drawable.purchase_order));
        if (isLogin) {
            menuDrawerItems.add(new MenuDrawer(getString(R.string.merchant_account), true, false, R.drawable.merchant_account));
        }
        menuDrawerItems.add(new MenuDrawer(getString(R.string.offers), true, false, R.drawable.offers));
        menuDrawerItems.add(new MenuDrawer(getString(R.string.notification_setting), true, false, R.drawable.ic_notification));
        List<MenuDrawer> settingList = new ArrayList<>();
        settingList.add(new MenuDrawer(getString(R.string.share), false, false, R.drawable.ic_menu_share));
        settingList.add(new MenuDrawer(getString(R.string.invite), false, false, R.drawable.invite));
        settingList.add(new MenuDrawer(getString(R.string.legal), false, false, R.drawable.legal));
        settingList.add(new MenuDrawer(getString(R.string.ratetheapp), false, false, R.drawable.ic_star));
        settingList.add(new MenuDrawer(getString(R.string.language_setting), false, false, R.drawable.language));
        if (isLogin) {
            settingList.add(new MenuDrawer(getString(R.string.preference_settings), false, false, R.drawable.settings));
            settingList.add(new MenuDrawer(getString(R.string.logout), false, false, R.drawable.ic_logout));
        }
        menuDrawerItems.add(new MenuDrawer(getString(R.string.action_settings), true, true, R.drawable.settings_square, settingList));
        menuDrawerItems.add(new MenuDrawer(getString(R.string.title_activity_contact_us), true, false, R.drawable.contact_us));
        if (!AppUtils.isRelease()) {
            menuDrawerItems.add(new MenuDrawer(getString(R.string.noqueue_apps), true, false, R.drawable.apps));
        }

        expandableListAdapter = new DrawerExpandableListAdapter(this, menuDrawerItems);
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
                if (isOnline()) {
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
            case R.drawable.settings: {
                Intent in = new Intent(launchActivity, PreferenceSettings.class);
                startActivity(in);
                break;
            }
            case R.drawable.ic_notification: {
                Intent in = new Intent(launchActivity, NotificationActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.ic_logout:
                ShowCustomDialog showDialog = new ShowCustomDialog(launchActivity, true);
                showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
                    @Override
                    public void btnPositiveClick() {
                        AppInitialize.clearPreferences();
                        NotificationDB.clearNotificationTable();
                        ReviewDB.clearReviewTable();
                        reCreateDeviceID(launchActivity, launchActivity);

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
                    Intent in = new Intent(launchActivity, AppointmentActivity.class);
                    startActivity(in);
                } else {
                    new CustomToast().showToast(launchActivity, "Please login to see the details");
                }
                break;
            }
            case R.drawable.language:
                Intent claIntent = new Intent(LaunchActivity.this, ChangeLanguageActivity.class);
                startActivity(claIntent);
                break;
            case R.drawable.contact_us: {
                Intent in = new Intent(LaunchActivity.this, ContactUsActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.apps: {
                Intent in = new Intent(LaunchActivity.this, AppsLinksActivity.class);
                startActivity(in);
                break;
            }
            case R.drawable.ic_star:
                AppUtils.openPlayStore(launchActivity);
                break;
            case R.drawable.ic_menu_share:
                // @TODO revert the permission changes when permission enabled in manifest
                //if (PermissionUtils.isExternalStoragePermissionAllowed(launchActivity)) {
                AppUtils.shareTheApp(launchActivity);
//                } else {
//                    PermissionUtils.requestStoragePermission(launchActivity);
//                }
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

    @Override
    public void deviceRegisterError() {
        /* dismissProgress(); no progress bar silent call here */
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        /* dismissProgress(); no progress bar silent call here */
        AppInitialize.processRegisterDeviceIdResponse(deviceRegistered, this);
        Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
        startActivity(loginIntent);
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
                FirebaseCrashlytics.getInstance().recordException(e);
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

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                // new push notification is received
                String payload = intent.getStringExtra(Constants.FIREBASE_TYPE);
                String codeQR = intent.getStringExtra(Constants.CODE_QR);
                Log.d(TAG, "payload=" + payload + " codeQR=" + codeQR);
                JsonData jsonData = (JsonData) intent.getSerializableExtra("jsonData");
                if (jsonData instanceof JsonTopicQueueData) {
                    Log.e("onReceiveJsonTopicQdata", jsonData.toString());
                } else if (jsonData instanceof JsonClientData) {
                    Log.e("onReceiveJsonClientData", jsonData.toString());
                } else if (jsonData instanceof JsonAlertData) {
                    Log.e("onReceiveJsonAlertData", jsonData.toString());
                } else if (jsonData instanceof JsonTopicOrderData) {
                    Log.e("onReceiveJsonTopicOdata", jsonData.toString());
                } else if (jsonData instanceof JsonClientTokenAndQueueData) {
                    Log.e("JsonClientTokenAndQData", jsonData.toString());
                } else if (jsonData instanceof JsonClientOrderData) {
                    Log.e("JsonClientOrderData", jsonData.toString());
                } else if (jsonData instanceof JsonChangeServiceTimeData) {
                    Log.e("JsonChangeServiceTimeData", jsonData.toString());
                } else if (jsonData instanceof JsonTopicAppointmentData) {
                    Log.e("JsonTopicAppointData", jsonData.toString());
                    NotificationDB.insertNotification(
                        NotificationDB.KEY_NOTIFY,
                        "",
                        jsonData.getBody(),
                        jsonData.getTitle(),
                        BusinessTypeEnum.PA.getName(),
                        jsonData.getImageURL());
                } else if (jsonData instanceof JsonMedicalFollowUp) {
                    Log.e("JsonMedicalFollowUp", jsonData.toString());
                    NotificationDB.insertNotification(
                        NotificationDB.KEY_NOTIFY,
                        ((JsonMedicalFollowUp) jsonData).getCodeQR(),
                        jsonData.getBody(),
                        jsonData.getTitle(),
                        BusinessTypeEnum.PA.getName(),
                        jsonData.getImageURL());
                }

                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    if (jsonData instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            ((JsonAlertData) jsonData).getCodeQR(),
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            ((JsonAlertData) jsonData).getBusinessType() == null
                                ? BusinessTypeEnum.PA.getName()
                                : ((JsonAlertData) jsonData).getBusinessType().getName(),
                            jsonData.getImageURL());
                        //Show some meaningful msg to the end user
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, jsonData.getTitle(), jsonData.getBody());
                        updateNotificationBadgeCount();
                    } else if (jsonData instanceof JsonClientData) {
                        String token = String.valueOf(((JsonClientData) jsonData).getToken());
                        String qid = ((JsonClientData) jsonData).getQueueUserId();
                        if (((JsonClientData) jsonData).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                /* update */
                            } else {
                                /* insert */
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

                            // Clear the App Shared Preferences entry for this queue
                            SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.APP_PACKAGE, Context.MODE_PRIVATE);
                            prefs.edit().remove(String.format(Constants.ESTIMATED_WAIT_TIME_PREF_KEY, codeQR)).apply();
                            prefs.edit().remove(String.format(Constants.CURRENTLY_SERVING_PREF_KEY, codeQR)).apply();

                            callReviewActivity(codeQR, token);
                            /* this code is added to close the join & after join screen if the request is processed */
                            if (AppInitialize.activityCommunicator != null) {
                                AppInitialize.activityCommunicator.requestProcessed(codeQR, token);
                            }
                        } else if (((JsonClientData) jsonData).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_SKIP, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                /* update */
                            } else {
                                /* insert */
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
                    } else if (jsonData instanceof JsonClientOrderData) {
                        String token = String.valueOf(((JsonClientOrderData) jsonData).getOrderNumber());
                        String qid = ((JsonClientOrderData) jsonData).getQueueUserId();
                        if (((JsonClientOrderData) jsonData).getPurchaseOrderState().getName().equalsIgnoreCase(PurchaseOrderStateEnum.OD.getName())) {
                            /*
                             * Save codeQR of review & show the review screen on app
                             * resume if there is any record in Review DB for queue review key
                             */
                            ReviewData reviewData = ReviewDB.getValue(codeQR, token);
                            if (null != reviewData) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_REVIEW_SHOWN, 1);
                                ReviewDB.updateReviewRecord(codeQR, token, cv);
                                /* update */
                            } else {
                                /* insert */
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
                            /*
                             * this code is added to close the join & after join screen if the request is processed
                             * Update the order screen/ Join Screen if open
                             */
                            if (AppInitialize.activityCommunicator != null) {
                                AppInitialize.activityCommunicator.requestProcessed(codeQR, token);
                            }
                        }
                    } else if (jsonData instanceof JsonTopicOrderData) {
                        updateNotification(jsonData, codeQR);
                    } else if (jsonData instanceof JsonTopicQueueData) {
                        updateNotification(jsonData, codeQR);
                    } else if (jsonData instanceof JsonClientTokenAndQueueData) {
                        List<JsonTokenAndQueue> jsonTokenAndQueueList = ((JsonClientTokenAndQueueData) jsonData).getTokenAndQueues();
                        if (null != jsonTokenAndQueueList && jsonTokenAndQueueList.size() > 0) {
                            TokenAndQueueDB.saveCurrentQueue(jsonTokenAndQueueList);
                        }
                        NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            ((JsonClientTokenAndQueueData) jsonData).getCodeQR(),
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            BusinessTypeEnum.PA.getName(),
                            jsonData.getImageURL());

                        for (int i = 0; i < jsonTokenAndQueueList.size(); i++) {
                            NoQueueMessagingService.subscribeTopics(jsonTokenAndQueueList.get(i).getTopic());
                        }
                        updateNotificationBadgeCount();
                        if (null != homeFragment) {
                            homeFragment.fetchCurrentAndHistoryList();
                        }
                    }
                } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                    if (jsonData instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            ((JsonAlertData) jsonData).getCodeQR(),
                            jsonData.getBody(),
                            jsonData.getTitle(),
                            ((JsonAlertData) jsonData).getBusinessType() == null
                                ? BusinessTypeEnum.PA.getName()
                                : ((JsonAlertData) jsonData).getBusinessType().getName(), jsonData.getImageURL());
                        /* Show some meaningful msg to the end user */
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, jsonData.getTitle(), jsonData.getBody());
                        updateNotificationBadgeCount();
                    } else if (jsonData instanceof JsonChangeServiceTimeData) {
                        String body = jsonData.getBody() + "\n " + "Token: " + ((JsonChangeServiceTimeData) jsonData).getJsonQueueChangeServiceTimes().get(0).getDisplayToken()
                            + "\n " + "Previous: " + ((JsonChangeServiceTimeData) jsonData).getJsonQueueChangeServiceTimes().get(0).getOldTimeSlotMessage()
                            + "\n " + "Updated: " + ((JsonChangeServiceTimeData) jsonData).getJsonQueueChangeServiceTimes().get(0).getUpdatedTimeSlotMessage();
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, jsonData.getTitle(), body);

                        NotificationDB.insertNotification(
                            NotificationDB.KEY_NOTIFY,
                            ((JsonChangeServiceTimeData) jsonData).getCodeQR(),
                            body,
                            jsonData.getTitle(),
                            ((JsonChangeServiceTimeData) jsonData).getBusinessType().getName(),
                            jsonData.getImageURL());
                        updateNotificationBadgeCount();
                        if (null != homeFragment) {
                            homeFragment.updateCurrentQueueList();
                        }
                    } else {
                        updateNotification(jsonData, codeQR);
                    }
                } else {
                    new CustomToast().showToast(launchActivity, "UnSupported Notification reached: " + payload);
                    FirebaseCrashlytics.getInstance().log("UnSupported Notification reached: " + payload);
                }
            }
        }
    }

    public static String getCountryCode() {
        if (UserUtils.isLogin()) {
            return AppInitialize.getCountryShortName();
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
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            return getCountry(launchActivity);
        }
    }

    private static String getCountry(Context context) {
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

    public void makeAnnouncement(List<JsonTextToSpeech> jsonTextToSpeeches, String msgId) {
        if (null == cacheMsgIds.getIfPresent(MSG_IDS)) {
            cacheMsgIds.put(MSG_IDS, new ArrayList<>());
        }
        ArrayList<String> msgIds = cacheMsgIds.getIfPresent(MSG_IDS);
        if (null == msgIds) {
            msgIds = new ArrayList<>();
        }
        if (!TextUtils.isEmpty(msgId) && !msgIds.contains(msgId)) {
            msgIds.add(msgId);
            cacheMsgIds.put(MSG_IDS, msgIds);
            textToSpeechHelper.makeAnnouncement(jsonTextToSpeeches);
        }
    }

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    public void reCreateDeviceID(Activity context, DeviceRegisterPresenter deviceRegisterPresenter) {
        if (new NetworkUtil(context).isOnline()) {
            AppInitialize.fetchDeviceId(deviceRegisterPresenter);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            builder.setTitle(null);
            View customDialogView = inflater.inflate(R.layout.dialog_general, null, false);
            TextView tvTitle = customDialogView.findViewById(R.id.tvtitle);
            TextView tv_msg = customDialogView.findViewById(R.id.tv_msg);
            tvTitle.setText(context.getString(R.string.networkerror));
            tv_msg.setText(context.getString(R.string.offline));
            builder.setView(customDialogView);
            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            Button btn_yes = customDialogView.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(v -> {
                mAlertDialog.dismiss();
                context.finish();
            });
            mAlertDialog.show();
            Log.w(TAG, "No network found");
        }
    }

    private Drawable setBadgeCount(Context context, int res, int badgeCount) {
        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable);
        Drawable mainIcon = ContextCompat.getDrawable(context, res);
        BadgeDrawable badge = new BadgeDrawable(context);
        badge.setCount(String.valueOf(badgeCount));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);
        return icon;
    }

}
