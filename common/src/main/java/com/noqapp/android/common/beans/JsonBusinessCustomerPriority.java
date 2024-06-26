package com.noqapp.android.common.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.CustomerPriorityLevelEnum;

import java.io.Serializable;

/**
 * hitender
 * 5/24/20 1:20 PM
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
public class JsonBusinessCustomerPriority extends AbstractDomain implements Serializable {

    @JsonProperty("pl")
    private CustomerPriorityLevelEnum customerPriorityLevel;

    @JsonProperty("pn")
    private String priorityName;

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public JsonBusinessCustomerPriority setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
        return this;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public JsonBusinessCustomerPriority setPriorityName(String priorityName) {
        this.priorityName = priorityName;
        return this;
    }
}
