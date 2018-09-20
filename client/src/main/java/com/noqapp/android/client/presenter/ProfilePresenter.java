package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter extends ResponseErrorPresenter {

    void profileResponse(JsonProfile profile, String email, String auth);

    void profileError();

    void authenticationFailure(int errorCode);
}
