package com.noqapp.android.merchant.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.DiscountTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-06-10 11:29
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
public class JsonDiscount extends AbstractDomain {

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("dn")
    private String discountName;

    @JsonProperty("dd")
    private String discountDescription;

    @JsonProperty("da")
    private int discountAmount;

    @JsonProperty("dt")
    private DiscountTypeEnum discountType;

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonDiscount setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public JsonDiscount setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public JsonDiscount setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public JsonDiscount setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public JsonDiscount setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }
}
