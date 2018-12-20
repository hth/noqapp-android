package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
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
    private JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal;

    @JsonProperty("iu")
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty("vt")
    private VigyaapanTypeEnum vigyaapanType;

    public String getVigyaapanId() {
        return vigyaapanId;
    }

    public JsonVigyaapanTV setVigyaapanId(String vigyaapanId) {
        this.vigyaapanId = vigyaapanId;
        return this;
    }

    public JsonProfessionalProfilePersonal getJsonProfessionalProfilePersonal() {
        return jsonProfessionalProfilePersonal;
    }

    public JsonVigyaapanTV setJsonProfessionalProfilePersonal(JsonProfessionalProfilePersonal jsonProfessionalProfilePersonal) {
        this.jsonProfessionalProfilePersonal = jsonProfessionalProfilePersonal;
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
}