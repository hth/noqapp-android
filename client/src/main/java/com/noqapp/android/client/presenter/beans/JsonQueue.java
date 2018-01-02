package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

    public double[] getCoordinate() {
        return coordinate;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public int getEndHour() {
        return endHour;
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

    public boolean isDayClosed() {
        return dayClosed;
    }

    public String getTopic() {
        return topic;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    /* Used when joining remotely. */
    public int getRemoteJoinCount() {
        return remoteJoinCount;
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

    public int getRatingCount() {
        return ratingCount;
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

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public void setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
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
