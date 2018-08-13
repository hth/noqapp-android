package com.noqapp.android.client.utils;

import org.greenrobot.eventbus.EventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
    static EventBus bus = EventBus.getDefault();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean isNetwork = NetworkUtils.getConnectivityStatusString(context);
        if (isNetwork) {
            //Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
            try {
                bus.post(true);
                Log.w("-------", "connect ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //Toast.makeText(context, "dis connected", Toast.LENGTH_SHORT).show();
            try {
                bus.post(false);
                Log.w("-------", "dis connect ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

