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
public class JsonHealthCareProfile {

    @JsonProperty("qr")
    private String codeQR;

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

    @JsonProperty("pd")
    private String prescriptionDictionary;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonHealthCareProfile setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public JsonHealthCareProfile setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonHealthCareProfile setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public JsonHealthCareProfile setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<JsonNameDatePair> getAwards() {
        return awards;
    }

    public JsonHealthCareProfile setAwards(List<JsonNameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public List<JsonStore> getStores() {
        return stores;
    }

    public JsonHealthCareProfile setStores(List<JsonStore> stores) {
        this.stores = stores;
        return this;
    }

    public String getPrescriptionDictionary() {
        return prescriptionDictionary;
    }

    public JsonHealthCareProfile setPrescriptionDictionary(String prescriptionDictionary) {
        this.prescriptionDictionary = prescriptionDictionary;
        return this;
    }

    @Override
    public String toString() {
        return "JsonHealthCareProfile{" +
                "codeQR='" + codeQR + '\'' +
                ", practiceStart=" + practiceStart +
                ", education=" + education +
                ", licenses=" + licenses +
                ", awards=" + awards +
                ", prescriptionDictionary='" + prescriptionDictionary + '\'' +
                '}';
    }
}
