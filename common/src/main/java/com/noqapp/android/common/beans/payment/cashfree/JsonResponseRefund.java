package com.noqapp.android.common.beans.payment.cashfree;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 2019-03-05 06:44
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
public class JsonResponseRefund extends AbstractDomain implements Serializable {

    @JsonProperty("status")
    private String status;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public JsonResponseRefund setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public JsonResponseRefund setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonResponseRefund setMessage(String message) {
        this.message = message;
        return this;
    }
}
