package com.noqapp.android.common.fcm.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.JsonQueueChangeServiceTime;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.MessageOriginEnum;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 10/29/20 1:09 PM
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
public class JsonChangeServiceTimeData extends JsonData implements Serializable {

    @JsonProperty("mo")
    private MessageOriginEnum messageOrigin;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    private List<JsonQueueChangeServiceTime> jsonQueueChangeServiceTimes = new LinkedList<>();

    public MessageOriginEnum getMessageOrigin() {
        return messageOrigin;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonChangeServiceTimeData setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonChangeServiceTimeData setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<JsonQueueChangeServiceTime> getJsonQueueChangeServiceTimes() {
        return jsonQueueChangeServiceTimes;
    }

    public JsonChangeServiceTimeData setJsonQueueChangeServiceTimes(List<JsonQueueChangeServiceTime> jsonQueueChangeServiceTimes) {
        this.jsonQueueChangeServiceTimes = jsonQueueChangeServiceTimes;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageOrigin", messageOrigin)
                .append("codeQR", codeQR)
                .append("jsonQueueChangeServiceTimes", jsonQueueChangeServiceTimes)
                .toString();
    }
}
