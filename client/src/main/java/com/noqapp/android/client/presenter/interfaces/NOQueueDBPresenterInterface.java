package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;


public interface NOQueueDBPresenterInterface {

    void dbSaved(boolean isCurrentQueue);

    void token_QueueList(List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist);
}
