package com.noqapp.client.utils;

import com.noqapp.client.model.database.utils.KeyValueUtils;
import com.noqapp.client.network.NoQueueFirebaseInstanceServices;
import com.noqapp.client.views.activities.LaunchActivity;

/**
 * User: hitender
 * Date: 5/9/17 6:49 PM
 */

public class UserUtils {

    public static String getEmail() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_MAIL);
    }

    public static String getAuth() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_AUTH);
    }

    public static String getDeviceId() {
        return LaunchActivity.getLaunchActivity().getDeviceID();
    }
}
