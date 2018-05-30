package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonProfile;
import com.noqapp.android.client.presenter.beans.JsonResponse;
import com.noqapp.android.client.presenter.beans.JsonUserAddressList;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfilePresenter {

    void queueResponse(JsonProfile profile, String email, String auth);

    void profileAddressResponse(JsonUserAddressList jsonUserAddressList);

    void queueError();

    void queueError(String error);

    void authenticationFailure(int errorCode);
}
