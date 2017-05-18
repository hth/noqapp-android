package com.noqapp.merchant.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    private Activity context;

    public NetworkHelper(Activity context) {
        this.context = context;
    }

    public boolean isOnline() {
        NetworkInfo info = getNetworkInfo();
        return null != info && info.isConnected();
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
