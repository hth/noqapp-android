package com.noqapp.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.client.model.types.QueueStatusEnum;

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
public class JsonTokenAndQueue {

    @JsonProperty("c")
    private String codeQR;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty("p")
    private String storePhone;

    @JsonProperty("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty("b")
    private int startHour;

    /* Store business end hour. */
    @JsonProperty("e")
    private int endHour;

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

    @JsonProperty ("st")
    private String servicedTime;

    @JsonProperty("u")
    private String createDate;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
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

    public String getServicedTime() {
        return servicedTime;
    }

    public void setServicedTime(String servicedTime) {
        this.servicedTime = servicedTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @JsonIgnoreProperties
    public int afterHowLong() {
        return token - servingNumber;
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueue{" +
                "codeQR='" + codeQR + '\'' +
                ", businessName='" + businessName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", storePhone='" + storePhone + '\'' +
                ", tokenAvailableFrom=" + tokenAvailableFrom +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", topic='" + topic + '\'' +
                ", servingNumber=" + servingNumber +
                ", lastNumber=" + lastNumber +
                ", token=" + token +
                ", queueStatus=" + queueStatus +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
