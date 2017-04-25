package com.noqapp.client.utils;

import android.util.Log;

import com.noqapp.client.network.NoQueueFirbaseInstanceServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtilities {
    private static final String TAG = NoQueueFirbaseInstanceServices.class.getSimpleName();

    private static final SimpleDateFormat SDF_DOB_FROM_UI = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat SDF_DOB = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String convertDOBToValidFormat(String dob) {
        try {
            Date date = SDF_DOB_FROM_UI.parse(dob);
            return SDF_DOB.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing DOB={}" + e.getLocalizedMessage(), e);
            return "";
        }
    }
}
