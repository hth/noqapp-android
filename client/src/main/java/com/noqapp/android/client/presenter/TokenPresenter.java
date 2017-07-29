package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonToken;

/**
 * User: hitender
 * Date: 4/1/17 12:22 PM
 */

public interface TokenPresenter {
    void tokenPresenterResponse(JsonToken token);

    void tokenPresenterError();

    void authenticationFailure(int errorCode);
}
