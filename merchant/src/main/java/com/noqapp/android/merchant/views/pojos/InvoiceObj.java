package com.noqapp.android.merchant.views.pojos;

import com.noqapp.android.common.beans.store.JsonPurchaseOrderProduct;

import java.util.LinkedList;
import java.util.List;

public class InvoiceObj {

    private String businessName;
    private String businessAddress;
    private String businessCustomerId;
    private String customerName;
    private String doctorName;
    private String orderId;
    private String payment_status;
    private String total_amount;
    private String paid_amount;
    private String balance_amount;
    private String payment_mode;
    private String transaction_via;
    private List<JsonPurchaseOrderProduct> purchaseOrderProducts = new LinkedList<>();

    public String getBusinessName() {
        return businessName;
    }

    public InvoiceObj setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public InvoiceObj setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public InvoiceObj setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public InvoiceObj setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public InvoiceObj setDoctorName(String doctorName) {
        this.doctorName = doctorName;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public InvoiceObj setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public InvoiceObj setPayment_status(String payment_status) {
        this.payment_status = payment_status;
        return this;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public InvoiceObj setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
        return this;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public InvoiceObj setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
        return this;
    }

    public String getBalance_amount() {
        return balance_amount;
    }

    public InvoiceObj setBalance_amount(String balance_amount) {
        this.balance_amount = balance_amount;
        return this;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public InvoiceObj setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
        return this;
    }

    public String getTransaction_via() {
        return transaction_via;
    }

    public InvoiceObj setTransaction_via(String transaction_via) {
        this.transaction_via = transaction_via;
        return this;
    }

    public List<JsonPurchaseOrderProduct> getPurchaseOrderProducts() {
        return purchaseOrderProducts;
    }

    public InvoiceObj setPurchaseOrderProducts(List<JsonPurchaseOrderProduct> purchaseOrderProducts) {
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
        sb.append(", payment_status='").append(payment_status).append('\'');
        sb.append(", total_amount='").append(total_amount).append('\'');
        sb.append(", paid_amount='").append(paid_amount).append('\'');
        sb.append(", balance_amount='").append(balance_amount).append('\'');
        sb.append(", payment_mode='").append(payment_mode).append('\'');
        sb.append(", transaction_via='").append(transaction_via).append('\'');
        sb.append(", purchaseOrderProducts=").append(purchaseOrderProducts);
        sb.append('}');
        return sb.toString();
    }
}
