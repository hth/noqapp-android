package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 4/9/17 5:15 PM
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
//@JsonInclude (JsonInclude.Include.NON_NULL)
public class ErrorEncounteredJson {

    @JsonProperty("systemError")
    private String systemError;

    @JsonProperty("systemErrorCode")
    private String systemErrorCode;

    @JsonProperty("reason")
    private String reason;

    public String getSystemError() {
        return systemError;
    }

    public void setSystemError(String systemError) {
        this.systemError = systemError;
    }

    public String getSystemErrorCode() {
        return systemErrorCode;
    }

    public void setSystemErrorCode(String systemErrorCode) {
        this.systemErrorCode = systemErrorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ErrorEncounteredJson{" +
                "systemError='" + systemError + '\'' +
                ", systemErrorCode='" + systemErrorCode + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
