package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

    /* This checks how many times the Business Customer Id has been changed. */
    @JsonProperty("cc")
    private int businessCustomerIdChangeCount;

    @JsonProperty ("vs")
    private boolean clientVisitedThisStore;

    @JsonProperty("vsd")
    private String clientVisitedThisStoreDate;

    @JsonProperty ("vb")
    private boolean clientVisitedThisBusiness;

    /** This record reference has to be used when submitting a form. */
    @JsonProperty ("rr")
    private String recordReferenceId;

    @JsonProperty("ti")
    private String transactionId;

    @JsonProperty("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @JsonProperty("c")
    private String created;

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

    public int getBusinessCustomerIdChangeCount() {
        return businessCustomerIdChangeCount;
    }

    public JsonQueuedPerson setBusinessCustomerIdChangeCount(int businessCustomerIdChangeCount) {
        this.businessCustomerIdChangeCount = businessCustomerIdChangeCount;
        return this;
    }

    public boolean isClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public JsonQueuedPerson setClientVisitedThisStore(boolean clientVisitedThisStore) {
        this.clientVisitedThisStore = clientVisitedThisStore;
        return this;
    }

    public String getClientVisitedThisStoreDate() {
        return clientVisitedThisStoreDate;
    }

    public JsonQueuedPerson setClientVisitedThisStoreDate(String clientVisitedThisStoreDate) {
        this.clientVisitedThisStoreDate = clientVisitedThisStoreDate;
        return this;
    }

    public boolean isClientVisitedThisBusiness() {
        return clientVisitedThisBusiness;
    }

    public JsonQueuedPerson setClientVisitedThisBusiness(boolean clientVisitedThisBusiness) {
        this.clientVisitedThisBusiness = clientVisitedThisBusiness;
        return this;
    }

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public JsonQueuedPerson setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonQueuedPerson setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonQueuedPerson setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonQueuedPerson setCreated(String created) {
        this.created = created;
        return this;
    }

    @Override
    public String toString() {
        return "JsonQueuedPerson{" +
                "token=" + token +
                ", queueUserId='" + queueUserId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", queueUserState=" + queueUserState +
                ", serverDeviceId='" + serverDeviceId + '\'' +
                ", dependents=" + dependents +
                ", businessCustomerId='" + businessCustomerId + '\'' +
                ", businessCustomerIdChangeCount=" + businessCustomerIdChangeCount +
                ", clientVisitedThisStore=" + clientVisitedThisStore +
                ", clientVisitedThisStoreDate='" + clientVisitedThisStoreDate + '\'' +
                ", clientVisitedThisBusiness=" + clientVisitedThisBusiness +
                ", recordReferenceId='" + recordReferenceId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", jsonPurchaseOrder=" + jsonPurchaseOrder +
                ", created='" + created + '\'' +
                '}';
    }
}