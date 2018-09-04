package com.noqapp.android.client.views.activities;

import static com.noqapp.android.client.BuildConfig.BUILD_TYPE;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.model.database.DatabaseHelper;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.types.QueueUserStateEnum;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.network.VersionCheckAsync;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.presenter.DeviceRegisterPresenter;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.NetworkStateChanged;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.fragments.ScanQueueFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.model.types.FCMTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.NetworkUtil;

import com.crashlytics.android.answers.Answers;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LaunchActivity extends LocationActivity implements OnClickListener, DeviceRegisterPresenter,AppBlacklistPresenter, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    @BindView(R.id.tv_badge)
    protected TextView tv_badge;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;

    @BindView(R.id.iv_search)
    protected ImageView iv_search;


    @BindView(R.id.iv_notification)
    protected ImageView iv_notification;

    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;

    private long lastPress;
    private Toast backPressToast;
    private FcmNotificationReceiver fcmNotificationReceiver;
    private ImageView iv_profile;
    private TextView tv_login, tv_name, tv_email;
    private ScanQueueFragment scanFragment;
    private DrawerLayout drawer;
    private Menu nav_Menu;
    public static DatabaseHelper dbHandler;
    public static Locale locale;
    public static SharedPreferences languagepref;
    public static String language;
    private static LaunchActivity launchActivity;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    public ActivityCommunicator activityCommunicator;


    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        EventBus.getDefault().register(this);
        JodaTimeAndroid.init(this);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        launchActivity = this;
