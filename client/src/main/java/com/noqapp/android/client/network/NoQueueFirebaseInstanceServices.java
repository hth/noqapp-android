package com.noqapp.android.client.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.android.client.model.DeviceModel;
import com.noqapp.android.client.presenter.beans.body.DeviceToken;
import com.noqapp.android.client.views.activities.NoQueueBaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class NoQueueFirebaseInstanceServices extends FirebaseInstanceIdService {
    private final String TAG = NoQueueFirebaseInstanceServices.class.getSimpleName();
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
                NoQueueBaseActivity.APP_PREF, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(NoQueueBaseActivity.XR_DID, "");
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
        editor.putString(NoQueueBaseActivity.XR_DID, deviceId);
        editor.apply();
    }
}
