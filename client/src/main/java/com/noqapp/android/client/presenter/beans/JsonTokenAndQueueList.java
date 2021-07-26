package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonScheduleList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/18/17 11:01 AM
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTokenAndQueueList implements Serializable {

    @JsonProperty("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    @JsonProperty ("jsl")
    private JsonScheduleList jsonScheduleList = new JsonScheduleList();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public void setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
    }

    public JsonScheduleList getJsonScheduleList() {
        return jsonScheduleList;
    }

    public JsonTokenAndQueueList setJsonScheduleList(JsonScheduleList jsonScheduleList) {
        this.jsonScheduleList = jsonScheduleList;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueueList{" +
                "tokenAndQueues=" + tokenAndQueues +
                ", error=" + error +
                '}';
    }
}
