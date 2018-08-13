package com.noqapp.android.client.utils;

import org.greenrobot.eventbus.EventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Copyright (C) 2016 Mikhael LOPEZ
 * Licensed under the Apache License Version 2.0
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // send network state changed
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            // there is Internet connection
            EventBus.getDefault().post(new NetworkStateChanged(true));
        } else {
            // no Internet connection
            EventBus.getDefault().post(new NetworkStateChanged(false));
        }
    }
}
