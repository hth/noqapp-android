package com.noqapp.android.client.utils;

import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;
import com.noqapp.android.client.views.fragments.NoQueueBaseFragment;

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
        return NoQueueBaseActivity.getXRemail();

    }

    public static String getAuth() {
        return NoQueueBaseActivity.getXRauth();
    }

    public static String getDeviceId() {
        return LaunchActivity.getLaunchActivity().getDeviceID();
    }
}
