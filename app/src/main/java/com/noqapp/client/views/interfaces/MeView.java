package com.noqapp.client.views.interfaces;

import com.noqapp.client.presenter.beans.Profile;

/**
 * Created by omkar on 4/8/17.
 */

public interface MeView {

    void queueResponse(Profile profile);
     void queueError();

}
