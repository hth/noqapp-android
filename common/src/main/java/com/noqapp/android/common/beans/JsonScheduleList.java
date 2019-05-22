package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-22 10:44
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
public class JsonScheduleList extends AbstractDomain implements Serializable {

    @JsonProperty("scs")
    private List<JsonSchedule> jsonSchedules = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonSchedule> getJsonSchedules() {
        return jsonSchedules;
    }

    public JsonScheduleList setJsonSchedules(List<JsonSchedule> jsonSchedules) {
        this.jsonSchedules = jsonSchedules;
        return this;
    }

    public JsonScheduleList addJsonSchedule(JsonSchedule jsonSchedule) {
        this.jsonSchedules.add(jsonSchedule);
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
        return "JsonScheduleList{" +
                "jsonSchedules=" + jsonSchedules +
                ", error=" + error +
                '}';
    }
}