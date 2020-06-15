package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.views.pojos.KioskModeInfo;
import com.noqapp.android.common.beans.JsonProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class is created to store the information which data need to be final(Consistent)
 * Through out the App. So only one class can extend this Activity. Otherwise it was a serious issue.
 */
public class NoQueueBaseActivity extends AppCompatActivity {
    private static final String PREKEY_PHONE = "phone";
    private static final String PREKEY_NAME = "name";
    private static final String PREKEY_MAIL = "mail";
    private static final String PREKEY_DOB = "dateOfBirth";
    private static final String PREKEY_ADD = "address";
    private static final String PREKEY_PROFILE_IMAGE = "imageUri";
    private static final String PREKEY_GENDER = "gender";
    private static final String PREKEY_INVITECODE = "invitecode";
    private static final String PREKEY_COUNTRY_SHORT_NAME = "countryshortname";
    private static final String PREKEY_IS_REVIEW_SHOWN = "reviewScreen";
    private static final String PREKEY_KIOSK_MODE_INFO = "kioskModeInfo";
    private static final String PREKEY_IS_MSG_ANNOUNCE = "msgAnnouncement";
    private static final String KEY_SHOW_HELPER = "showHelper";
    private static final String KEY_PREVIOUS_USER_QID = "previousUserQID";
    private static final String KEY_USER_PROFILE = "userProfile";
    /* Secured Shared Preference. */
    static final String TOKEN_FCM = "tokenFCM";
    public static NoQueueBaseActivity noQueueBaseActivity;

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferences.edit();
    }

    public static String getTokenFCM() {
        return sharedPreferences.getString(TOKEN_FCM, "");
    }

    public static void setTokenFCM(String tokenFCM) {
        sharedPreferences.edit().putString(TOKEN_FCM, tokenFCM).apply();
    }

    public static String getUserName() {
        return sharedPreferences.getString(NoQueueBaseActivity.PREKEY_NAME, "Guest User");
    }

    public static String getUserDOB() {
        return sharedPreferences.getString(NoQueueBaseActivity.PREKEY_DOB, "DD-MM-YYYY");
    }

    public static String getUserProfileUri() {
        return sharedPreferences.getString(NoQueueBaseActivity.PREKEY_PROFILE_IMAGE, "");
    }

    public static void setUserProfileUri(String profileUri) {
        sharedPreferences.edit().putString(PREKEY_PROFILE_IMAGE, profileUri).apply();
    }

    public static String getPhoneNo() {
        return sharedPreferences.getString(PREKEY_PHONE, "");
    }

    public static String getGender() {
        return sharedPreferences.getString(PREKEY_GENDER, "");
    }

    public static String getInviteCode() {
        return sharedPreferences.getString(PREKEY_INVITECODE, "");
    }

    public static String getAddress() {
        return sharedPreferences.getString(PREKEY_ADD, "");
    }

    public static String getCountryShortName() {
        return sharedPreferences.getString(NoQueueBaseActivity.PREKEY_COUNTRY_SHORT_NAME, Constants.DEFAULT_COUNTRY_CODE);
    }

    public static boolean isReviewShown() {
        return sharedPreferences.getBoolean(NoQueueBaseActivity.PREKEY_IS_REVIEW_SHOWN, false);
    }

    public static void setReviewShown(boolean check) {
        sharedPreferences.edit().putBoolean(PREKEY_IS_REVIEW_SHOWN, check).apply();
    }

    public static String getMail() {
        return sharedPreferences.getString(APIConstant.Key.XR_MAIL, "");
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
        return sharedPreferences.getString(APIConstant.Key.XR_AUTH, "");
    }

    public static String getDeviceID() {
        return sharedPreferences.getString(APIConstant.Key.XR_DID, "");
    }

    /* Previous QID helps keeps track if new user has logged in. */
    public static String getPreviousUserQID() {
        return sharedPreferences.getString(KEY_PREVIOUS_USER_QID, "");
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
        editor.putString(PREKEY_ADD, profile.getAddress());
        editor.putString(PREKEY_PROFILE_IMAGE, profile.getProfileImage());
        editor.putString(APIConstant.Key.XR_MAIL, email);
        editor.putString(APIConstant.Key.XR_AUTH, auth);
        editor.commit();
    }

    public static void saveMailAuth(String email, String auth) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(APIConstant.Key.XR_MAIL, email);
        editor.putString(APIConstant.Key.XR_AUTH, auth);
        editor.commit();
    }

    public static void clearPreferences() {
        // Clear all data except DID , FCM Token, previousUserQID & showHelper && kiosk mode
        String did = sharedPreferences.getString(APIConstant.Key.XR_DID, "");
        String tokenFCM = getTokenFCM();
        String previousUserQID = getPreviousUserQID();
        boolean showHelper = getShowHelper();
        KioskModeInfo kioskModeInfo = getKioskModeInfo();
        getSharedPreferencesEditor().clear().commit();
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(APIConstant.Key.XR_DID, did);
        editor.putString(TOKEN_FCM, tokenFCM);
        editor.putString(KEY_PREVIOUS_USER_QID, previousUserQID);
        editor.putBoolean(KEY_SHOW_HELPER, showHelper);
        editor.commit();
        setKioskModeInfo(kioskModeInfo);
        if (null != LaunchActivity.getLaunchActivity()) {
            LaunchActivity.getLaunchActivity().updateDrawerUI();
        }
    }

    public static JsonProfile getUserProfile() {
        String json = sharedPreferences.getString(KEY_USER_PROFILE, "");
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
        String json = sharedPreferences.getString(PREKEY_KIOSK_MODE_INFO, "");
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
        return sharedPreferences.getBoolean(KEY_SHOW_HELPER, true);
    }

    public static void setMsgAnnouncementEnable(boolean isMsgAnnounce) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(PREKEY_IS_MSG_ANNOUNCE, isMsgAnnounce);
        editor.apply();
    }

    public static boolean isMsgAnnouncementEnable() {
        return sharedPreferences.getBoolean(PREKEY_IS_MSG_ANNOUNCE, true);
    }

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    protected Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noQueueBaseActivity = this;
        sharedPreferences = noQueueBaseActivity.getPreferences(Context.MODE_PRIVATE);
    }
}
