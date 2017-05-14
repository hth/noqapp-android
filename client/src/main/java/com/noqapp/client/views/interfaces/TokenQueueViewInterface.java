package com.noqapp.client.views.interfaces;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public interface TokenQueueViewInterface {

    void currentQueueSaved();
    void historyQueueSaved();

    void tokenQueueList(List<JsonTokenAndQueue> currentQueueList, List<JsonTokenAndQueue> historyQueueList);
}
