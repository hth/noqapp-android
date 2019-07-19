package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 08:36
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
public class JsonHospitalVisitScheduleList extends AbstractDomain implements Serializable {

    @JsonProperty("ir")
    private List<JsonHospitalVisitSchedule> jsonHospitalVisitScheduleStaticData = new ArrayList<>();

    @JsonProperty("ims")
    private List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonHospitalVisitSchedule> getJsonHospitalVisitScheduleStaticData() {
        return jsonHospitalVisitScheduleStaticData;
    }

    public JsonHospitalVisitScheduleList setJsonHospitalVisitScheduleStaticData(List<JsonHospitalVisitSchedule> jsonHospitalVisitScheduleStaticData) {
        this.jsonHospitalVisitScheduleStaticData = jsonHospitalVisitScheduleStaticData;
        return this;
    }

    public JsonHospitalVisitScheduleList addJsonImmunizationStaticData(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        this.jsonHospitalVisitScheduleStaticData.add(jsonHospitalVisitSchedule);
        return this;
    }

    public List<JsonHospitalVisitSchedule> getJsonHospitalVisitSchedules() {
        return jsonHospitalVisitSchedules;
    }

    public JsonHospitalVisitScheduleList setJsonHospitalVisitSchedules(List<JsonHospitalVisitSchedule> jsonHospitalVisitSchedules) {
        this.jsonHospitalVisitSchedules = jsonHospitalVisitSchedules;
        return this;
    }

    public JsonHospitalVisitScheduleList addJsonImmunization(JsonHospitalVisitSchedule jsonHospitalVisitSchedule) {
        this.jsonHospitalVisitSchedules.add(jsonHospitalVisitSchedule);
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
        final StringBuilder sb = new StringBuilder("JsonHospitalVisitScheduleList{");
        sb.append("jsonHospitalVisitScheduleStaticData=").append(jsonHospitalVisitScheduleStaticData);
        sb.append(", jsonHospitalVisitSchedules=").append(jsonHospitalVisitSchedules);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
