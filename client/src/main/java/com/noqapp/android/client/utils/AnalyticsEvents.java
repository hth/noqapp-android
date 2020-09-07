package com.noqapp.android.client.utils;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.MyApplication;

public class AnalyticsEvents {
    private static final String TAG = AnalyticsEvents.class.getSimpleName();

    public static final String EVENT_REVIEW_SCREEN = "Review_Screen";
    public static final String EVENT_FEED = "Feed_Dec_18";
    public static final String EVENT_RATE_APP = "Rate_The_App";
    public static final String EVENT_SEARCH = "Search_Screen";
    public static final String EVENT_BUZZER_SCREEN = "Buzzer_Screen";
    public static final String EVENT_CHANGE_LOCATION = "Change_location_Screen";
    public static final String EVENT_NOTIFICATION_SCREEN = "Notification_Screen";
    public static final String EVENT_CHANGE_LANGUAGE = "Change_Language";
    public static final String EVENT_CONTACT_US_SCREEN = "Contact_Us_Screen";
    public static final String EVENT_DEPENDENT_ADDED = "Dependent_Added";
    public static final String EVENT_JOIN_SCREEN = "Join_Queue";
    public static final String EVENT_JOIN_KIOSK_SCREEN = "Join_Queue_kiosk";
    public static final String EVENT_CANCEL_QUEUE = "Cancel_Queue";
    public static final String EVENT_CANCEL_ORDER = "Cancel_Order";
    public static final String EVENT_PLACE_ORDER = "Place_Order";
    public static final String EVENT_INVITE = "Invite_Screen";
    public static final String EVENT_LOGIN_SCREEN = "Login_Screen";
    public static final String EVENT_ERROR = "Log_Error";
    public static final String EVENT_SCAN_STORE_CODE_QR_SCREEN = "Scan_Store_CodeQR";

    public static void logContentEvent(String event) {
        try {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.CONTENT, event);
            MyApplication.getFireBaseAnalytics().logEvent(event, params);
        } catch (Exception e) {
            Log.e(TAG, "Failed logging event " + e.getLocalizedMessage());
        }
    }
}
