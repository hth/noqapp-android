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

    private Context context;
    public Token_QueueViewInterface tokenQueueViewInterface;

    public NoQueueDBPresenter(Context context) {
        this.context = context;
    }

    public void saveToken_Queue(List<JsonTokenAndQueue> listTokenAndQueue) {
        NoQueueDB queueDB = new NoQueueDB(context);
        queueDB.queueDBPresenterInterface = this;
        queueDB.save(listTokenAndQueue);

    }

    public void currentTokenQueueListFromDB()
    {
        NoQueueDB queueDB = new NoQueueDB(context);
        queueDB.queueDBPresenterInterface = this;
        List<JsonTokenAndQueue> list = queueDB.getCurrentQueueList();
        this.token_QueueList(list);
    }


    @Override
    public void dbSaved(int msg) {
        tokenQueueViewInterface.dataSavedStatus(msg);
    }

    @Override
    public void token_QueueList(List<JsonTokenAndQueue> list) {
        tokenQueueViewInterface.token_QueueList(list);
    }
}
