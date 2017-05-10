package com.noqapp.client.network;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.client.model.DeviceModel;
import com.noqapp.client.model.database.DBUtils;
import com.noqapp.client.model.database.utils.KeyValueUtils;
import com.noqapp.client.presenter.beans.body.DeviceToken;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import static com.noqapp.client.model.database.utils.KeyValueUtils.KEYS.XR_DID;

public class NoQueueFirebaseInstanceServices extends FirebaseInstanceIdService {
    private static final String TAG = NoQueueFirebaseInstanceServices.class.getSimpleName();

    public static String deviceId;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token=" + fcmToken);
        sendRegistrationToServer(fcmToken);
    }

    private void sendRegistrationToServer(String refreshToken) {
        DeviceToken deviceToken = new DeviceToken(refreshToken);
        if (DBUtils.countTables() > 0) {
            if (StringUtils.isBlank(KeyValueUtils.getValue(XR_DID))) {
                KeyValueUtils.updateInsert(XR_DID, createOrFindDeviceId());
            }
            //TODO presenter to be included
            DeviceModel.register(KeyValueUtils.getValue(XR_DID), deviceToken);
            Log.d(TAG, "Registered deviceId=" + deviceId);
        } else {
            Log.d(TAG, "No tables, skipping registering deviceId");
        }
    }

    public static String createOrFindDeviceId() {
        if (DBUtils.countTables() > 0 && StringUtils.isNotBlank(KeyValueUtils.getValue(XR_DID))) {
            /* Do not call UserUtils.getDeviceId() since it maps to this call. */
            deviceId = KeyValueUtils.getValue(XR_DID);
        } else {
            deviceId = UUID.randomUUID().toString();
        }

        return deviceId;
    }
}
