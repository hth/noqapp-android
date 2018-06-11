package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.merchant.presenter.beans.JsonToken;

import java.util.HashMap;

/**
 * User: chandra
 * Date: 5/27/17 10:00 AM
 */
public interface AdapterCallback {

    void onMethodCallback(JsonToken token);

    void saveCounterNames(String codeQR, String name);

    HashMap<String, String> getNameList();
}