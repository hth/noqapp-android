package com.noqapp.android.merchant.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.merchant.presenter.beans.ErrorEncounteredJson;

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

    @JsonProperty("c")
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

    @JsonProperty("pj")
    private boolean preventJoining;

    @JsonProperty("dc")
    private boolean dayClosed = false;

    //TODO add this property in Queue Settings screen
    @JsonProperty("at")
    private int availableTokenCount;

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

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public QueueSetting setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public QueueSetting setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public int getAvailableTokenCount() {
        return availableTokenCount;
    }

    public QueueSetting setAvailableTokenCount(int availableTokenCount) {
        this.availableTokenCount = availableTokenCount;
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
                ", preventJoining=" + preventJoining +
                ", dayClosed=" + dayClosed +
                ", availableTokenCount=" + availableTokenCount +
                ", error=" + error +
                '}';
    }
}
