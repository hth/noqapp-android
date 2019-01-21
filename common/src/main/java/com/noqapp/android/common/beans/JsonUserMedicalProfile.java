package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.medical.BloodTypeEnum;
import com.noqapp.android.common.model.types.medical.OccupationEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * hitender
 * 5/30/18 5:35 AM
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
public class JsonUserMedicalProfile implements Serializable {

    @JsonProperty("bt")
    private BloodTypeEnum bloodType;

    @JsonProperty("oc")
    private OccupationEnum occupation;

    @JsonProperty("ph")
    private String pastHistory;

    @JsonProperty("fh")
    private String familyHistory;

    @JsonProperty("ka")
    private String knownAllergies;

    @JsonProperty("ma")
    private String medicineAllergies;

    @JsonProperty("hd")
    private boolean historyDirty;

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public JsonUserMedicalProfile setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public OccupationEnum getOccupation() {
        return occupation;
    }

    public JsonUserMedicalProfile setOccupation(OccupationEnum occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public JsonUserMedicalProfile setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public JsonUserMedicalProfile setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public JsonUserMedicalProfile setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getMedicineAllergies() {
        return medicineAllergies;
    }

    public JsonUserMedicalProfile setMedicineAllergies(String medicineAllergies) {
        this.medicineAllergies = medicineAllergies;
        return this;
    }

    public boolean isHistoryDirty() {
        return historyDirty;
    }

    public JsonUserMedicalProfile setHistoryDirty(boolean historyDirty) {
        this.historyDirty = historyDirty;
        return this;
    }

    @Override
    public String toString() {
        return "JsonUserMedicalProfile{" +
                "bloodType=" + bloodType +
                ", occupation=" + occupation +
                ", pastHistory='" + pastHistory + '\'' +
                ", familyHistory='" + familyHistory + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", medicineAllergies='" + medicineAllergies + '\'' +
                ", historyDirty=" + historyDirty +
                '}';
    }
}