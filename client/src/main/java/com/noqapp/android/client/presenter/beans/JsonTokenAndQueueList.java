package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.ErrorEncounteredJson;

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
public class JsonTokenAndQueueList {

    @JsonProperty("sb")
    private boolean sinceBeginning;

    @JsonProperty("tqs")
    private List<JsonTokenAndQueue> tokenAndQueues = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonTokenAndQueue> getTokenAndQueues() {
        return tokenAndQueues;
    }

    public void setTokenAndQueues(List<JsonTokenAndQueue> tokenAndQueues) {
        this.tokenAndQueues = tokenAndQueues;
    }

    public boolean isSinceBeginning() {
        return sinceBeginning;
    }

    public void setSinceBeginning(boolean sinceBeginning) {
        this.sinceBeginning = sinceBeginning;
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
