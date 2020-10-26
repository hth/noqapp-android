package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 10/5/18 9:40 AM
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
public class JsonPurchaseOrderHistorical extends AbstractDomain implements Serializable {

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("da")
    private String deliveryAddress;

    @JsonProperty("sd")
    private int storeDiscount;

    @JsonProperty("op")
    private String orderPrice;

    @JsonProperty ("ta")
    private String tax;

    @JsonProperty("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty("pm")
    private PaymentModeEnum paymentMode;

    @JsonProperty("py")
    private PaymentStatusEnum paymentStatus;

    @JsonProperty("ps")
    private PurchaseOrderStateEnum presentOrderState;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("rv")
    private String review;

    /* Order Number. */
    @JsonProperty("tn")
    private int tokenNumber;

    @JsonProperty ("dt")
    private String displayToken;

    /* Locked when being served. */
    @JsonProperty("sn")
    private String serverName;

    @JsonProperty("sb")
    private String serviceBeginTime;

    @JsonProperty("se")
    private String serviceEndTime;

    @JsonProperty("ti")
    private String transactionId;

    /* This is business name. */
    @JsonProperty("dn")
    private String displayName;

    @JsonProperty("u")
    private String created;

    @JsonProperty("an")
    private String additionalNote;

    @JsonProperty("pops")
    private List<JsonPurchaseOrderProductHistorical> jsonPurchaseOrderProductHistoricalList = new ArrayList<>();

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonPurchaseOrderHistorical setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonPurchaseOrderHistorical setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public JsonPurchaseOrderHistorical setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public int getStoreDiscount() {
        return storeDiscount;
    }

    public JsonPurchaseOrderHistorical setStoreDiscount(int storeDiscount) {
        this.storeDiscount = storeDiscount;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public JsonPurchaseOrderHistorical setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public String getTax() {
        return tax;
    }

    public JsonPurchaseOrderHistorical setTax(String tax) {
        this.tax = tax;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public JsonPurchaseOrderHistorical setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public JsonPurchaseOrderHistorical setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public JsonPurchaseOrderHistorical setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public PurchaseOrderStateEnum getPresentOrderState() {
        return presentOrderState;
    }

    public JsonPurchaseOrderHistorical setPresentOrderState(PurchaseOrderStateEnum presentOrderState) {
        this.presentOrderState = presentOrderState;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonPurchaseOrderHistorical setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonPurchaseOrderHistorical setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getReview() {
        return review;
    }

    public JsonPurchaseOrderHistorical setReview(String review) {
        this.review = review;
        return this;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public JsonPurchaseOrderHistorical setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
        return this;
    }

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
    }

    public String getServerName() {
        return serverName;
    }

    public JsonPurchaseOrderHistorical setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServiceBeginTime() {
        return serviceBeginTime;
    }

    public JsonPurchaseOrderHistorical setServiceBeginTime(String serviceBeginTime) {
        this.serviceBeginTime = serviceBeginTime;
        return this;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public JsonPurchaseOrderHistorical setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonPurchaseOrderHistorical setCreated(String created) {
        this.created = created;
        return this;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public JsonPurchaseOrderHistorical setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonPurchaseOrderHistorical setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonPurchaseOrderHistorical setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public List<JsonPurchaseOrderProductHistorical> getJsonPurchaseOrderProductHistoricalList() {
        return jsonPurchaseOrderProductHistoricalList;
    }

    public void setJsonPurchaseOrderProductHistoricalList(List<JsonPurchaseOrderProductHistorical> jsonPurchaseOrderProductHistoricalList) {
        this.jsonPurchaseOrderProductHistoricalList = jsonPurchaseOrderProductHistoricalList;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public JsonPurchaseOrderHistorical setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getArea() {
        return area;
    }

    public JsonPurchaseOrderHistorical setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonPurchaseOrderHistorical setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonPurchaseOrderHistorical setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonPurchaseOrderHistorical setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "JsonPurchaseOrderHistorical{" +
                "queueUserId='" + queueUserId + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", storeDiscount=" + storeDiscount +
                ", orderPrice='" + orderPrice + '\'' +
                ", deliveryMode=" + deliveryMode +
                ", paymentMode=" + paymentMode +
                ", presentOrderState=" + presentOrderState +
                ", businessType=" + businessType +
                ", ratingCount=" + ratingCount +
                ", review='" + review + '\'' +
                ", tokenNumber=" + tokenNumber +
                ", serverName='" + serverName + '\'' +
                ", serviceBeginTime='" + serviceBeginTime + '\'' +
                ", serviceEndTime='" + serviceEndTime + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", created='" + created + '\'' +
                ", additionalNote='" + additionalNote + '\'' +
                ", jsonPurchaseOrderProductHistoricalList=" + jsonPurchaseOrderProductHistoricalList +
                ", storeAddress='" + storeAddress + '\'' +
                ", area='" + area + '\'' +
                ", town='" + town + '\'' +
                ", countryShortName='" + countryShortName + '\'' +
                ", error=" + error +
                '}';
    }
}
