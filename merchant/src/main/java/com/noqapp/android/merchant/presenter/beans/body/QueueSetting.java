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

    @JsonProperty("pj")
    private boolean dayClosed;

    @JsonProperty("dc")
    private boolean preventJoining;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public void setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public void setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "QueueSetting{" +
                "codeQR='" + codeQR + '\'' +
                ", dayClosed=" + dayClosed +
                ", preventJoining=" + preventJoining +
                ", error=" + error +
                '}';
    }
}
