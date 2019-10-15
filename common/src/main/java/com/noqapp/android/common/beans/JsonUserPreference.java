package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.CommunicationModeEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentMethodEnum;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-06-25 08:18
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
public class JsonUserPreference extends AbstractDomain implements Serializable {

    @JsonProperty("ps")
    private CommunicationModeEnum promotionalSMS;

    @JsonProperty("fn")
    private CommunicationModeEnum firebaseNotification;

    @JsonProperty("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty("pm")
    private PaymentMethodEnum paymentMethod;

    @JsonProperty("uai")
    private String userAddressId;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public CommunicationModeEnum getPromotionalSMS() {
        return promotionalSMS;
    }

    public JsonUserPreference setPromotionalSMS(CommunicationModeEnum promotionalSMS) {
        this.promotionalSMS = promotionalSMS;
        return this;
    }

    public CommunicationModeEnum getFirebaseNotification() {
        return firebaseNotification;
    }

    public JsonUserPreference setFirebaseNotification(CommunicationModeEnum firebaseNotification) {
        this.firebaseNotification = firebaseNotification;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public JsonUserPreference setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public JsonUserPreference setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public String getUserAddressId() {
        return userAddressId;
    }

    public JsonUserPreference setUserAddressId(String userAddressId) {
        this.userAddressId = userAddressId;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonUserPreference{" + "promotionalSMS=" + promotionalSMS +
                ", firebaseNotification=" + firebaseNotification +
                ", error=" + error +
                '}';
    }
}
