package com.noqapp.client.presenter;

import com.noqapp.client.presenter.beans.Profile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void queueResponse(Profile profile);

    void queueError();
}
