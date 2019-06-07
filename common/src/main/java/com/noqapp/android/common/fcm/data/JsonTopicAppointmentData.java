package com.noqapp.android.common.fcm.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.MessageOriginEnum;

/**
 * User: hitender
 * Date: 2019-06-07 13:50
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
public class JsonTopicAppointmentData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public JsonTopicAppointmentData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonTopicAppointmentData setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonTopicAppointmentData setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonTopicAppointmentData{" +
                "messageOrigin=" + messageOrigin +
                ", message='" + message + '\'' +
                ", error=" + error +
                '}';
    }
}
