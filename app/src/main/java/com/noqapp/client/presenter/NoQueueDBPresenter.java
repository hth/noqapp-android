package com.noqapp.client.presenter;

import android.content.Context;

import com.noqapp.client.model.database.NoQueueDB;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPresenterInterface;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public class NoQueueDBPresenter implements NOQueueDBPresenterInterface {

    public Token_QueueViewInterface tokenQueueViewInterface;
    private Context context;

    public NoQueueDBPresenter(Context context) {
        this.context = context;
    }

    public void saveToken_Queue(List<JsonTokenAndQueue> listTokenAndQueue, boolean isCurrentQueueCall) {
        NoQueueDB queueDB = new NoQueueDB(context);
        queueDB.queueDBPresenterInterface = this;
        queueDB.save(listTokenAndQueue, isCurrentQueueCall);

    }

    public void currentandHistoryTokenQueueListFromDB() {
        NoQueueDB queueDB = new NoQueueDB(context);
        queueDB.queueDBPresenterInterface = this;
        List<JsonTokenAndQueue> list = queueDB.getCurrentQueueList();
        List<JsonTokenAndQueue> historyQueuelist = queueDB.getHistoryQueueList();

        this.token_QueueList(list, historyQueuelist);
    }


    @Override
    public void dbSaved(int msg) {
        tokenQueueViewInterface.dataSavedStatus(msg);
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> list, List<JsonTokenAndQueue> historylist) {
        tokenQueueViewInterface.token_QueueList(list, historylist);
    }


}
