package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonToken;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: hitender
 * Date: 4/1/17 12:22 PM
 */

public interface TokenPresenter extends ResponseErrorPresenter {

    void tokenPresenterResponse(JsonToken token);

    void tokenPresenterError();

    void authenticationFailure(int errorCode);
}
