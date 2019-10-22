package com.noqapp.android.client.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 10/22/19 9:15 AM
 */
@SuppressWarnings ({
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
public class Survey {

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("qr")
    private int overallRating;

    @JsonProperty("dr")
    private String[] detailedResponse;

    @JsonProperty("id")
    private String questionnaireId;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public Survey setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public Survey setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public Survey setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public Survey setOverallRating(int overallRating) {
        this.overallRating = overallRating;
        return this;
    }

    public String[] getDetailedResponse() {
        return detailedResponse;
    }

    public Survey setDetailedResponse(String[] detailedResponse) {
        this.detailedResponse = detailedResponse;
        return this;
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public Survey setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
        return this;
    }
}
