package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-06-10 17:11
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
public class DiscountOnOrder extends AbstractDomain {

    @JsonProperty("di")
    private String discountId;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty("qid")
    private String queueUserId;

    public String getDiscountId() {
        return discountId;
    }

    public DiscountOnOrder setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public DiscountOnOrder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public DiscountOnOrder setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }
}
