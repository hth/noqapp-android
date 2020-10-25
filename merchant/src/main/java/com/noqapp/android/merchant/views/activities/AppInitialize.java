package com.noqapp.android.merchant.views.activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.common.beans.JsonBusinessCustomerPriority;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.model.types.UserLevelEnum;
import com.noqapp.android.common.utils.FontsOverride;
import com.noqapp.android.merchant.model.APIConstant;
import com.noqapp.android.merchant.presenter.beans.JsonCheckAsset;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.pojos.PreferenceObjects;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by chandra on 5/20/17.
 */
public class AppInitialize extends Application {
    public static SharedPreferences preferences;
    public static final String PREKEY_IS_NOTIFICATION_SOUND_ENABLE = "isNotificationSoundEnable";
    public static final String PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE = "isNotificationReceiveEnable";
    public static final String KEY_USER_NAME = "userName";
    public static final String IS_LOGIN = "isLoggedIn";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String TOKEN_FCM = "tokenFCM";
    public static final String KEY_IS_ACCESS_GRANT = "accessGrant";
    public static final String KEY_USER_LEVEL = "userLevel";
    public static final String KEY_CUSTOMER_PRIORITY = "customerPriority";
    public static final String PRIORITY_ACCESS = "priorityAccess";
    public static final String KEY_MERCHANT_COUNTER_NAME = "counterName";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_LIST = "userList";
    public static final String KEY_USER_AUTH = "auth";
    public static final String KEY_LAST_UPDATE = "last_update";
    public static final String KEY_SUGGESTION_PREF = "suggestionsPrefs";
    public static final String KEY_SUGGESTION_PRODUCT_PREF = "suggestionsProductsPrefs";
    public static final String KEY_INVENTORY_PREF = "inventoryPrefs";
    public static final String KEY_COUNTER_NAME_LIST = "counterNames";
    public static final String KEY_USER_PROFILE = "userProfile";
    public static final String KEY_USER_PROFESSIONAL_PROFILE = "userProfessionalProfile";
    private static final String PREKEY_IS_MSG_ANNOUNCE = "msgAnnouncement";
    private static final String PREKEY_IS_TV_SPLIT_VIEW = "tvSplitView";
    private static final String PREKEY_TV_REFRESH_TIME = "tvRefreshTime";

    public AppInitialize() {
        super();
    }

