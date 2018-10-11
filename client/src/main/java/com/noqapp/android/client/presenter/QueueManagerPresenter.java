package com.noqapp.android.client.presenter;

import com.noqapp.android.client.presenter.beans.JsonProfessionalProfile;
import com.noqapp.android.common.presenter.ResponseErrorPresenter;

/**
 * User: chandra
 * Date: 5/10/17 8:28 PM
 */

public interface QueueManagerPresenter extends ResponseErrorPresenter {

    void queueManagerResponse(JsonProfessionalProfile jsonProfessionalProfile);

}
