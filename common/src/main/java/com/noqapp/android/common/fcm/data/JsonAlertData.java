package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 8/1/18 6:43 PM
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
public class JsonAlertData extends JsonData {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qr")
    private String codeQR;


    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonAlertData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonAlertData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public JsonAlertData setMessageOrigin(MessageOriginEnum messageOrigin) {
        this.messageOrigin = messageOrigin;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public JsonAlertData setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public String toString() {
        return super.toString()+"JsonAlertData{" +
                "messageOrigin=" + messageOrigin +
                ", messageId='" + messageId + '\'' +
                ", businessType=" + businessType +
                ", codeQR='" + codeQR + '\'' +
                '}';
    }
}