    /**
     * On application startup, override system default locale to which user set
     * in application preference.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.overrideFont(this, "DEFAULT", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "MONOSPACE", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "SERIF", "fonts/roboto_regular.ttf");
        FontsOverride.overrideFont(this, "SANS_SERIF", "fonts/roboto_regular.ttf");
        setLocale(this);

        preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
    }

    private Locale getLocaleFromPref() {
        Locale locale = Locale.getDefault();
        String language = getPreferences().getString("pref_language", "");
        if (StringUtils.isNotBlank(language)) {
            locale = new Locale(language);
            Locale.setDefault(locale);
        }
        return locale;
    }

    public void setLocale(Context ctx) {
        Locale newLocale = getLocaleFromPref();
        Resources activityRes = ctx.getResources();
        Configuration activityConf = activityRes.getConfiguration();

        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getBaseContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf, applicationRes.getDisplayMetrics());
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public static boolean isNotificationSoundEnable() {
        Log.e("Sound enable", String.valueOf(preferences.getBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, true)));
        return preferences.getBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, true);
    }

    public static void setNotificationSoundEnable(boolean check) {
        preferences.edit().putBoolean(PREKEY_IS_NOTIFICATION_SOUND_ENABLE, check).apply();
    }

    public static boolean isNotificationReceiveEnable() {
        return preferences.getBoolean(PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE, true);
    }

    public static void setNotificationReceiveEnable(boolean check) {
        preferences.edit().putBoolean(PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE, check).apply();
    }

    public static String getDeviceId() {
        if (null != preferences) {
            return preferences.getString(APIConstant.Key.XR_DID, "");
        }
        return null;
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor() {
        return preferences.edit();
    }

    public static String getCounterName() {
        return preferences.getString(KEY_MERCHANT_COUNTER_NAME, "");
    }

    public static void setCounterName(HashMap<String, String> mHashmap) {
        String strInput = new Gson().toJson(mHashmap);
        getSharedPreferencesEditor().putString(KEY_MERCHANT_COUNTER_NAME, strInput).apply();
    }

    public static String getSuggestionsPrefs() {
        return preferences.getString(KEY_SUGGESTION_PREF, null);
    }

    public static String getSuggestionsProductPrefs() {
        return preferences.getString(KEY_SUGGESTION_PRODUCT_PREF, null);
    }

    public static String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public static void setUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public static void setUserList(ArrayList<String> userList) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        String json = new Gson().toJson(userList);
        editor.putString(KEY_USER_LIST, json);
        editor.apply();
    }

    public static ArrayList<String> getUserList() {
        String json = preferences.getString(KEY_USER_LIST, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> arrayList = new Gson().fromJson(json, type);
        return arrayList == null ? new ArrayList<String>() : arrayList;
    }

    public static void setSuggestionsPrefs(PreferenceObjects testCaseObjects) {
        String strInput = new Gson().toJson(testCaseObjects);
        preferences.edit().putString(KEY_SUGGESTION_PREF, strInput).apply();
    }

    public static Map<String, List<JsonCheckAsset>> getInventoryPrefs() {
        String storedHashMapString = preferences.getString(KEY_INVENTORY_PREF, null);
        java.lang.reflect.Type type = new TypeToken<Map<String, List<JsonCheckAsset>>>() {
        }.getType();
        Map<String, List<JsonCheckAsset>> tempList = new Gson().fromJson(storedHashMapString, type);
        return null != tempList ? tempList : new HashMap<>();
    }

    public static void setInventoryPrefs(Map<String, List<JsonCheckAsset>> dataList) {
        String strInput = new Gson().toJson(dataList);
        preferences.edit().putString(KEY_INVENTORY_PREF, strInput).apply();
    }

    public static void setSuggestionsProductsPrefs(PreferenceObjects testCaseObjects) {
        String strInput = new Gson().toJson(testCaseObjects);
        preferences.edit().putString(KEY_SUGGESTION_PRODUCT_PREF, strInput).apply();
    }

    public static ArrayList<String> getCounterNames() {
        //Retrieve the values
        String jsonText = preferences.getString(KEY_COUNTER_NAME_LIST, null);
        ArrayList<String> nameList = new Gson().fromJson(jsonText, ArrayList.class);
        return null != nameList ? nameList : new ArrayList<String>();
    }

    public static void setCounterNames(ArrayList<String> mHashmap) {
        String strInput = new Gson().toJson(mHashmap);
        preferences.edit().putString(KEY_COUNTER_NAME_LIST, strInput).apply();
    }

    public static UserLevelEnum getUserLevel() {
        return UserLevelEnum.valueOf(preferences.getString(KEY_USER_LEVEL, ""));
    }

    public static void setUserLevel(String userLevel) {
        preferences.edit().putString(KEY_USER_LEVEL, userLevel).apply();
    }

    public static void setBusinessCustomerPriority(String customerPriority) {
        preferences.edit().putString(KEY_CUSTOMER_PRIORITY, customerPriority).apply();
    }

    public static void setPriorityAccess(boolean priorityAccess) {
        preferences.edit().putBoolean(PRIORITY_ACCESS, priorityAccess).apply();
    }

    public static void setMsgAnnouncementEnable(boolean isMsgAnnounce) {
        preferences.edit().putBoolean(PREKEY_IS_MSG_ANNOUNCE, isMsgAnnounce).apply();
    }

    public static boolean isMsgAnnouncementEnable() {
        return preferences.getBoolean(PREKEY_IS_MSG_ANNOUNCE, true);
    }

    public static void setTvSplitViewEnable(boolean isTvSplitView) {
        preferences.edit().putBoolean(PREKEY_IS_TV_SPLIT_VIEW, isTvSplitView).apply();
    }

    public static boolean isTvSplitViewEnable() {
        return preferences.getBoolean(PREKEY_IS_TV_SPLIT_VIEW, true);
    }

    public static void setTvRefreshTime(int refreshTime) {
        preferences.edit().putInt(PREKEY_TV_REFRESH_TIME, refreshTime).apply();
    }

    public static List<JsonBusinessCustomerPriority> getBusinessCustomerPriority() {
        Gson gson = new Gson();
        List<JsonBusinessCustomerPriority> businessCustomerPriority;
        String customerPriority = preferences.getString(KEY_CUSTOMER_PRIORITY, "");
        Type type = new TypeToken<List<JsonBusinessCustomerPriority>>() {}.getType();
        businessCustomerPriority = gson.fromJson(customerPriority, type);
        return businessCustomerPriority;
    }

    public static boolean getPriorityAccess() {
        return preferences.getBoolean(PRIORITY_ACCESS, false);
    }

    public static int getTvRefreshTime() {
        return preferences.getInt(PREKEY_TV_REFRESH_TIME, 5);
    }

    public static String getUSerID() {
        return preferences.getString(KEY_USER_ID, "");
    }

    public static String getAuth() {
        return preferences.getString(KEY_USER_AUTH, "");
    }

    public static String getEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public static long getLastUpdateTime() {
        return preferences.getLong(KEY_LAST_UPDATE, System.currentTimeMillis());
    }

    public static void setLastUpdateTime(long lastUpdateTime) {
        preferences.edit().putLong(KEY_LAST_UPDATE, lastUpdateTime).apply();
    }

    public static boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    public static boolean isAccessGrant() {
        return preferences.getBoolean(KEY_IS_ACCESS_GRANT, true);
    }

    public static void setAccessGrant(boolean isAccessGrant) {
        preferences.edit().putBoolean(KEY_IS_ACCESS_GRANT, isAccessGrant).apply();
    }

    public static String getTokenFCM() {
        return preferences.getString(TOKEN_FCM, "");
    }

    public static void setTokenFCM(String tokenFCM) {
        preferences.edit().putString(TOKEN_FCM, tokenFCM).apply();
    }

    public static void setUserInformation(String userName, String userId, String email, String auth, boolean isLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_AUTH, auth);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.apply();
    }

    public static void setUserProfile(JsonProfile jsonProfile) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(jsonProfile);
        editor.putString(KEY_USER_PROFILE, json);
        editor.apply();
        preferences.edit().putString(AppUtils.CURRENCY_SYMBOL, AppUtils.getCurrencySymbol(jsonProfile.getCountryShortName())).apply();
    }

    public static JsonProfile getUserProfile() {
        String json = preferences.getString(KEY_USER_PROFILE, "");
        return new Gson().fromJson(json, JsonProfile.class);

    }

    public static void setUserProfessionalProfile(JsonProfessionalProfilePersonal jsonProfessionalProfile) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(jsonProfessionalProfile);
        editor.putString(KEY_USER_PROFESSIONAL_PROFILE, json);
        editor.apply();
    }

    public static JsonProfessionalProfilePersonal getUserProfessionalProfile() {
        String json = preferences.getString(KEY_USER_PROFESSIONAL_PROFILE, "");
        return new Gson().fromJson(json, JsonProfessionalProfilePersonal.class);
    }

    public static String getCurrencySymbol() {
        return preferences.getString(AppUtils.CURRENCY_SYMBOL, "");
    }

    public static String getDeviceID() {
        return preferences.getString(APIConstant.Key.XR_DID, "");
    }

    public static void setDeviceID(String deviceId) {
        preferences.edit().putString(APIConstant.Key.XR_DID, deviceId).apply();
    }

    public static void clearPreferences() {
        // Clear all data except DID & FCM Token
        String did = getDeviceID();
        String tokenFCM = getTokenFCM();
        preferences.edit().clear().apply();
        setDeviceID(did);
        setTokenFCM(tokenFCM);
    }
}
