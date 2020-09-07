package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;
import com.noqapp.android.client.model.types.AmenityEnum;
import com.noqapp.android.client.model.types.FacilityEnum;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.AppointmentStateEnum;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.OnOffEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

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

    @JsonProperty("ls")
    private int lunchTimeStart;

    @JsonProperty("le")
    private int lunchTimeEnd;

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

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("l")
    private int lastNumber;

    @JsonProperty("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("u")
    private String created;

    @JsonProperty("ra")
    private float rating;

    @JsonProperty("rc")
    private int reviewCount;

    @JsonProperty("as")
    private long averageServiceTime;

    @JsonProperty("sd")
    private int limitServiceByDays = 0;

    @JsonProperty("pa")
    private OnOffEnum priorityAccess = OnOffEnum.F;

    //***************************/
    //*  Queue Settings Starts. */
    //***************************/
    @JsonProperty("rj")
    private boolean remoteJoinAvailable = false;

    @JsonProperty("lu")
    private boolean allowLoggedInUser = false;

    @JsonProperty("at")
    private int availableTokenCount;
    //***************************/
    //*  Queue Settings Ends.   */
    //***************************/

    //*********************************/
    //*  Queue Price Setting Starts.  */
    //*********************************/
    @JsonProperty("ep")
    private boolean enabledPayment;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("cp")
    private int cancellationPrice;

    //*********************************/
    //*  Queue Price Settings Ends.   */
    //*********************************/

    //******************************************/
    //*  Queue Appointment Setting Starts.     */
    //******************************************/
    @JsonProperty("ps")
    private AppointmentStateEnum appointmentState;

    @JsonProperty("pd")
    private int appointmentDuration;

    @JsonProperty("pf")
    private int appointmentOpenHowFar;
    //******************************************/
    //*  Queue Appointment Setting Ends.       */
    //******************************************/

    @JsonProperty("bc")
    private String bizCategoryId;

    @JsonProperty("ff")
    private String famousFor;

    @JsonProperty("dd")
    private int discount;

    @JsonProperty("md")
    private int minimumDeliveryOrder;

    @JsonProperty("dr")
    private int deliveryRange;

    @JsonProperty("si")
    private Set<String> storeServiceImages = new LinkedHashSet<>();

    @JsonProperty("ii")
    private Set<String> storeInteriorImages = new LinkedHashSet<>();

    @JsonProperty("pm")
    private List<PaymentModeEnum> paymentModes = new LinkedList<>();

    @JsonProperty("dm")
    private List<DeliveryModeEnum> deliveryModes = new LinkedList<>();

    @JsonProperty("am")
    private List<AmenityEnum> amenities = new LinkedList<>();

    @JsonProperty("fa")
    private List<FacilityEnum> facilities = new LinkedList<>();

    @JsonProperty("sl")
    private String timeSlotMessage;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonQueue setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueue setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public JsonQueue setGeoHash(String geoHash) {
        this.geoHash = geoHash;
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

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonQueue setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonQueue setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonQueue setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonQueue setTown(String town) {
        this.town = town;
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

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
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

    public String getCreated() {
        return created;
    }

    public JsonQueue setCreated(Date created) {
        this.created = DateFormatUtils.format(created, CommonHelper.ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        return this;
    }

    public float getRating() {
        return rating;
    }

    public JsonQueue setRating(float rating) {
        this.rating = rating;
        return this;
    }

    public JsonQueue setCreated(String created) {
        this.created = created;
        return this;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public JsonQueue setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public int getLimitServiceByDays() {
        return limitServiceByDays;
    }

    public JsonQueue setLimitServiceByDays(int limitServiceByDays) {
        this.limitServiceByDays = limitServiceByDays;
        return this;
    }

    public OnOffEnum getPriorityAccess() {
        return priorityAccess;
    }

    public void setPriorityAccess(OnOffEnum priorityAccess) {
        this.priorityAccess = priorityAccess;
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

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public JsonQueue setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public JsonQueue setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public JsonQueue setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public JsonQueue setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getFamousFor() {
        return famousFor;
    }

    public JsonQueue setFamousFor(String famousFor) {
        this.famousFor = famousFor;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public JsonQueue setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getMinimumDeliveryOrder() {
        return minimumDeliveryOrder;
    }

    public JsonQueue setMinimumDeliveryOrder(int minimumDeliveryOrder) {
        this.minimumDeliveryOrder = minimumDeliveryOrder;
        return this;
    }

    public int getDeliveryRange() {
        return deliveryRange;
    }

    public JsonQueue setDeliveryRange(int deliveryRange) {
        this.deliveryRange = deliveryRange;
        return this;
    }

    public Set<String> getStoreServiceImages() {
        return storeServiceImages;
    }

    public JsonQueue setStoreServiceImages(Set<String> storeServiceImages) {
        this.storeServiceImages = storeServiceImages;
        return this;
    }

    public Set<String> getStoreInteriorImages() {
        return storeInteriorImages;
    }

    public JsonQueue setStoreInteriorImages(Set<String> storeInteriorImages) {
        this.storeInteriorImages = storeInteriorImages;
        return this;
    }

    public List<PaymentModeEnum> getPaymentModes() {
        return paymentModes;
    }

    public JsonQueue setPaymentModes(List<PaymentModeEnum> paymentModes) {
        this.paymentModes = paymentModes;
        return this;
    }

    public List<DeliveryModeEnum> getDeliveryModes() {
        return deliveryModes;
    }

    public JsonQueue setDeliveryModes(List<DeliveryModeEnum> deliveryModes) {
        this.deliveryModes = deliveryModes;
        return this;
    }

    public List<AmenityEnum> getAmenities() {
        return amenities;
    }

    public JsonQueue setAmenities(List<AmenityEnum> amenities) {
        this.amenities = amenities;
        return this;
    }

    public List<FacilityEnum> getFacilities() {
        return facilities;
    }

    public JsonQueue setFacilities(List<FacilityEnum> facilities) {
        this.facilities = facilities;
        return this;
    }

    public boolean isEnabledPayment() {
        return enabledPayment;
    }

    public JsonQueue setEnabledPayment(boolean enabledPayment) {
        this.enabledPayment = enabledPayment;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonQueue setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getCancellationPrice() {
        return cancellationPrice;
    }

    public JsonQueue setCancellationPrice(int cancellationPrice) {
        this.cancellationPrice = cancellationPrice;
        return this;
    }

    public String getTimeSlotMessage() {
        return timeSlotMessage;
    }

    public void setTimeSlotMessage(String timeSlotMessage) {
        this.timeSlotMessage = timeSlotMessage;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    public JsonTokenAndQueue getJsonTokenAndQueue() {
        JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue()
            .setCodeQR(codeQR)
            .setGeoHash(geoHash)
            .setBusinessName(businessName)
            .setDisplayName(displayName)
            .setStoreAddress(storeAddress)
            .setArea(area)
            .setTown(town)
            .setCountryShortName(countryShortName)
            .setStorePhone(storePhone)
            .setTokenAvailableFrom(tokenAvailableFrom)
            .setStartHour(startHour)
            .setEndHour(endHour)
            .setDelayedInMinutes(delayedInMinutes)
            .setTopic(topic)
            .setServingNumber(servingNumber)
            .setLastNumber(lastNumber)
            .setQueueStatus(queueStatus)
            .setServiceEndTime(serviceEndTime)
            .setAverageServiceTime(averageServiceTime)
            .setCreateDate(created)
            .setBusinessType(businessType)
            .setPurchaseOrderState(PurchaseOrderStateEnum.IN);
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
        final StringBuffer sb = new StringBuffer("JsonQueue{");
        sb.append("bizStoreId='").append(bizStoreId).append('\'');
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append(", geoHash='").append(geoHash).append('\'');
        sb.append(", businessName='").append(businessName).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", businessType=").append(businessType);
        sb.append(", storeAddress='").append(storeAddress).append('\'');
        sb.append(", area='").append(area).append('\'');
        sb.append(", town='").append(town).append('\'');
        sb.append(", countryShortName='").append(countryShortName).append('\'');
        sb.append(", storePhone='").append(storePhone).append('\'');
        sb.append(", tokenAvailableFrom=").append(tokenAvailableFrom);
        sb.append(", startHour=").append(startHour);
        sb.append(", tokenNotAvailableFrom=").append(tokenNotAvailableFrom);
        sb.append(", endHour=").append(endHour);
        sb.append(", delayedInMinutes=").append(delayedInMinutes);
        sb.append(", preventJoining=").append(preventJoining);
        sb.append(", dayClosed=").append(dayClosed);
        sb.append(", topic='").append(topic).append('\'');
        sb.append(", servingNumber=").append(servingNumber);
        sb.append(", lastNumber=").append(lastNumber);
        sb.append(", queueStatus=").append(queueStatus);
        sb.append(", serviceEndTime='").append(serviceEndTime).append('\'');
        sb.append(", created='").append(created).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", averageServiceTime=").append(averageServiceTime);
        sb.append(", remoteJoinAvailable=").append(remoteJoinAvailable);
        sb.append(", allowLoggedInUser=").append(allowLoggedInUser);
        sb.append(", availableTokenCount=").append(availableTokenCount);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", cancellationPrice=").append(cancellationPrice);
        sb.append(", bizCategoryId='").append(bizCategoryId).append('\'');
        sb.append(", famousFor='").append(famousFor).append('\'');
        sb.append(", discount=").append(discount);
        sb.append(", minimumDeliveryOrder=").append(minimumDeliveryOrder);
        sb.append(", deliveryRange=").append(deliveryRange);
        sb.append(", storeServiceImages=").append(storeServiceImages);
        sb.append(", storeInteriorImages=").append(storeInteriorImages);
        sb.append(", paymentModes=").append(paymentModes);
        sb.append(", deliveryModes=").append(deliveryModes);
        sb.append(", amenities=").append(amenities);
        sb.append(", facilities=").append(facilities);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
