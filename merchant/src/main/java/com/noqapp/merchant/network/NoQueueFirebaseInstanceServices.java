package com.noqapp.merchant.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.merchant.model.DeviceModel;
import com.noqapp.merchant.presenter.beans.body.DeviceToken;
import com.noqapp.merchant.views.activities.LaunchActivity;

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
        if (deviceId.equals("")) {
            deviceId = UUID.randomUUID().toString().toUpperCase();
            setSharPreferanceDeviceID(sharedpreferences, deviceId);
            Log.v("device id_created", deviceId);
        } else {
            Log.v("device id exist", deviceId);
        }
        DeviceModel.register(deviceId, deviceToken);
    }

    private void setSharPreferanceDeviceID(SharedPreferences sharedpreferences, String deviceid) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(LaunchActivity.XR_DID, deviceid);
        editor.commit();
    }
}
