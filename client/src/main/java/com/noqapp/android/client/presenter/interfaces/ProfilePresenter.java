package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.common.beans.JsonProfile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void profileResponse(JsonProfile profile, String email, String auth);

    void profileError();

    void profileError(String error);

    void authenticationFailure(int errorCode);
}
