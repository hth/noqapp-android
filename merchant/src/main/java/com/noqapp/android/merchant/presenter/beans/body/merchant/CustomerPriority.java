package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.ActionTypeEnum;

/**
 * hitender
 * 5/24/20 3:43 PM
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
public class CustomerPriority extends AbstractDomain {

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("at")
    private ActionTypeEnum actionType;

    @JsonProperty("qr")
    private String codeQR;

    public String getQueueUserId() {
        return queueUserId;
    }

    public CustomerPriority setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public CustomerPriority setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }
}