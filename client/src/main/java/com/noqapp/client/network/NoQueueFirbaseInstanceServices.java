package com.noqapp.client.network;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.client.model.DeviceModel;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.views.activities.LaunchActivity;

import java.util.UUID;

/**
 * Created by omkar on 4/2/17.
 */

public class NoQueueFirbaseInstanceServices extends FirebaseInstanceIdService {
    public static String UDID;

    private static final String TAG = NoQueueFirbaseInstanceServices.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refereshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refress Token ::: " + refereshedToken);

        sendRegistrationToServer(refereshedToken);

    }

    private void sendRegistrationToServer(String refreshToken) {
        String UDID;
        DeviceToken deviceToken = new DeviceToken(refreshToken);
//        if (null != LaunchActivity.getLaunchActivity() && LaunchActivity.getUDID(LaunchActivity.getLaunchActivity()).equals("")) {
//            UDID = LaunchActivity.getUDID(LaunchActivity.getLaunchActivity());
//            Log.v("fresh device ID :", UDID);
//        } else {
//            UDID = UUID.randomUUID().toString();
//            Log.v("old device ID :", UDID);
//        }
        UDID = UUID.randomUUID().toString();
        DeviceModel.register(UDID, deviceToken);
    }
}
