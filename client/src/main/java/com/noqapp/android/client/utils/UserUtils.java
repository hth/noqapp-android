package com.noqapp.android.client.utils;

import com.noqapp.android.client.views.activities.LaunchActivity;

/**
 * User: hitender
 * Date: 5/9/17 6:49 PM
 */

public class UserUtils {

    public static boolean isLogin() {
        return true;
    }

    public static String getEmail() {
        return "ajinkya.5@mail.noqapp.com";
    }

    public static String getAuth() {
        return "$2a$15$T7GXB3ziRwHNNKp55uIYG.yfBlnlpqBZGnOBYKFhgLCzAfU/p0.Dm";
    }

    public static String getDeviceId() {
        return LaunchActivity.getLaunchActivity().getDeviceID();
    }
}
