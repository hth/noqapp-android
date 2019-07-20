package com.noqapp.android.common.beans.medical;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;

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

    @JsonProperty("vn")
    private String name;

    @JsonProperty("vh")
    private String header;

    @JsonProperty("vd")
    private String visitedDate;

    @JsonProperty("ed")
    private String expectedDate;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getName() {
        return name;
    }

    public JsonHospitalVisitSchedule setName(String name) {
        this.name = name;
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

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonHospitalVisitSchedule{");
        sb.append("name='").append(name).append('\'');
        sb.append(", header='").append(header).append('\'');
        sb.append(", visitedDate='").append(visitedDate).append('\'');
        sb.append(", expectedDate='").append(expectedDate).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}