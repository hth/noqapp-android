package com.noqapp.android.merchant.views.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonLatestAppVersion;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.MobileSystemErrorCodeEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.pojos.MenuDrawer;
import com.noqapp.android.common.utils.NetworkUtil;
import com.noqapp.android.common.views.activities.AppUpdateActivity;
import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.APIConstant;
import com.noqapp.android.merchant.model.DeviceApiCalls;
import com.noqapp.android.merchant.model.database.DatabaseHelper;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.presenter.beans.JsonToken;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.ShowAlertInformation;
import com.noqapp.android.merchant.utils.ShowCustomDialog;
import com.noqapp.android.merchant.views.adapters.DrawerExpandableListAdapter;
import com.noqapp.android.merchant.views.fragments.AccessDeniedFragment;
import com.noqapp.android.merchant.views.fragments.LoginFragment;
import com.noqapp.android.merchant.views.fragments.MerchantListFragment;
import com.noqapp.android.merchant.views.interfaces.AppBlacklistPresenter;
import com.noqapp.android.merchant.views.interfaces.FragmentCommunicator;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import org.apache.commons.text.WordUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseLaunchActivity extends AppCompatActivity implements AppBlacklistPresenter,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static DatabaseHelper dbHandler;
    private static SharedPreferences sharedpreferences;
    protected List<MenuDrawer> menuDrawerItems = new ArrayList<>();

    public static void setMerchantListFragment(MerchantListFragment merchantListFragment) {
        BaseLaunchActivity.merchantListFragment = merchantListFragment;
    }

    protected DeviceApiCalls deviceApiCalls;
    public static MerchantListFragment merchantListFragment;
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
    protected final String KEY_SUGGESTION_PREF = "suggestionsPrefs";
    protected final String KEY_SUGGESTION_PRODUCT_PREF = "suggestionsProductsPrefs";
    protected final String KEY_INVENTORY_PREF = "inventoryPrefs";
    protected final String KEY_COUNTER_NAME_LIST = "counterNames";
    protected final String KEY_USER_PROFILE = "userProfile";
    protected final String KEY_USER_PROFESSIONAL_PROFILE = "userProfessionalProfile";
    private static final String FCM_TOKEN = "fcmToken";
    protected TextView tv_name;
    public FragmentCommunicator fragmentCommunicator;
    protected long lastPress;
    protected Toast backpressToast;
    public NetworkUtil networkUtil;
    protected FcmNotificationReceiver fcmNotificationReceiver;
    protected static LaunchActivity launchActivity;
    public FrameLayout list_fragment, list_detail_fragment;
    public Toolbar toolbar;
    protected TextView tv_toolbar_title;
    protected ImageView actionbarBack;

    public static Locale locale;
    public static SharedPreferences languagepref;
    public static String language;
    protected DrawerLayout mDrawerLayout;
    protected ExpandableListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected boolean isInventoryApp = false;
    public static boolean isTablet = false;

    public void enableDisableDrawer(boolean isEnable) {
        mDrawerLayout.setDrawerLockMode(isEnable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppUtils.isTablet(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isTablet = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isTablet = false;
        }
        super.onCreate(savedInstanceState);
        sharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
        languagepref = PreferenceManager.getDefaultSharedPreferences(this);
        languagepref.registerOnSharedPreferenceChangeListener(this);
        language = languagepref.getString("pref_language", "");

        if (null != getIntent().getExtras()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("fcmToken"))) {
                setFCMToken(getIntent().getStringExtra("fcmToken"));
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("deviceId"))) {
                setDeviceID(getIntent().getStringExtra("deviceId"));
            }
        }

        deviceApiCalls = new DeviceApiCalls();
        deviceApiCalls.setAppBlacklistPresenter(this);

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
        ((MyApplication) getApplication()).setLocale(this);
        fcmNotificationReceiver = new FcmNotificationReceiver();
        fcmNotificationReceiver.register(this, new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    protected void initDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        if (isLoggedIn()) {
            updateMenuList(getUserLevel() == UserLevelEnum.S_MANAGER);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        actionbarBack.setOnClickListener(v -> onBackPressed());
        if (isLoggedIn()) {
            if (isAccessGrant()) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (isInventoryApp) {
                    replaceFragmentWithoutBackStack(R.id.frame_layout, getInventoryHome());
                } else {
                    if (isTablet) {
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f);
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
                }
            } else {
                if (isTablet) {
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                    LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
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
            if (isTablet) {
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
                list_fragment.setLayoutParams(lp1);
                list_detail_fragment.setLayoutParams(lp0);
            }
            replaceFragmentWithoutBackStack(R.id.frame_layout, new LoginFragment());
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public Fragment getInventoryHome() {
        return null;
    }

    private void menuClick(int drawable) {
        switch (drawable) {
            case R.drawable.pie_chart:
                if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
                    Intent in1 = new Intent(launchActivity, ChartListActivity.class);
                    in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
                    startActivity(in1);
                } else {
                    new CustomToast().showToast(launchActivity, "No queue available");
                }
                break;
            case R.drawable.profile_red:
                Intent in3 = new Intent(launchActivity, ManagerProfileActivity.class);
                startActivity(in3);
                break;
            case R.drawable.logout:
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
            case R.drawable.ic_notification: {
                Intent in = new Intent(launchActivity, NotificationSettings.class);
                startActivity(in);
                break;
            }
            case R.drawable.ic_reviews:
                if (merchantListFragment.getTopics() != null && merchantListFragment.getTopics().size() > 0) {
                    Intent in1 = new Intent(launchActivity, ReviewListActivity.class);
                    in1.putExtra("jsonTopic", (Serializable) merchantListFragment.getTopics());
                    startActivity(in1);
                } else {
                    new CustomToast().showToast(launchActivity, "No queue available");
                }
                break;
            case R.drawable.case_history:
                callPreference();
                break;
            case R.drawable.pharmacy:
                callPreferredStore();
                break;
            case R.drawable.ic_add:
                callMarqueeSettings();
                break;
            case R.drawable.add_user:
                callAddPatient();
                break;
            case R.drawable.all_patient:
                callAllPatient();
                break;
            case R.drawable.all_history:
                callAllHistory();
                break;
            default:

        }
    }

    public void updateMenuList(boolean showChart) {
        // Fill menu items
        menuDrawerItems.clear();
        menuDrawerItems.add(new MenuDrawer("Profile", true, false, R.drawable.profile_red));
        menuDrawerItems.add(new MenuDrawer("Reviews", true, false, R.drawable.ic_reviews));

        List<MenuDrawer> settingList = new ArrayList<>();
        settingList.add(new MenuDrawer("Share the app", false, false, R.drawable.ic_menu_share));
        settingList.add(new MenuDrawer(getString(R.string.legal), false, false, R.drawable.legal));
        settingList.add(new MenuDrawer("Rate the app", false, false, R.drawable.ic_star));
        settingList.add(new MenuDrawer(getString(R.string.language_setting), false, false, R.drawable.language));
        if (isLoggedIn()) {
            settingList.add(new MenuDrawer(getString(R.string.notification_setting), false, false, R.drawable.ic_notification));
        }
        menuDrawerItems.add(new MenuDrawer("Settings", true, true, R.drawable.settings_square, settingList));
        menuDrawerItems.add(new MenuDrawer("Logout", true, false, R.drawable.logout));
        if (showChart) {
            menuDrawerItems.add(0, new MenuDrawer("Statistics", true, false, R.drawable.pie_chart));
        }

        DrawerExpandableListAdapter expandableListAdapter = new DrawerExpandableListAdapter(this, menuDrawerItems);
        mDrawerList.setAdapter(expandableListAdapter);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        mDrawerList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (menuDrawerItems.get(groupPosition).isGroup()) {
                if (!menuDrawerItems.get(groupPosition).isHasChildren()) {
                    int drawableId = menuDrawerItems.get(groupPosition).getIcon();
                    menuClick(drawableId);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
            return false;
        });

        mDrawerList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (menuDrawerItems.get(groupPosition).getChildList() != null) {
                MenuDrawer model = menuDrawerItems.get(groupPosition).getChildList().get(childPosition);
                int drawableId = model.getIcon();
                menuClick(drawableId);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            return false;
        });
        ((TextView) findViewById(R.id.tv_version)).setText(
                AppUtils.isRelease()
                        ? getString(R.string.version_no, BuildConfig.VERSION_NAME)
                        : getString(R.string.version_no, "Not for release"));
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

    public String getSuggestionsPrefs() {
        return sharedpreferences.getString(KEY_SUGGESTION_PREF, null);
    }

    public String getSuggestionsProductPrefs() {
        return sharedpreferences.getString(KEY_SUGGESTION_PRODUCT_PREF, null);
    }

    public void setSuggestionsPrefs(PreferenceObjects testCaseObjects) {
        String strInput = new Gson().toJson(testCaseObjects);
        sharedpreferences.edit().putString(KEY_SUGGESTION_PREF, strInput).apply();
    }

    public Map<String, List<JsonCheckAsset>> getInventoryPrefs() {
        String storedHashMapString = sharedpreferences.getString(KEY_INVENTORY_PREF, null);
        java.lang.reflect.Type type = new TypeToken<Map<String, List<JsonCheckAsset>>>() {
        }.getType();
        Map<String, List<JsonCheckAsset>> tempList = new Gson().fromJson(storedHashMapString, type);
        return null != tempList ? tempList : new HashMap<>();
    }

    public void setInventoryPrefs(Map<String, List<JsonCheckAsset>> dataList) {
        String strInput = new Gson().toJson(dataList);
        sharedpreferences.edit().putString(KEY_INVENTORY_PREF, strInput).apply();
    }

    public void setSuggestionsProductsPrefs(PreferenceObjects testCaseObjects) {
        String strInput = new Gson().toJson(testCaseObjects);
        sharedpreferences.edit().putString(KEY_SUGGESTION_PRODUCT_PREF, strInput).apply();
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
        sharedpreferences.edit().putString(AppUtils.CURRENCY_SYMBOL, AppUtils.getCurrencySymbol(jsonProfile.getCountryShortName())).apply();
    }

    public JsonProfile getUserProfile() {
        String json = sharedpreferences.getString(KEY_USER_PROFILE, "");
        return new Gson().fromJson(json, JsonProfile.class);

    }

    public void setUserProfessionalProfile(JsonProfessionalProfilePersonal jsonProfessionalProfile) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String json = new Gson().toJson(jsonProfessionalProfile);
        editor.putString(KEY_USER_PROFESSIONAL_PROFILE, json);
        editor.apply();
    }

    public JsonProfessionalProfilePersonal getUserProfessionalProfile() {
        String json = sharedpreferences.getString(KEY_USER_PROFESSIONAL_PROFILE, "");
        return new Gson().fromJson(json, JsonProfessionalProfilePersonal.class);

    }

    public static String getCurrencySymbol() {
        return sharedpreferences.getString(AppUtils.CURRENCY_SYMBOL, "");
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
            String payload = intent.getStringExtra(Constants.FIREBASE_TYPE);
            Log.v("Notify msg background",
                    "Push notification: " + message + "\n" + "qrcode : " + qrcode
                            + "\n" + "status : " + status
                            + "\n" + "current_serving : " + current_serving
                            + "\n" + "lastno : " + lastno
                            + "\n" + "payload : " + payload
            );

            JsonData jsonData = (JsonData) intent.getSerializableExtra("jsonData");
            if (jsonData instanceof JsonTopicQueueData) {
                Log.e("onReceiveJsonTopicQdata", jsonData.toString());
                if (fragmentCommunicator != null) {
                    fragmentCommunicator.passDataToFragment(qrcode, current_serving, status, lastno, payload);
                }
            } else if (jsonData instanceof JsonClientData) {
                Log.e("onReceiveJsonClientData", jsonData.toString());
            } else if (jsonData instanceof JsonAlertData) {
                Log.e("onReceiveJsonAlertData", jsonData.toString());
            } else if (jsonData instanceof JsonTopicOrderData) {
                Log.e("onReceiveJsonTopicOdata", jsonData.toString());
                if (null != fragmentCommunicator) {
                    fragmentCommunicator.passDataToFragment(qrcode, current_serving, status, lastno, payload);
                }
            } else if (jsonData instanceof JsonClientOrderData) {
                Log.e("JsonClientOrderData", jsonData.toString());
            }
        }
    }

    @Override
    public void appBlacklistError(ErrorEncounteredJson eej) {
        if (null != eej) {
            if (MobileSystemErrorCodeEnum.valueOf(eej.getSystemError()) == MobileSystemErrorCodeEnum.MOBILE_UPGRADE) {
                Intent in = new Intent(launchActivity, AppUpdateActivity.class);
                startActivity(in);
                finish();
            } else {
                new ErrorResponseHandler().processError(this, eej);
            }
        }
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
    public void appBlacklistResponse(JsonLatestAppVersion jsonLatestAppVersion) {
        if (null != jsonLatestAppVersion && !TextUtils.isEmpty(jsonLatestAppVersion.getLatestAppVersion())) {
            if (AppUtils.isRelease()) {
                try {
                    String currentVersion = Constants.appVersion();
                    if (Integer.parseInt(currentVersion.replace(".", "")) < Integer.
                            parseInt(jsonLatestAppVersion.getLatestAppVersion().replace(".", ""))) {
                        ShowAlertInformation.showThemePlayStoreDialog(
                                this,
                                getString(R.string.playstore_update_title),
                                getString(R.string.playstore_update_msg),
                                true);
                    }
                } catch (Exception e) {
                    Log.e(BaseLaunchActivity.class.getSimpleName(), "Compare version check reason=" + e.getLocalizedMessage(), e);
                }
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
        if (isTablet) {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams lp0 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.0f);
            list_fragment.setLayoutParams(lp1);
            list_detail_fragment.setLayoutParams(lp0);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        if (null != merchantListFragment) {
            merchantListFragment.clearData();
            merchantListFragment = null;
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
        mDrawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                backpressToast = new CustomToast().getToast(launchActivity, getString(R.string.exit_the_app));
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
        super.onPause();
    }


    public void enableDisableBack(boolean isShown) {
        actionbarBack.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.MESSAGE) && extras.containsKey(Constants.QRCODE)) {
                updateListByNotification(intent);
            }
        }
    }

    protected void showLogoutDialog() {
        ShowCustomDialog showDialog = new ShowCustomDialog(this);
        showDialog.setDialogClickListener(new ShowCustomDialog.DialogClickListener() {
            @Override
            public void btnPositiveClick() {
                clearLoginData(false);
                Answers.getInstance().logCustom(new CustomEvent("Logout")
                        .putCustomAttribute("Success", "true"));
            }

            @Override
            public void btnNegativeClick() {
                //Do nothing
            }
        });
        showDialog.displayDialog(getString(R.string.title_logout), getString(R.string.msg_logout));
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
        final Dialog dialog = new Dialog(launchActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        final LinearLayout ll_hindi = dialog.findViewById(R.id.ll_hindi);
        final LinearLayout ll_english = dialog.findViewById(R.id.ll_english);
        final RadioButton rb_hi = dialog.findViewById(R.id.rb_hi);
        final RadioButton rb_en = dialog.findViewById(R.id.rb_en);

        if (language.equals("hi")) {
            rb_hi.setChecked(true);
            rb_en.setChecked(false);
        } else {
            rb_en.setChecked(true);
            rb_hi.setChecked(false);
        }
        ll_hindi.setOnClickListener(v -> {
            AppUtils.changeLanguage("hi");
            dialog.dismiss();
        });
        ll_english.setOnClickListener(v -> {
            AppUtils.changeLanguage("en");
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_language")) {
            ((MyApplication) getApplication()).setLocale(this);
            this.recreate();
        }
    }

    public static void clearPreferences() {
        // Clear all data except DID & FCM Token
        String did = getDeviceID();
        String fcmToken = getFCMToken();
        sharedpreferences.edit().clear().apply();
        setDeviceID(did);
        setFCMToken(fcmToken);
    }

    public void callPreference() {

    }

    public void callPreferredStore() {

    }

    public void callMarqueeSettings() {

    }

    public void callAddPatient() {

    }

    public void callAllHistory() {

    }

    public void callAllPatient() {

    }

    public class FcmNotificationReceiver extends BroadcastReceiver {
        public boolean isRegistered;

        public void register(Context context, IntentFilter filter) {
            try {
                if (!isRegistered) {
                    LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
                    Log.e("FCM Receiver: ", "register");
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
                updateListByNotification(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fcmNotificationReceiver) {
            fcmNotificationReceiver.unregister(this);
        }
    }
}
