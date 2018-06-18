package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.merchant.model.types.QueueUserStateEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 9/7/17 6:24 AM
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
public class JsonQueuedPerson implements Serializable {

    @JsonProperty("t")
    private int token;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("n")
    private String customerName = "";

    @JsonProperty("p")
    private String customerPhone = "";

    @JsonProperty("qu")
    private QueueUserStateEnum queueUserState;

    @JsonProperty("sid")
    private String serverDeviceId = "";

    /* Dependents can be anyone minor or other elderly family members. */
    @JsonProperty ("dp")
    private List<JsonQueuedDependent> dependents = new ArrayList<>();

    @JsonProperty("bc")
    private String businessCustomerId;

    public int getToken() {
        return token;
    }

    public JsonQueuedPerson setToken(int token) {
        this.token = token;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonQueuedPerson setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonQueuedPerson setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonQueuedPerson setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonQueuedPerson setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public String getServerDeviceId() {
        return serverDeviceId;
    }

    public JsonQueuedPerson setServerDeviceId(String serverDeviceId) {
        this.serverDeviceId = serverDeviceId;
        return this;
    }

    public List<JsonQueuedDependent> getDependents() {
        return dependents;
    }

    public JsonQueuedPerson setDependents(List<JsonQueuedDependent> dependents) {
        this.dependents = dependents;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public JsonQueuedPerson setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }
}