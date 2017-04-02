package com.noqapp.client.network;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.client.model.DeviceModel;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.views.activities.LaunchActivity;

/**
 * Created by omkar on 4/2/17.
 */

public class NoQueueFirbaseInstanceServices extends FirebaseInstanceIdService {

    private static final String TAG = NoQueueFirbaseInstanceServices.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refereshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refress Token ::: " + refereshedToken);

        sendRegistrationToServer(refereshedToken);

    }

    private  void sendRegistrationToServer(String refreshToken)
    {
        DeviceModel deviceModel = new DeviceModel();
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setFcmToken(refreshToken);
        deviceModel.register(LaunchActivity.DID, deviceToken);
    }
}
