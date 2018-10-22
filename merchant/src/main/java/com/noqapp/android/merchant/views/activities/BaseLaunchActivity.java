package com.noqapp.android.merchant.views.activities;

import static com.noqapp.android.merchant.BuildConfig.BUILD_TYPE;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.NavigationBean;
import com.noqapp.android.common.beans.medical.JsonMedicalMedicine;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.APIConstant;
import com.noqapp.android.merchant.model.DeviceModel;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.network.VersionCheckAsync;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.views.adapters.NavigationDrawerAdapter;
import com.noqapp.android.merchant.views.fragments.AccessDeniedFragment;
import com.noqapp.android.merchant.views.fragments.LoginFragment;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.views.interfaces.AppBlacklistPresenter;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.apache.commons.lang3.text.WordUtils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseLaunchActivity extends AppCompatActivity implements AppBlacklistPresenter, SharedPreferences.OnSharedPreferenceChangeListener {
    public static DatabaseHelper dbHandler;
    private static SharedPreferences sharedpreferences;

    public static void setMerchantListFragment(MerchantListFragment merchantListFragment) {
        BaseLaunchActivity.merchantListFragment = merchantListFragment;
    }

    protected DeviceModel deviceModel;
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
    protected final String KEY_SUGGESTION = "suggestions";
    protected final String KEY_MEDICINES = "medicines";
    protected final String KEY_COUNTER_NAME_LIST = "counterNames";
    protected final String KEY_USER_PROFILE = "userProfile";
    private static final String FCM_TOKEN = "fcmToken";
    protected TextView tv_name;
    public FragmentCommunicator fragmentCommunicator;
    public ProgressDialog progressDialog;
    protected long lastPress;
    protected Toast backpressToast;
    public NetworkUtil networkUtil;
    protected BroadcastReceiver broadcastReceiver;
    protected static LaunchActivity launchActivity;
    public FrameLayout list_fragment, list_detail_fragment;
    public Toolbar toolbar;
    protected TextView tv_toolbar_title;
    protected ImageView actionbarBack;

    public static Locale locale;
    public static SharedPreferences languagepref;
    public static String language;
    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected NavigationDrawerAdapter drawerAdapter;
    protected ArrayList<NavigationBean> drawerItem = new ArrayList<>();

    public void enableDisableDrawer(boolean isEnable) {
        mDrawerLayout.setDrawerLockMode(isEnable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new AppUtils().isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
        languagepref = PreferenceManager.getDefaultSharedPreferences(this);
        languagepref.registerOnSharedPreferenceChangeListener(this);
        language = languagepref.getString(
                "pref_language", "");


        if (null != getIntent().getExtras()) {
            setFCMToken(getIntent().getStringExtra("fcmToken"));
            setDeviceID(getIntent().getStringExtra("deviceId"));
        }

        deviceModel = new DeviceModel();
        deviceModel.setAppBlacklistPresenter(this);

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

    protected void initDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        drawerItem.clear();
        if (isLoggedIn()) {
            updateMenuList(getUserLevel() == UserLevelEnum.S_MANAGER);
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPosition = drawerAdapter.getData().get(position).getIcon();
                switch (selectedPosition) {
                    case R.drawable.pie_chart:
                        if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
                            Intent in1 = new Intent(launchActivity, ChartListActivity.class);
                            in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
                            startActivity(in1);
                        } else {
                            Toast.makeText(launchActivity, "No queue available", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.drawable.profile_red:
                        Intent in3 = new Intent(launchActivity, ManagerProfileActivity.class);
                        startActivity(in3);
                        break;
                    case R.mipmap.logout:
                        showLogoutDialog();
                        break;
                    case R.drawable.ic_menu_share:
                        AppUtils.shareTheApp(launchActivity);
                        break;
                    case R.drawable.ic_star:
                        AppUtils.openPlayStore(launchActivity);
                        break;
                    case R.drawable.language:
                        showChangeLangDialog();
                        break;
                    case R.drawable.legal: {
                        Intent in = new Intent(launchActivity, PrivacyActivity.class);
                        startActivity(in);
                        break;
                    }
                    default:
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        actionbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (isLoggedIn()) {
            if (isAccessGrant()) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (new AppUtils().isTablet(getApplicationContext())) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.3f);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.7f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp2);
                    merchantListFragment = new MerchantListFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, merchantListFragment);
                    //  fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    merchantListFragment = new MerchantListFragment();
                    replaceFragmentWithoutBackStack(R.id.frame_layout, merchantListFragment);
                    // setUserName();
                }
            } else {
                if (new AppUtils().isTablet(getApplicationContext())) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
                    LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 0.0f);
                    list_fragment.setLayoutParams(lp1);
                    list_detail_fragment.setLayoutParams(lp0);
                }
                AccessDeniedFragment adf = new AccessDeniedFragment();
                Bundle b = new Bundle();
                b.putString("errorMsg", getString(R.string.error_access_denied));
                adf.setArguments(b);
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
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
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
        String json = new Gson().toJson(userList);
        editor.putString(KEY_USER_LIST, json);
        editor.apply();
    }

    public ArrayList<String> getUserList() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = sharedPrefs.getString(KEY_USER_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = new Gson().fromJson(json, type);
        return arrayList == null ? new ArrayList<String>() : arrayList;
    }

    public String getCounterName() {
        return sharedpreferences.getString(KEY_MERCHANT_COUNTER_NAME, "");
    }

    public void setCounterName(HashMap<String, String> mHashmap) {
        String strInput = new Gson().toJson(mHashmap);
        sharedpreferences.edit().putString(KEY_MERCHANT_COUNTER_NAME, strInput).apply();
    }

    public String getSuggestions() {
        return sharedpreferences.getString(KEY_SUGGESTION, null);
    }

    public void setSuggestions(Map<String, List<String>> map) {
        String strInput = new Gson().toJson(map);
        sharedpreferences.edit().putString(KEY_SUGGESTION, strInput).apply();
    }

    public void setFavouriteMedicines(List<JsonMedicalMedicine> jsonMedicalMedicines) {
        Gson gson = new Gson();
        String json = gson.toJson(jsonMedicalMedicines);
        sharedpreferences.edit().putString(KEY_MEDICINES, json).apply();
    }

    public List<JsonMedicalMedicine> getFavouriteMedicines() {
        Type type = new TypeToken<List<JsonMedicalMedicine>>() {
        }.getType();
        String listData = sharedpreferences.getString(KEY_MEDICINES, null);
        List<JsonMedicalMedicine> jsonMedicalMedicines = new Gson().fromJson(listData, type);
        return jsonMedicalMedicines;
    }

    public ArrayList<String> getCounterNames() {
        //Retrieve the values
        String jsonText = sharedpreferences.getString(KEY_COUNTER_NAME_LIST, null);
        ArrayList<String> nameList = new Gson().fromJson(jsonText, ArrayList.class);
        return null != nameList ? nameList : new ArrayList<String>();
    }

    public void setCounterNames(ArrayList<String> mHashmap) {
        String strInput = new Gson().toJson(mHashmap);
        sharedpreferences.edit().putString(KEY_COUNTER_NAME_LIST, strInput).apply();
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

    public static String getFCMToken() {
        return sharedpreferences.getString(FCM_TOKEN, "");
    }

    public static void setFCMToken(String fcmtoken) {
        sharedpreferences.edit().putString(FCM_TOKEN, fcmtoken).apply();
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

    public void setUserProfile(JsonProfile jsonProfile) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String json = new Gson().toJson(jsonProfile);
        editor.putString(KEY_USER_PROFILE, json);
        editor.apply();
    }

    public JsonProfile getUserProfile() {
        String json = sharedpreferences.getString(KEY_USER_PROFILE, "");
        return new Gson().fromJson(json, JsonProfile.class);

    }

    public static String getDeviceID() {
        return sharedpreferences.getString(APIConstant.Key.XR_DID, "");
    }

    private static void setDeviceID(String deviceId) {
        sharedpreferences.edit().putString(APIConstant.Key.XR_DID, deviceId).apply();
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
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void appBlacklistError() {
        ShowAlertInformation.showThemePlayStoreDialog(launchActivity, getString(R.string.playstore_title), getString(R.string.playstore_msg), false);
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
    public void appBlacklistResponse() {
        if (isOnline() && !BUILD_TYPE.equals("debug")) {
            //TODO(hth) This can be replaced with version received when looking for blacklist
            if (null != launchActivity) {
                new VersionCheckAsync(launchActivity).execute();
            }
        }
    }

    @Override
    public void authenticationFailure() {
        AppUtils.authenticationProcessing();
    }

    public static LaunchActivity getLaunchActivity() {
        return launchActivity;
    }

    public void clearLoginData(boolean showAlert) {
        //un-subscribe the topics
        if (null != merchantListFragment) {
            merchantListFragment.unSubscribeTopics();
        }
        MerchantListFragment.selected_pos = 0;
        if (new AppUtils().isTablet(getApplicationContext())) {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp0);
            if (null != merchantListFragment) {
                merchantListFragment.clearData();
                merchantListFragment = null;
            }
        }
        // logout
        clearPreferences();
        //navigate to signup/login
        replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
        if (showAlert) {
            ShowAlertInformation.showThemeDialog(this, getString(R.string.authentication_fail), getString(R.string.authentication_fail_msg));
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

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
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

    protected void showLogoutDialog() {
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
                Answers.getInstance().logCustom(new CustomEvent("Logout")
                        .putCustomAttribute("Success", "true"));
            }
        });
        mAlertDialog.show();
    }

    public void setActionBarTitle(String title) {
        tv_toolbar_title.setText(title);
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

    public void showChangeLangDialog() {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
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

        ll_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.changeLanguage("hi");
            }
        });
        ll_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.changeLanguage("en");
            }
        });
        dialogBuilder.setTitle("");
        android.app.AlertDialog b = dialogBuilder.create();
        b.show();
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

    public void updateMenuList(boolean showChart) {
        drawerItem.clear();
        drawerItem.add(new NavigationBean(R.drawable.profile_red, "Profile"));
        drawerItem.add(new NavigationBean(R.drawable.legal, getString(R.string.legal)));
        drawerItem.add(new NavigationBean(R.drawable.ic_menu_share, "Share the app"));
        drawerItem.add(new NavigationBean(R.drawable.ic_star, "Rate the app"));
        drawerItem.add(new NavigationBean(R.drawable.language, "Change language"));
        drawerItem.add(new NavigationBean(R.mipmap.logout, "Logout"));
        if (showChart)
            drawerItem.add(0, new NavigationBean(R.drawable.pie_chart, "Charts"));
        drawerAdapter = new NavigationDrawerAdapter(this, drawerItem);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ((TextView) findViewById(R.id.tv_version)).setText(
                BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")
                        ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                        : getString(R.string.version_no, "Not for release"));
    }

    public static void clearPreferences() {
        // Clear all data except DID & FCM Token
        String did = getDeviceID();
        String fcmToken = getFCMToken();
        sharedpreferences.edit().clear().apply();
        setDeviceID(did);
        setFCMToken(fcmToken);
    }

}