//        NoQueueBaseActivity.saveMailAuth("","");
        if (null != getIntent().getExtras()) {
            NoQueueBaseActivity.setFCMToken(getIntent().getStringExtra("fcmToken"));
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                    NoQueueBaseActivity.APP_PREF, Context.MODE_PRIVATE);
            setSharedPreferenceDeviceID(sharedpreferences, getIntent().getStringExtra("deviceId"));
        }
        Log.v("device id check", getDeviceID());
        setReviewShown(false);//Reset the flag when app is killed
        networkUtil = new NetworkUtil(this);
        fcmNotificationReceiver = new FcmNotificationReceiver();
        fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
        //Language setup
        languagepref = PreferenceManager.getDefaultSharedPreferences(this);
        languagepref.registerOnSharedPreferenceChangeListener(this);
        language = languagepref.getString(
                "pref_language", "");


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

        iv_search.setOnClickListener(this);
        actionbarBack.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        fl_notification.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.VISIBLE);
        actionbarBack.setVisibility(View.GONE);
        initProgress();
        scanFragment = new ScanQueueFragment();
        replaceFragmentWithoutBackStack(R.id.frame_layout, scanFragment);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_transaction).setVisible(false);
        nav_Menu.findItem(R.id.nav_app_setting).setVisible(false);
        nav_Menu.findItem(R.id.nav_logout).setVisible(UserUtils.isLogin());
        nav_Menu.findItem(R.id.nav_version).setTitle(BuildConfig.BUILD_TYPE.equalsIgnoreCase("release") ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                : getString(R.string.version_no, "Not for release"));
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout mParent = (LinearLayout) navigationView.getHeaderView(0);
        iv_profile = mParent.findViewById(R.id.iv_profile);
        tv_login = mParent.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        tv_name = mParent.findViewById(R.id.tv_name);
        tv_email = mParent.findViewById(R.id.tv_email);
        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setAppBlacklistPresenter(this);
            deviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }

    }


    private void setSharedPreferenceDeviceID(SharedPreferences sharedpreferences, String deviceId) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(NoQueueBaseActivity.XR_DID, deviceId);
        editor.apply();
    }

    @Override
    public void updateLocationUI() {
        if (null != scanFragment) {
            scanFragment.updateUIWithNewLocation(latitute, longitute, cityName);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.QRCODE) && extras.containsKey(Constants.ISREVIEW)
                    && extras.containsKey(Constants.TOKEN)) {
                String codeQR = extras.getString(Constants.QRCODE);
                String token = extras.getString(Constants.TOKEN);
                String quserID = extras.getString(Constants.QuserID);
                boolean isReview = extras.getBoolean(Constants.ISREVIEW, false);
                if (isReview) {
                    callReviewActivity(codeQR, token);
                } else {
                    callSkipScreen(codeQR, token, quserID);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.actionbarBack:
                onBackPressed();
                break;
            case R.id.iv_search:
                scanFragment.callSearch();

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
                    Toast.makeText(launchActivity, "Please login to view the profile", Toast.LENGTH_LONG).show();
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

    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
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
                dismissProgress();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
    }

    public void dismissProgress() {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void setProgressTitle(String msg) {
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.setMessage(msg);
    }

    public boolean isOnline() {
        return networkUtil.isOnline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        languagepref.registerOnSharedPreferenceChangeListener(this);
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if (notify_count > 0) {
            tv_badge.setVisibility(View.VISIBLE);
        } else {
            tv_badge.setVisibility(View.INVISIBLE);
        }
        nav_Menu.findItem(R.id.nav_logout).setVisible(UserUtils.isLogin());
        if (UserUtils.isLogin()) {
            tv_login.setText("Logout");
            tv_email.setText(NoQueueBaseActivity.getActualMail());
            tv_name.setText(NoQueueBaseActivity.getUserName());
        } else {
            tv_login.setText("Login");
            tv_email.setText("Please login");
            tv_name.setText("Guest User");
        }
        tv_login.setVisibility(View.GONE);
        Picasso.with(this).load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(NoQueueBaseActivity.getUserProfileUri())) {
                Picasso.with(this)
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, NoQueueBaseActivity.getUserProfileUri()))
                        .placeholder(ImageUtils.getProfilePlaceholder(this))
                        .error(ImageUtils.getProfileErrorPlaceholder(this))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        if (null != fcmNotificationReceiver)
            fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());

        ReviewData reviewData = ReviewDB.getPendingReview();
        // shown only one time if the review is canceled
        if (StringUtils.isNotBlank(reviewData.getCodeQR()) && !isReviewShown()) {
            callReviewActivity(reviewData.getCodeQR(), reviewData.getToken());
        }

        ReviewData reviewDataSkip = ReviewDB.getSkippedQueue();
        // shown only one time if it is skipped
        if (StringUtils.isNotBlank(reviewDataSkip.getCodeQR())) {
            ReviewDB.deleteReview(reviewData.getCodeQR(), reviewData.getToken());
            Toast.makeText(launchActivity, "You were Skip", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
//        if(null != fcmNotificationReceiver)
//            fcmNotificationReceiver.unregister(this);
        super.onPause();
        languagepref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != fcmNotificationReceiver)
            fcmNotificationReceiver.unregister(this);
    }

    @Subscribe
    public void onEvent(NetworkStateChanged networkStateChanged) {
        if (networkStateChanged.isInternetConnected()) {
            //Toast.makeText(this,"network available",Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this,"no network available",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 3000) {
            backPressToast = Toast.makeText(launchActivity, getString(R.string.exit_app), Toast.LENGTH_LONG);
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

    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.GONE);
    }

    public String getDeviceID() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(APP_PREF, MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");
    }

    private void callReviewActivity(String codeQR, String token) {
        JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR, token);
        if (null == jtk)
            jtk = TokenAndQueueDB.getHistoryQueueObject(codeQR, token);
        if (null != jtk) {
            Intent in = new Intent(launchActivity, ReviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object", jtk);
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

    private void callSkipScreen(String codeQR, String token, String quserID) {
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
            cv.put(DatabaseTable.Review.Q_USER_ID, quserID);
            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
            cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
            cv.put(DatabaseTable.Review.KEY_GOTO, "");
            ReviewDB.insert(cv);
        }
        Toast.makeText(launchActivity, "You were Skip", Toast.LENGTH_LONG).show();
        Bundle b = new Bundle();
        b.putString(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
        b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
        b.putBoolean("isCategoryData", false);
        //   JoinFragment jf = new JoinFragment();
        //   jf.setArguments(b);
        // remove previous screens
        List<Fragment> currentTabFragments = null;//fragmentsStack.get(getCurrentSelectedTabTag());
        if (null != currentTabFragments && currentTabFragments.size() > 1) {
            int size = currentTabFragments.size();
            // clear the stack till first screen of current tab
            currentTabFragments.subList(1, size).clear();
        }
        //  NoQueueBaseFragment.replaceFragmentWithBackStack(this, R.id.frame_layout, jf, TAG, currentSelectedTabTag);
    }

    private void updateNotification(Intent intent, String codeQR, boolean isReview) {
        String current_serving = intent.getStringExtra(Constants.CurrentlyServing);
        String go_to = intent.getStringExtra(Constants.GoTo_Counter);
        String fcm_type = intent.getStringExtra(Constants.FCM_TYPE);
        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
            if (null != jtk) {
                //update DB & after join screen
                jtk.setServingNumber(Integer.parseInt(current_serving));
                /*
                 * Save codeQR of goto & show it in after join screen on app
                 * Review DB for review key && current serving == token no.
                 */
                if (Integer.parseInt(current_serving) == jtk.getToken() && isReview) {
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
                        cv.put(DatabaseTable.Review.Q_USER_ID, jtk.getQueueUserId());
                        cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                        cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                        cv.put(DatabaseTable.Review.KEY_GOTO, go_to);
                        ReviewDB.insert(cv);
                    }
                }

                if (jtk.isTokenExpired() && jsonTokenAndQueueArrayList.size() == 1) {
                    //un subscribe the topic
                    //TODO @chandra write logic for unsubscribe
                    NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
                }
                TokenAndQueueDB.updateCurrentListQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                if (activityCommunicator != null) {
                    boolean isUpdated = activityCommunicator.updateUI(codeQR, jtk, go_to);

                    if (isUpdated) {
                        ReviewData reviewData = ReviewDB.getValue(codeQR, current_serving);
                        if (null != reviewData) {
                            if (!reviewData.getIsBuzzerShow().equals("1")) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                                ReviewDB.updateReviewRecord(codeQR, current_serving, cv);
                                Intent blinker = new Intent(this, BlinkerActivity.class);
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
                            cv.put(DatabaseTable.Review.Q_USER_ID, jtk.getQueueUserId());
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "1");
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                            cv.put(DatabaseTable.Review.KEY_GOTO, "");
                            ReviewDB.insert(cv);
                            Intent blinker = new Intent(this, BlinkerActivity.class);
                            startActivity(blinker);
                        }
                    }
                }
                try {
                    // In case of order update the order status
                    if (fcm_type.equalsIgnoreCase(FCMTypeEnum.O.name()) && Integer.parseInt(current_serving) == jtk.getToken()) {
                        jtk.setPurchaseOrderState(PurchaseOrderStateEnum.valueOf(intent.getStringExtra(Constants.ORDER_STATE)));
                        TokenAndQueueDB.updateCurrentListOrderObject(codeQR, intent.getStringExtra(Constants.ORDER_STATE), String.valueOf(jtk.getToken()));
                    }
                    scanFragment.updateListFromNotification(jtk, go_to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "codeQR=" + codeQR + " current_serving=" + current_serving + " goTo=" + go_to);
            }
        }
    }

    @Override
    public void appBlacklistError() {
        ShowAlertInformation.showThemePlayStoreDialog(launchActivity, getString(R.string.playstore_title), getString(R.string.playstore_msg), false);
    }

    @Override
    public void appBlacklistResponse() {
        if (isOnline() && !BUILD_TYPE.equals("debug")) {
            //TODO(hth) This can be replaced with version received when looking for blacklist
            new VersionCheckAsync(this).execute();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_invite: {
                Intent in = new Intent(this, InviteActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_share:
                AppUtilities.shareTheApp(launchActivity);
                break;
            case R.id.nav_legal:
                if (LaunchActivity.getLaunchActivity().isOnline()) {
                    Intent in = new Intent(this, WebViewActivity.class);
                    in.putExtra("url", Constants.URL_ABOUT_US);
                    startActivity(in);
                } else {
                    ShowAlertInformation.showNetworkDialog(this);
                }
                break;
            case R.id.nav_medical: {
                Intent in = new Intent(launchActivity, MedicalHistoryActivity.class);
                startActivity(in);
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    AppUtilities.exportDatabase(this);
                break;
            }
            case R.id.nav_app_setting: {
                Intent in = new Intent(launchActivity, SettingsActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_transaction:
                Toast.makeText(launchActivity, "Coming soon... ", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_change_language:
                showChangeLangDialog();
                break;
            case R.id.nav_rate_app:
                AppUtilities.openPlayStore(launchActivity);
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(launchActivity)
                        .setTitle(getString(R.string.logout))
                        .setMessage(getString(R.string.logout_msg))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NoQueueBaseActivity.clearPreferences();
                                Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("pref_language")) {
            ((MyApplication) getApplication()).setLocale();
            this.recreate();
        }
    }


    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_language, null);
        dialogBuilder.setView(dialogView);

        final LinearLayout ll_hindi = dialogView.findViewById(R.id.ll_hindi);
        final LinearLayout ll_english = dialogView.findViewById(R.id.ll_english);
        final RadioButton rb_hi = dialogView.findViewById(R.id.rb_hi);
        final RadioButton rb_en = dialogView.findViewById(R.id.rb_en);

        if (language.equals("hi")) {
            rb_hi.setChecked(true);
            rb_en.setChecked(false);
        } else {
            rb_en.setChecked(true);
            rb_hi.setChecked(false);
        }
        final AlertDialog b = dialogBuilder.create();
        ll_hindi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.changeLanguage("hi");
                b.dismiss();
            }
        });
        ll_english.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.changeLanguage("en");
                b.dismiss();
            }
        });
        dialogBuilder.setTitle("");

        b.show();
    }

    public class FcmNotificationReceiver extends BroadcastReceiver {
        public boolean isRegistered;

        public void register(Context context, IntentFilter filter) {
            try {
                if (!isRegistered) {
                    LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
                    Log.e("FCM Reciver: ", "register");
                    isRegistered = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void unregister(Context context) {
            if (isRegistered) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                Log.e("FCM Reciver: ", "unregister");
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

                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    String userStatus = intent.getStringExtra("u");
                    String token = intent.getStringExtra(Constants.TOKEN);
                    String quserID = intent.getStringExtra(Constants.QuserID);
                    /**
                     * Save codeQR of review & show the review screen on app
                     * resume if there is any record in Review DB for review key
                     */
                    if (null == userStatus) {
                        updateNotification(intent, codeQR, false);
                    } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
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
                            cv.put(DatabaseTable.Review.Q_USER_ID, quserID);
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
                    } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
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
                            cv.put(DatabaseTable.Review.Q_USER_ID, quserID);
                            cv.put(DatabaseTable.Review.KEY_BUZZER_SHOWN, "-1");
                            cv.put(DatabaseTable.Review.KEY_SKIP, "-1");
                            cv.put(DatabaseTable.Review.KEY_GOTO, "");
                            ReviewDB.insert(cv);
                        }
                        callSkipScreen(codeQR, token, quserID);
                    }
                } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                    updateNotification(intent, codeQR, true);
                } else {
                    // Toast.makeText(launchActivity, "Notification : " + payload, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public void reCreateDeviceID() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                APP_PREF, Context.MODE_PRIVATE);
        String deviceId = UUID.randomUUID().toString().toUpperCase();
        Log.d(TAG, "Re-Created deviceId=" + deviceId);
        sharedpreferences.edit().putString(NoQueueBaseActivity.XR_DID, deviceId).apply();
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDeviceRegisterPresenter(this);
        deviceModel.register(deviceId, new DeviceToken(NoQueueBaseActivity.getFCMToken()));
    }
    @Override
    public void deviceRegisterError() {

    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        if(deviceRegistered.getRegistered() == 1) {
            Log.e("Device register", "deviceRegister Success");
        }else{
            Log.e("Device register error: ",deviceRegistered.toString());
            Toast.makeText(this,"Device register error: ",Toast.LENGTH_LONG).show();
        }
    }

}
