package com.noqapp.client.presenter.interfaces;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public interface NOQueueDBPresenterInterface {

    void dbSaved(boolean isCurrentQueue);

    void token_QueueList(List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist);
}
