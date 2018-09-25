package com.noqapp.android.client.presenter;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;


public interface DependencyPresenter extends ResponseErrorPresenter {

    void dependencyResponse(JsonProfile profile);

    void dependencyError();
}
