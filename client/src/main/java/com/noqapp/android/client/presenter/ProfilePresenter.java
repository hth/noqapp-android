package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonProfile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void queueResponse(JsonProfile profile, String email, String auth);

    void queueError();
}
