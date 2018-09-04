package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.JsonProfile;


public interface DependencyPresenter {

    void dependencyResponse(JsonProfile profile);

    void dependencyError();

    void authenticationFailure(int errorCode);
}
