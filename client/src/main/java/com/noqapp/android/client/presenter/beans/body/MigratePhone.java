package com.noqapp.android.client.presenter.beans.body;

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
public class MigratePhone {
    @JsonProperty("PH")
    private String phone;

    @JsonProperty("CS")
    private String countryShortName;

    @JsonProperty("TZ")
    private String timeZoneId;

    public String getPhone() {
        return phone;
    }

    public MigratePhone setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public MigratePhone setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public MigratePhone setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
        return this;
    }
}
