package com.noqapp.client.views.interfaces;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public interface TokenQueueViewInterface {

    void dataSavedStatus(int msg);

    void tokenQueueList(List<JsonTokenAndQueue> currentQueueList, List<JsonTokenAndQueue> historyQueueList);
}
