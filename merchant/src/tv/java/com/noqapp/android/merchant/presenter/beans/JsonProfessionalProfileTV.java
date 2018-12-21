package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.JsonProfessionalProfilePersonal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2018-12-21 14:11
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
public class JsonProfessionalProfileTV extends JsonProfessionalProfilePersonal {

    @JsonProperty("pi")
    private String profileImage;

    @JsonProperty("pt")
    private String professionType;

    public String getProfileImage() {
        return profileImage;
    }

    public JsonProfessionalProfileTV setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getProfessionType() {
        return professionType;
    }

    public JsonProfessionalProfileTV setProfessionType(String professionType) {
        this.professionType = professionType;
        return this;
    }

    @Override
    public String toString() {
        return "JsonProfessionalProfileTV{" +
                "profileImage='" + profileImage + '\'' +
                ", professionType='" + professionType + '\'' +
                '}';
    }
}
