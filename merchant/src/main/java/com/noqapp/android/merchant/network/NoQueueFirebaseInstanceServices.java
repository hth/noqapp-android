package com.noqapp.android.merchant.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.android.merchant.model.DeviceModel;
import com.noqapp.android.merchant.views.activities.LaunchActivity;
import com.noqapp.library.beans.body.DeviceToken;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class NoQueueFirebaseInstanceServices extends FirebaseInstanceIdService {
    private static final String TAG = NoQueueFirebaseInstanceServices.class.getSimpleName();
    private String deviceId = "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token=" + fcmToken);
        sendRegistrationToServer(fcmToken);
    }

    private void sendRegistrationToServer(String refreshToken) {
        DeviceToken deviceToken = new DeviceToken(refreshToken);

        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(
                LaunchActivity.mypref, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(LaunchActivity.XR_DID, "");
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString().toUpperCase();
            setSharedPreferenceDeviceID(sharedpreferences, deviceId);
            Log.d(TAG, "Device Id created" + deviceId);
        } else {
            Log.d(TAG, "Device Id exist" + deviceId);
        }
        DeviceModel.register(deviceId, deviceToken);
    }

    private void setSharedPreferenceDeviceID(SharedPreferences sharedpreferences, String deviceId) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(LaunchActivity.XR_DID, deviceId);
        editor.apply();
    }
}
