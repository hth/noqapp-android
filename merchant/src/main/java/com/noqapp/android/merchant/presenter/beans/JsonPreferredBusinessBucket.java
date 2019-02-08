package com.noqapp.android.merchant.presenter.beans;

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
 * 2019-01-23 18:25
 */
@SuppressWarnings({
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
public class JsonPreferredBusinessBucket implements Serializable {

    @JsonProperty("pbl")
    private List<JsonPreferredBusinessList> jsonPreferredBusinessLists = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonPreferredBusinessList> getJsonPreferredBusinessLists() {
        return jsonPreferredBusinessLists;
    }

    public JsonPreferredBusinessBucket setJsonPreferredBusinessLists(List<JsonPreferredBusinessList> jsonPreferredBusinessLists) {
        this.jsonPreferredBusinessLists = jsonPreferredBusinessLists;
        return this;
    }

    public JsonPreferredBusinessBucket addJsonPreferredBusinessList(JsonPreferredBusinessList jsonPreferredBusinessList) {
        this.jsonPreferredBusinessLists.add(jsonPreferredBusinessList);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonPreferredBusinessBucket setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonPreferredBusinessBucket{" +
                "jsonPreferredBusinessLists=" + jsonPreferredBusinessLists +
                ", error=" + error +
                '}';
    }
}