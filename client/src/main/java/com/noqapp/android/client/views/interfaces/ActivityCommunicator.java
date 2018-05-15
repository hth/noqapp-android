package com.noqapp.android.client.views.interfaces;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;

public interface ActivityCommunicator {

    boolean updateUI(String qrCode, JsonTokenAndQueue jq, String go_to);

    void requestProcessed(String qrCode);
}
