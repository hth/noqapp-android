package com.noqapp.android.client.presenter.beans;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
@Entity(tableName = "token_queue")
public class JsonTokenAndQueue implements Serializable {

    @JsonProperty("qr")
    @ColumnInfo(name = "qr_code")
    private String codeQR;

    @JsonProperty("gh")
    @ColumnInfo(name = "geo_hash")
    private String geoHash;

    @JsonProperty("n")
    @ColumnInfo(name = "business_name")
    private String businessName;

    @JsonProperty("di")
    @ColumnInfo(name = "display_image")
    private String displayImage;

    @JsonProperty("d")
    @ColumnInfo(name = "display_name")
    private String displayName;

    @JsonProperty("sa")
    @ColumnInfo(name = "store_address")
    private String storeAddress;

    @JsonProperty("ar")
    @ColumnInfo(name = "area")
    private String area;

    @JsonProperty("to")
    @ColumnInfo(name = "to")
    private String town;

    @JsonProperty("cs")
    @ColumnInfo(name = "country_short_name")
    private String countryShortName;

    @JsonProperty("p")
    @ColumnInfo(name = "store_phone")
    private String storePhone;

    @JsonProperty("bt")
    @ColumnInfo(name = "business_type")
    private BusinessTypeEnum businessType;

    @JsonProperty("f")
    @ColumnInfo(name = "token_available_from")
    private Integer tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty("b")
    @ColumnInfo(name = "start_hour")
    private Integer startHour;

    /* Store business end hour. */
    @JsonProperty("e")
    @ColumnInfo(name = "end_hour")
    private Integer endHour;

    @JsonProperty("ls")
    @ColumnInfo(name = "lunch_time_start")
    private Integer lunchTimeStart;

    @JsonProperty("le")
    @ColumnInfo(name = "lunch_time_end")
    private Integer lunchTimeEnd;

    @JsonProperty("de")
    @ColumnInfo(name = "delayed_minutes")
    private Integer delayedInMinutes;

    @JsonProperty("o")
    @ColumnInfo(name = "topic")
    private String topic;

    @JsonProperty("s")
    @ColumnInfo(name = "serving_number")
    private Integer servingNumber;

    @JsonProperty("ds")
    @ColumnInfo(name = "display_serving_number")
    private String displayServingNumber;

    @JsonProperty("l")
    @ColumnInfo(name = "last_number")
    private Integer lastNumber;

    @JsonProperty("t")
    @ColumnInfo(name = "token")
    private Integer token = 0;

    @JsonProperty ("dt")
    @ColumnInfo(name = "display_token")
    private String displayToken;

    @JsonProperty("sl")
    @ColumnInfo(name = "time_slot_message")
    private String timeSlotMessage;

    @JsonProperty("qid")
    @ColumnInfo(name = "queue_user_id")
    private String queueUserId;

    @JsonProperty("q")
    @ColumnInfo(name = "queue_status")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("os")
    @ColumnInfo(name = "purchase_order_state")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty("se")
    @ColumnInfo(name = "service_end_time")
    private String serviceEndTime;

    @JsonProperty("ra")
    @ColumnInfo(name = "rating_count")
    private Integer ratingCount;

    @JsonProperty("as")
    @ColumnInfo(name = "average_service_time")
    private long averageServiceTime;

    @JsonProperty("hr")
    @ColumnInfo(name = "hours_saved")
    private Integer hoursSaved;

    @JsonProperty("u")
    @ColumnInfo(name = "create_date")
    private String createDate;

    @JsonProperty ("po")
    @ColumnInfo(name = "json_purchase_order")
    private JsonPurchaseOrder jsonPurchaseOrder;

    @JsonProperty("bc")
    @ColumnInfo(name = "biz_category_id")
    private String bizCategoryId;

    @JsonProperty("ti")
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "transaction_id")
    private String transactionId = "";

    @ColumnInfo(name = "history_queue")
    private Integer historyQueue = -1;

    @ColumnInfo(name = "has_updated")
    private Integer hasUpdated = -1;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
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

    public void setArea(String area) {
        this.area = area;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
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

    public Integer getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public void setTokenAvailableFrom(Integer tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getLunchTimeStart() {
        return lunchTimeStart;
    }

    public void setLunchTimeStart(Integer lunchTimeStart) {
        this.lunchTimeStart = lunchTimeStart;
    }

    public Integer getLunchTimeEnd() {
        return lunchTimeEnd;
    }

    public void setLunchTimeEnd(Integer lunchTimeEnd) {
        this.lunchTimeEnd = lunchTimeEnd;
    }

    public Integer getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public void setDelayedInMinutes(Integer delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(Integer servingNumber) {
        this.servingNumber = servingNumber;
    }

    public String getDisplayServingNumber() {
        return displayServingNumber;
    }

    public void setDisplayServingNumber(String displayServingNumber) {
        this.displayServingNumber = displayServingNumber;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
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

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public void setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
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
    public void setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public Integer getHoursSaved() {
        return hoursSaved;
    }

    public void setHoursSaved(Integer hoursSaved) {
        this.hoursSaved = hoursSaved;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public void setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
    }

    @JsonIgnoreProperties
    public Integer afterHowLong() {
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

    public void setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTimeSlotMessage() {
        return timeSlotMessage;
    }

    public void setTimeSlotMessage(String timeSlotMessage) {
        this.timeSlotMessage = timeSlotMessage;
    }

    public void setHistoryQueue(Integer historyQueue) {
        this.historyQueue = historyQueue;
    }

    public Integer getHistoryQueue() {
        return historyQueue;
    }

    public Integer getHasUpdated() {
        return hasUpdated;
    }

    public void setHasUpdated(Integer hasUpdated) {
        this.hasUpdated = hasUpdated;
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
            .append("historyQueue", historyQueue)
            .append("hasUpdated", hasUpdated)
            .toString();
    }
}
