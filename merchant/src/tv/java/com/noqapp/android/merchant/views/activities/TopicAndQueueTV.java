package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.io.Serializable;

public class TopicAndQueueTV implements Serializable {
    private JsonTopic jsonTopic;
    private JsonQueueTV jsonQueueTV;

    public JsonTopic getJsonTopic() {
        return jsonTopic;
    }

    public TopicAndQueueTV setJsonTopic(JsonTopic jsonTopic) {
        this.jsonTopic = jsonTopic;
        return this;
    }

    public JsonQueueTV getJsonQueueTV() {
        return jsonQueueTV;
    }

    public TopicAndQueueTV setJsonQueueTV(JsonQueueTV jsonQueueTV) {
        this.jsonQueueTV = jsonQueueTV;
        return this;
    }
}
