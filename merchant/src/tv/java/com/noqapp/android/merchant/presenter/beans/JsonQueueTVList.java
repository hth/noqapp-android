package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-17 18:27
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonQueueTVList extends AbstractDomain implements Serializable {

    @JsonProperty("qs")
    private List<JsonQueueTV> queues = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonQueueTV> getQueues() {
        return queues;
    }

    public JsonQueueTVList setQueues(List<JsonQueueTV> queues) {
        this.queues = queues;
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
        return "JsonQueueTVList{" +
                "queues=" + queues +
                ", error=" + error +
                '}';
    }
}
