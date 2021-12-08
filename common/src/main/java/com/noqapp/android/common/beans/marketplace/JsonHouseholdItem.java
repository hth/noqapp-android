package com.noqapp.android.common.beans.marketplace;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.category.HouseholdItemCategoryEnum;
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

    @JsonProperty("hc")
    private HouseholdItemCategoryEnum householdItemCategory;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public ItemConditionEnum getItemCondition() {
        return itemCondition;
    }

    public JsonHouseholdItem setItemCondition(ItemConditionEnum itemCondition) {
        this.itemCondition = itemCondition;
        return this;
    }

    public HouseholdItemCategoryEnum getHouseholdItemCategory() {
        return householdItemCategory;
    }

    public JsonHouseholdItem setHouseholdItemCategory(HouseholdItemCategoryEnum householdItemCategory) {
        this.householdItemCategory = householdItemCategory;
        return this;
    }

    @Override
    public ErrorEncounteredJson getError() {
        return error;
    }

    @Override
    public JsonHouseholdItem setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }
}
