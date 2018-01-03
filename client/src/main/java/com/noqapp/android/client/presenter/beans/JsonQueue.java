package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;
import com.noqapp.android.client.model.types.BusinessTypeEnum;
import com.noqapp.android.client.model.types.QueueStatusEnum;
import com.noqapp.android.client.utils.Constants;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 3/26/17 2:19 PM
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
public class JsonQueue implements Serializable {

    @JsonProperty("c")
    private String codeQR;

    @JsonProperty("cor")
    private double[] coordinate;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("sa")
    private String storeAddress;

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

    @JsonProperty("m")
    private int tokenNotAvailableFrom;

    /* Store business end hour. */
    @JsonProperty("e")
    private int endHour;

    @JsonProperty("de")
    private int delayedInMinutes;

    @JsonProperty("pj")
    private boolean preventJoining;

    @JsonProperty("dc")
    private boolean dayClosed = false;

    @JsonProperty("o")
    private String topic;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("l")
    private int lastNumber;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("rj")
    private int remoteJoinCount;

    @JsonProperty("u")
    private String created;

    @JsonProperty("ra")
    private float rating;

    @JsonProperty("rc")
    private int ratingCount;

    @JsonProperty("as")
    private long averageServiceTime;

    @JsonProperty("ja")
    private boolean remoteJoinAvailable = false;

    @JsonProperty("lu")
    private boolean allowLoggedInUser = false;

    //TODO add this property in Queue Settings screen
    @JsonProperty("at")
    private int availableTokenCount;

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueue setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonQueue setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonQueue setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonQueue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonQueue setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonQueue setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonQueue setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonQueue setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public JsonQueue setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public JsonQueue setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public JsonQueue setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public JsonQueue setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public JsonQueue setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public JsonQueue setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public JsonQueue setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonQueue setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonQueue setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public JsonQueue setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
        return this;
    }

    /* Used when joining remotely. */
    public int getRemoteJoinCount() {
        return remoteJoinCount;
    }

    public JsonQueue setRemoteJoinCount(int remoteJoinCount) {
        this.remoteJoinCount = remoteJoinCount;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonQueue setCreated(Date created) {
        this.created = DateFormatUtils.format(created, Constants.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }

    public float getRating() {
        return rating;
    }

    public JsonQueue setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonQueue setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public boolean isRemoteJoinAvailable() {
        return remoteJoinAvailable;
    }

    public void setRemoteJoinAvailable(boolean remoteJoinAvailable) {
        this.remoteJoinAvailable = remoteJoinAvailable;
    }

    public int getPeopleInQueue() {
        return lastNumber - servingNumber;
    }

    public boolean isAllowLoggedInUser() {
        return allowLoggedInUser;
    }

    public JsonQueue setAllowLoggedInUser(boolean allowLoggedInUser) {
        this.allowLoggedInUser = allowLoggedInUser;
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public JsonQueue setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonQueue setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    public JsonTokenAndQueue getJsonTokenAndQueue() {
        JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue();
        jsonTokenAndQueue.setCodeQR(codeQR);
        jsonTokenAndQueue.setCoordinate(coordinate);
        jsonTokenAndQueue.setBusinessName(businessName);
        jsonTokenAndQueue.setDisplayName(displayName);
        jsonTokenAndQueue.setStoreAddress(storeAddress);
        jsonTokenAndQueue.setCountryShortName(countryShortName);
        jsonTokenAndQueue.setStorePhone(storePhone);
        jsonTokenAndQueue.setTokenAvailableFrom(tokenAvailableFrom);
        jsonTokenAndQueue.setStartHour(startHour);
        jsonTokenAndQueue.setEndHour(endHour);
        jsonTokenAndQueue.setDelayedInMinutes(delayedInMinutes);
        jsonTokenAndQueue.setTopic(topic);
        jsonTokenAndQueue.setServingNumber(servingNumber);
        jsonTokenAndQueue.setLastNumber(lastNumber);
        jsonTokenAndQueue.setQueueStatus(queueStatus);
        jsonTokenAndQueue.setServiceEndTime(serviceEndTime);
        jsonTokenAndQueue.setAverageServiceTime(averageServiceTime);
        jsonTokenAndQueue.setCreateDate(created);
        jsonTokenAndQueue.setBizCategoryId(bizCategoryId);
        return jsonTokenAndQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonQueue jsonQueue = (JsonQueue) o;
        return Objects.equal(codeQR, jsonQueue.codeQR);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codeQR);
    }

    @Override
    public String toString() {
        return "JsonQueue{" +
                "codeQR='" + codeQR + '\'' +
                ", coordinate=" + Arrays.toString(coordinate) +
                ", businessName='" + businessName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", storePhone='" + storePhone + '\'' +
                ", businessType=" + businessType +
                ", tokenAvailableFrom=" + tokenAvailableFrom +
                ", startHour=" + startHour +
                ", tokenNotAvailableFrom=" + tokenNotAvailableFrom +
                ", endHour=" + endHour +
                ", delayedInMinutes=" + delayedInMinutes +
                ", preventJoining=" + preventJoining +
                ", dayClosed=" + dayClosed +
                ", topic='" + topic + '\'' +
                ", servingNumber=" + servingNumber +
                ", lastNumber=" + lastNumber +
                ", queueStatus=" + queueStatus +
                ", serviceEndTime='" + serviceEndTime + '\'' +
                ", remoteJoinCount=" + remoteJoinCount +
                ", created='" + created + '\'' +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", averageServiceTime=" + averageServiceTime +
                ", remoteJoinAvailable=" + remoteJoinAvailable +
                ", allowLoggedInUser=" + allowLoggedInUser +
                ", availableTokenCount=" + availableTokenCount +
                ", bizCategoryId='" + bizCategoryId + '\'' +
                ", error=" + error +
                '}';
    }
}
