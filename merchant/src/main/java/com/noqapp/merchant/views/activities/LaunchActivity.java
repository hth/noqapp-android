package com.noqapp.merchant.views.activities;

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

import com.google.firebase.messaging.FirebaseMessaging;
import com.noqapp.merchant.R;
import com.noqapp.merchant.helper.NetworkHelper;
import com.noqapp.merchant.model.types.QueueStatusEnum;
import com.noqapp.merchant.network.NOQueueMessagingService;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.utils.AppUtils;
import com.noqapp.merchant.utils.Constants;
import com.noqapp.merchant.views.fragments.LoginFragment;
import com.noqapp.merchant.views.fragments.MerchantListFragment;

import java.util.UUID;

public class LaunchActivity extends AppCompatActivity {
    public static final String mypref="shared_pref";
    public static String XR_DID ="X-R-DID";
    private static LaunchActivity launchActivity;
    protected TextView tv_toolbar_title;
    public NetworkHelper networkHelper;
    public static final String MyPREFERENCES = "AppPref";
    private static SharedPreferences sharedpreferences;
    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_USER_ID = "userID";
    private final String KEY_USER_AUTH = "auth";
    private ImageView iv_logout;
    private long lastPress;
    private Toast backpressToast;
    public ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private static MerchantListFragment merchantListFragment;
    public Toolbar toolbar;
    private ImageView actionbarBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!new AppUtils().isTablet(getApplicationContext()))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launchActivity = this;
        Log.v("device id check",getDeviceID());
        networkHelper = new NetworkHelper(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        actionbarBack=(ImageView) findViewById(R.id.actionbarBack);
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
                                unSubscribeTopics();
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
                    Log.v("notification response",
                            "Push notification: " + message + "\n" + "qrcode : " + qrcode
                                    + "\n" + "status : " + status
                                    + "\n" + "current_serving : " + current_serving
                                    + "\n" + "lastno : " + lastno
                    );

                    for (int i = 0; i < MerchantListFragment.topics.size(); i++) {
                        JsonTopic jt = MerchantListFragment.topics.get(i);
                        if (jt.getCodeQR().equalsIgnoreCase(qrcode)) {
                            jt.setServingNumber(Integer.parseInt(current_serving));
                            jt.setQueueStatus(QueueStatusEnum.valueOf(status));
                            jt.setToken(Integer.parseInt(lastno));
                            MerchantListFragment.topics.set(i, jt);
                            if (null != merchantListFragment.adapter)
                                merchantListFragment.adapter.notifyDataSetChanged();
                            if (null != merchantListFragment.merchantViewPagerFragment)
                                merchantListFragment.merchantViewPagerFragment.adapter.notifyDataSetChanged();
                            break;
                        }
                    }
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

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    public boolean isOnline() {
        return networkHelper.isOnline();
    }

    public static SharedPreferences getSharePreferance() {
        return sharedpreferences;
    }

    public String getUserName() {
        return sharedpreferences.getString(KEY_USER_NAME, "");
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

    public void setUserName(String name) {
        sharedpreferences.edit().putString(KEY_USER_NAME, name);
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


    private void restartApp() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
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

    private void unSubscribeTopics() {
        if (null != MerchantListFragment.topics && MerchantListFragment.topics.size() > 0) {
            for (int i = 0; i < MerchantListFragment.topics.size(); i++) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(MerchantListFragment.topics.get(i).getTopic());
                FirebaseMessaging.getInstance().unsubscribeFromTopic(MerchantListFragment.topics.get(i).getTopic() + "_M");
            }
        }
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

    public void enableDisableBack(boolean isShown){
        actionbarBack.setVisibility(isShown?View.VISIBLE:View.INVISIBLE);
    }

    public String getDeviceID(){
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                mypref, Context.MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");

    }
}
