package com.noqapp.android.client.views.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.model.database.DatabaseHelper;
import com.noqapp.android.client.model.database.utils.NotificationDB;
import com.noqapp.android.client.model.database.utils.ReviewDB;
import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.client.model.types.QueueUserStateEnum;
import com.noqapp.android.client.network.NoQueueMessagingService;
import com.noqapp.android.client.network.VersionCheckAsync;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.NetworkStateChanged;
import com.noqapp.android.client.utils.NetworkUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.fragments.ScanQueueFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.views.interfaces.AppBlacklistPresenter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.noqapp.android.client.BuildConfig.BUILD_TYPE;

public class LaunchActivity extends LocationActivity implements OnClickListener, AppBlacklistPresenter, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    public static DatabaseHelper dbHandler;
    public static String tabHome = "ScanQ";
    public static String tabList = "Queues";
    public static String tabMe = "Me";
    private static LaunchActivity launchActivity;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    // Tabs associated with list of fragments
    public ActivityCommunicator activityCommunicator;


    public static Locale locale;
    public static SharedPreferences languagepref;
    public static String language;
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
    private BroadcastReceiver broadcastReceiver;
    private String currentSelectedTabTag = "";
    private ImageView iv_profile;
    private TextView tv_login, tv_name, tv_email;
    private ScanQueueFragment scanfragment;
    private DrawerLayout drawer;
    private double old_latitute = 0;
    private double old_longitute = 0;

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    public String getCurrentSelectedTabTag() {
        return currentSelectedTabTag;
    }

    // Used in TabListener to keep currentSelectedTabTag actual.
    public void setCurrentSelectedTabTag(String currentSelectedTabTag) {
        this.currentSelectedTabTag = currentSelectedTabTag;
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
        DeviceModel.appBlacklistPresenter = this;
        Log.v("device id check", getDeviceID());
        setReviewShown(false);//Reset the flag when app is killed
        //AppUtilities.exportDatabase(this);
        networkUtil = new NetworkUtil(this);

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
        setCurrentSelectedTabTag(tabHome);
        scanfragment = new ScanQueueFragment();
        replaceFragmentWithoutBackStack(R.id.frame_layout, scanfragment);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout mParent = (LinearLayout) navigationView.getHeaderView(0);
        iv_profile = mParent.findViewById(R.id.iv_profile);
        tv_login = mParent.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        tv_name = mParent.findViewById(R.id.tv_name);
        tv_email = mParent.findViewById(R.id.tv_email);
        final Intent in = new Intent(this, ReviewActivity.class);
        //startActivity(in);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String payload = intent.getStringExtra(Constants.Firebase_Type);
                    String codeQR = intent.getStringExtra(Constants.CodeQR);
                    Log.d(TAG, "payload=" + payload + " codeQR=" + codeQR);

                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        String userStatus = intent.getStringExtra("u");
                        /**
                         * Save codeQR of review & show the review screen on app
                         * resume if there is any record in Review DB for review key
                         */
                        if (null == userStatus) {
                            updateNotification(intent, codeQR, false);
                        } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.S.getName())) {
                            ReviewDB.insert(ReviewDB.KEY_REVIEW, codeQR, codeQR);
                            callReviewActivity(codeQR);
                            // this code is added to close the join & after join screen if the request is processed
                            if (activityCommunicator != null) {
                                activityCommunicator.requestProcessed(codeQR);
                            }
                        } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                            ReviewDB.insert(ReviewDB.KEY_SKIP, codeQR, codeQR);
                            //TODO @CHANDRA implement it for activtiy
                            callSkipScreen(codeQR);
                        }
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        updateNotification(intent, codeQR, true);
                    } else {
                        // Toast.makeText(launchActivity, "Notification : " + payload, Toast.LENGTH_LONG).show();
                    }
                }


            }
        };

        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    public void updateLocationUI() {
        if (null != scanfragment && Double.compare(old_latitute, latitute) != 0) {
            try {
                scanfragment.updateUIwithNewLocation(latitute, longitute, cityName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.QRCODE) && extras.containsKey(Constants.ISREVIEW)) {
                String codeQR = extras.getString(Constants.QRCODE);
                boolean isReview = extras.getBoolean(Constants.ISREVIEW, false);
                if (isReview) {
                    callReviewActivity(codeQR);
                } else {
                    callSkipScreen(codeQR);
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
                scanfragment.callSearch();

                break;
            case R.id.iv_notification:
                Intent in = new Intent(launchActivity, NotificationActivity.class);
                startActivity(in);
                break;
            case R.id.tv_login:
                if (tv_login.getText().equals(getString(R.string.logout))) {
                    new AlertDialog.Builder(launchActivity)
                            .setTitle(getString(R.string.logout))
                            .setMessage(getString(R.string.logout_msg))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // logout
                                    NoQueueBaseActivity.clearPreferences();
                                    //navigate to signup/login
                                    // replaceFragmentWithoutBackStack(getActivity(), R.id.frame_layout, new MeFragment(), TAG);
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
                } else {
                    Intent loginIntent = new Intent(launchActivity, LoginActivity.class);
                    startActivity(loginIntent);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.iv_profile:
                if(UserUtils.isLogin()) {
                    Intent intent = new Intent(launchActivity, UserProfileActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    Toast.makeText(launchActivity,"Please login to view the profile",Toast.LENGTH_LONG).show();
                }
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
                //Remove the AfterJoinFragment screen if having same qr code from tablist
                if (activityCommunicator != null) {
                    activityCommunicator.requestProcessed(intent_qrCode);
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
        if (UserUtils.isLogin()) {
            tv_login.setText("Logout");
            tv_email.setText(UserUtils.getEmail());
            tv_name.setText(NoQueueBaseActivity.getUserName());
        } else {
            tv_login.setText("Login");
            tv_email.setText("guest.user@email.com");
            tv_name.setText("Guest User");
        }

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        //  LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());

        String codeQR = ReviewDB.getValue(ReviewDB.KEY_REVIEW);
        // shown only one time if the review is canceled
        if (StringUtils.isNotBlank(codeQR) && !isReviewShown()) {
            callReviewActivity(codeQR);
        }

        String codeQRSkip = ReviewDB.getValue(ReviewDB.KEY_SKIP);
        // shown only one time if it is skipped
        if (StringUtils.isNotBlank(codeQRSkip)) {
            ReviewDB.insert(ReviewDB.KEY_SKIP, "", "");
            // Toast.makeText(launchActivity, "Skip Screen shown", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
        languagepref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    private void callReviewActivity(String codeQR) {
        JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
        if (null == jtk)
            jtk = TokenAndQueueDB.getHistoryQueueObject(codeQR);
        if (null != jtk) {
            Intent in = new Intent(launchActivity, ReviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("object", jtk);
            in.putExtras(bundle);
            startActivityForResult(in, Constants.requestCodeJoinQActivity);
            NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
        } else {
            ReviewDB.insert(ReviewDB.KEY_REVIEW, "", "");
        }
    }

    private void callSkipScreen(String codeQR) {
        ReviewDB.insert(ReviewDB.KEY_SKIP, "", "");
        Bundle b = new Bundle();
        b.putString(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
        b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_REJOIN, true);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_AUTOJOIN_ELIGIBLE, false);
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
        //
        //  NoQueueBaseFragment.replaceFragmentWithBackStack(this, R.id.frame_layout, jf, TAG, currentSelectedTabTag);
    }

    private void updateNotification(Intent intent, String codeQR, boolean isReview) {
        // Toast.makeText(launchActivity, "Notification payload C: " + payload, Toast.LENGTH_LONG).show();
        String current_serving = intent.getStringExtra(Constants.CurrentlyServing);
        String go_to = intent.getStringExtra(Constants.GoTo_Counter);
        JsonTokenAndQueue jtk = TokenAndQueueDB.getCurrentQueueObject(codeQR);
        //update DB & after join screen
        jtk.setServingNumber(Integer.parseInt(current_serving));
        /*
         * Save codeQR of goto & show it in after join screen on app
         * Review DB for review key && current serving == token no.
         */
        if (Integer.parseInt(current_serving) == jtk.getToken() && isReview) {
            ReviewDB.insert(ReviewDB.KEY_GOTO, codeQR, go_to);
        }

        if (jtk.isTokenExpired()) {
            //un subscribe the topic
            NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
        }
        TokenAndQueueDB.updateJoinQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));

        if (activityCommunicator != null) {
            boolean isUpdated = activityCommunicator.updateUI(codeQR, jtk, go_to);
            if (isUpdated) {
                Intent blinker = new Intent(this, BlinkerActivity.class);
                startActivity(blinker);
            }
        }
        try {
            scanfragment.updateListFromNotification(jtk, go_to);
        } catch (Exception e) {
            e.printStackTrace();
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
            if (null != launchActivity) {
                new VersionCheckAsync(launchActivity).execute();
            }
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
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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
                break;
            }
            case R.id.nav_app_setting: {
                Intent in = new Intent(launchActivity, SettingsActivity.class);
                startActivity(in);
                break;
            }
            case R.id.nav_transaction:
                Toast.makeText(launchActivity, "Comming soon... ", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_change_language:
                showChangeLangDialog();
                break;
            case R.id.nav_rate_app:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
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
            restartActivity();
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_language, null);
        dialogBuilder.setView(dialogView);

        final LinearLayout ll_hindi = (LinearLayout) dialogView.findViewById(R.id.ll_hindi);
        final LinearLayout ll_english = (LinearLayout) dialogView.findViewById(R.id.ll_english);
        final RadioButton rb_hi = (RadioButton) dialogView.findViewById(R.id.rb_hi);
        final RadioButton rb_en = (RadioButton) dialogView.findViewById(R.id.rb_en);

        if (language.equals("hi")) {
            rb_hi.setChecked(true);
            rb_en.setChecked(false);
        } else {
            rb_en.setChecked(true);
            rb_hi.setChecked(false);
        }


        ll_hindi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.changeLanguage("hi");
            }
        });
        ll_english.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.changeLanguage("en");
            }
        });
        dialogBuilder.setTitle("");
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
