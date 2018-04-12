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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.NetworkUtil;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.fragments.AfterJoinFragment;
import com.noqapp.android.client.views.fragments.JoinFragment;
import com.noqapp.android.client.views.fragments.ListQueueFragment;
import com.noqapp.android.client.views.fragments.LoginFragment;
import com.noqapp.android.client.views.fragments.MeFragment;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;
import com.noqapp.android.client.views.fragments.RegistrationFragment;
import com.noqapp.android.client.views.fragments.ScanQueueFragment;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.views.interfaces.AppBlacklistPresenter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.noqapp.android.client.BuildConfig.BUILD_TYPE;

public class LaunchActivity extends LocationActivity implements OnClickListener, AppBlacklistPresenter ,NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    public static DatabaseHelper dbHandler;
    public static String tabHome = "ScanQ";
    public static String tabList = "Queues";
    public static String tabMe = "Me";
    private static LaunchActivity launchActivity;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    // Tabs associated with list of fragments
    public Map<String, List<Fragment>> fragmentsStack = new HashMap<String, List<Fragment>>();
    public ActivityCommunicator activityCommunicator;
    @BindView(R.id.tv_badge)
    protected TextView tv_badge;


    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;

    @BindView(R.id.iv_notification)
    protected ImageView iv_notification;
    @BindView(R.id.fl_notification)
    protected FrameLayout fl_notification;


    private long lastPress;
    private Toast backPressToast;
    private BroadcastReceiver broadcastReceiver;
    private String currentSelectedTabTag = "";
    private ImageView iv_profile;
    private TextView tv_login,tv_name,tv_email;

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

        actionbarBack.setOnClickListener(this);
        iv_notification.setOnClickListener(this);
        fl_notification.setVisibility(View.VISIBLE);
        actionbarBack.setVisibility(View.GONE);
        initProgress();
        setCurrentSelectedTabTag(tabHome);
        if (null == fragmentsStack.get(tabHome)) {
            Fragment fragment = new ScanQueueFragment();
            createStackForTab(tabHome);
            addFragmentToStack(fragment);
            replaceFragmentWithoutBackStack(R.id.frame_layout, fragment);
        } else {
            replaceFragmentWithoutBackStack(R.id.frame_layout, getLastFragment());
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout mParent = (LinearLayout) navigationView.getHeaderView( 0 );
        iv_profile = mParent.findViewById(R.id.iv_profile);
        tv_login = mParent.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
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
                        } else if (userStatus.equalsIgnoreCase(QueueUserStateEnum.N.getName())) {
                            ReviewDB.insert(ReviewDB.KEY_SKIP, codeQR, codeQR);
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
        iv_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(launchActivity, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void updateLocationUI() {
        Toast.makeText(launchActivity, "Location : " + cityName, Toast.LENGTH_LONG).show();
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
                List<Fragment> currentTabFragments = fragmentsStack.get(tabList);
                if (null != currentTabFragments && currentTabFragments.size() > 1) {
                    int size = currentTabFragments.size();
                    Fragment currentFragment = currentTabFragments.get(size - 1);
                    if (currentFragment.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                        String codeQR = ((AfterJoinFragment) currentFragment).getCodeQR();
                        if (intent_qrCode.equals(codeQR)) {
                            // clear the stack till first screen of tablist
                            currentTabFragments.subList(1, currentTabFragments.size()).clear();
                        }
                    }
                }
                //Remove the AfterJoinFragment screen if having same qr code from Homelist
                List<Fragment> currentTabFragmentsQ = fragmentsStack.get(tabHome);
                if (null != currentTabFragmentsQ && currentTabFragmentsQ.size() > 1) {
                    int size = currentTabFragmentsQ.size();
                    Fragment currentFragment = currentTabFragmentsQ.get(size - 1);
                    if (currentFragment.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                        String codeQR = ((AfterJoinFragment) currentFragment).getCodeQR();
                        if (intent_qrCode.equals(codeQR)) {
                            // clear the stack till first screen of tabHome
                            currentTabFragmentsQ.subList(1, currentTabFragmentsQ.size()).clear();
                        }
                    }
                }
                dismissProgress();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (f instanceof ScanQueueFragment || f instanceof RegistrationFragment || f instanceof LoginFragment) {
            f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        int notify_count = NotificationDB.getNotificationCount();
        tv_badge.setText(String.valueOf(notify_count));
        if(notify_count>0){
            tv_badge.setVisibility(View.VISIBLE);
        }else{
            tv_badge.setVisibility(View.INVISIBLE);
        }
        if(UserUtils.isLogin()){
            tv_login.setText("Logout");
            tv_email.setText(UserUtils.getEmail());
            tv_name.setText(NoQueueBaseActivity.getUserName());
        }else{
            tv_login.setText("Login");
            tv_email.setText("guest.user@email.com");
            tv_name.setText("Guest User");
        }

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));

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
    }

    /**
     * Method for adding list of fragment for tab to our Back Stack
     *
     * @param tabTag The identifier tag for the tab
     */
    public void createStackForTab(String tabTag) {
        List<Fragment> tabFragments = new ArrayList<Fragment>();
        fragmentsStack.put(tabTag, tabFragments);
    }

    /**
     * @param fragment The fragment that will be added to the Back Stack
     */
    public void addFragmentToStack(Fragment fragment) {
        fragmentsStack.get(currentSelectedTabTag).add(fragment);
    }

    /**
     * Used in TabListener for showing last opened screen from selected tab
     *
     * @return The last added fragment of actual tab will be returned
     */
    public Fragment getLastFragment() {
        List<Fragment> fragments = fragmentsStack.get(currentSelectedTabTag);
        return fragments.get(fragments.size() - 1);
    }

    /**
     * Override default behavior of hardware Back button
     * for navigation thru fragments on tab hierarchy
     */
    @Override
    public void onBackPressed() {


        List<Fragment> currentTabFragments = fragmentsStack.get(currentSelectedTabTag);

        if (currentTabFragments.size() > 1) {

            int size = currentTabFragments.size();
            if (size == 4 && (currentSelectedTabTag.equals(tabHome) || currentSelectedTabTag.equals(tabList))) {
                /* This condition is added for the skip screen */
                currentTabFragments.remove(size - 1);
                size = currentTabFragments.size();
            }
            // if it is not first screen then
            // current screen is closed and removed from Back Stack and shown the previous one
            Fragment fragment = currentTabFragments.get(size - 2);
            Fragment currentFragment = currentTabFragments.get(size - 1);
            currentTabFragments.remove(size - 1);


            if (currentFragment.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                currentTabFragments.remove(currentTabFragments.size() - 1);
                fragmentsStack.put(tabList, null);
                //onClick(rl_list);
            } else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        } else {
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
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
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
        JoinFragment jf = new JoinFragment();
        jf.setArguments(b);
        // remove previous screens
        List<Fragment> currentTabFragments = fragmentsStack.get(getCurrentSelectedTabTag());
        if (null != currentTabFragments && currentTabFragments.size() > 1) {
            int size = currentTabFragments.size();
            // clear the stack till first screen of current tab
            currentTabFragments.subList(1, size).clear();
        }
        //
        NoQueueBaseFragment.replaceFragmentWithBackStack(this, R.id.frame_layout, jf, TAG, currentSelectedTabTag);
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

        if(activityCommunicator != null){
            activityCommunicator.updateUI(codeQR,jtk, go_to);
        }
        List<Fragment> currentTabFragments = fragmentsStack.get(currentSelectedTabTag);
        if (null != currentTabFragments && currentTabFragments.size() > 1) {
            int size = currentTabFragments.size();
            Fragment currentfrg = currentTabFragments.get(size - 1);
            if (currentfrg.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                String qcode = ((AfterJoinFragment) currentfrg).getCodeQR();
                if (codeQR.equals(qcode)) {
                    //updating the serving status
                    ((AfterJoinFragment) currentfrg).setObject(jtk, go_to);
                }
            }
        } else if (null != currentTabFragments && currentTabFragments.size() == 1) {
            try {
                int size = currentTabFragments.size();
                Fragment currentFragment = currentTabFragments.get(size - 1);
                if (currentFragment.getClass().getSimpleName().equals(ListQueueFragment.class.getSimpleName())) {
                    ((ListQueueFragment) currentFragment).updateListFromNotification(jtk, go_to);
                }
            } catch (Exception e) {
                e.printStackTrace();
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

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_invite) {
            Intent in = new Intent(this, InviteActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + this.getPackageName());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_legal) {
            if (LaunchActivity.getLaunchActivity().isOnline()) {
                Intent in = new Intent(this, WebViewActivity.class);
                in.putExtra("url", Constants.URL_ABOUT_US);
                startActivity(in);
            } else {
                ShowAlertInformation.showNetworkDialog(this);
            }
        } else if (id == R.id.nav_medical) {
            Intent in = new Intent(launchActivity, MedicalHistoryActivity.class);
            startActivity(in);
        } else if (id == R.id.nav_rate_app) {
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
