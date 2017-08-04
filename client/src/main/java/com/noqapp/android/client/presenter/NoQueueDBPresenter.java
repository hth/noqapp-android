package com.noqapp.android.client.presenter;

import android.content.Context;

import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;

import java.util.List;

/**
 * User: omkar
 * Date: 4/1/17 12:37 PM
 */
public class NoQueueDBPresenter implements NOQueueDBPresenterInterface {

    public TokenQueueViewInterface tokenQueueViewInterface;
    private Context context;

    public NoQueueDBPresenter(Context context) {
        this.context = context;
    }

    public void saveTokenQueue(List<JsonTokenAndQueue> tokenAndQueues, boolean isCurrentQueueCall, boolean sinceBeginning) {
        TokenAndQueueDB.queueDBPresenterInterface = this;

        if (isCurrentQueueCall) {
            /* Delete before inserting as this is always a fresh data on every call. */
            TokenAndQueueDB.deleteCurrentQueue();
            TokenAndQueueDB.saveCurrentQueue(tokenAndQueues);
        } else {
            if (sinceBeginning) {
                TokenAndQueueDB.deleteHistoryQueue();
            }
            TokenAndQueueDB.saveHistoryQueue(tokenAndQueues);
        }
        //TokenAndQueueDB.save(tokenAndQueues, isCurrentQueueCall);
    }

    public void getCurrentAndHistoryTokenQueueListFromDB() {
        TokenAndQueueDB.queueDBPresenterInterface = this;
        List<JsonTokenAndQueue> currentQueueList = TokenAndQueueDB.getCurrentQueueList();
        List<JsonTokenAndQueue> historyQueueList = TokenAndQueueDB.getHistoryQueueList();

        this.token_QueueList(currentQueueList, historyQueueList);
    }

    @Override
    public void dbSaved(boolean msg) {
        if (msg) {
            tokenQueueViewInterface.currentQueueSaved();
        } else {
            tokenQueueViewInterface.historyQueueSaved();
        }
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> currentQueueList, List<JsonTokenAndQueue> historyQueueList) {
        tokenQueueViewInterface.tokenQueueList(currentQueueList, historyQueueList);
    }
}
