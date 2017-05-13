package com.noqapp.client.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noqapp.client.model.DeviceModel;
import com.noqapp.client.model.database.DBUtils;
import com.noqapp.client.model.database.utils.KeyValueUtils;
import com.noqapp.client.presenter.beans.body.DeviceToken;
import com.noqapp.client.views.activities.NoQueueBaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import static com.noqapp.client.model.database.utils.KeyValueUtils.KEYS.XR_DID;

public class NoQueueFirebaseInstanceServices extends FirebaseInstanceIdService {
    private final String TAG = NoQueueFirebaseInstanceServices.class.getSimpleName();
    private String deviceId="";


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
                NoQueueBaseActivity.mypref, Context.MODE_PRIVATE);
        deviceId =sharedpreferences.getString(NoQueueBaseActivity.KEY_DEVICE_ID, "");
        if(deviceId.equals("")){
            deviceId = UUID.randomUUID().toString();
            setSharPreferanceDeviceID(sharedpreferences,deviceId);
            Log.v("device id_created",deviceId);

        }else {
            Log.v("device id exist", deviceId);
        }
        DeviceModel.register(deviceId, deviceToken);

    }

    private void setSharPreferanceDeviceID(SharedPreferences sharedpreferences, String deviceid) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(NoQueueBaseActivity.KEY_DEVICE_ID, deviceid);
        editor.commit();
    }


}
