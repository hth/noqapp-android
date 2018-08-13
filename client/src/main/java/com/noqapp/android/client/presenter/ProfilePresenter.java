package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonProfile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void queueResponse(JsonProfile profile, String email, String auth);

    void queueError();

    void queueError(String error);

    void authenticationFailure(int errorCode);
}
