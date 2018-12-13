package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.JsonHour;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 4/16/17 5:49 PM
 */

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTopic extends JsonToken implements Serializable {

    @JsonProperty("o")
    private String topic;

    @JsonProperty("hour")
    private JsonHour hour;

    @JsonProperty("dv")
    private JsonDataVisibility jsonDataVisibility;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public JsonHour getHour() {
        return hour;
    }

    public JsonTopic setHour(JsonHour hour) {
        this.hour = hour;
        return this;
    }

    public JsonDataVisibility getJsonDataVisibility() {
        return jsonDataVisibility;
    }

    public JsonTopic setJsonDataVisibility(JsonDataVisibility jsonDataVisibility) {
        this.jsonDataVisibility = jsonDataVisibility;
        return this;
    }
}
