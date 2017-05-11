package com.noqapp.client.presenter;

import android.content.Context;

import com.noqapp.client.model.database.utils.NoQueueDB;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.noqapp.client.views.interfaces.TokenQueueViewInterface;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public class NoQueueDBPresenter implements NOQueueDBPresenterInterface {

    public TokenQueueViewInterface tokenQueueViewInterface;
    private Context context;

    public NoQueueDBPresenter(Context context) {
        this.context = context;
    }

    public void saveTokenQueue(List<JsonTokenAndQueue> tokenAndQueues, boolean isCurrentQueueCall) {
        NoQueueDB.queueDBPresenterInterface = this;

        /* Delete before inserting as this is always a fresh data on every call. */
        NoQueueDB.deleteCurrentQueue();
        NoQueueDB.save(tokenAndQueues, isCurrentQueueCall);
    }

    public void getCurrentAndHistoryTokenQueueListFromDB() {
        NoQueueDB.queueDBPresenterInterface = this;
        List<JsonTokenAndQueue> currentQueueList = NoQueueDB.getCurrentQueueList();
        List<JsonTokenAndQueue> historyQueueList = NoQueueDB.getHistoryQueueList();

        this.token_QueueList(currentQueueList, historyQueueList);
    }

    @Override
    public void dbSaved(int msg) {
        tokenQueueViewInterface.dataSavedStatus(msg);
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> currentQueueList, List<JsonTokenAndQueue> historyQueueList) {
        tokenQueueViewInterface.tokenQueueList(currentQueueList, historyQueueList);
    }
}
