package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import java.io.Serializable;

/**
 * hitender
 * 2018-12-17 18:16
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
public class JsonQueuedPersonTV extends AbstractDomain implements Serializable {

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("n")
    private String customerName = "";

    @JsonProperty ("p")
    private String customerPhone = "";

    @JsonProperty ("qu")
    private QueueUserStateEnum queueUserState;

    public int getToken() {
        return token;
    }

    public JsonQueuedPersonTV setToken(int token) {
        this.token = token;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public JsonQueuedPersonTV setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonQueuedPersonTV setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonQueuedPersonTV setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonQueuedPersonTV setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonQueuedPersonTV setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    @Override
    public String toString() {
        return "JsonQueuedPersonTV{" +
                "token=" + token +
                ", queueUserId='" + queueUserId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", queueUserState=" + queueUserState +
                '}';
    }
}
