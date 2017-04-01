package com.noqapp.client.presenter;

import android.content.Context;

import com.noqapp.client.model.database.NoQueueDB;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.interfaces.NOQueueDBPreseneterInterface;
import com.noqapp.client.views.interfaces.Token_QueueViewInterface;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public class NoQueueDBPresenter implements NOQueueDBPreseneterInterface {

    private Context context;
    public Token_QueueViewInterface tokenQueueViewInterface;

    public NoQueueDBPresenter(Context context) {
        this.context = context;
    }

    public void saveToken_Queue(List<JsonTokenAndQueue> listTokenAndQueue) {
        NoQueueDB queueDB = new NoQueueDB(context);
        queueDB.queueDBPreseneterInterface = this;
        queueDB.save(listTokenAndQueue);

    }


    @Override
    public void dbSaved(String msg) {
        tokenQueueViewInterface.dataSavedStatus(msg);
    }
}
