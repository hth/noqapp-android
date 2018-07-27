package com.noqapp.android.common.beans.order;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.order.DeliveryTypeEnum;
import com.noqapp.android.common.model.types.order.PaymentTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.BusinessTypeEnum;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 4/1/18.
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
public class JsonPurchaseOrder extends AbstractDomain implements Serializable {

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("p")
    private String customerPhone;

    @JsonProperty("da")
    private String deliveryAddress;

    @JsonProperty("sd")
    private int storeDiscount;

    @JsonProperty("op")
    private String orderPrice;

    @JsonProperty("dt")
    private DeliveryTypeEnum deliveryType;

    @JsonProperty("pt")
    private PaymentTypeEnum paymentType;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("pop")
    private List<JsonPurchaseOrderProduct> purchaseOrderProducts = new LinkedList<>();

    /* Populated from TokenQueue. */
    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty("n")
    private String customerName;

    @JsonProperty("e")
    private String expectedServiceBegin;

    @JsonProperty("ti")
    private String transactionId;

    @JsonProperty("os")
    private PurchaseOrderStateEnum purchaseOrderState;

    public JsonPurchaseOrder() {
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonPurchaseOrder setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public JsonPurchaseOrder setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonPurchaseOrder setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public JsonPurchaseOrder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public int getStoreDiscount() {
        return storeDiscount;
    }

    public JsonPurchaseOrder setStoreDiscount(int storeDiscount) {
        this.storeDiscount = storeDiscount;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public JsonPurchaseOrder setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public DeliveryTypeEnum getDeliveryType() {
        return deliveryType;
    }

    public JsonPurchaseOrder setDeliveryType(DeliveryTypeEnum deliveryType) {
        this.deliveryType = deliveryType;
        return this;
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public JsonPurchaseOrder setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonPurchaseOrder setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public List<JsonPurchaseOrderProduct> getPurchaseOrderProducts() {
        return purchaseOrderProducts;
    }

    public JsonPurchaseOrder setPurchaseOrderProducts(List<JsonPurchaseOrderProduct> purchaseOrderProducts) {
        this.purchaseOrderProducts = purchaseOrderProducts;
        return this;
    }

    public JsonPurchaseOrder addPurchaseOrderProduct(JsonPurchaseOrderProduct purchaseOrderProduct) {
        this.purchaseOrderProducts.add(purchaseOrderProduct);
        return this;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public JsonPurchaseOrder setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
        return this;
    }

    public int getToken() {
        return token;
    }

    public JsonPurchaseOrder setToken(int token) {
        this.token = token;
        return this;
    }

    public String getExpectedServiceBegin() {
        return expectedServiceBegin;
    }

    public JsonPurchaseOrder setExpectedServiceBegin(String expectedServiceBegin) {
        this.expectedServiceBegin = expectedServiceBegin;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public JsonPurchaseOrder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public JsonPurchaseOrder setPurchaseOrderState(PurchaseOrderStateEnum purchaseOrderState) {
        this.purchaseOrderState = purchaseOrderState;
        return this;
    }
}