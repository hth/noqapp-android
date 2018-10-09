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
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.network.VersionCheckAsync;
import com.noqapp.android.client.presenter.AppBlacklistPresenter;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.beans.ReviewData;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ErrorResponseHandler;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.adapters.NavigationDrawerAdapter;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.fragments.ScanQueueFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.NavigationBean;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.NetworkUtil;

import com.crashlytics.android.answers.Answers;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class LaunchActivity extends LocationActivity implements OnClickListener, DeviceRegisterPresenter, AppBlacklistPresenter, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    private TextView tv_badge;
    private TextView tv_toolbar_title;

    private long lastPress;
    private Toast backPressToast;
    private FcmNotificationReceiver fcmNotificationReceiver;
    private ImageView iv_profile;
    private TextView tv_name, tv_email;
    private ScanQueueFragment scanFragment;
    private DrawerLayout drawer;
    protected ListView mDrawerList;
    private ImageView actionbarBack;
    public static DatabaseHelper dbHandler;
    public static Locale locale;
    public static SharedPreferences languagepref;
    public static String language;
    private static LaunchActivity launchActivity;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    public ActivityCommunicator activityCommunicator;
    protected ArrayList<NavigationBean> drawerItem = new ArrayList<>();
    private NavigationDrawerAdapter drawerAdapter;

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());
        JodaTimeAndroid.init(this);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        setContentView(R.layout.activity_launch);
        tv_badge = findViewById(R.id.tv_badge);
        actionbarBack = findViewById(R.id.actionbarBack);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        ImageView iv_search = findViewById(R.id.iv_search);
        ImageView iv_notification = findViewById(R.id.iv_notification);
        FrameLayout fl_notification = findViewById(R.id.fl_notification);
        launchActivity = this;
