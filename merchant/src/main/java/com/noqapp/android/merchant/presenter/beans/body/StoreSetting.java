package com.noqapp.android.merchant.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.AppointmentStateEnum;

import java.io.Serializable;

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
public class StoreSetting implements Serializable {

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

    @JsonProperty("ls")
    private int lunchTimeStart;

    @JsonProperty("le")
    private int lunchTimeEnd;

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

    @JsonProperty("fd")
    private int freeFollowupDays;

    @JsonProperty("df")
    private int discountedFollowupDays;

    @JsonProperty("dp")
    private int discountedFollowupProductPrice;

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

    @JsonProperty("sa")
    private ActionTypeEnum storeActionType;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public StoreSetting setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public StoreSetting setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public StoreSetting setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public StoreSetting setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public StoreSetting setEndHour(int endHour) {
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

    public StoreSetting setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public StoreSetting setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public boolean isTempDayClosed() {
        return tempDayClosed;
    }

    public StoreSetting setTempDayClosed(boolean tempDayClosed) {
        this.tempDayClosed = tempDayClosed;
        return this;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public StoreSetting setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public StoreSetting setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
        return this;
    }

    public String getFromDay() {
        return fromDay;
    }

    public StoreSetting setFromDay(String fromDay) {
        this.fromDay = fromDay;
        return this;
    }

    public String getUntilDay() {
        return untilDay;
    }

    public StoreSetting setUntilDay(String untilDay) {
        this.untilDay = untilDay;
        return this;
    }

    public String getScheduledFromDay() {
        return scheduledFromDay;
    }

    public StoreSetting setScheduledFromDay(String scheduledFromDay) {
        this.scheduledFromDay = scheduledFromDay;
        return this;
    }

    public String getScheduledUntilDay() {
        return scheduledUntilDay;
    }

    public StoreSetting setScheduledUntilDay(String scheduledUntilDay) {
        this.scheduledUntilDay = scheduledUntilDay;
        return this;
    }

    public boolean isEnabledPayment() {
        return enabledPayment;
    }

    public StoreSetting setEnabledPayment(boolean enabledPayment) {
        this.enabledPayment = enabledPayment;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public StoreSetting setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getCancellationPrice() {
        return cancellationPrice;
    }

    public StoreSetting setCancellationPrice(int cancellationPrice) {
        this.cancellationPrice = cancellationPrice;
        return this;
    }

    public int getFreeFollowupDays() {
        return freeFollowupDays;
    }

    public StoreSetting setFreeFollowupDays(int freeFollowupDays) {
        this.freeFollowupDays = freeFollowupDays;
        return this;
    }

    public int getDiscountedFollowupDays() {
        return discountedFollowupDays;
    }

    public StoreSetting setDiscountedFollowupDays(int discountedFollowupDays) {
        this.discountedFollowupDays = discountedFollowupDays;
        return this;
    }

    public int getDiscountedFollowupProductPrice() {
        return discountedFollowupProductPrice;
    }

    public StoreSetting setDiscountedFollowupProductPrice(int discountedFollowupProductPrice) {
        this.discountedFollowupProductPrice = discountedFollowupProductPrice;
        return this;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public StoreSetting setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public StoreSetting setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
        return this;
    }

    public int getAppointmentOpenHowFar() {
        return appointmentOpenHowFar;
    }

    public StoreSetting setAppointmentOpenHowFar(int appointmentOpenHowFar) {
        this.appointmentOpenHowFar = appointmentOpenHowFar;
        return this;
    }

    public ActionTypeEnum getStoreActionType() {
        return storeActionType;
    }

    public StoreSetting setStoreActionType(ActionTypeEnum storeActionType) {
        this.storeActionType = storeActionType;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public StoreSetting setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "StoreSetting{" +
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
                ", productPrice=" + productPrice +
                ", cancellationPrice=" + cancellationPrice +
                ", storeActionType=" + storeActionType +
                ", error=" + error +
                '}';
    }
}
