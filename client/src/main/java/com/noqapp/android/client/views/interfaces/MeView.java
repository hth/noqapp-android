package com.noqapp.android.client.views.interfaces;

import com.noqapp.android.client.presenter.beans.JsonProfile;

/**
 * Created by omkar on 4/8/17.
 */

public interface MeView {

    void queueResponse(JsonProfile profile, String email, String auth);

    void queueError();

}
