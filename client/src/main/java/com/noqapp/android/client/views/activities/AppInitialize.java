package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.model.api.DeviceClientApiImpl;
import com.noqapp.android.client.model.open.DeviceClientImpl;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.UserUtils;
import com.noqapp.android.client.views.interfaces.ActivityCommunicator;
import com.noqapp.android.client.views.pojos.KioskModeInfo;
import com.noqapp.android.client.views.pojos.LocationPref;
import com.noqapp.android.common.beans.DeviceRegistered;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.beans.JsonUserAddress;
import com.noqapp.android.common.beans.body.DeviceToken;
import com.noqapp.android.common.customviews.CustomToast;
import com.noqapp.android.common.presenter.DeviceRegisterPresenter;
import com.noqapp.android.common.utils.CommonHelper;
import com.noqapp.android.common.utils.FontsOverride;

import net.danlew.android.joda.JodaTimeAndroid;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.noqapp.android.client.model.APIConstant.Key.XR_MAIL;

/**
 * Created by chandra on 5/20/17.
 */
public class AppInitialize extends MultiDexApplication implements DeviceRegisterPresenter {
    private static final String TAG = AppInitialize.class.getSimpleName();
    public static SharedPreferences preferences;
    public static final String PREKEY_IS_NOTIFICATION_SOUND_ENABLE = "isNotificationSoundEnable";
    public static final String PREKEY_IS_NOTIFICATION_RECEIVE_ENABLE = "isNotificationReceiveEnable";
    private static final String KEY_LOCATION_PREFERENCE = "locationPreference";

    private static final String PREKEY_IS_MSG_ANNOUNCE = "msgAnnouncement";
    private static final String PREKEY_PHONE = "phone";
    private static final String PREKEY_NAME = "name";
    private static final String PREKEY_MAIL = "mail";
    private static final String PREKEY_DOB = "dateOfBirth";
    private static final String PREKEY_ADD = "address";
    private static final String PREKEY_ADDRESS_ID = "addressId";
    private static final String PREKEY_PROFILE_IMAGE = "imageUri";
    private static final String PREKEY_GENDER = "gender";
    private static final String PREKEY_INVITECODE = "invitecode";
    private static final String PREKEY_COUNTRY_SHORT_NAME = "countryshortname";
    private static final String PREKEY_IS_REVIEW_SHOWN = "reviewScreen";
    private static final String PREKEY_KIOSK_MODE_INFO = "kioskModeInfo";
    private static final String KEY_SHOW_HELPER = "showHelper";
    private static final String KEY_PREVIOUS_USER_QID = "previousUserQID";
    private static final String KEY_USER_PROFILE = "userProfile";
    private static final String KEY_FAVOURITE_CODE_QRS = "favouriteCodeQR";
    private static final String KEY_LOCATION_CHANGED_MANUALLY = "locationChangeManually";
    /* Secured Shared Preference. */
    public static final String TOKEN_FCM = "tokenFCM";
    public static ActivityCommunicator activityCommunicator;
    public static Location location;
    public static String cityName = "";
    public static String area = "";
    public static String town = "";
    public static boolean isLockMode = false;
    private static AppInitialize appInitialize;

    public AppInitialize() {
        super();
    }

    public static FirebaseAnalytics getFireBaseAnalytics() {
        return fireBaseAnalytics;
    }

    private static FirebaseAnalytics fireBaseAnalytics;

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

        preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        fireBaseAnalytics = FirebaseAnalytics.getInstance(this); //needs android.permission.WAKE_LOCK
        location = new Location("");
        JodaTimeAndroid.init(this);
        //https://stackoverflow.com/questions/26178212/first-launch-of-activity-with-google-maps-is-very-slow
        MapsInitializer.initialize(this);
        isLockMode = getKioskModeInfo().isKioskModeEnable();
        appInitialize = this;
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

