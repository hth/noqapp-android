package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FCMTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 1/1/17 7:06 AM
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
public class JsonTopicQueueData extends JsonData implements Serializable{

    @JsonProperty("ft")
    private FCMTypeEnum fcmType;

    @JsonProperty("message")
    private String message;

    @JsonProperty("ln")
    private int lastNumber;

    @JsonProperty("cs")
    private int currentlyServing;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("g")
    private String goTo;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("mi")
    private String messageId;

    public FCMTypeEnum getFcmType() {
        return fcmType;
    }

    public JsonTopicQueueData setFcmType(FCMTypeEnum fcmType) {
        this.fcmType = fcmType;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonTopicQueueData setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonTopicQueueData setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getCurrentlyServing() {
        return currentlyServing;
    }

    public JsonTopicQueueData setCurrentlyServing(int currentlyServing) {
        this.currentlyServing = currentlyServing;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonTopicQueueData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonTopicQueueData setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getGoTo() {
        return goTo;
    }

    public JsonTopicQueueData setGoTo(String goTo) {
        this.goTo = goTo;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonTopicQueueData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public JsonTopicQueueData setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public String toString() {
        return super.toString()+"JsonTopicQueueData{" +
                "fcmType=" + fcmType +
                ", message='" + message + '\'' +
                ", lastNumber=" + lastNumber +
                ", currentlyServing=" + currentlyServing +
                ", codeQR='" + codeQR + '\'' +
                ", queueStatus=" + queueStatus +
                ", goTo='" + goTo + '\'' +
                ", businessType=" + businessType +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
