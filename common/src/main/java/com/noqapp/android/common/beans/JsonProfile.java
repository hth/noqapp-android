package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.GenderEnum;
import com.noqapp.android.common.model.types.UserLevelEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("pi")
    private String profileImage;

    @JsonProperty("nm")
    private String name;

    @JsonProperty("em")
    private String mail;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("pr")
    private String phoneRaw;

    @JsonProperty("tz")
    private String timeZone;

    @JsonProperty("ic")
    private String inviteCode;

    @JsonProperty("ep")
    private int earnedPoint;

    @JsonProperty("epp")
    private int earnedPointPreviously;

    @JsonProperty("bd")
    private String birthday;

    @JsonProperty("ge")
    private GenderEnum gender;

    @JsonProperty("ul")
    private UserLevelEnum userLevel;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("av")
    private boolean accountValidated;

    @JsonProperty("mp")
    private JsonUserMedicalProfile jsonUserMedicalProfile;

    /* Dependents can be anyone minor or other elderly family members. */
    @JsonProperty("dp")
    private List<JsonProfile> dependents = new ArrayList<>();

    @JsonProperty("ads")
    private List<JsonUserAddress> jsonUserAddresses = new ArrayList<>();

    @JsonProperty("up")
    private JsonUserPreference jsonUserPreference;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("cbs")
    private Map<String, String> codeQRAndBizStoreIds = new HashMap<>();

    @JsonProperty("pv")
    private boolean profileVerified;

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

    public int getEarnedPoint() {
        return earnedPoint;
    }

    public JsonProfile setEarnedPoint(int earnedPoint) {
        this.earnedPoint = earnedPoint;
        return this;
    }

    public int getEarnedPointPreviously() {
        return earnedPointPreviously;
    }

    public JsonProfile setEarnedPointPreviously(int earnedPointPreviously) {
        this.earnedPointPreviously = earnedPointPreviously;
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

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public JsonProfile setAccountValidated(boolean accountValidated) {
        this.accountValidated = accountValidated;
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

    public List<JsonUserAddress> getJsonUserAddresses() {
        return jsonUserAddresses;
    }

    public JsonProfile setJsonUserAddresses(List<JsonUserAddress> jsonUserAddresses) {
        this.jsonUserAddresses = jsonUserAddresses;
        return this;
    }

    public JsonUserPreference getJsonUserPreference() {
        return jsonUserPreference;
    }

    public JsonProfile setJsonUserPreference(JsonUserPreference jsonUserPreference) {
        this.jsonUserPreference = jsonUserPreference;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonProfile setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public Map<String, String> getCodeQRAndBizStoreIds() {
        return codeQRAndBizStoreIds;
    }

    public JsonProfile setCodeQRAndBizStoreIds(Map<String, String> codeQRAndBizStoreIds) {
        this.codeQRAndBizStoreIds = codeQRAndBizStoreIds;
        return this;
    }

    public boolean isProfileVerified() {
        return profileVerified;
    }

    public JsonProfile setProfileVerified(boolean profileVerified) {
        this.profileVerified = profileVerified;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonProfile setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @JsonIgnore
    public String findPrimaryOrAnyExistingAddressId() {
        JsonUserAddress jsonUserAddress = findPrimaryOrAnyExistingAddress();
        if (null == jsonUserAddress) {
            return null;
        }

        return jsonUserAddress.getId();
    }

    @JsonIgnore
    public JsonUserAddress findPrimaryOrAnyExistingAddress() {
        for (JsonUserAddress jsonUserAddress : jsonUserAddresses) {
            if (jsonUserAddress.isPrimaryAddress()) {
                return jsonUserAddress;
            }
        }

        return jsonUserAddresses.isEmpty() ? null : jsonUserAddresses.get(0);
    }

    @Override
    public String toString() {
        return "JsonProfile{" +
                "queueUserId='" + queueUserId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", phoneRaw='" + phoneRaw + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", birthday='" + birthday + '\'' +
                ", gender=" + gender +
                ", userLevel=" + userLevel +
                ", businessType=" + businessType +
                ", accountValidated=" + accountValidated +
                ", jsonUserMedicalProfile=" + jsonUserMedicalProfile +
                ", dependents=" + dependents +
                ", error=" + error +
                '}';
    }
}