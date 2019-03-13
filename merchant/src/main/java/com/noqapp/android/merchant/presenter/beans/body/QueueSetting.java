package com.noqapp.android.merchant.presenter.beans.body;

import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.ServicePaymentEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by chandra on 7/15/17.
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
public class QueueSetting {

    @JsonProperty("qr")
    private String codeQR;

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

    @JsonProperty ("dc")
    private boolean dayClosed = false;

    @JsonProperty("tc")
    private boolean tempDayClosed;

    @JsonProperty("pj")
    private boolean preventJoining;

    @JsonProperty("at")
    private int availableTokenCount;

    @JsonProperty ("fr")
    private String fromDay;

    @JsonProperty ("un")
    private String untilDay;

    @JsonProperty ("scFr")
    private String scheduledFromDay;

    @JsonProperty ("scUn")
    private String scheduledUntilDay;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("cp")
    private int cancellationPrice;

    @JsonProperty("sp")
    private ServicePaymentEnum servicePayment;

    @JsonProperty("sa")
    private ActionTypeEnum storeActionType;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public QueueSetting setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public QueueSetting setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public QueueSetting setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public QueueSetting setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public QueueSetting setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public QueueSetting setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public QueueSetting setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public boolean isTempDayClosed() {
        return tempDayClosed;
    }

    public QueueSetting setTempDayClosed(boolean tempDayClosed) {
        this.tempDayClosed = tempDayClosed;
        return this;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public QueueSetting setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public QueueSetting setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public String getFromDay() {
        return fromDay;
    }

    public QueueSetting setFromDay(String fromDay) {
        this.fromDay = fromDay;
        return this;
    }

    public String getUntilDay() {
        return untilDay;
    }

    public QueueSetting setUntilDay(String untilDay) {
        this.untilDay = untilDay;
        return this;
    }

    public String getScheduledFromDay() {
        return scheduledFromDay;
    }

    public QueueSetting setScheduledFromDay(String scheduledFromDay) {
        this.scheduledFromDay = scheduledFromDay;
        return this;
    }

    public String getScheduledUntilDay() {
        return scheduledUntilDay;
    }

    public QueueSetting setScheduledUntilDay(String scheduledUntilDay) {
        this.scheduledUntilDay = scheduledUntilDay;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public QueueSetting setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getCancellationPrice() {
        return cancellationPrice;
    }

    public QueueSetting setCancellationPrice(int cancellationPrice) {
        this.cancellationPrice = cancellationPrice;
        return this;
    }

    public ServicePaymentEnum getServicePayment() {
        return servicePayment;
    }

    public QueueSetting setServicePayment(ServicePaymentEnum servicePayment) {
        this.servicePayment = servicePayment;
        return this;
    }

    public ActionTypeEnum getStoreActionType() {
        return storeActionType;
    }

    public QueueSetting setStoreActionType(ActionTypeEnum storeActionType) {
        this.storeActionType = storeActionType;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public QueueSetting setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "QueueSetting{" +
                "codeQR='" + codeQR + '\'' +
                ", tokenAvailableFrom=" + tokenAvailableFrom +
                ", startHour=" + startHour +
                ", tokenNotAvailableFrom=" + tokenNotAvailableFrom +
                ", endHour=" + endHour +
                ", delayedInMinutes=" + delayedInMinutes +
                ", dayClosed=" + dayClosed +
                ", tempDayClosed=" + tempDayClosed +
                ", preventJoining=" + preventJoining +
                ", availableTokenCount=" + availableTokenCount +
                ", fromDay='" + fromDay + '\'' +
                ", untilDay='" + untilDay + '\'' +
                ", scheduledFromDay='" + scheduledFromDay + '\'' +
                ", scheduledUntilDay='" + scheduledUntilDay + '\'' +
                ", storeActionType=" + storeActionType +
                ", error=" + error +
                '}';
    }
}