    public static LocationPref getLocationPreference() {
        String json = preferences.getString(KEY_LOCATION_PREFERENCE, "");
        if (TextUtils.isEmpty(json)) {
            return new LocationPref();
        } else {
            return new Gson().fromJson(json, LocationPref.class);
        }
    }

    public static void setLocationPreference(LocationPref locationPreference) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = new Gson().toJson(locationPreference);
        editor.putString(KEY_LOCATION_PREFERENCE, json);
        editor.apply();
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor() {
        return preferences.edit();
    }

    public static String getTokenFCM() {
        return preferences.getString(TOKEN_FCM, "");
    }

    public static void setTokenFCM(String tokenFCM) {
        preferences.edit().putString(TOKEN_FCM, tokenFCM).apply();
    }

    public static String getUserName() {
        return preferences.getString(PREKEY_NAME, "Guest User");
    }

    public static boolean isLocationChangedManually() {
        return preferences.getBoolean(KEY_LOCATION_CHANGED_MANUALLY, false);
    }

    public static void setLocationChangedManually(boolean flag) {
        preferences.edit().putBoolean(KEY_LOCATION_CHANGED_MANUALLY, flag).apply();
    }

    public static String getUserDOB() {
        return preferences.getString(PREKEY_DOB, "DD-MM-YYYY");
    }

    public static String getUserProfileUri() {
        return preferences.getString(PREKEY_PROFILE_IMAGE, "");
    }

    public static void setUserProfileUri(String profileUri) {
        preferences.edit().putString(PREKEY_PROFILE_IMAGE, profileUri).apply();
    }

    public static String getPhoneNo() {
        return preferences.getString(PREKEY_PHONE, "");
    }

    public static String getGender() {
        return preferences.getString(PREKEY_GENDER, "");
    }

    public static String getInviteCode() {
        return preferences.getString(PREKEY_INVITECODE, "");
    }

    public static String getAddress() {
        return preferences.getString(PREKEY_ADD, "");
    }

    public static void setAddress(String address) {
        preferences.edit().putString(PREKEY_ADD, address).apply();
    }

    public static String getSelectedAddressId() {
        return preferences.getString(PREKEY_ADDRESS_ID, "");
    }

    public static void setSelectedAddressId(String addressId) {
        preferences.edit().putString(PREKEY_ADDRESS_ID, addressId).apply();
    }

    public static String getCountryShortName() {
        return preferences.getString(PREKEY_COUNTRY_SHORT_NAME, Constants.DEFAULT_COUNTRY_CODE);
    }

    public static boolean isReviewShown() {
        return preferences.getBoolean(PREKEY_IS_REVIEW_SHOWN, false);
    }

    public static void setReviewShown(boolean check) {
        preferences.edit().putBoolean(PREKEY_IS_REVIEW_SHOWN, check).apply();
    }

    public static String getMail() {
        return preferences.getString(XR_MAIL, "");
    }

    public static String getActualMail() {
        return getMail().endsWith(Constants.MAIL_NOQAPP_COM) ? "" : getMail();
    }

    public static String getCustomerNameWithQid(String customerName, String queueUserId) {
        return customerName + " " + queueUserId;
    }

    public static String getOfficeMail() {
        return getActualMail();
    }

    public static String getOfficePhoneNo() {
        return getPhoneNo();
    }

    public static boolean showEmailVerificationField() {
        if (getUserProfile().isAccountValidated()) {
            return false;
        } else {
            return !getMail().endsWith(Constants.MAIL_NOQAPP_COM);
        }
    }

    public static boolean isEmailVerified() {
        return getUserProfile().isAccountValidated();
    }

    public static String getAuth() {
        if (null != preferences) {
            return preferences.getString(APIConstant.Key.XR_AUTH, "");
        }
        return null;
    }

    public static String getDeviceId() {
        if (null != preferences) {
            return preferences.getString(APIConstant.Key.XR_DID, "");
        }
        return null;
    }

    /* Previous QID helps keeps track if new user has logged in. */
    public static String getPreviousUserQID() {
        return preferences.getString(KEY_PREVIOUS_USER_QID, "");
    }

