package com.noqapp.android.common.fcm.data;

import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.FCMTypeEnum;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

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
public class JsonDisplayData extends JsonData {

    @JsonProperty("ft")
    private FCMTypeEnum fcmType;

    @JsonProperty("mi")
    private String messageId;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("qr")
    private String codeQR;


    public FCMTypeEnum getFcmType() {
        return fcmType;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonDisplayData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonDisplayData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public JsonDisplayData setFcmType(FCMTypeEnum fcmType) {
        this.fcmType = fcmType;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public JsonDisplayData setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    public String toString() {
        return super.toString()+"JsonDisplayData{" +
                "fcmType=" + fcmType +
                ", messageId='" + messageId + '\'' +
                ", businessType=" + businessType +
                ", codeQR='" + codeQR + '\'' +
                '}';
    }
}
