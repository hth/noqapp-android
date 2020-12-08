package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.AppointmentStateEnum;
import com.noqapp.android.common.model.types.AppointmentStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.utils.Formatter;

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
    private String queueUserId;

    @JsonProperty("gq")
    private String guardianQid;

    @JsonProperty("as")
    private AppointmentStatusEnum appointmentStatus;

    @JsonProperty("cc")
    private String chiefComplain;

    @JsonProperty("jp")
    private JsonProfile jsonProfile;

    @JsonProperty("qd")
    private JsonQueueDisplay jsonQueueDisplay;

    @JsonProperty("ps")
    private AppointmentStateEnum appointmentState;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    // add to keep track the actual assign slot in case of splitting the slot to show in appointment UI
    @JsonIgnore
    private int multipleSlotStartTiming;

    @JsonIgnore
    private int multipleSlotEndTiming;

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

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonSchedule setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getGuardianQid() {
        return guardianQid;
    }

    public JsonSchedule setGuardianQid(String guardianQid) {
        this.guardianQid = guardianQid;
        return this;
    }

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public JsonSchedule setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public JsonSchedule setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public JsonProfile getJsonProfile() {
        return jsonProfile;
    }

    public JsonSchedule setJsonProfile(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
        return this;
    }

    public JsonQueueDisplay getJsonQueueDisplay() {
        return jsonQueueDisplay;
    }

    public JsonSchedule setJsonQueueDisplay(JsonQueueDisplay jsonQueueDisplay) {
        this.jsonQueueDisplay = jsonQueueDisplay;
        return this;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public JsonSchedule setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    @JsonIgnoreProperties
    public int getMultipleSlotStartTiming() {
        return multipleSlotStartTiming;
    }

    @JsonIgnoreProperties
    public void setMultipleSlotStartTiming(int multipleSlotStartTiming) {
        this.multipleSlotStartTiming = multipleSlotStartTiming;
    }

    @JsonIgnoreProperties
    public int getMultipleSlotEndTiming() {
        return multipleSlotEndTiming;
    }

    @JsonIgnoreProperties
    public void setMultipleSlotEndTiming(int multipleSlotEndTiming) {
        this.multipleSlotEndTiming = multipleSlotEndTiming;
    }

    public String getAppointmentTimeByAppointmentState() {
        switch (appointmentState) {
            case S:
                return "Walk-In Token";
            case A:
            case O:
            case F:
            default:
                return Formatter.convertMilitaryTo12HourFormat(startTime);
        }
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonSchedule{" +
                "scheduleAppointmentId='" + scheduleAppointmentId + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", scheduleDate='" + scheduleDate + '\'' +
                ", totalAppointments=" + totalAppointments +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", queueUserId='" + queueUserId + '\'' +
                ", guardianQid='" + guardianQid + '\'' +
                ", appointmentStatus=" + appointmentStatus +
                ", chiefComplain='" + chiefComplain + '\'' +
                ", jsonProfile=" + jsonProfile +
                ", jsonQueueDisplay=" + jsonQueueDisplay +
                ", error=" + error +
                ", multipleSlotStartTiming=" + multipleSlotStartTiming +
                ", multipleSlotEndTiming=" + multipleSlotEndTiming +
                '}';
    }
}
