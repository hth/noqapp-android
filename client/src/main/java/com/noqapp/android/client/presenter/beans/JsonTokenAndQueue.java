package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.model.types.QueueStatusEnum;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Arrays;

/**
 * User: hitender
 * Date: 4/1/17 3:23 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTokenAndQueue implements Serializable {

    @JsonProperty("c")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("p")
    private String storePhone;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty("b")
    private int startHour;

    /* Store business end hour. */
    @JsonProperty("e")
    private int endHour;

    @JsonProperty("de")
    private int delayedInMinutes;

    @JsonProperty("o")
    private String topic;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("l")
    private int lastNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("as")
    private long averageServiceTime;

    @JsonProperty("hr")
    private int hoursSaved;

    @JsonProperty("u")
    private String createDate;

    @JsonProperty("bc")
    private String bizCategoryId;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonTokenAndQueue setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getArea() {
        return area;
    }

    public JsonTokenAndQueue setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonTokenAndQueue setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public void setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public JsonTokenAndQueue setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public void setHoursSaved(int hoursSaved) {
        this.hoursSaved = hoursSaved;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public void setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    @JsonIgnoreProperties
    public int afterHowLong() {
        return token - servingNumber;
    }

    @JsonIgnoreProperties
    public boolean isTokenExpired() {
        return afterHowLong() <= 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("codeQR", codeQR)
                .append("geoHash", geoHash)
                .append("businessName", businessName)
                .append("displayName", displayName)
                .append("storeAddress", storeAddress)
                .append("area", area)
                .append("town", town)
                .append("countryShortName", countryShortName)
                .append("storePhone", storePhone)
                .append("businessType", businessType)
                .append("tokenAvailableFrom", tokenAvailableFrom)
                .append("startHour", startHour)
                .append("endHour", endHour)
                .append("delayedInMinutes", delayedInMinutes)
                .append("topic", topic)
                .append("servingNumber", servingNumber)
                .append("lastNumber", lastNumber)
                .append("token", token)
                .append("queueStatus", queueStatus)
                .append("serviceEndTime", serviceEndTime)
                .append("ratingCount", ratingCount)
                .append("averageServiceTime", averageServiceTime)
                .append("hoursSaved", hoursSaved)
                .append("createDate", createDate)
                .append("bizCategoryId", bizCategoryId)
                .toString();
    }
}
