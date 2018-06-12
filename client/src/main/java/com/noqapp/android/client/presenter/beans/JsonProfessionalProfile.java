package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.JsonNameDatePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 5/30/18 10:26 AM
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
public class JsonProfessionalProfile {

    @JsonProperty ("wp")
    private String webProfileId;

    @JsonProperty("ps")
    private Date practiceStart;

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses = new LinkedList<>();

    @JsonProperty("aw")
    private List<JsonNameDatePair> awards = new LinkedList<>();

    @JsonProperty("st")
    private List<JsonStore> stores = new ArrayList<>();

    @JsonProperty("dd")
    private String dataDictionary;

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonProfessionalProfile setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public JsonProfessionalProfile setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonProfessionalProfile setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public JsonProfessionalProfile setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<JsonNameDatePair> getAwards() {
        return awards;
    }

    public JsonProfessionalProfile setAwards(List<JsonNameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public List<JsonStore> getStores() {
        return stores;
    }

    public JsonProfessionalProfile setStores(List<JsonStore> stores) {
        this.stores = stores;
        return this;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public JsonProfessionalProfile setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
        return this;
    }

    @Override
    public String toString() {
        return "JsonProfessionalProfile{" +
                "webProfileId='" + webProfileId + '\'' +
                ", practiceStart=" + practiceStart +
                ", education=" + education +
                ", licenses=" + licenses +
                ", awards=" + awards +
                ", dataDictionary='" + dataDictionary + '\'' +
                '}';
    }
}
