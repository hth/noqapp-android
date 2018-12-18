package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.io.Serializable;
import java.util.List;

public class TvObject implements Serializable {
    private JsonTopic jsonTopic;
    private List<JsonQueuedPerson> jsonQueuedPersonList;

    public JsonTopic getJsonTopic() {
        return jsonTopic;
    }

    public TvObject setJsonTopic(JsonTopic jsonTopic) {
        this.jsonTopic = jsonTopic;
        return this;
    }

    public List<JsonQueuedPerson> getJsonQueuedPersonList() {
        return jsonQueuedPersonList;
    }

    public TvObject setJsonQueuedPersonList(List<JsonQueuedPerson> jsonQueuedPersonList) {
        this.jsonQueuedPersonList = jsonQueuedPersonList;
        return this;
    }
}
