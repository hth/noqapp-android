package com.noqapp.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class JsonProfessionalProfilePersonal {

    @JsonProperty("wp")
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

    @JsonProperty("dd")
    private String dataDictionary;

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonProfessionalProfilePersonal setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public JsonProfessionalProfilePersonal setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
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
}
