package com.noqapp.client.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by omkar on 4/8/17.
 */
public class NoQueueBaseActivity extends AppCompatActivity {

    public static final String PREKEY_PHONE = "phone";
    public static final String PREKEY_NAME = "name";
    public static final String PREKEY_MAIL = "mail";
    public static final String PREKEY_GENDER = "gender";
    public static final String PREKEY_REMOTESCAN = "remotescan";
    public static final String PREKEY_AUTOJOIN = "autojoin";
    public static final String PREKEY_INVITECODE = "invitecode";
    public static final String PREKEY_COUNTRY_SHORT_NAME = "countryshortname";
    public static final String PREKEY_UUID= "uuid";
    public static final int ACCOUNTKIT_REQUEST_CODE = 99;

    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    public static SharedPreferences.Editor getSharedPreferencesEditor(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.edit();
    }

    protected Context getContext() {
        return this;
    }
}
