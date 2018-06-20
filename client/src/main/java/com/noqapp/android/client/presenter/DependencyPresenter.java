package com.noqapp.android.client.presenter;

import com.noqapp.common.beans.JsonProfile;



public interface DependencyPresenter {

    void dependencyResponse(JsonProfile profile);

    void dependencyError();

    void authenticationFailure(int errorCode);
}
