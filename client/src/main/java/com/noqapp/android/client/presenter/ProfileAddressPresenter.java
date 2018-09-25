package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonUserAddressList;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: hitender
 * Date: 4/8/17 8:28 PM
 */

public interface ProfileAddressPresenter extends ResponseErrorPresenter {

    void profileAddressResponse(JsonUserAddressList jsonUserAddressList);

    void profileAddressError();
}
