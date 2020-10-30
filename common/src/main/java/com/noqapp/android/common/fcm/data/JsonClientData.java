package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.model.types.MessageOriginEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * Data associated after client has been served or either was skipped for no show.
 * User: hitender
 * Date: 3/7/17 11:07 AM
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
public class JsonClientData extends JsonData implements Serializable {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("u")
    private QueueUserStateEnum queueUserState;

    @JsonProperty("o")
    private String topic;

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonClientData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonClientData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonClientData setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public int getToken() {
        return token;
    }

    public JsonClientData setToken(int token) {
        this.token = token;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonClientData setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonClientData setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    @Override
    public String toString() {
        return super.toString()+"JsonClientData{" +
                "messageOrigin=" + messageOrigin +
                ", codeQR='" + codeQR + '\'' +
                ", queueUserId='" + queueUserId + '\'' +
                ", token=" + token +
                ", queueUserState=" + queueUserState +
                ", topic='" + topic + '\'' +
                '}';
    }
}
