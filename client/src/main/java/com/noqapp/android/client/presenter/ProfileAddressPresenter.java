package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.common.beans.JsonProfile;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfileAddressPresenter {

    void profileAddressResponse(JsonUserAddressList jsonUserAddressList);

    void profileAddressError();

    void authenticationFailure(int errorCode);
}