//        NoQueueBaseActivity.saveMailAuth("","");
        if (null != getIntent().getExtras()) {
            NoQueueBaseActivity.setFCMToken(getIntent().getStringExtra("fcmToken"));
            NoQueueBaseActivity.setDeviceID(getIntent().getStringExtra("deviceId"));
        }
        Log.v("device id check", getDeviceID());
        setReviewShown(false);//Reset the flag when app is killed
        networkUtil = new NetworkUtil(this);
        fcmNotificationReceiver = new FcmNotificationReceiver();
        fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
        //Language setup
        languagepref = PreferenceManager.getDefaultSharedPreferences(this);
        languagepref.registerOnSharedPreferenceChangeListener(this);
        language = languagepref.getString("pref_language", "");

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
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mDrawerList = findViewById(R.id.drawer_list);
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.nav_header_main, mDrawerList, false);
        mDrawerList.addHeaderView(headerView);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)//when user click on header section
                    return;
                int selectedPosition = drawerAdapter.getData().get(position - 1).getIcon();
                switch (selectedPosition) {
                    case R.drawable.invite: {
                        Intent in = new Intent(LaunchActivity.this, InviteActivity.class);
                        startActivity(in);
                        break;
                    }
                    case R.drawable.merchant_account:
                        if (LaunchActivity.getLaunchActivity().isOnline()) {
                            Intent in = new Intent(LaunchActivity.this, WebViewActivity.class);
                            in.putExtra("url", UserUtils.isLogin() ? Constants.URL_MERCHANT_LOGIN : Constants.URL_MERCHANT_REGISTER);
                            startActivity(in);
                        } else {
                            ShowAlertInformation.showNetworkDialog(LaunchActivity.this);
                        }
                        break;
                    case R.drawable.ic_menu_share:
                        AppUtilities.shareTheApp(launchActivity);
                        break;
                    case R.drawable.legal: {
                        Intent in = new Intent(LaunchActivity.this, PrivacyActivity.class);
                        startActivity(in);
                        break;
                    }
                    case R.drawable.purchase_order: {
                        Intent in = new Intent(LaunchActivity.this, OrderQueueHistoryActivity.class);
                        startActivity(in);
                        break;
                    }
                    case R.drawable.medical_history: {
                        Intent in = new Intent(launchActivity, MedicalHistoryActivity.class);
                        startActivity(in);
                        if (BuildConfig.BUILD_TYPE.equals("debug"))
                            AppUtilities.exportDatabase(LaunchActivity.this);
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
                    case R.drawable.language:
                        showChangeLangDialog();
                        break;
                    case R.drawable.show_in_map:
                        Intent in = new Intent(LaunchActivity.this, ContactUsActivity.class);
                        startActivity(in);
                        break;
                    case R.drawable.ic_star:
                        AppUtilities.openPlayStore(launchActivity);
                        break;
                    case R.drawable.ic_logout:
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
            }
        });
        iv_profile = headerView.findViewById(R.id.iv_profile);
        tv_name = headerView.findViewById(R.id.tv_name);
        tv_email = headerView.findViewById(R.id.tv_email);
        tv_email.setOnClickListener(this);
        iv_profile.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_version)).setText(
                BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")
                        ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                        : getString(R.string.version_no, "Not for release"));
        updateMenuList(UserUtils.isLogin());


        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceModel deviceModel = new DeviceModel();
            deviceModel.setAppBlacklistPresenter(this);
            deviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }
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
                String qid = extras.getString(Constants.QID);
                boolean isReview = extras.getBoolean(Constants.ISREVIEW, false);
                if (isReview) {
                    callReviewActivity(codeQR, token);
                } else {
                    callSkipScreen(codeQR, token, qid);
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
        updateNotificationBadgeCount();
        updateMenuList(UserUtils.isLogin());
        if (UserUtils.isLogin()) {
            tv_email.setText(NoQueueBaseActivity.getActualMail());
            tv_name.setText(NoQueueBaseActivity.getUserName());
        } else {
            tv_email.setText("Please login");
            tv_name.setText("Guest User");
        }
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
        // shown only one time if the queueReview is canceled
        if (StringUtils.isNotBlank(reviewData.getCodeQR()) && !isReviewShown() && !NoQueueBaseActivity.getShowHelper()) {
            callReviewActivity(reviewData.getCodeQR(), reviewData.getToken());
        }

        ReviewData reviewDataSkip = ReviewDB.getSkippedQueue();
        // shown only one time if it is skipped
        if (StringUtils.isNotBlank(reviewDataSkip.getCodeQR())) {
            ReviewDB.deleteReview(reviewData.getCodeQR(), reviewData.getToken());
            Toast.makeText(launchActivity, "You were Skip", Toast.LENGTH_LONG).show();
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
        languagepref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fcmNotificationReceiver)
            fcmNotificationReceiver.unregister(this);
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
        Toast.makeText(launchActivity, "You were Skip", Toast.LENGTH_LONG).show();
        Intent in = new Intent(this, JoinActivity.class);
        in.putExtra(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
        in.putExtra(NoQueueBaseFragment.KEY_FROM_LIST, false);
        in.putExtra(NoQueueBaseActivity.KEY_IS_REJOIN, true);
        in.putExtra("isCategoryData", false);
        startActivity(in);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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

    @Override
    public void authenticationFailure() {

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
                } else if (object instanceof JsonClientOrderData) {
                    Log.e("JsonClientOrderData", ((JsonClientOrderData) object).toString());
                }

                if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                    if (object instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonAlertData) object).getCodeQR(),
                                ((JsonAlertData) object).getBody(),
                                ((JsonAlertData) object).getTitle(),
                                ((JsonAlertData) object).getBusinessType().getName());
                        //Show some meaningful msg to the end user
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, ((JsonAlertData) object).getTitle() + " is " + ((JsonAlertData) object).getBody());
                        updateNotificationBadgeCount();
                    } else if (object instanceof JsonClientData) {
                        String token = String.valueOf(((JsonClientData) object).getToken());
                        String qid = ((JsonClientData) object).getQueueUserId();
                        if (((JsonClientData) object).getQueueUserState().getName().equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                            /*
                             * Save codeQR of queueReview & show the queueReview screen on app
                             * resume if there is any record in Review DB for queueReview key
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
                             * Save codeQR of queueReview & show the queueReview screen on app
                             * resume if there is any record in Review DB for queueReview key
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
                            //TODO @Chandra update the order screen if open
//                            if (activityCommunicator != null) {
//                                activityCommunicator.requestProcessed(codeQR, token);
//                            }
                        }
                    } else if (object instanceof JsonTopicOrderData) {
                        updateNotification(object, codeQR);
                    }
                } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                    if (object instanceof JsonAlertData) {
                        NotificationDB.insertNotification(
                                NotificationDB.KEY_NOTIFY,
                                ((JsonAlertData) object).getCodeQR(),
                                ((JsonAlertData) object).getBody(),
                                ((JsonAlertData) object).getTitle(),
                                ((JsonAlertData) object).getBusinessType().getName());
                        //Show some meaningful msg to the end user
                        ShowAlertInformation.showInfoDisplayDialog(LaunchActivity.this, ((JsonAlertData) object).getTitle() + " is " + ((JsonAlertData) object).getBody());
                        updateNotificationBadgeCount();
                    } else {
                        updateNotification(object, codeQR);
                    }
                } else {
                    Toast.makeText(launchActivity, "UnSupported Notification reached: " + payload, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void updateNotification(Object object, String codeQR) {
        String go_to = "";
        String messageOrigin = "";
        String current_serving = "";
        if (object instanceof JsonTopicQueueData) {
            current_serving = String.valueOf(((JsonTopicQueueData) object).getCurrentlyServing());//intent.getStringExtra(Constants.CurrentlyServing);
            go_to = ((JsonTopicQueueData) object).getGoTo();//intent.getStringExtra(Constants.GoTo_Counter);
            messageOrigin = ((JsonTopicQueueData) object).getMessageOrigin().name();//intent.getStringExtra(Constants.MESSAGE_ORIGIN);
        } else if (object instanceof JsonTopicOrderData) {
            current_serving = String.valueOf(((JsonTopicOrderData) object).getCurrentlyServing());//intent.getStringExtra(Constants.CurrentlyServing);
            go_to = ((JsonTopicOrderData) object).getGoTo();//intent.getStringExtra(Constants.GoTo_Counter);
            messageOrigin = ((JsonTopicOrderData) object).getMessageOrigin().name();//intent.getStringExtra(Constants.MESSAGE_ORIGIN);
        }
        ArrayList<JsonTokenAndQueue> jsonTokenAndQueueArrayList = TokenAndQueueDB.getCurrentQueueObjectList(codeQR);
        for (int i = 0; i < jsonTokenAndQueueArrayList.size(); i++) {
            JsonTokenAndQueue jtk = jsonTokenAndQueueArrayList.get(i);
            if (null != jtk) {
                //update DB & after join screen
                jtk.setServingNumber(Integer.parseInt(current_serving));
                /*
                 * Save codeQR of goto & show it in after join screen on app
                 * Review DB for queueReview key && current serving == token no.
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
                    scanFragment.updateListFromNotification(jtk, go_to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "codeQR=" + codeQR + " current_serving=" + current_serving + " goTo=" + go_to);
            }
        }
    }

    public void reCreateDeviceID() {
        String deviceId = UUID.randomUUID().toString().toUpperCase();
        Log.d(TAG, "Re-Created deviceId=" + deviceId);
        NoQueueBaseActivity.setDeviceID(deviceId);
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDeviceRegisterPresenter(this);
        deviceModel.register(deviceId, new DeviceToken(NoQueueBaseActivity.getFCMToken()));
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
            Toast.makeText(this, "Device register error: ", Toast.LENGTH_LONG).show();
        }
    }

    public void updateMenuList(boolean isLogin) {
        drawerItem.clear();
        drawerItem.add(new NavigationBean(R.drawable.medical_history, getString(R.string.medical_history)));
        drawerItem.add(new NavigationBean(R.drawable.purchase_order, getString(R.string.order_history)));
        drawerItem.add(new NavigationBean(R.drawable.merchant_account, getString(R.string.merchant_account)));
        drawerItem.add(new NavigationBean(R.drawable.ic_menu_share, getString(R.string.share)));
        drawerItem.add(new NavigationBean(R.drawable.invite, getString(R.string.invite)));
        drawerItem.add(new NavigationBean(R.drawable.legal, getString(R.string.legal)));
        drawerItem.add(new NavigationBean(R.drawable.ic_star, getString(R.string.ratetheapp)));
        drawerItem.add(new NavigationBean(R.drawable.language, getString(R.string.language_setting)));
        drawerItem.add(new NavigationBean(R.drawable.show_in_map, getString(R.string.title_activity_contact_us)));
        if (isLogin) {
            drawerItem.add(new NavigationBean(R.drawable.ic_logout, getString(R.string.logout)));
        }
        drawerAdapter = new NavigationDrawerAdapter(this, drawerItem);
        mDrawerList.setAdapter(drawerAdapter);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
