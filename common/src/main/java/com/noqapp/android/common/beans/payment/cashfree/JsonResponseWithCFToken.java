package com.noqapp.android.common.beans.payment.cashfree;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;

/**
 * hitender
 * 2019-02-28 14:09
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
public class JsonResponseWithCFToken extends AbstractDomain implements Serializable {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cftoken")
    private String cftoken;

    @JsonProperty("orderAmount")
    private String orderAmount;

    @JsonProperty("error")
    private ErrorEncounteredJson error;


    public String getStatus() {
        return status;
    }

    public JsonResponseWithCFToken setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonResponseWithCFToken setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCftoken() {
        return cftoken;
    }

    public JsonResponseWithCFToken setCftoken(String cftoken) {
        this.cftoken = cftoken;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public JsonResponseWithCFToken setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
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
        final StringBuilder sb = new StringBuilder("JsonResponseWithCFToken{");
        sb.append("status='").append(status).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", cftoken='").append(cftoken).append('\'');
        sb.append(", orderAmount='").append(orderAmount).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
