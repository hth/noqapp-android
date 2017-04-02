package com.noqapp.client.views.interfaces;

import com.noqapp.client.presenter.beans.JsonTokenAndQueue;

import java.util.List;

/**
 * Created by omkar on 4/1/17.
 */

public interface Token_QueueViewInterface {

    void dataSavedStatus(int msg);
    void token_QueueList(List<JsonTokenAndQueue> list);
}
