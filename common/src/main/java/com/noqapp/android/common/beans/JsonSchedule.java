package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.AppointmentStatusEnum;

import java.io.Serializable;

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
public class JsonSchedule extends AbstractDomain implements Serializable {

    @JsonProperty("id")
    private String scheduleAppointmentId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("sd")
    private String scheduleDate;

    @JsonProperty("ta")
    private int totalAppointments;

    @JsonProperty("st")
    private int startTime;

    @JsonProperty("et")
    private int endTime;

    @JsonProperty("qid")
    private String qid;

    @JsonProperty("as")
    private AppointmentStatusEnum appointmentStatus;

    @JsonProperty("jp")
    private JsonProfile jsonProfile;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getScheduleAppointmentId() {
        return scheduleAppointmentId;
    }

    public JsonSchedule setScheduleAppointmentId(String scheduleAppointmentId) {
        this.scheduleAppointmentId = scheduleAppointmentId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonSchedule setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public JsonSchedule setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
        return this;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public JsonSchedule setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public int getStartTime() {
        return startTime;
    }

    public JsonSchedule setStartTime(int startTime) {
        this.startTime = startTime;
        return this;
    }

    public int getEndTime() {
        return endTime;
    }

    public JsonSchedule setEndTime(int endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public JsonSchedule setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public JsonSchedule setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public JsonProfile getJsonProfile() {
        return jsonProfile;
    }

    public JsonSchedule setJsonProfile(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
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
        final StringBuilder sb = new StringBuilder("JsonSchedule{");
        sb.append("scheduleAppointmentId='").append(scheduleAppointmentId).append('\'');
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append(", scheduleDate='").append(scheduleDate).append('\'');
        sb.append(", totalAppointments=").append(totalAppointments);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", qid='").append(qid).append('\'');
        sb.append(", appointmentStatus=").append(appointmentStatus);
        sb.append(", jsonProfile=").append(jsonProfile);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
