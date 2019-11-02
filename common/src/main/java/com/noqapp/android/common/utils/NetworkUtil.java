package com.noqapp.android.common.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private Activity context;

    public NetworkUtil(Activity context) {
        this.context = context;
    }

    public boolean isOnline() {
        NetworkInfo info = getNetworkInfo();
        if (null == info) {
            return false;
        }
        return info.isConnected();
    }

    public boolean isNotOnline() {
        return !isOnline();
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
