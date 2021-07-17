package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 7/16/21 3:35 PM
 */
@SuppressWarnings ({
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
public class JsonScheduleFlex extends AbstractDomain {

    @JsonProperty("ts")
    private String timeSlot;

    @JsonProperty("ba")
    private long bookedAppointments;

    @JsonProperty("ta")
    private int totalAppointments;

    @JsonProperty("st")
    private int startTime;

    public String getTimeSlot() {
        return timeSlot;
    }

    public JsonScheduleFlex setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }

    public long getBookedAppointments() {
        return bookedAppointments;
    }

    public JsonScheduleFlex setBookedAppointments(long bookedAppointments) {
        this.bookedAppointments = bookedAppointments;
        return this;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public JsonScheduleFlex setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public int getStartTime() {
        return startTime;
    }

    public JsonScheduleFlex setStartTime(int startTime) {
        this.startTime = startTime;
        return this;
    }
}
