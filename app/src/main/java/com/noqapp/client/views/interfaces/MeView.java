package com.noqapp.client.views.interfaces;

import com.noqapp.client.presenter.beans.JsonProfile;

/**
 * Created by omkar on 4/8/17.
 */

public interface MeView {

    void queueResponse(JsonProfile profile);
     void queueError();

}
