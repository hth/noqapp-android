package com.noqapp.android.client.presenter;

import com.noqapp.android.client.model.database.utils.TokenAndQueueDB;
import com.noqapp.android.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.android.client.views.interfaces.TokenQueueViewInterface;

import java.util.List;


public class NoQueueDBPresenter {

    public TokenQueueViewInterface tokenQueueViewInterface;

    public void saveHistoryTokenQueue(List<JsonTokenAndQueue> tokenAndQueues, boolean sinceBeginning) {
        if (sinceBeginning) {
            TokenAndQueueDB.deleteHistoryQueue();
        }
        boolean msg = TokenAndQueueDB.saveHistoryQueue(tokenAndQueues);
        if (msg) {
            tokenQueueViewInterface.historyQueueSaved();
        }
    }


    public void saveCurrentTokenQueue(List<JsonTokenAndQueue> tokenAndQueues) {
        /* Delete before inserting as this is always a fresh data on every call. */
        TokenAndQueueDB.deleteCurrentQueue();
        boolean msg = TokenAndQueueDB.saveCurrentQueue(tokenAndQueues);
        if (msg) {
            tokenQueueViewInterface.currentQueueSaved();
        }
    }

    public void getCurrentTokenQueueListFromDB() {
        List<JsonTokenAndQueue> currentQueueList = TokenAndQueueDB.getCurrentQueueList();
        tokenQueueViewInterface.tokenCurrentQueueList(currentQueueList);
    }

    public void getHistoryTokenQueueListFromDB() {
        List<JsonTokenAndQueue> historyQueueList = TokenAndQueueDB.getHistoryQueueList();
        tokenQueueViewInterface.tokenHistoryQueueList(historyQueueList);
    }
}
