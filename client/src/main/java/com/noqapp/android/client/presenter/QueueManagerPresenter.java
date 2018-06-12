package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface QueueManagerPresenter {

    void queueManagerResponse(JsonProfessionalProfile jsonProfessionalProfile);

    void queueManagerError();

    void authenticationFailure(int errorCode);
}
