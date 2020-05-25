package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;

/**
 * hitender
 * 5/10/20 5:04 AM
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
public class QueueAuthorize extends AbstractDomain {
    @JsonProperty("codeQR")
    private String codeQR;

    @JsonProperty("fci")
    private String firstCustomerId;

    @JsonProperty("aci")
    private String additionalCustomerId;

    public String getCodeQR() {
        return codeQR;
    }

    public QueueAuthorize setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getFirstCustomerId() {
        return firstCustomerId;
    }

    public void setFirstCustomerId(String firstCustomerId) {
        this.firstCustomerId = firstCustomerId;
    }

    public String getAdditionalCustomerId() {
        return additionalCustomerId;
    }

    public void setAdditionalCustomerId(String additionalCustomerId) {
        this.additionalCustomerId = additionalCustomerId;
    }
}
