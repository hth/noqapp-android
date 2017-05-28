package com.noqapp.android.client.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.noqapp.android.client.presenter.beans.JsonProfile;

/**
 * Created by omkar on 4/8/17.
 */
public class NoQueueBaseActivity extends AppCompatActivity {

    public static final String PREKEY_PHONE = "phone";
    public static final String PREKEY_NAME = "name";
    public static final String PREKEY_MAIL = "mail";
    public static final String PREKEY_GENDER = "gender";
    public static final String PREKEY_REMOTE_JOIN = "remoteJoin";
    public static final String PREKEY_AUTOJOIN = "autojoin";
    public static final String PREKEY_INVITECODE = "invitecode";
    public static final String PREKEY_COUNTRY_SHORT_NAME = "countryshortname";
    public static final int ACCOUNTKIT_REQUEST_CODE = 99;

    /* Secured Shared Preference. */
    public static final String APP_PREF = "shared_pref";
    public static String XR_DID = "X-R-DID";
    public static NoQueueBaseActivity noQueueBaseActivity;
    private static SharedPreferences sharedpreferences;
    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        return sharedpreferences.edit();
    }

    protected Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noQueueBaseActivity=this;
        sharedpreferences = noQueueBaseActivity.getPreferences(Context.MODE_PRIVATE);
    }



    public static void setRemoteJoinCount(int remoteJoinCount) {
        sharedpreferences.edit().putInt(PREKEY_REMOTE_JOIN, remoteJoinCount).commit();
    }

    public static int getRemoteJoinCount() {
        return sharedpreferences.getInt(PREKEY_REMOTE_JOIN, 0);
    }
    public static void setAutoJoinStatus( boolean autoJoinStatus) {
        sharedpreferences.edit().putBoolean(NoQueueBaseActivity.PREKEY_AUTOJOIN, autoJoinStatus).commit();
    }

    public static boolean getAutoJoinStatus() {
        return sharedpreferences.getBoolean(PREKEY_AUTOJOIN, true);
    }


    public static String getUserName() {
        return sharedpreferences.getString(NoQueueBaseActivity.PREKEY_NAME, "Guest User");
    }

    public static String getPhoneNo() {
        return sharedpreferences.getString(PREKEY_PHONE, "");
    }

    public static String getGender() {
        return sharedpreferences.getString(PREKEY_GENDER, "");
    }

    public static String getInviteCode() {
        return sharedpreferences.getString(PREKEY_INVITECODE, "");
    }


    public static String getCountryShortName() {
        return sharedpreferences.getString(NoQueueBaseActivity.PREKEY_COUNTRY_SHORT_NAME, "US");
    }

    public static void commitProfile(JsonProfile profile){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(PREKEY_PHONE, profile.getPhoneRaw());
        editor.putString(PREKEY_NAME, profile.getName());
        editor.putString(PREKEY_GENDER, profile.getGender());
        editor.putString(PREKEY_MAIL, profile.getMail());
        editor.putInt(PREKEY_REMOTE_JOIN, profile.getRemoteJoin());
        editor.putBoolean(PREKEY_AUTOJOIN, true);
        editor.putString(PREKEY_INVITECODE, profile.getInviteCode());
        editor.putString(PREKEY_COUNTRY_SHORT_NAME, profile.getCountryShortName());
        editor.commit();

    }

    public static void clearPreferences(){
        getSharedPreferencesEditor().clear().commit();
    }
}
