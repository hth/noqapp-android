package com.noqapp.android.merchant.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.model.types.DiscountTypeEnum;

import java.io.Serializable;

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
public class JsonDiscount extends AbstractDomain implements Serializable {

    @JsonProperty("di")
    private String discountId;

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

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getDiscountId() {
        return discountId;
    }

    public JsonDiscount setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }


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

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JsonDiscount{" +
                "bizNameId='" + bizNameId + '\'' +
                ", discountName='" + discountName + '\'' +
                ", discountDescription='" + discountDescription + '\'' +
                ", discountAmount=" + discountAmount +
                ", discountType=" + discountType +
                ", error=" + error +
                '}';
    }
}
