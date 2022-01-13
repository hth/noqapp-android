package com.noqapp.android.client.utils;

import com.noqapp.android.client.views.activities.NoQueueClientApplication;

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
        return NoQueueClientApplication.getMail();
    }

    public static String getAuth() {
        return NoQueueClientApplication.getAuth();
    }

    public static String getDeviceId() {
        return NoQueueClientApplication.getDeviceId();
    }
}
