package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.ErrorEncounteredJson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-10 13:07
 */
@SuppressWarnings ({
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
public class JsonDiscountList extends AbstractDomain implements Serializable {

    @JsonProperty("ds")
    private List<JsonDiscount> discounts = new ArrayList<>();

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public List<JsonDiscount> getDiscounts() {
        return discounts;
    }

    public JsonDiscountList setDiscounts(List<JsonDiscount> discounts) {
        this.discounts = discounts;
        return this;
    }

    public JsonDiscountList addDiscount(JsonDiscount discount) {
        this.discounts.add(discount);
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonDiscountList{" +
                "discounts=" + discounts +
                ", error=" + error +
                '}';
    }
}
