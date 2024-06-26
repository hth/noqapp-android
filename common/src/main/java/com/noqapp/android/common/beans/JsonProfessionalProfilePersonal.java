package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.medical.FormVersionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class JsonProfessionalProfilePersonal implements Serializable {

    @JsonProperty("nm")
    private String name;

    @JsonProperty("wp")
    private String webProfileId;

    @JsonProperty("ps")
    private String practiceStart;

    @JsonProperty("am")
    private String aboutMe;

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses = new LinkedList<>();

    @JsonProperty("aw")
    private List<JsonNameDatePair> awards = new LinkedList<>();

    @JsonProperty("dd")
    private String dataDictionary;

    @JsonProperty("re")
    private Map<String, JsonReviewList> reviews = new HashMap<>();

    @JsonProperty("fv")
    private FormVersionEnum formVersion;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getName() {
        return name;
    }

    public JsonProfessionalProfilePersonal setName(String name) {
        this.name = name;
        return this;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonProfessionalProfilePersonal setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public String getPracticeStart() {
        return practiceStart;
    }

    public JsonProfessionalProfilePersonal setPracticeStart(String practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public JsonProfessionalProfilePersonal setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonProfessionalProfilePersonal setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public JsonProfessionalProfilePersonal setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<JsonNameDatePair> getAwards() {
        return awards;
    }

    public JsonProfessionalProfilePersonal setAwards(List<JsonNameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public JsonProfessionalProfilePersonal setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
        return this;
    }

    public Map<String, JsonReviewList> getReviews() {
        return reviews;
    }

    public JsonProfessionalProfilePersonal setReviews(Map<String, JsonReviewList> reviews) {
        this.reviews = reviews;
        return this;
    }

    public FormVersionEnum getFormVersion() {
        return formVersion;
    }

    public JsonProfessionalProfilePersonal setFormVersion(FormVersionEnum formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonProfessionalProfilePersonal{" +
                "name='" + name + '\'' +
                ", webProfileId='" + webProfileId + '\'' +
                ", practiceStart='" + practiceStart + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", education=" + education +
                ", licenses=" + licenses +
                ", awards=" + awards +
                ", dataDictionary='" + dataDictionary + '\'' +
                ", reviews=" + reviews +
                ", error=" + error +
                '}';
    }
}
