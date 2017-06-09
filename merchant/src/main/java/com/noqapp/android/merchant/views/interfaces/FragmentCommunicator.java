package com.noqapp.android.merchant.views.interfaces;

/**
 * User: chandra
 * Date: 5/27/17 10:00 AM
 */
public interface FragmentCommunicator {
    void passDataToFragment(
            String codeQR,
            String current_serving,
            String status,
            String lastNumber,
            String payload);
}
