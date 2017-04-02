package com.noqapp.client.network;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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

    }
}
