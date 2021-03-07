package com.noqapp.android.common.beans.marketplace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.category.ItemConditionEnum;

/**
 * hitender
 * 3/7/21 12:06 PM
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
public class JsonHouseholdItem extends JsonMarketplace {

    @JsonProperty("ic")
    private ItemConditionEnum itemCondition;

    public ItemConditionEnum getItemCondition() {
        return itemCondition;
    }

    public JsonHouseholdItem setItemCondition(ItemConditionEnum itemCondition) {
        this.itemCondition = itemCondition;
        return this;
    }
}
