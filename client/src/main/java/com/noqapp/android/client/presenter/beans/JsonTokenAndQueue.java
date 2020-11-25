package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

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

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("di")
    private String displayImage;

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

    @JsonProperty("ls")
    private int lunchTimeStart;

    @JsonProperty("le")
    private int lunchTimeEnd;

    @JsonProperty("de")
    private int delayedInMinutes;

    @JsonProperty("o")
    private String topic;

    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("ds")
    private String displayServingNumber;

    @JsonProperty("l")
    private int lastNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("sl")
    private String timeSlotMessage;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("os")
    private PurchaseOrderStateEnum purchaseOrderState;

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

    @JsonProperty ("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("ti")
    private String transactionId;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonTokenAndQueue setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
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

    public JsonTokenAndQueue setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public JsonTokenAndQueue setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonTokenAndQueue setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonTokenAndQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
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

    public JsonTokenAndQueue setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public JsonTokenAndQueue setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonTokenAndQueue setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public JsonTokenAndQueue setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public JsonTokenAndQueue setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public JsonTokenAndQueue setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getLunchTimeStart() {
        return lunchTimeStart;
    }

    public void setLunchTimeStart(int lunchTimeStart) {
        this.lunchTimeStart = lunchTimeStart;
    }

    public int getLunchTimeEnd() {
        return lunchTimeEnd;
    }

    public void setLunchTimeEnd(int lunchTimeEnd) {
        this.lunchTimeEnd = lunchTimeEnd;
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

    public JsonTokenAndQueue setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonTokenAndQueue setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public String getDisplayServingNumber() {
        return displayServingNumber;
    }

    public JsonTokenAndQueue setDisplayServingNumber(String displayServingNumber) {
        this.displayServingNumber = displayServingNumber;
        return this;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public JsonTokenAndQueue setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
        return this;
    }

    public int getToken() {
        return token;
    }

    public JsonTokenAndQueue setToken(int token) {
        this.token = token;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonTokenAndQueue setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public JsonTokenAndQueue setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public JsonTokenAndQueue setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public JsonTokenAndQueue setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonTokenAndQueue setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    /**
     * Get average service time for a token.
     * @return average time in milli seconds
     */
    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    /**
     * Set average service time for a token.
     * @param averageServiceTime average time in milli seconds
     * @return token and queue
     */
    public JsonTokenAndQueue setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public JsonTokenAndQueue setHoursSaved(int hoursSaved) {
        this.hoursSaved = hoursSaved;
        return this;
    }

    public String getCreateDate() {
        return createDate;
    }

    public JsonTokenAndQueue setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public JsonTokenAndQueue setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    @JsonIgnoreProperties
    public int afterHowLong() {
        return token - servingNumber;
    }

    @JsonIgnoreProperties
    public String afterHowLongForDisplay() {
        return displayToken.substring(0, 1) + (Integer.parseInt(displayToken.substring(1)) - (Integer.parseInt(displayToken.substring(1)) - (100 + servingNumber)));
    }

    @JsonIgnoreProperties
    public boolean isTokenExpired() {
        return afterHowLong() <= 0;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonTokenAndQueue setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonTokenAndQueue setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getTimeSlotMessage() {
        return timeSlotMessage;
    }

    public void setTimeSlotMessage(String timeSlotMessage) {
        this.timeSlotMessage = timeSlotMessage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("codeQR", codeQR)
                .append("geoHash", geoHash)
                .append("businessName", businessName)
                .append("displayImage", displayImage)
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
                .append("lunchTimeStart", lunchTimeStart)
                .append("lunchTimeEnd", lunchTimeEnd)
                .append("delayedInMinutes", delayedInMinutes)
                .append("topic", topic)
                .append("servingNumber", servingNumber)
                .append("displayServingNumber", displayServingNumber)
                .append("lastNumber", lastNumber)
                .append("token", token)
                .append("displayToken", displayToken)
                .append("queueUserId", queueUserId)
                .append("queueStatus", queueStatus)
                .append("purchaseOrderState", purchaseOrderState)
                .append("serviceEndTime", serviceEndTime)
                .append("ratingCount", ratingCount)
                .append("averageServiceTime", averageServiceTime)
                .append("hoursSaved", hoursSaved)
                .append("createDate", createDate)
                .append("jsonPurchaseOrder", jsonPurchaseOrder)
                .append("bizCategoryId", bizCategoryId)
                .append("transactionId", transactionId)
                .append("timeSlotMessage", timeSlotMessage)
                .toString();
    }
}
