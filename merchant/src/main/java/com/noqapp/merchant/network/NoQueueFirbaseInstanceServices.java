package com.noqapp.merchant.network;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class NoQueueFirbaseInstanceServices extends FirebaseInstanceIdService {

    private static final String TAG = NoQueueFirbaseInstanceServices.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token ::: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshToken) {
//        DeviceModel deviceModel = new DeviceModel();
//        DeviceToken deviceToken = new DeviceToken();
//        deviceToken.setFcmToken(refreshToken);
//        deviceModel.register(LaunchActivity.DID, deviceToken);
    }
}
