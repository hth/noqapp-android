package com.noqapp.android.merchant.views.interfaces;

import com.noqapp.android.merchant.presenter.beans.JsonToken;

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

    void acquireCustomer(JsonToken token);

    /**
     * This method to update the list of people in Q if user
     * cancel the token
     */
    void updatePeopleQueue( String codeQR);
}
