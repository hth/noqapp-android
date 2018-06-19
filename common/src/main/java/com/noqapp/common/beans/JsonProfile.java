package com.noqapp.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.model.types.GenderEnum;
import com.noqapp.common.model.types.UserLevelEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/8/17 8:25 PM
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
//@JsonInclude (JsonInclude.Include.NON_NULL)
public final class JsonProfile implements Serializable {

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty("nm")
    private String name;

    @JsonProperty("em")
    private String mail;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("ad")
    private String address;

    @JsonProperty("pr")
    private String phoneRaw;

    @JsonProperty("tz")
    private String timeZone;

    @JsonProperty("ic")
    private String inviteCode;

    @JsonProperty("rj")
    private int remoteJoin;

    @JsonProperty("bd")
    private String birthday;

    @JsonProperty("ge")
    private GenderEnum gender;

    @JsonProperty("ul")
    private UserLevelEnum userLevel;

    @JsonProperty ("mp")
    private JsonUserMedicalProfile jsonUserMedicalProfile;

    /* Dependents can be anyone minor or other elderly family members. */
    @JsonProperty ("dp")
    private List<JsonProfile> dependents = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonProfile setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public JsonProfile setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getName() {
        return name;
    }

    public JsonProfile setName(String name) {
        this.name = name;
        return this;
    }

    public String getMail() {
        return mail;
    }

    public JsonProfile setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonProfile setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonProfile setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public JsonProfile setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public JsonProfile setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public JsonProfile setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
        return this;
    }

    public int getRemoteJoin() {
        return remoteJoin;
    }

    public JsonProfile setRemoteJoin(int remoteJoin) {
        this.remoteJoin = remoteJoin;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public JsonProfile setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public JsonProfile setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public JsonProfile setUserLevel(UserLevelEnum userLevel) {
        this.userLevel = userLevel;
        return this;
    }

    public JsonUserMedicalProfile getJsonUserMedicalProfile() {
        return jsonUserMedicalProfile;
    }

    public JsonProfile setJsonUserMedicalProfile(JsonUserMedicalProfile jsonUserMedicalProfile) {
        this.jsonUserMedicalProfile = jsonUserMedicalProfile;
        return this;
    }

    public List<JsonProfile> getDependents() {
        return dependents;
    }

    public JsonProfile setDependents(List<JsonProfile> dependents) {
        this.dependents = dependents;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonProfile setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonProfile{" +
                "queueUserId='" + queueUserId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", address='" + address + '\'' +
                ", phoneRaw='" + phoneRaw + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", remoteJoin=" + remoteJoin +
                ", birthday='" + birthday + '\'' +
                ", gender=" + gender +
                ", userLevel=" + userLevel +
                ", jsonUserMedicalProfile=" + jsonUserMedicalProfile +
                ", dependents=" + dependents +
                ", error=" + error +
                '}';
    }
}