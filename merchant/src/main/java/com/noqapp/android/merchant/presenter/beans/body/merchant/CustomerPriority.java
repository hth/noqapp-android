package com.noqapp.android.merchant.presenter.beans.body.merchant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.ActionTypeEnum;
import com.noqapp.android.common.model.types.BusinessCustomerAttributeEnum;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;

import java.util.ArrayList;
import java.util.List;

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

    @JsonProperty("pl")
    private CustomerPriorityLevelEnum customerPriorityLevel = CustomerPriorityLevelEnum.I;

    @JsonProperty("ca")
    private List<BusinessCustomerAttributeEnum> businessCustomerAttributes = new ArrayList<>();

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

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public void setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
    }

    public List<BusinessCustomerAttributeEnum> getBusinessCustomerAttributes() {
        return businessCustomerAttributes;
    }

    public void setBusinessCustomerAttributes(List<BusinessCustomerAttributeEnum> businessCustomerAttributes) {
        this.businessCustomerAttributes = businessCustomerAttributes;
    }
}