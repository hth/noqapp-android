package com.noqapp.android.common.beans;

import com.noqapp.android.common.model.types.DiscountTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 2019-06-12 06:48
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
public class JsonCoupon extends AbstractDomain implements Serializable {

    @JsonProperty("ci")
    private String couponId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("cc")
    private String couponCode;

    @JsonProperty("dn")
    private String discountName;

    @JsonProperty("dd")
    private String discountDescription;

    @JsonProperty("da")
    private int discountAmount;

    @JsonProperty("dt")
    private DiscountTypeEnum discountType;

    @JsonProperty("sd")
    private String couponStartDate;

    @JsonProperty("ed")
    private String couponEndDate;

    @JsonProperty("mu")
    private boolean multiUse;

    @JsonProperty("qid")
    private String qid;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public String getCouponId() {
        return couponId;
    }

    public JsonCoupon setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonCoupon setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public JsonCoupon setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public JsonCoupon setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public JsonCoupon setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public JsonCoupon setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public JsonCoupon setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }

    public String getCouponStartDate() {
        return couponStartDate;
    }

    public JsonCoupon setCouponStartDate(String couponStartDate) {
        this.couponStartDate = couponStartDate;
        return this;
    }

    public String getCouponEndDate() {
        return couponEndDate;
    }

    public JsonCoupon setCouponEndDate(String couponEndDate) {
        this.couponEndDate = couponEndDate;
        return this;
    }

    public boolean isMultiUse() {
        return multiUse;
    }

    public JsonCoupon setMultiUse(boolean multiUse) {
        this.multiUse = multiUse;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public JsonCoupon setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public JsonCoupon setError(ErrorEncounteredJson error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonCoupon{");
        sb.append("couponId='").append(couponId).append('\'');
        sb.append(", bizNameId='").append(bizNameId).append('\'');
        sb.append(", couponCode='").append(couponCode).append('\'');
        sb.append(", discountName='").append(discountName).append('\'');
        sb.append(", discountDescription='").append(discountDescription).append('\'');
        sb.append(", discountAmount=").append(discountAmount);
        sb.append(", discountType=").append(discountType);
        sb.append(", couponStartDate='").append(couponStartDate).append('\'');
        sb.append(", couponEndDate='").append(couponEndDate).append('\'');
        sb.append(", multiUse=").append(multiUse);
        sb.append(", qid='").append(qid).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}