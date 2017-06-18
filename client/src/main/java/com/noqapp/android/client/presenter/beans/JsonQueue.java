package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.QueueStatusEnum;
import com.noqapp.android.client.utils.Constants;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: omkar
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

    public int getPeopleInQueue() {
        return lastNumber - servingNumber;
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
        jsonTokenAndQueue.setTopic(topic);
        jsonTokenAndQueue.setServingNumber(servingNumber);
        jsonTokenAndQueue.setLastNumber(lastNumber);
        jsonTokenAndQueue.setQueueStatus(queueStatus);
        jsonTokenAndQueue.setServiceEndTime(serviceEndTime);
        jsonTokenAndQueue.setCreateDate(created);
        return jsonTokenAndQueue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("codeQR", codeQR)
                .append("coordinate", coordinate)
                .append("businessName", businessName)
                .append("displayName", displayName)
                .append("storeAddress", storeAddress)
                .append("countryShortName", countryShortName)
                .append("storePhone", storePhone)
                .append("tokenAvailableFrom", tokenAvailableFrom)
                .append("startHour", startHour)
                .append("tokenNotAvailableFrom", tokenNotAvailableFrom)
                .append("endHour", endHour)
                .append("preventJoining", preventJoining)
                .append("dayClosed", dayClosed)
                .append("topic", topic)
                .append("servingNumber", servingNumber)
                .append("lastNumber", lastNumber)
                .append("queueStatus", queueStatus)
                .append("serviceEndTime", serviceEndTime)
                .append("remoteJoinCount", remoteJoinCount)
                .append("created", created)
                .append("rating", rating)
                .append("ratingCount", ratingCount)
                .append("error", error)
                .toString();
    }
}
