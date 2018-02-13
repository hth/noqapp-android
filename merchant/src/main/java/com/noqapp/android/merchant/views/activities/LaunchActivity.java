package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.DeviceModel;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
import com.noqapp.android.merchant.network.NoQueueMessagingService;
import com.noqapp.android.merchant.network.VersionCheckAsync;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.NetworkUtil;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.UserUtils;
import com.noqapp.android.merchant.views.fragments.AccessDeniedFragment;
import com.noqapp.android.merchant.views.fragments.LoginFragment;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.views.interfaces.AppBlacklistPresenter;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;

import static com.noqapp.android.merchant.BuildConfig.BUILD_TYPE;

public class LaunchActivity extends AppCompatActivity implements AppBlacklistPresenter {

    public static final String mypref = "shared_pref";
    public static final String XR_DID = "X-R-DID";
    public static final String MyPREFERENCES = "AppPref";
    private static LaunchActivity launchActivity;
    private static SharedPreferences sharedpreferences;
    private static MerchantListFragment merchantListFragment;
    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_USER_EMAIL = "userEmail";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_IS_ACCESS_GRANT = "accessGrant";
    private final String KEY_USER_LEVEL = "userLevel";
    private final String KEY_MERCHANT_COUNTER_NAME = "counterName";
    private final String KEY_USER_ID = "userID";
    private final String KEY_USER_AUTH = "auth";
    private final String KEY_LAST_UPDATE = "last_update";
    public FragmentCommunicator fragmentCommunicator;
    public NetworkUtil networkUtil;
    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    public FrameLayout list_fragment, list_detail_fragment;
    protected TextView tv_toolbar_title;
    private ImageView iv_logout;
    private long lastPress;
    private Toast backpressToast;
    private BroadcastReceiver broadcastReceiver;
    private ImageView actionbarBack;
    private TextView tv_name;

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
        DeviceModel.appBlacklistPresenter = this;
        Log.v("device id check", getDeviceID());
        networkUtil = new NetworkUtil(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tv_toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        actionbarBack = (ImageView) findViewById(R.id.actionbarBack);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        if (new AppUtils().isTablet(this)) {
            list_fragment = (FrameLayout) findViewById(R.id.frame_layout);
            list_detail_fragment = (FrameLayout) findViewById(R.id.list_detail_fragment);
        }
        initProgress();
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });


        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isLoggedIn()) {
            if (isAccessGrant()) {
                if (!new AppUtils().isTablet(getApplicationContext())) {
                    merchantListFragment = new MerchantListFragment();
                    replaceFragmentWithoutBackStack(R.id.frame_layout, merchantListFragment);
                    // setUserName();
                } else {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.6f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp2);
                    merchantListFragment = new MerchantListFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, merchantListFragment);
                    //  fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            } else {

                if (new AppUtils().isTablet(getApplicationContext())) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
                    LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.0f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp0);
                }
                AccessDeniedFragment adf = new AccessDeniedFragment();
                replaceFragmentWithoutBackStack(R.id.frame_layout, adf);
            }
            setUserName();
        } else {
            if (new AppUtils().isTablet(getApplicationContext())) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
                LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.0f);
                list_fragment.setLayoutParams(lp1);
                list_detail_fragment.setLayoutParams(lp0);
            }

            replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    updateListByNotification(intent);
                }
            }
        };

        /* Call to check if the current version of app blacklist or old. */
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            DeviceModel.isSupportedAppVersion(UserUtils.getDeviceId());
        }
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
        sharedpreferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getCounterName() {
        return sharedpreferences.getString(KEY_MERCHANT_COUNTER_NAME, "");
    }

    public void setCounterName(HashMap<String, String> mHashmap) {
        Gson gson = new Gson();
        String strInput = gson.toJson(mHashmap);
        sharedpreferences.edit().putString(KEY_MERCHANT_COUNTER_NAME, strInput).apply();
    }

    public UserLevelEnum getUserLevel() {
        return UserLevelEnum.valueOf(sharedpreferences.getString(KEY_USER_LEVEL, ""));
    }

    public void setUserLevel(String userLevel) {
        sharedpreferences.edit().putString(KEY_USER_LEVEL, userLevel).apply();
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

    public long getLastUpdateTime() {
        return sharedpreferences.getLong(KEY_LAST_UPDATE, System.currentTimeMillis());
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        sharedpreferences.edit().putLong(KEY_LAST_UPDATE, lastUpdateTime).apply();
    }

    public boolean isLoggedIn() {
        return sharedpreferences.getBoolean(IS_LOGIN, false);
    }

    public boolean isAccessGrant() {
        return sharedpreferences.getBoolean(KEY_IS_ACCESS_GRANT, true);
    }

    public void setAccessGrant(boolean isAccessGrant) {
        sharedpreferences.edit().putBoolean(KEY_IS_ACCESS_GRANT, isAccessGrant).apply();
    }

    public void setUserInformation(String userName, String userId, String email, String auth, boolean isLogin) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_AUTH, auth);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.apply();
    }

    public void enableLogout() {
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
                //super.onBackPressed();
                finish();
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
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setProgressTitle(String msg) {
        progressDialog.setMessage(msg);
    }

    public void setUserName() {
        tv_name.setText(WordUtils.initials(getUserName()));
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
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(mypref, Context.MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");
    }

    public void updateListByNotification(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String message = intent.getStringExtra(Constants.MESSAGE);
            String qrcode = intent.getStringExtra(Constants.QRCODE);
            String status = intent.getStringExtra(Constants.STATUS);
            String current_serving = intent.getStringExtra(Constants.CURRENT_SERVING);
            String lastno = intent.getStringExtra(Constants.LASTNO);
            String payload = intent.getStringExtra(Constants.Firebase_Type);
            Log.v("Notify msg background",
                    "Push notification: " + message + "\n" + "qrcode : " + qrcode
                            + "\n" + "status : " + status
                            + "\n" + "current_serving : " + current_serving
                            + "\n" + "lastno : " + lastno
                            + "\n" + "payload : " + payload
            );

            if (fragmentCommunicator != null) {
                fragmentCommunicator.passDataToFragment(qrcode, current_serving, status, lastno, payload);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.MESSAGE) && extras.containsKey(Constants.QRCODE)) {
                updateListByNotification(intent);
            }
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(launchActivity);
        LayoutInflater inflater = LayoutInflater.from(launchActivity);
        builder.setTitle(null);
        View customDialogView = inflater.inflate(R.layout.dialog_logout, null, false);
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Button btn_yes = (Button) customDialogView.findViewById(R.id.btn_yes);
        Button btn_no = (Button) customDialogView.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clearLoginData(false);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public void clearLoginData(boolean showAlert) {
        //un-subscribe the topics
        if (null != merchantListFragment) {
            merchantListFragment.unSubscribeTopics();
        }
        // logout
        sharedpreferences.edit().clear().apply();
        MerchantListFragment.selected_pos = 0;
        if (new AppUtils().isTablet(getApplicationContext())) {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp0);
        }
        //navigate to signup/login
        replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
        if (showAlert) {
            ShowAlertInformation.showThemeDialog(this, getString(R.string.authentication_fail), getString(R.string.authentication_fail_msg));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_SETTING) {
            if (resultCode == RESULT_OK) {
                boolean isDataClear = data.getExtras().getBoolean(Constants.CLEAR_DATA, false);
                if (isDataClear)
                    clearLoginData(true);
            }
        } else if (requestCode == Constants.RESULT_ACQUIRE) {
            if (resultCode == RESULT_OK) {
                boolean isCustomerAcquire = data.getExtras().getBoolean(Constants.CUSTOMER_ACQUIRE, false);
                if (isCustomerAcquire) {
                    // update the acquire no
                    if (fragmentCommunicator != null) {
                        fragmentCommunicator.acquireCustomer((JsonToken) data.getSerializableExtra("data"));
                    }
                }
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
