package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.JsonNameDatePair;
import com.noqapp.android.common.beans.store.JsonPurchaseOrder;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.TransactionViaEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.utils.CommonHelper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
public class Receipt extends AbstractDomain implements Serializable {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("p")
    private String storePhone;

    @JsonProperty("bc")
    private String businessCustomerId;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty ("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    /* Professional Name. */
    @JsonProperty("nm")
    private String name;

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education;

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses;

    public String getCodeQR() {
        return codeQR;
    }

    public Receipt setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public Receipt setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public Receipt setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public Receipt setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public Receipt setStorePhone(String storePhone) {
        this.storePhone = storePhone;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public Receipt setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public Receipt setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Receipt setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    public Receipt setJsonPurchaseOrder(JsonPurchaseOrder jsonPurchaseOrder) {
        this.jsonPurchaseOrder = jsonPurchaseOrder;
        return this;
    }

    public String getName() {
        return name;
    }

    public Receipt setName(String name) {
        this.name = name;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public Receipt setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public Receipt setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public String computeBalanceAmount() {
        switch (jsonPurchaseOrder.getPaymentStatus()) {
            case PA:
                return "0";
            default:
                if (StringUtils.isBlank(jsonPurchaseOrder.getPartialPayment())) {
                    return CommonHelper.displayPrice(jsonPurchaseOrder.getOrderPrice());
                } else {
                    return CommonHelper.displayPrice(
                            String.valueOf(Double.parseDouble(jsonPurchaseOrder.getOrderPrice()) - Double.parseDouble(jsonPurchaseOrder.getPartialPayment())));
                }
        }
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "codeQR='" + codeQR + '\'' +
                ", businessType=" + businessType +
                ", businessName='" + businessName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", storePhone='" + storePhone + '\'' +
                ", businessCustomerId='" + businessCustomerId + '\'' +
                ", queueUserId='" + queueUserId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", jsonPurchaseOrder=" + jsonPurchaseOrder +
                ", name='" + name + '\'' +
                ", education=" + education +
                ", licenses=" + licenses +
                '}';
    }
}
