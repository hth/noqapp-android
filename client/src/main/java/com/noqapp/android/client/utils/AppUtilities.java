package com.noqapp.android.client.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppUtilities {
    private static final String TAG = AppUtilities.class.getSimpleName();

    private static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat SDF_DOB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static Map<String, Locale> localeMap;

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_DOB.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        if (null == localeMap) {
            String[] countries = Locale.getISOCountries();
            localeMap = new HashMap<>(countries.length);
            for (String country : countries) {
                Locale locale = new Locale("", country);
                localeMap.put(locale.getISO3Country().toUpperCase(), locale);
            }
        }
        return localeMap.get(iso3CountryCode).getCountry();
    }

    public static String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
        Locale locale = new Locale("", iso2CountryCode);
        return locale.getISO3Country();
    }

    public static void makeCall(Activity context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{Manifest.permission.CALL_PHONE},
                        111);
            } else {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                } catch (ActivityNotFoundException ex) {
                    Log.w(TAG, "Failed calling reason=" + ex.getLocalizedMessage());
                    Toast.makeText(context, "Please install a calling application", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static void openAddressInMap(Activity context, String address) {
        try {
            String map = "http://maps.google.co.in/maps?q=" + address;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
        }
    }

    public void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
