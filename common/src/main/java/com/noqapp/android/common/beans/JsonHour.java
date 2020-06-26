package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 3/26/18 10:52 PM
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
public class JsonHour implements Serializable {

    @JsonProperty("dw")
    private int dayOfWeek;

    @JsonProperty("tf")
    private int tokenAvailableFrom;

    @JsonProperty("sh")
    private int startHour;

    @JsonProperty ("as")
    private int appointmentStartHour;

    @JsonProperty("te")
    private int tokenNotAvailableFrom;

    @JsonProperty("eh")
    private int endHour;

    @JsonProperty("ae")
    private int appointmentEndHour;

    @JsonProperty("ls")
    private int lunchTimeStart;

    @JsonProperty("le")
    private int lunchTimeEnd;

    @JsonProperty("pj")
    private boolean preventJoining;

    @JsonProperty("dc")
    private boolean dayClosed = false;

    /* When business queue delays the start time. Delayed by minutes. */
    @JsonProperty("de")
    private int delayedInMinutes = 0;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public JsonHour setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonHour setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public JsonHour setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getAppointmentStartHour() {
        return appointmentStartHour;
    }

    public JsonHour setAppointmentStartHour(int appointmentStartHour) {
        this.appointmentStartHour = appointmentStartHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public JsonHour setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public JsonHour setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getAppointmentEndHour() {
        return appointmentEndHour;
    }

    public JsonHour setAppointmentEndHour(int appointmentEndHour) {
        this.appointmentEndHour = appointmentEndHour;
        return this;
    }

    public int getLunchTimeStart() {
        return lunchTimeStart;
    }

    public void setLunchTimeStart(int lunchTimeStart) {
        this.lunchTimeStart = lunchTimeStart;
    }

    public int getLunchTimeEnd() {
        return lunchTimeEnd;
    }

    public void setLunchTimeEnd(int lunchTimeEnd) {
        this.lunchTimeEnd = lunchTimeEnd;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public JsonHour setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public JsonHour setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public JsonHour setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonHour{");
        sb.append("dayOfWeek=").append(dayOfWeek);
        sb.append(", tokenAvailableFrom=").append(tokenAvailableFrom);
        sb.append(", startHour=").append(startHour);
        sb.append(", appointmentStartHour=").append(appointmentStartHour);
        sb.append(", tokenNotAvailableFrom=").append(tokenNotAvailableFrom);
        sb.append(", endHour=").append(endHour);
        sb.append(", appointmentEndHour=").append(appointmentEndHour);
        sb.append(", preventJoining=").append(preventJoining);
        sb.append(", dayClosed=").append(dayClosed);
        sb.append(", delayedInMinutes=").append(delayedInMinutes);
        sb.append('}');
        return sb.toString();
    }
}