package com.noqapp.android.common.presenter;

import com.noqapp.android.common.beans.ErrorEncounteredJson;

public interface ResponseErrorPresenter extends AuthenticationFailure{

    void responseErrorPresenter(ErrorEncounteredJson eej);

    void responseErrorPresenter(int errorCode);

}
