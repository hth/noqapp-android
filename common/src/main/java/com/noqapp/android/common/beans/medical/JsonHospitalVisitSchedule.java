package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BooleanReplacementEnum;
import com.noqapp.android.common.model.types.medical.HospitalVisitForEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-07-19 08:17
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
public class JsonHospitalVisitSchedule extends AbstractDomain implements Serializable {

    @JsonProperty("id")
    private String hospitalVisitScheduleId;

    @JsonProperty("hv")
    private HospitalVisitForEnum hospitalVisitFor;

    @JsonProperty("vf")
    private Map<String, BooleanReplacementEnum> visitingFor;

    @JsonProperty("vh")
    private String header;

    @JsonProperty("vd")
    private String visitedDate;

    @JsonProperty("ed")
    private String expectedDate;

    @JsonProperty("pb")
    private String performedBy;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getHospitalVisitScheduleId() {
        return hospitalVisitScheduleId;
    }

    public JsonHospitalVisitSchedule setHospitalVisitScheduleId(String hospitalVisitScheduleId) {
        this.hospitalVisitScheduleId = hospitalVisitScheduleId;
        return this;
    }

    public HospitalVisitForEnum getHospitalVisitFor() {
        return hospitalVisitFor;
    }

    public JsonHospitalVisitSchedule setHospitalVisitFor(HospitalVisitForEnum hospitalVisitFor) {
        this.hospitalVisitFor = hospitalVisitFor;
        return this;
    }

    public Map<String, BooleanReplacementEnum> getVisitingFor() {
        return visitingFor;
    }

    public JsonHospitalVisitSchedule setVisitingFor(Map<String, BooleanReplacementEnum> visitingFor) {
        this.visitingFor = visitingFor;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public JsonHospitalVisitSchedule setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getVisitedDate() {
        return visitedDate;
    }

    public JsonHospitalVisitSchedule setVisitedDate(String visitedDate) {
        this.visitedDate = visitedDate;
        return this;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public JsonHospitalVisitSchedule setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
        return this;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public JsonHospitalVisitSchedule setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
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
        final StringBuilder sb = new StringBuilder("JsonHospitalVisitSchedule{");
        sb.append("visitingFor='").append(visitingFor).append('\'');
        sb.append(", header='").append(header).append('\'');
        sb.append(", visitedDate='").append(visitedDate).append('\'');
        sb.append(", expectedDate='").append(expectedDate).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}