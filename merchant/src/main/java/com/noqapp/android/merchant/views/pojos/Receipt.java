package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;
import com.noqapp.android.common.model.types.TransactionViaEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;

import java.util.LinkedList;
import java.util.List;

public class Receipt {
    private String businessName;
    private String businessAddress;
    private String businessCustomerId;
    private String customerName;
    private String doctorName;
    private String orderId;
    private PaymentStatusEnum paymentStatus;
    private String totalAmount;
    private String paidAmount;
    private String balanceAmount;
    private PaymentModeEnum paymentMode;
    private TransactionViaEnum transactionVia;
    private List<JsonPurchaseOrderProduct> purchaseOrderProducts = new LinkedList<>();

    public String getBusinessName() {
        return businessName;
    }

    public Receipt setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public Receipt setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
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

    public String getDoctorName() {
        return doctorName;
    }

    public Receipt setDoctorName(String doctorName) {
        this.doctorName = doctorName;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public Receipt setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public Receipt setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public Receipt setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public Receipt setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public Receipt setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MedicalInvoiceObj{");
        sb.append("businessName='").append(businessName).append('\'');
        sb.append(", businessAddress='").append(businessAddress).append('\'');
        sb.append(", businessCustomerId='").append(businessCustomerId).append('\'');
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", doctorName='").append(doctorName).append('\'');
        sb.append(", orderId='").append(orderId).append('\'');
        sb.append(", paymentStatus='").append(paymentStatus).append('\'');
        sb.append(", totalAmount='").append(totalAmount).append('\'');
        sb.append(", paidAmount='").append(paidAmount).append('\'');
        sb.append(", balanceAmount='").append(balanceAmount).append('\'');
        sb.append(", paymentMode='").append(paymentMode).append('\'');
        sb.append(", transactionVia='").append(transactionVia).append('\'');
        sb.append(", purchaseOrderProducts=").append(purchaseOrderProducts);
        sb.append('}');
        return sb.toString();
    }
}
