package com.noqapp.android.merchant.views.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.NetworkUtil;
import com.noqapp.android.merchant.views.fragments.LoginFragment;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;

import org.apache.commons.lang3.StringUtils;

public class LaunchActivity extends AppCompatActivity {

    public static final String mypref = "shared_pref";
    public static final String XR_DID = "X-R-DID";
    public static final String MyPREFERENCES = "AppPref";
    private static LaunchActivity launchActivity;
    private static SharedPreferences sharedpreferences;
    private static MerchantListFragment merchantListFragment;
    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_MERCHANT_COUNTER_NAME = "counterName";
    private final String KEY_USER_ID = "userID";
    private final String KEY_USER_AUTH = "auth";
    public FragmentCommunicator fragmentCommunicator;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    protected TextView tv_toolbar_title;
    private ImageView iv_logout;
    private long lastPress;
    private Toast backpressToast;
    private BroadcastReceiver broadcastReceiver;
    private ImageView actionbarBack;

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    public static SharedPreferences getSharePreferance() {
        return sharedpreferences;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        Log.v("device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initProgress();
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(launchActivity)
                        .setTitle(getString(R.string.title_logout))
                        .setMessage(getString(R.string.msg_logout))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //unsubscribe the topics
                                if (null != merchantListFragment)
                                    merchantListFragment.unSubscribeTopics();
                                // logout
                                sharedpreferences.edit().clear().commit();
                                //navigate to signup/login
                                replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
            }
        });

        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isLoggedIn()) {
            merchantListFragment = new MerchantListFragment();
            replaceFragmentWithoutBackStack(R.id.frame_layout, merchantListFragment);
        } else {
            replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String qrcode = intent.getStringExtra("qrcode");
                    String status = intent.getStringExtra("status");
                    String current_serving = intent.getStringExtra("current_serving");
                    String lastno = intent.getStringExtra("lastno");
                    String payload = intent.getStringExtra("f");
                    Log.v("notification response",
                            "Push notification: " + message + "\n" + "qrcode : " + qrcode
                                    + "\n" + "status : " + status
                                    + "\n" + "current_serving : " + current_serving
                                    + "\n" + "lastno : " + lastno
                                    + "\n" + "payload : " + payload
                    );
                    if (fragmentCommunicator != null)
                        fragmentCommunicator.passDataToFragment(qrcode, current_serving, status, lastno, payload);


                }
            }
        };

    }

    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
    }

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
        enableLogout();
    }

    public void replaceFragmentWithBackStack(int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment, tag).addToBackStack(tag).commit();

    }

    public boolean isOnline() {
        return networkUtil.isOnline();
    }

    public String getUserName() {
        return sharedpreferences.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String name) {
        sharedpreferences.edit().putString(KEY_USER_NAME, name).commit();
    }

    public void setCounterName(String countername) {
        sharedpreferences.edit().putString(KEY_MERCHANT_COUNTER_NAME, countername).commit();
    }
    public String getCounterName() {
        return sharedpreferences.getString(KEY_MERCHANT_COUNTER_NAME, "");
    }
    public String getUSerID() {
        return sharedpreferences.getString(KEY_USER_ID, "");
    }

    public String getAuth() {
        return sharedpreferences.getString(KEY_USER_AUTH, "");
    }

    public String getEmail() {
        return sharedpreferences.getString(KEY_USER_EMAIL, "");
    }

    public boolean isLoggedIn() {
        return sharedpreferences.getBoolean(IS_LOGIN, false);
    }

    public void setSharPreferancename(String userName, String userID, String emailno, String auth, boolean isLogin) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userID);
        editor.putString(KEY_USER_EMAIL, emailno);
        editor.putString(KEY_USER_AUTH, auth);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    private void enableLogout() {

        if (isLoggedIn()) {
            iv_logout.setVisibility(View.VISIBLE);
        } else {
            iv_logout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                backpressToast = Toast.makeText(launchActivity, getString(R.string.exit_the_app), Toast.LENGTH_LONG);
                backpressToast.show();
                lastPress = currentTime;
            } else {
                if (backpressToast != null) backpressToast.cancel();
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
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
        progressDialog.setMessage(msg);

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NoQueueMessagingService.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    public String getDeviceID() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                mypref, Context.MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");

    }
}
