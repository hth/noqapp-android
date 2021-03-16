package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 7/31/18 5:48 PM
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
public class JsonTopicOrderData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("message")
    private String message;

    @JsonProperty("ln")
    private int lastNumber;

    @JsonProperty("cs")
    private int currentlyServing;

    @JsonProperty("dsn")
    private String displayServingNow;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("g")
    private String goTo;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("os")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonTopicOrderData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonTopicOrderData setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonTopicOrderData setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public JsonTopicOrderData setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
        return this;
    }

    public String getDisplayServingNow() {
        return displayServingNow;
    }

    public JsonTopicOrderData setDisplayServingNow(String displayServingNow) {
        this.displayServingNow = displayServingNow;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonTopicOrderData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonTopicOrderData setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getGoTo() {
        return goTo;
    }

    public JsonTopicOrderData setGoTo(String goTo) {
        this.goTo = goTo;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonTopicOrderData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public JsonTopicOrderData setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public JsonTopicOrderData setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonTopicOrderData setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return super.toString()+"JsonTopicOrderData{" +
                "messageOrigin=" + messageOrigin +
                ", message='" + message + '\'' +
                ", lastNumber=" + lastNumber +
                ", currentlyServing=" + currentlyServing +
                ", codeQR='" + codeQR + '\'' +
                ", queueStatus=" + queueStatus +
                ", goTo='" + goTo + '\'' +
                ", businessType=" + businessType +
                ", purchaseOrderState=" + purchaseOrderState +
                ", messageId='" + messageId + '\'' +
                ", error=" + error +
                '}';
    }
}
