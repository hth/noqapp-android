package com.noqapp.android.merchant.utils;

import com.noqapp.android.merchant.views.activities.LaunchActivity;

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
        return LaunchActivity.getLaunchActivity().getEmail();
    }

    public static String getAuth() {
        return LaunchActivity.getLaunchActivity().getAuth();
    }

    public static String getDeviceId() {
        return LaunchActivity.getLaunchActivity().getDeviceID();
    }
}
