package com.noqapp.client.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.client.model.DeviceModel;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.views.activities.NoQueueBaseActivity;

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
                NoQueueBaseActivity.SHARED_PREF_SEC, Context.MODE_PRIVATE);
        deviceId = sharedpreferences.getString(NoQueueBaseActivity.XR_DID, "");
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
        editor.putString(NoQueueBaseActivity.XR_DID, deviceid);
        editor.commit();
    }
}
