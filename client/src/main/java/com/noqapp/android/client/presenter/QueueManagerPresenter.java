package com.noqapp.android.client.presenter;

import com.noqapp.library.beans.JsonHealthCareProfile;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface QueueManagerPresenter {

    void queueManagerResponse(JsonHealthCareProfile jsonHealthCareProfile);

    void queueManagerError();

    void authenticationFailure(int errorCode);
}
