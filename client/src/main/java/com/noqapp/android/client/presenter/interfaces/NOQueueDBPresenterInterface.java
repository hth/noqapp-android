package com.noqapp.android.client.presenter.interfaces;

import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public interface NOQueueDBPresenterInterface {

    void dbSaved(boolean isCurrentQueue);

    void token_QueueList(List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist);
}
