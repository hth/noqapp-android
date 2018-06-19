package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.noqapp.android.client.model.APIConstant;
import com.noqapp.android.client.model.database.DatabaseTable;
import com.noqapp.common.beans.JsonProfile;

/**
 * This Class is created to store the information which data need to be final(Consistent)
 * Through out the App. So only one class can extend this Activity. Otherwise it was a serious issue.
 *
 */
public class NoQueueBaseActivity extends AppCompatActivity {
    public static final String PREKEY_PHONE = "phone";
    public static final String PREKEY_NAME = "name";
    public static final String PREKEY_MAIL = "mail";
    public static final String PREKEY_DOB = "dateOfBirth";
    public static final String PREKEY_ADD = "address";
    public static final String PREKEY_PROFILE_IMAGE = "imageUri";
    //TODO add address from profile
    public static final String PREKEY_GENDER = "gender";
   // public static final String PREKEY_REMOTE_JOIN = "remoteJoin";
    //public static final String PREKEY_AUTOJOIN = "autojoin";
    public static final String PREKEY_INVITECODE = "invitecode";
    public static final String PREKEY_COUNTRY_SHORT_NAME = "countryshortname";
    public static final int ACCOUNTKIT_REQUEST_CODE = 99;
    public static final String PREKEY_IS_REVIEW_SHOWN = "reviewScreen";


    public static final String KEY_CODE_QR = DatabaseTable.TokenQueue.CODE_QR;
    public static final String KEY_FROM_LIST = "fromList";
    public static final String KEY_IS_HISTORY = "isHistory";
    public static final String KEY_IS_REJOIN = "isRejoin";
  //  public static final String KEY_IS_AUTOJOIN_ELIGIBLE = "autoJoinEligible";
    public static final String KEY_JSON_TOKEN_QUEUE = "jsonTokenQueue";
    protected static final String KEY_USER_PROFILE = "userProfile";
    /* Secured Shared Preference. */
    public static final String APP_PREF = "shared_pref";
    public static String XR_DID = "X-R-DID";
    public static NoQueueBaseActivity noQueueBaseActivity;
    private static SharedPreferences sharedPreferences;

    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedPreferences.edit();
    }

//    public static int getRemoteJoinCount() {
//        return sharedPreferences.getInt(PREKEY_REMOTE_JOIN, 0);
//    }
//
//    public static void setRemoteJoinCount(int remoteJoinCount) {
//        sharedPreferences.edit().putInt(PREKEY_REMOTE_JOIN, remoteJoinCount < 0 ? 0 : remoteJoinCount).commit();
//    }

//    public static boolean getAutoJoinStatus() {
//        return sharedPreferences.getBoolean(PREKEY_AUTOJOIN, true);
//    }
//
//    public static void setAutoJoinStatus(boolean autoJoinStatus) {
//        sharedPreferences.edit().putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, autoJoinStatus).commit();
//    }

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
        return sharedPreferences.getString(NoQueueBaseActivity.PREKEY_COUNTRY_SHORT_NAME, "US");
    }

    public static boolean isReviewShown() {
        return sharedPreferences.getBoolean(NoQueueBaseActivity.PREKEY_IS_REVIEW_SHOWN, false);
    }

    public static void setReviewShown(boolean isReviewShown) {
        sharedPreferences.edit().putBoolean(PREKEY_IS_REVIEW_SHOWN, isReviewShown).apply();
    }

    public static String getMail() {
        return sharedPreferences.getString(APIConstant.Key.XR_MAIL, "");
    }

    public static String getAuth() {
        return sharedPreferences.getString(APIConstant.Key.XR_AUTH, "");
    }

    public static void commitProfile(JsonProfile profile, String email, String auth) {
        setUserProfile(profile);
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(PREKEY_PHONE, profile.getPhoneRaw());
        editor.putString(PREKEY_NAME, profile.getName());
        editor.putString(PREKEY_GENDER, profile.getGender().name());
        editor.putString(PREKEY_DOB, profile.getBirthday());
        editor.putString(PREKEY_MAIL, profile.getMail());
       // editor.putInt(PREKEY_REMOTE_JOIN, profile.getRemoteJoin());
       // editor.putBoolean(PREKEY_AUTOJOIN, true);
        editor.putString(PREKEY_INVITECODE, profile.getInviteCode());
        editor.putString(PREKEY_COUNTRY_SHORT_NAME, profile.getCountryShortName());
        editor.putString(PREKEY_ADD,profile.getAddress());
        editor.putString(PREKEY_PROFILE_IMAGE,profile.getProfileImage());
        editor.putString(APIConstant.Key.XR_MAIL, email);
        editor.putString(APIConstant.Key.XR_AUTH, auth);
        editor.commit();
    }
    public static void setUserProfile(JsonProfile jsonProfile){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        Gson gson = new Gson();
        String json = gson.toJson(jsonProfile);
        editor.putString(KEY_USER_PROFILE, json);
        editor.apply();
    }

    public JsonProfile getUserProfile(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_USER_PROFILE, "");
        JsonProfile obj = gson.fromJson(json, JsonProfile.class);
        return obj;
    }
    public static void clearPreferences() {
        getSharedPreferencesEditor().clear().commit();
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
