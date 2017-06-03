package com.noqapp.android.merchant.views.interfaces;

/**
 * Created by chandra on 5/27/17.
 */

public interface FragmentCommunicator {
    public void passDataToFragment(String qrcodeValue, String current_serving, String status, String lastno, String payload);
}
