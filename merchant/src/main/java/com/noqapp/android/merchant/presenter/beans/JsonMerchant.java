package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.ErrorEncounteredJson;
import com.noqapp.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.common.beans.JsonProfile;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/22/17 11:47 AM
 */
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonMerchant implements Serializable {
    @JsonProperty("p")
    private JsonProfile jsonProfile;

    @JsonProperty ("pp")
    private JsonProfessionalProfilePersonal jsonProfessionalProfile;

    @JsonProperty("ts")
    private List<JsonTopic> topics = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonProfile getJsonProfile() {
        return jsonProfile;
    }

    public void setJsonProfile(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
    }

    public JsonProfessionalProfilePersonal getJsonProfessionalProfile() {
        return jsonProfessionalProfile;
    }

    public JsonMerchant setJsonProfessionalProfile(JsonProfessionalProfilePersonal jsonProfessionalProfile) {
        this.jsonProfessionalProfile = jsonProfessionalProfile;
        return this;
    }

    public List<JsonTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<JsonTopic> topics) {
        this.topics = topics;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("jsonProfile", jsonProfile)
                .append("topics", topics)
                .append("error", error)
                .toString();
    }
}
