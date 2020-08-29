package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.QueueStatusEnum;

/**
 * User: hitender
 * Date: 4/1/17 12:13 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonToken {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("n")
    private String customerName;

    @JsonProperty("e")
    private String expectedServiceBegin;

    /* Do not use it. Its not being used or sent across the line. */
    @JsonProperty("ti")
    @JsonIgnore
    @SuppressWarnings("unused")
    private String transactionId;

    @JsonProperty("v")
    private boolean clientVisitedThisStore;

    @JsonProperty("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @JsonProperty("sl")
    private String timeSlotMessage;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public boolean isClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public JsonToken setExpectedServiceBegin(String expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public boolean hasClientVisitedThisStore() {
        return clientVisitedThisStore;
    }

    public JsonToken setClientVisitedThisStore(boolean clientVisitedThisStore) {
        this.clientVisitedThisStore = clientVisitedThisStore;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonToken setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    public String getTimeSlotMessage() {
        return timeSlotMessage;
    }

    public void setTimeSlotMessage(String timeSlotMessage) {
        this.timeSlotMessage = timeSlotMessage;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @JsonIgnoreProperties
    public int afterHowLong() {
        int position = token - servingNumber;
        return Math.max(position, 0);
    }

    @JsonIgnoreProperties
    public JsonTokenAndQueue getJsonTokenAndQueue() {
        JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
        jsonTokenAndQueue.setCodeQR(codeQR);
        jsonTokenAndQueue.setDisplayName(displayName);
        jsonTokenAndQueue.setQueueStatus(queueStatus);
        jsonTokenAndQueue.setServingNumber(servingNumber);
        jsonTokenAndQueue.setToken(token);
        return jsonTokenAndQueue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonToken{");
        sb.append("codeQR='").append(codeQR).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", queueStatus=").append(queueStatus);
        sb.append(", servingNumber=").append(servingNumber);
        sb.append(", token=").append(token);
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", expectedServiceBegin='").append(expectedServiceBegin).append('\'');
        sb.append(", transactionId='").append(transactionId).append('\'');
        sb.append(", clientVisitedThisStore=").append(clientVisitedThisStore);
        sb.append(", jsonPurchaseOrder=").append(jsonPurchaseOrder);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}