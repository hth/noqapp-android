package com.noqapp.android.client.views.interfaces;


import com.noqapp.android.common.beans.JsonProfile;

public interface MeView {

    void queueResponse(JsonProfile profile, String email, String auth);

    void queueError();

}
