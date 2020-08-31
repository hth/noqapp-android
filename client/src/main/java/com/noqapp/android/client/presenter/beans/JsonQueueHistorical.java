package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueUserStateEnum;

import java.io.Serializable;

/**
 * hitender
 * 10/5/18 9:40 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonQueueHistorical extends AbstractDomain implements Serializable{

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("tn")
    private int tokenNumber;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty ("dn")
    private String displayName;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("qs")
    private QueueUserStateEnum queueUserState;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("hr")
    private int hoursSaved;

    @JsonProperty ("rv")
    private String review;

    @JsonProperty ("sn")
    private String serverName;

    @JsonProperty ("sb")
    private String serviceBeginTime;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("gq")
    private String guardianQid;

    @JsonProperty("u")
    private String created;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("di")
    private String displayImage;

    @JsonProperty("bci")
    private String bizCategoryId;

    @JsonProperty("bc")
    private String bizCategoryName;

    @JsonProperty ("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueueHistorical setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonQueueHistorical setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public JsonQueueHistorical setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonQueueHistorical setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonQueueHistorical setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public JsonQueueHistorical setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonQueueHistorical setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public JsonQueueHistorical setHoursSaved(int hoursSaved) {
        this.hoursSaved = hoursSaved;
        return this;
    }

    public String getReview() {
        return review;
    }

    public JsonQueueHistorical setReview(String review) {
        this.review = review;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public JsonQueueHistorical setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServiceBeginTime() {
        return serviceBeginTime;
    }

    public JsonQueueHistorical setServiceBeginTime(String serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
        return this;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public JsonQueueHistorical setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
        return this;
    }

    public String getGuardianQid() {
        return guardianQid;
    }

    public JsonQueueHistorical setGuardianQid(String guardianQid) {
        this.guardianQid = guardianQid;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonQueueHistorical setCreated(String created) {
        this.created = created;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonQueueHistorical setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueueHistorical setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonQueueHistorical setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonQueueHistorical setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonQueueHistorical setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public JsonQueueHistorical setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonQueueHistorical setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public JsonQueueHistorical setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonQueueHistorical setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonQueueHistorical{");
        sb.append("codeQR='").append(codeQR).append('\'');
        sb.append(", queueUserId='").append(queueUserId).append('\'');
        sb.append(", tokenNumber=").append(tokenNumber);
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", businessType=").append(businessType);
        sb.append(", queueUserState=").append(queueUserState);
        sb.append(", ratingCount=").append(ratingCount);
        sb.append(", hoursSaved=").append(hoursSaved);
        sb.append(", review='").append(review).append('\'');
        sb.append(", serverName='").append(serverName).append('\'');
        sb.append(", serviceBeginTime='").append(serviceBeginTime).append('\'');
        sb.append(", serviceEndTime='").append(serviceEndTime).append('\'');
        sb.append(", guardianQid='").append(guardianQid).append('\'');
        sb.append(", created='").append(created).append('\'');
        sb.append(", businessName='").append(businessName).append('\'');
        sb.append(", storeAddress='").append(storeAddress).append('\'');
        sb.append(", area='").append(area).append('\'');
        sb.append(", town='").append(town).append('\'');
        sb.append(", countryShortName='").append(countryShortName).append('\'');
        sb.append(", displayImage='").append(displayImage).append('\'');
        sb.append(", bizCategoryId='").append(bizCategoryId).append('\'');
        sb.append(", bizCategoryName='").append(bizCategoryName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}