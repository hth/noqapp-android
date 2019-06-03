package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
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

    @JsonProperty("hours")
    private List<JsonHour> jsonHours = new LinkedList<>();

    @JsonProperty("pe")
    private boolean appointmentEnable;

    @JsonProperty("pd")
    private int appointmentDuration;

    @JsonProperty("pf")
    private int appointmentOpenHowFar;

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

    public List<JsonHour> getJsonHours() {
        return jsonHours;
    }

    public JsonScheduleList setJsonHours(List<JsonHour> jsonHours) {
        this.jsonHours = jsonHours;
        return this;
    }

    public boolean isAppointmentEnable() {
        return appointmentEnable;
    }

    public JsonScheduleList setAppointmentEnable(boolean appointmentEnable) {
        this.appointmentEnable = appointmentEnable;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public JsonScheduleList setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public JsonScheduleList setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
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
        final StringBuilder sb = new StringBuilder("JsonScheduleList{");
        sb.append("jsonSchedules=").append(jsonSchedules);
        sb.append(", jsonHours=").append(jsonHours);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}