package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @JsonProperty("pl")
    private CustomerPriorityLevelEnum customerPriorityLevel = CustomerPriorityLevelEnum.I;

    @JsonProperty("ca")
    private Set<BusinessCustomerAttributeEnum> businessCustomerAttributes = new HashSet<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

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

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public void setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
    }

    public Set<BusinessCustomerAttributeEnum> getBusinessCustomerAttributes() {
        return businessCustomerAttributes;
    }

    public void setBusinessCustomerAttributes(Set<BusinessCustomerAttributeEnum> businessCustomerAttributes) {
        this.businessCustomerAttributes = businessCustomerAttributes;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonQueuedPerson setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonQueuedPerson{");
        sb.append("token=").append(token);
        sb.append(", queueUserId='").append(queueUserId).append('\'');
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", customerPhone='").append(customerPhone).append('\'');
        sb.append(", queueUserState=").append(queueUserState);
        sb.append(", serverDeviceId='").append(serverDeviceId).append('\'');
        sb.append(", dependents=").append(dependents);
        sb.append(", businessCustomerId='").append(businessCustomerId).append('\'');
        sb.append(", businessCustomerIdChangeCount=").append(businessCustomerIdChangeCount);
        sb.append(", clientVisitedThisStore=").append(clientVisitedThisStore);
        sb.append(", clientVisitedThisStoreDate='").append(clientVisitedThisStoreDate).append('\'');
        sb.append(", clientVisitedThisBusiness=").append(clientVisitedThisBusiness);
        sb.append(", recordReferenceId='").append(recordReferenceId).append('\'');
        sb.append(", transactionId='").append(transactionId).append('\'');
        sb.append(", jsonPurchaseOrder=").append(jsonPurchaseOrder);
        sb.append(", created='").append(created).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}