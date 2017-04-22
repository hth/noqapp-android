package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.JsonProfile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void queueResponse(JsonProfile profile);

    void queueError();
}