    public static void setPreviousUserQID(String queueUserID) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(KEY_PREVIOUS_USER_QID, queueUserID);
        editor.apply();
    }

    public static void setDeviceID(String did) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(APIConstant.Key.XR_DID, did);
        editor.apply();
    }

    public static void commitProfile(JsonProfile profile, String email, String auth) {
        setUserProfile(profile);
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(PREKEY_PHONE, profile.getPhoneRaw());
        editor.putString(PREKEY_NAME, profile.getName());
        editor.putString(PREKEY_GENDER, profile.getGender().name());
        editor.putString(PREKEY_DOB, profile.getBirthday());
        editor.putString(PREKEY_MAIL, profile.getMail());
        editor.putString(PREKEY_INVITECODE, profile.getInviteCode());
        editor.putString(PREKEY_COUNTRY_SHORT_NAME, profile.getCountryShortName());
        editor.putString(PREKEY_ADD, profile.findPrimaryOrAnyExistingAddress() == null ? "" : profile.findPrimaryOrAnyExistingAddress().getAddress());
        editor.putString(PREKEY_PROFILE_IMAGE, profile.getProfileImage());
        editor.putString(XR_MAIL, email);
        editor.putString(APIConstant.Key.XR_AUTH, auth);
        editor.commit();
    }

    public static void saveMailAuth(String email, String auth) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(XR_MAIL, email);
        editor.putString(APIConstant.Key.XR_AUTH, auth);
        editor.commit();
    }

    public static void clearPreferences() {
        // Clear all data except DID , FCM Token, previousUserQID & showHelper && kiosk mode
        String did = preferences.getString(APIConstant.Key.XR_DID, "");
        String tokenFCM = getTokenFCM();
        String previousUserQID = getPreviousUserQID();
        String email = getActualMail();
        String userName = getUserName();
        boolean showHelper = getShowHelper();
        KioskModeInfo kioskModeInfo = getKioskModeInfo();
        getSharedPreferencesEditor().clear().commit();
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(APIConstant.Key.XR_DID, did);
        editor.putString(TOKEN_FCM, tokenFCM);
        editor.putString(KEY_PREVIOUS_USER_QID, previousUserQID);
        editor.putString(PREKEY_MAIL, email);
        editor.putString(PREKEY_NAME, userName);
        editor.putString(XR_MAIL, email);
        editor.putBoolean(KEY_SHOW_HELPER, showHelper);
        editor.commit();
        setKioskModeInfo(kioskModeInfo);
    }

    public static JsonProfile getUserProfile() {
        String json = preferences.getString(KEY_USER_PROFILE, "");
        return new Gson().fromJson(json, JsonProfile.class);
    }

    public static List<JsonProfile> getAllProfileList() {
        List<JsonProfile> profileList = new ArrayList<>();
        if (null != getUserProfile().getDependents()) {
            profileList = getUserProfile().getDependents();
        }
        profileList.add(0, getUserProfile());
        return profileList;
    }

    public static void setUserProfile(JsonProfile jsonProfile) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        String json = new Gson().toJson(jsonProfile);
        editor.putString(KEY_USER_PROFILE, json);
        editor.apply();
    }

    public static KioskModeInfo getKioskModeInfo() {
        String json = preferences.getString(PREKEY_KIOSK_MODE_INFO, "");
        if (TextUtils.isEmpty(json)) {
            return new KioskModeInfo();
        } else {
            return new Gson().fromJson(json, KioskModeInfo.class);
        }
    }

    public static void setKioskModeInfo(KioskModeInfo kioskModeInfo) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        String json = new Gson().toJson(kioskModeInfo);
        editor.putString(PREKEY_KIOSK_MODE_INFO, json);
        editor.apply();
    }

    public static void setShowHelper(boolean showHelper) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(KEY_SHOW_HELPER, showHelper);
        editor.apply();
    }

    public static boolean getShowHelper() {
        // Show only first time in the app lifecycle
        return preferences.getBoolean(KEY_SHOW_HELPER, true);
    }

    public static void setMsgAnnouncementEnable(boolean isMsgAnnounce) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(PREKEY_IS_MSG_ANNOUNCE, isMsgAnnounce);
        editor.apply();
    }

    public static boolean isMsgAnnouncementEnable() {
        return preferences.getBoolean(PREKEY_IS_MSG_ANNOUNCE, true);
    }

    @Override
    public void deviceRegisterResponse(DeviceRegistered deviceRegistered) {
        processRegisterDeviceIdResponse(deviceRegistered, this);
    }

    @Override
    public void authenticationFailure() {
        /* dismissProgress(); no progress bar silent call here */
    }

    @Override
    public void deviceRegisterError() {
        /* dismissProgress(); no progress bar silent call here */
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        /* dismissProgress(); no progress bar silent call here */
        // new ErrorResponseHandler().processError(this, eej);
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        /* dismissProgress(); no progress bar silent call here */
        // new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
    }

    public static void fetchDeviceId() {
        fetchDeviceId(appInitialize);
    }

    public static void fetchDeviceId(DeviceRegisterPresenter deviceRegisterPresenter) {
        DeviceToken deviceToken = new DeviceToken(
                AppInitialize.getTokenFCM(),
                Constants.appVersion(),
                CommonHelper.getLocation(AppInitialize.location.getLatitude(), AppInitialize.location.getLongitude()));
        if (UserUtils.isLogin()) {
            DeviceClientApiImpl deviceClientApi = new DeviceClientApiImpl();
            deviceClientApi.setDeviceRegisterPresenter(deviceRegisterPresenter);
            deviceClientApi.register(UserUtils.getDeviceId(), UserUtils.getEmail(), UserUtils.getAuth(), deviceToken);
        } else {
            DeviceClientImpl deviceRegistration = new DeviceClientImpl();
            deviceRegistration.setDeviceRegisterPresenter(deviceRegisterPresenter);
            deviceRegistration.register(deviceToken);
        }
    }

    public static void processRegisterDeviceIdResponse(DeviceRegistered deviceRegistered, Context context) {
        if (1 == deviceRegistered.getRegistered()) {
            Log.d(TAG, "Device register success");
            JsonUserAddress jsonUserAddress = CommonHelper.getAddress(deviceRegistered.getGeoPointOfQ().getLat(), deviceRegistered.getGeoPointOfQ().getLon(), context);
            AppInitialize.cityName = jsonUserAddress.getLocationAsString();
            Log.d(TAG, "Launch device register City Name=" + AppInitialize.cityName);

            LocationPref locationPref = AppInitialize.getLocationPreference()
                    .setArea(jsonUserAddress.getArea())
                    .setTown(jsonUserAddress.getTown())
                    .setLatitude(deviceRegistered.getGeoPointOfQ().getLat())
                    .setLongitude(deviceRegistered.getGeoPointOfQ().getLon());
            AppInitialize.setLocationPreference(locationPref);
            AppInitialize.setDeviceID(deviceRegistered.getDeviceId());
            AppInitialize.location.setLatitude(locationPref.getLatitude());
            AppInitialize.location.setLongitude(locationPref.getLongitude());
        } else {
            Log.e(TAG, "Device register error: " + deviceRegistered.toString());
            try {
                new CustomToast().showToast(context, "Device registration error");
            } catch (Exception e) {
                Log.e(TAG, "BadTokenException exception caught while showing the window " + e.getLocalizedMessage());
            }
        }
    }

    public static void saveFavouriteList(List<String> list) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(KEY_FAVOURITE_CODE_QRS, json);
        editor.apply();
    }

    public static ArrayList<String> getFavouriteList() {
        Gson gson = new Gson();
        String json = preferences.getString(KEY_FAVOURITE_CODE_QRS, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> favouriteList = gson.fromJson(json, type);
        return null == favouriteList ? new ArrayList<>() : favouriteList;
    }
}
