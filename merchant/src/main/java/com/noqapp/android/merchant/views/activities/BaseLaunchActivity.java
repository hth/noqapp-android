package com.noqapp.android.merchant.views.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.model.types.UserLevelEnum;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;
import com.noqapp.common.utils.NetworkUtil;

import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseLaunchActivity extends AppCompatActivity {
    public static DatabaseHelper dbHandler;
    public static final String mypref = "shared_pref";
    public static final String XR_DID = "X-R-DID";
    public static final String MyPREFERENCES = "AppPref";
    protected static SharedPreferences sharedpreferences;
    protected static MerchantListFragment merchantListFragment;
    protected final String IS_LOGIN = "IsLoggedIn";
    protected final String KEY_USER_EMAIL = "userEmail";
    protected final String KEY_USER_NAME = "userName";
    protected final String KEY_IS_ACCESS_GRANT = "accessGrant";
    protected final String KEY_USER_LEVEL = "userLevel";
    protected final String KEY_MERCHANT_COUNTER_NAME = "counterName";
    protected final String KEY_USER_ID = "userID";
    protected final String KEY_USER_LIST = "userList";
    protected final String KEY_USER_AUTH = "auth";
    protected final String KEY_LAST_UPDATE = "last_update";
    protected TextView tv_name;
    public FragmentCommunicator fragmentCommunicator;
    public ProgressDialog progressDialog;
    protected long lastPress;
    protected Toast backpressToast;
    public NetworkUtil networkUtil;
    protected BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    updateListByNotification(intent);
                }
            }
        };

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

    public void setUserList(ArrayList<String> userList) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        editor.putString(KEY_USER_LIST, json);
        editor.commit();
    }

    public ArrayList<String> getUserList() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString(KEY_USER_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = gson.fromJson(json, type);
        return arrayList == null ? new ArrayList<String>() : arrayList;
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

    public String getDeviceID() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(mypref, Context.MODE_PRIVATE);
        return sharedpreferences.getString(XR_DID, "");
    }

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    public void replaceFragmentWithBackStack(int container, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment, tag).addToBackStack(tag).commit();
    }

    protected void initProgress() {
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
}
