package com.noqapp.android.client.views.activities;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.android.client.model.database.DatabaseHelper;
import com.noqapp.android.client.model.database.utils.NoQueueDB;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.NetworkUtil;
import com.noqapp.android.client.views.fragments.AfterJoinFragment;
import com.noqapp.android.client.views.fragments.LoginFragment;
import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.client.network.NOQueueMessagingService;
import com.noqapp.android.client.views.fragments.ListQueueFragment;
import com.noqapp.android.client.views.fragments.MeFragment;
import com.noqapp.android.client.views.fragments.RegistrationFragment;
import com.noqapp.android.client.views.fragments.ScanQueueFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LaunchActivity extends NoQueueBaseActivity implements OnClickListener {
    private static final String TAG = LaunchActivity.class.getSimpleName();

    private static LaunchActivity launchActivity;
    public NetworkUtil networkUtil;
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

    public static DatabaseHelper dbHandler;
    private long lastPress;
    private Toast backpressToast;
    public ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    // Tabs associated with list of fragments
    public Map<String, List<Fragment>> fragmentsStack = new HashMap<String, List<Fragment>>();
    private String currentSelectedTabTag = "";

    public static String tabHome = "ScanQ";
    public static String tabList = "Queues";
    public static String tabMe = "Me";

    // Used in TabListener to keep currentSelectedTabTag actual.
    public void setCurrentSelectedTabTag(String currentSelectedTabTag) {
        this.currentSelectedTabTag = currentSelectedTabTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DatabaseHelper.getsInstance(getApplicationContext());
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        launchActivity = this;
        Log.v("device id check", getDeviceID());

        networkUtil = new NetworkUtil(this);
        rl_home.setOnClickListener(this);
        rl_list.setOnClickListener(this);
        rl_me.setOnClickListener(this);
        actionbarBack.setOnClickListener(this);
        iv_home.setBackgroundResource(R.mipmap.home_select);
        tv_home.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
        initProgress();
        onClick(rl_me);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String payload = intent.getStringExtra("f");
                    String codeQR = intent.getStringExtra("c");
                    Log.d(TAG, "payload=" + payload);

                    if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.P.getName())) {
                        Toast.makeText(launchActivity, "Notification payload P: " + payload, Toast.LENGTH_LONG).show();
                        JsonTokenAndQueue jtk = NoQueueDB.getCurrentQueueObject(codeQR);
                        Intent in = new Intent(launchActivity, ReviewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("object", jtk);
                        in.putExtras(bundle);
                        startActivityForResult(in, Constants.requestCodeJoinQActivity);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(jtk.getTopic());
                        Log.v("object is :", jtk.toString());
                    } else if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.C.getName())) {
                        Toast.makeText(launchActivity, "Notification payload C: " + payload, Toast.LENGTH_LONG).show();
                        String current_serving = intent.getStringExtra("cs");
                        JsonTokenAndQueue jtk = NoQueueDB.getCurrentQueueObject(codeQR);
                        //update DB & after join screen
                        jtk.setServingNumber(Integer.parseInt(current_serving));
                        if (jtk.isTokenExpired()) {
                            //un subscribe the topic
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(jtk.getTopic());
                        }
                        NoQueueDB.updateJoinQueueObject(codeQR, current_serving, String.valueOf(jtk.getToken()));
                        List<Fragment> currentTabFragments = fragmentsStack.get(currentSelectedTabTag);
                        if (null != currentTabFragments && currentTabFragments.size() > 1) {
                            int size = currentTabFragments.size();
                            Fragment currentfrg = currentTabFragments.get(size - 1);
                            if (currentfrg.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                                String qcode = ((AfterJoinFragment) currentfrg).getCodeQR();
                                if (codeQR.equals(qcode)) {
                                    //updating the serving status
                                    ((AfterJoinFragment) currentfrg).setObject(jtk);
                                }
                            }
                        }

                    } else {
                        Toast.makeText(launchActivity, "Notification : " + payload, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("CODEQR")) {
                String codeQR = extras.getString("CODEQR");
                JsonTokenAndQueue jtk = NoQueueDB.getCurrentQueueObject(codeQR);
                Intent in = new Intent(launchActivity, ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object", jtk);
                in.putExtras(bundle);
                startActivityForResult(in, Constants.requestCodeJoinQActivity);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(jtk.getTopic());
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

                iv_home.setBackgroundResource(R.mipmap.home_select);
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
                iv_list.setBackgroundResource(R.mipmap.list_select);
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
                iv_me.setBackgroundResource(R.mipmap.me_select);
                tv_me.setTextColor(ContextCompat.getColor(this, R.color.color_btn_select));
                break;
            case R.id.actionbarBack:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
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
                String intent_qrCode = data.getExtras().getString("CODEQR");
                //Remove the AfterJoinFragment screen if having same qr code
                List<Fragment> currentTabFragments = fragmentsStack.get(tabList);
                if (null != currentTabFragments && currentTabFragments.size() > 1) {
                    int size = currentTabFragments.size();
                    Fragment currentfrg = currentTabFragments.get(size - 1);
                    if (currentfrg.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                        String qcode = ((AfterJoinFragment) currentfrg).getCodeQR();
                        if (intent_qrCode.equals(qcode)) {
                            currentTabFragments.remove(currentTabFragments.size() - 1);
                            currentTabFragments.remove(currentTabFragments.size() - 1);
                        }
                    }
                }
                dismissProgress();
                onClick(rl_list);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NOQueueMessagingService.clearNotifications(getApplicationContext());
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
            // if it is not first screen then
            // current screen is closed and removed from Back Stack and shown the previous one
            int size = currentTabFragments.size();
            Fragment fragment = currentTabFragments.get(size - 2);
            Fragment currentfrg = currentTabFragments.get(size - 1);
            currentTabFragments.remove(size - 1);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
            if (currentfrg.getClass().getSimpleName().equals(AfterJoinFragment.class.getSimpleName())) {
                currentTabFragments.remove(currentTabFragments.size() - 1);
                fragmentsStack.put(tabList, null);
                onClick(rl_list);
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                backpressToast = Toast.makeText(launchActivity, getString(R.string.exit_app), Toast.LENGTH_LONG);
                backpressToast.show();
                lastPress = currentTime;
            } else {
                if (backpressToast != null) backpressToast.cancel();
                super.onBackPressed();
            }
        }
    }

    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    public String getDeviceID() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                SHARED_PREF_SEC, MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");
    }
}
