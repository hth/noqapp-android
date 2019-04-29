package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
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

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("bc")
    private String businessCustomerId;

    @JsonProperty("n")
    private String customerName;

    @JsonProperty("nm")
    private String name;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty("py")
    private PaymentStatusEnum paymentStatus;

    @JsonProperty ("op")
    private String orderPrice;

    @JsonProperty("pp")
    private String partialPayment;

    @JsonProperty ("pm")
    private PaymentModeEnum paymentMode;

    @JsonProperty("tv")
    private TransactionViaEnum transactionVia;

    @JsonProperty ("pop")
    private List<JsonPurchaseOrderProduct> purchaseOrderProducts = new LinkedList<>();

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

    public String getCustomerName() {
        return customerName;
    }

    public Receipt setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public Receipt setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Receipt setName(String name) {
        this.name = name;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Receipt setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public Receipt setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public Receipt setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public String getPartialPayment() {
        return partialPayment;
    }

    public Receipt setPartialPayment(String partialPayment) {
        this.partialPayment = partialPayment;
        return this;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public Receipt setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public TransactionViaEnum getTransactionVia() {
        return transactionVia;
    }

    public Receipt setTransactionVia(TransactionViaEnum transactionVia) {
        this.transactionVia = transactionVia;
        return this;
    }

    public List<JsonPurchaseOrderProduct> getPurchaseOrderProducts() {
        return purchaseOrderProducts;
    }

    public Receipt setPurchaseOrderProducts(List<JsonPurchaseOrderProduct> purchaseOrderProducts) {
        this.purchaseOrderProducts = purchaseOrderProducts;
        return this;
    }

    public String computeBalanceAmount() {
        switch (paymentStatus) {
            case PA:
                return "0";
            default:
                if (StringUtils.isBlank(partialPayment)) {
                    return CommonHelper.displayPrice(orderPrice);
                } else {
                    return CommonHelper.displayPrice(
                            String.valueOf(Double.parseDouble(orderPrice) - Double.parseDouble(partialPayment)));
                }
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MedicalInvoiceObj{");
        sb.append("businessName='").append(businessName).append('\'');
        sb.append(", storeAddress='").append(storeAddress).append('\'');
        sb.append(", businessCustomerId='").append(businessCustomerId).append('\'');
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", transactionId='").append(transactionId).append('\'');
        sb.append(", paymentStatus='").append(paymentStatus).append('\'');
        sb.append(", orderPrice='").append(orderPrice).append('\'');
        sb.append(", partialPayment='").append(partialPayment).append('\'');
        sb.append(", paymentMode='").append(paymentMode).append('\'');
        sb.append(", transactionVia='").append(transactionVia).append('\'');
        sb.append(", purchaseOrderProducts=").append(purchaseOrderProducts);
        sb.append('}');
        return sb.toString();
    }
}
