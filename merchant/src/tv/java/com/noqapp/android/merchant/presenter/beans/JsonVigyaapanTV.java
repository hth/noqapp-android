package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;
import com.noqapp.android.common.beans.VigyaapanTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-20 12:55
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
public class JsonVigyaapanTV extends AbstractDomain {

    @JsonProperty("vi")
    private String vigyaapanId;

    @JsonProperty("pp")
    private JsonProfessionalProfileTV jsonProfessionalProfileTV;

    @JsonProperty("iu")
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty("vt")
    private VigyaapanTypeEnum vigyaapanType;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }


    public String getVigyaapanId() {
        return vigyaapanId;
    }

    public JsonVigyaapanTV setVigyaapanId(String vigyaapanId) {
        this.vigyaapanId = vigyaapanId;
        return this;
    }

    public JsonProfessionalProfileTV getJsonProfessionalProfileTV() {
        return jsonProfessionalProfileTV;
    }

    public JsonVigyaapanTV setJsonProfessionalProfileTV(JsonProfessionalProfileTV jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV = jsonProfessionalProfileTV;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public JsonVigyaapanTV setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public VigyaapanTypeEnum getVigyaapanType() {
        return vigyaapanType;
    }

    public JsonVigyaapanTV setVigyaapanType(VigyaapanTypeEnum vigyaapanType) {
        this.vigyaapanType = vigyaapanType;
        return this;
    }

    @Override
    public String toString() {
        return "JsonVigyaapanTV{" +
                "vigyaapanId='" + vigyaapanId + '\'' +
                ", jsonProfessionalProfileTV=" + jsonProfessionalProfileTV +
                ", imageUrls=" + imageUrls +
                ", vigyaapanType=" + vigyaapanType +
                ", error=" + error +
                '}';
    }
}