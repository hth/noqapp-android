package com.noqapp.android.client.views.interfaces;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;


public interface TokenQueueViewInterface {

    void currentQueueSaved();

    void historyQueueSaved();

    void tokenQueueList(List<JsonTokenAndQueue> currentQueueList, List<JsonTokenAndQueue> historyQueueList);
}
