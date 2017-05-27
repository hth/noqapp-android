package com.noqapp.android.client.utils;

import com.noqapp.android.client.views.activities.LaunchActivity;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 5/9/17 6:49 PM
 */

public class UserUtils {

    public static boolean isLogin() {
        return StringUtils.isNotBlank(getAuth());
    }

    public static String getEmail() {
        return "hdjjdjd.46@mail.noqapp.com";
    }

    public static String getAuth() {
        return "$2a$15$QFOPQgo5eRlAW8QRAhSzDOc.W3gFhfBxwhsLJt0qLb4WLEqZRQ/vm";
    }

    public static String getDeviceId() {
        return LaunchActivity.getLaunchActivity().getDeviceID();
    }
}
