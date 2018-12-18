package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonQueuedPerson;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.io.Serializable;
import java.util.List;

public class TvObject implements Serializable {
    private JsonTopic jsonTopic;
    private JsonQueueTV jsonQueueTV;

    public JsonTopic getJsonTopic() {
        return jsonTopic;
    }

    public TvObject setJsonTopic(JsonTopic jsonTopic) {
        this.jsonTopic = jsonTopic;
        return this;
    }

    public JsonQueueTV getJsonQueueTV() {
        return jsonQueueTV;
    }

    public TvObject setJsonQueueTV(JsonQueueTV jsonQueueTV) {
        this.jsonQueueTV = jsonQueueTV;
        return this;
    }
}
