package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.CommunicationModeEnum;

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

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonUserPreference{");
        sb.append("promotionalSMS=").append(promotionalSMS);
        sb.append(", firebaseNotification=").append(firebaseNotification);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
