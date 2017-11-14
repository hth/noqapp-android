package com.noqapp.android.client.views.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.model.database.DatabaseHelper;
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
import com.noqapp.android.client.views.interfaces.AppBlacklistPresenter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.noqapp.android.client.BuildConfig.BUILD_TYPE;

public class LaunchActivity extends NoQueueBaseActivity implements OnClickListener, AppBlacklistPresenter {
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
    @BindView(R.id.rl_list)
    protected RelativeLayout rl_list;

    @BindView(R.id.rl_home)
    protected RelativeLayout rl_home;

    @BindView(R.id.rl_me)
    protected RelativeLayout rl_me;

    @BindView(R.id.tv_home)
    protected TextView tv_home;

    @BindView(R.id.tv_list)
    protected TextView tv_list;

    @BindView(R.id.tv_me)
    protected TextView tv_me;

    @BindView(R.id.iv_home)
    protected ImageView iv_home;

    @BindView(R.id.iv_list)
    protected ImageView iv_list;

    @BindView(R.id.iv_me)
    protected ImageView iv_me;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tv_toolbar_title)
    protected TextView tv_toolbar_title;

    @BindView(R.id.actionbarBack)
    protected ImageView actionbarBack;

    private long lastPress;
    private Toast backPressToast;
    private BroadcastReceiver broadcastReceiver;
    private String currentSelectedTabTag = "";

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
        rl_home.setOnClickListener(this);
        rl_list.setOnClickListener(this);
        rl_me.setOnClickListener(this);
        actionbarBack.setOnClickListener(this);
        iv_home.setBackgroundResource(R.mipmap.home_active);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
        initProgress();
        onClick(rl_home);
        final Intent in = new Intent(this, ReviewActivity.class);
        //startActivity(in);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String payload = intent.getStringExtra(Constants.MSG_TYPE_F);
                    String codeQR = intent.getStringExtra(Constants.MSG_TYPE_C);
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
        Fragment fragment;
        resetButtons();
        switch (id) {
            case R.id.rl_home:
                setCurrentSelectedTabTag(tabHome);
                if (null == fragmentsStack.get(tabHome)) {
                    fragment = new ScanQueueFragment();
                    createStackForTab(tabHome);
                    addFragmentToStack(fragment);
                    replaceFragmentWithoutBackStack(R.id.frame_layout, fragment);
                } else {
                    replaceFragmentWithoutBackStack(R.id.frame_layout, getLastFragment());
                }
                iv_home.setBackgroundResource(R.mipmap.home_active);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;
            case R.id.rl_list:
                setCurrentSelectedTabTag(tabList);
                if (null == fragmentsStack.get(tabList)) {
                    fragment = new ListQueueFragment();
                    createStackForTab(tabList);
                    addFragmentToStack(fragment);
                    replaceFragmentWithoutBackStack(R.id.frame_layout, fragment);
                } else {
                    replaceFragmentWithoutBackStack(R.id.frame_layout, getLastFragment());
                }
                iv_list.setBackgroundResource(R.mipmap.list_active);
                tv_list.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;
            case R.id.rl_me:
                setCurrentSelectedTabTag(tabMe);
                if (null == fragmentsStack.get(tabMe)) {
                    fragment = MeFragment.getInstance();
                    createStackForTab(tabMe);
                    addFragmentToStack(fragment);
                    replaceFragmentWithoutBackStack(R.id.frame_layout, fragment);
                } else {
                    replaceFragmentWithoutBackStack(R.id.frame_layout, getLastFragment());
                }
                iv_me.setBackgroundResource(R.mipmap.me_active);
                tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;
            case R.id.actionbarBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
    }

    private void resetButtons() {
        iv_home.setBackgroundResource(R.mipmap.home_inactive);
        iv_list.setBackgroundResource(R.mipmap.list_inactive);
        iv_me.setBackgroundResource(R.mipmap.me_inactive);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_list.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
        tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_unselect));
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
                            currentTabFragments.remove(currentTabFragments.size() - 1);
                            currentTabFragments.remove(currentTabFragments.size() - 1);
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
                            currentTabFragmentsQ.remove(currentTabFragmentsQ.size() - 1);
                            currentTabFragmentsQ.remove(currentTabFragmentsQ.size() - 1);
                        }
                    }
                }
                dismissProgress();
                onClick(rl_list);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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
                onClick(rl_list);
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
                super.onBackPressed();
            }
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
        Intent in = new Intent(launchActivity, ReviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", jtk);
        in.putExtras(bundle);
        startActivityForResult(in, Constants.requestCodeJoinQActivity);
        NoQueueMessagingService.unSubscribeTopics(jtk.getTopic());
    }

    private void callSkipScreen(String codeQR) {
        ReviewDB.insert(ReviewDB.KEY_SKIP, "", "");
        Bundle b = new Bundle();
        b.putString(NoQueueBaseFragment.KEY_CODE_QR, codeQR);
        b.putBoolean(NoQueueBaseFragment.KEY_FROM_LIST, false);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_HISTORY, false);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_REJOIN, true);
        b.putBoolean(NoQueueBaseFragment.KEY_IS_AUTOJOIN_ELIGIBLE, false);
        JoinFragment jf = new JoinFragment();
        jf.setArguments(b);
        NoQueueBaseFragment.replaceFragmentWithBackStack(this, R.id.frame_layout, jf, TAG, currentSelectedTabTag);
    }

    private void updateNotification(Intent intent, String codeQR, boolean isReview) {
        // Toast.makeText(launchActivity, "Notification payload C: " + payload, Toast.LENGTH_LONG).show();
        String current_serving = intent.getStringExtra(Constants.MSG_TYPE_CS);
        String go_to = intent.getStringExtra(Constants.MSG_TYPE_G);
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
            new VersionCheckAsync(launchActivity).execute();
        }
    }
}
