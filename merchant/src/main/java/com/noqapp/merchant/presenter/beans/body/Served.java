package com.noqapp.merchant.presenter.beans.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.merchant.model.types.QueueStatusEnum;
import com.noqapp.merchant.model.types.QueueUserStateEnum;

/**
 * User: hitender
 * Date: 4/22/17 6:38 PM
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
public class Served {
    @JsonProperty("c")
    private String codeQR;

    @JsonProperty("t")
    private int servedNumber;

    @JsonProperty("q")
    private QueueUserStateEnum queueUserState;

    @JsonProperty("s")
    private QueueStatusEnum queueStatus;

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public int getServedNumber() {
        return servedNumber;
    }

    public void setServedNumber(int servedNumber) {
        this.servedNumber = servedNumber;
    }

    public QueueUserStateEnum getQueueUserState() {
        return queueUserState;
    }

    public void setQueueUserState(QueueUserStateEnum queueUserState) {
        this.queueUserState = queueUserState;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
    }
}

