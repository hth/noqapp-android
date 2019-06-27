package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 8/7/18 6:08 PM
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
public class OrderServed implements Serializable {
    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("t")
    private int servedNumber;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("p")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty("s")
    private QueueStatusEnum queueStatus;

    @JsonProperty("g")
    private String goTo;

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

    public String getTransactionId() {
        return transactionId;
    }

    public OrderServed setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public OrderServed setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public OrderServed setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public OrderServed setQueueStatus(QueueStatusEnum queueStatus) {
        this.queueStatus = queueStatus;
        return this;
    }

    public String getGoTo() {
        return goTo;
    }

    public void setGoTo(String goTo) {
        this.goTo = goTo;
    }
}
