package com.noqapp.android.common.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class UpdateProfile {
    @JsonProperty("QID")
    private String queueUserId;

    @JsonProperty("FN")
    private String firstName;

    @JsonProperty("BD")
    private String birthday;

    @JsonProperty("GE")
    private String gender;

    @JsonProperty("TZ")
    private String timeZoneId;

    public String getQueueUserId() {
        return queueUserId;
    }

    public UpdateProfile setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UpdateProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public UpdateProfile setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public UpdateProfile setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public UpdateProfile setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
        return this;
    }
}
