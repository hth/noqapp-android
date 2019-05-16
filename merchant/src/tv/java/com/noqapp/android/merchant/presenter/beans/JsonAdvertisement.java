package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.AdvertisementTypeEnum;

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
public class JsonAdvertisement extends AbstractDomain {

    @JsonProperty("vi")
    private String advertisementId;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("bd")
    private String body;

    @JsonProperty("pp")
    private JsonProfessionalProfileTV jsonProfessionalProfileTV;

    @JsonProperty("iu")
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty("tcs")
    private List<String> termAndConditions = new ArrayList<>();

    @JsonProperty("vt")
    private AdvertisementTypeEnum advertisementType;

    @JsonProperty("ed")
    private String endDate;

    @JsonProperty("ei")
    private boolean endDateInitialized;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getAdvertisementId() {
        return advertisementId;
    }

    public JsonAdvertisement setAdvertisementId(String advertisementId) {
        this.advertisementId = advertisementId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonAdvertisement setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public JsonAdvertisement setBody(String body) {
        this.body = body;
        return this;
    }

    public JsonProfessionalProfileTV getJsonProfessionalProfileTV() {
        return jsonProfessionalProfileTV;
    }

    public JsonAdvertisement setJsonProfessionalProfileTV(JsonProfessionalProfileTV jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV = jsonProfessionalProfileTV;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public JsonAdvertisement setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public List<String> getTermAndConditions() {
        return termAndConditions;
    }

    public JsonAdvertisement setTermAndConditions(List<String> termAndConditions) {
        this.termAndConditions = termAndConditions;
        return this;
    }

    public AdvertisementTypeEnum getAdvertisementType() {
        return advertisementType;
    }

    public JsonAdvertisement setAdvertisementType(AdvertisementTypeEnum advertisementType) {
        this.advertisementType = advertisementType;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public JsonAdvertisement setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public boolean isEndDateInitialized() {
        return endDateInitialized;
    }

    public JsonAdvertisement setEndDateInitialized(boolean endDateInitialized) {
        this.endDateInitialized = endDateInitialized;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonAdvertisement setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonAdvertisement{" +
                "advertisementId='" + advertisementId + '\'' +
                ", jsonProfessionalProfileTV=" + jsonProfessionalProfileTV +
                ", imageUrls=" + imageUrls +
                ", advertisementType=" + advertisementType +
                ", endDate='" + endDate + '\'' +
                ", endDateInitialized=" + endDateInitialized +
                ", error=" + error +
                '}';
    }
}