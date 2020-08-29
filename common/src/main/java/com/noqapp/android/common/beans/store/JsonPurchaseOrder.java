package com.noqapp.android.common.beans.store;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonCoupon;
import com.noqapp.android.common.beans.payment.cashfree.JsonResponseWithCFToken;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.TransactionViaEnum;
import com.noqapp.android.common.model.types.order.DeliveryModeEnum;
import com.noqapp.android.common.model.types.order.PaymentModeEnum;
import com.noqapp.android.common.model.types.order.PaymentStatusEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;
import com.noqapp.android.common.utils.CommonHelper;

import java.io.Serializable;
import java.math.BigDecimal;
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

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("p")
    private String customerPhone;

    @JsonProperty("da")
    private String deliveryAddress;

    @JsonProperty("sd")
    private int storeDiscount;

    @JsonProperty("pp")
    private String partialPayment;

    @JsonProperty("op")
    private String orderPrice;

    @JsonProperty("dm")
    private DeliveryModeEnum deliveryMode;

    @JsonProperty("pm")
    private PaymentModeEnum paymentMode;

    @JsonProperty("py")
    private PaymentStatusEnum paymentStatus;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("ci")
    private String couponId;

    //TODO on discounted purchase do not allow coupons to be added
    @JsonProperty("dp")
    private boolean discountedPurchase;

    @JsonProperty("dn")
    private String displayName;

    @JsonProperty("pop")
    private List<JsonPurchaseOrderProduct> purchaseOrderProducts = new LinkedList<>();

    /* Populated from TokenQueue. */
    @JsonProperty("s")
    private int servingNumber;

    @JsonProperty("t")
    private int token;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty("n")
    private String customerName;

    @JsonProperty("e")
    private String expectedServiceBegin;

    @JsonProperty("ti")
    private String transactionId;

    @JsonProperty("ps")
    private PurchaseOrderStateEnum presentOrderState;

    @JsonProperty("c")
    private String created;

    @JsonProperty("an")
    private String additionalNote;

    @JsonProperty("tm")
    private String transactionMessage;

    @JsonProperty("tv")
    private TransactionViaEnum transactionVia;

    @JsonProperty("cft")
    private JsonResponseWithCFToken jsonResponseWithCFToken;

    @JsonProperty("cp")
    private JsonCoupon jsonCoupon;

    @JsonProperty("cz")
    private boolean customized;

    @JsonProperty("error")
    private ErrorEncounteredJson error;

    public JsonPurchaseOrder() {
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonPurchaseOrder setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonPurchaseOrder setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonPurchaseOrder setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
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

    public String getPartialPayment() {
        return partialPayment;
    }

    public JsonPurchaseOrder setPartialPayment(String partialPayment) {
        this.partialPayment = partialPayment;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public JsonPurchaseOrder setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public JsonPurchaseOrder setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public JsonPurchaseOrder setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public JsonPurchaseOrder setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonPurchaseOrder setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getCouponId() {
        return couponId;
    }

    public JsonPurchaseOrder setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public boolean isDiscountedPurchase() {
        return discountedPurchase;
    }

    public JsonPurchaseOrder setDiscountedPurchase(boolean discountedPurchase) {
        this.discountedPurchase = discountedPurchase;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JsonPurchaseOrder setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getDisplayToken() {
        return displayToken;
    }

    public void setDisplayToken(String displayToken) {
        this.displayToken = displayToken;
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

    public PurchaseOrderStateEnum getPresentOrderState() {
        return presentOrderState;
    }

    public JsonPurchaseOrder setPresentOrderState(PurchaseOrderStateEnum presentOrderState) {
        this.presentOrderState = presentOrderState;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public JsonPurchaseOrder setCreated(String created) {
        this.created = created;
        return this;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public JsonPurchaseOrder setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
        return this;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public JsonPurchaseOrder setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
        return this;
    }

    public TransactionViaEnum getTransactionVia() {
        return transactionVia;
    }

    public JsonPurchaseOrder setTransactionVia(TransactionViaEnum transactionVia) {
        this.transactionVia = transactionVia;
        return this;
    }

    public JsonResponseWithCFToken getJsonResponseWithCFToken() {
        return jsonResponseWithCFToken;
    }

    public JsonPurchaseOrder setJsonResponseWithCFToken(JsonResponseWithCFToken jsonResponseWithCFToken) {
        this.jsonResponseWithCFToken = jsonResponseWithCFToken;
        return this;
    }

    public JsonCoupon getJsonCoupon() {
        return jsonCoupon;
    }

    public JsonPurchaseOrder setJsonCoupon(JsonCoupon jsonCoupon) {
        this.jsonCoupon = jsonCoupon;
        return this;
    }

    public boolean isCustomized() {
        return customized;
    }

    public JsonPurchaseOrder setCustomized(boolean customized) {
        this.customized = customized;
        return this;
    }

    public String computeBalanceAmount() {
        switch (paymentStatus) {
            case PA:
                return "0";
            default:
                if (TextUtils.isEmpty(partialPayment)) {
                    return CommonHelper.displayPrice(orderPrice);
                } else {
                    return CommonHelper.displayPrice(new BigDecimal(orderPrice).subtract(new BigDecimal(partialPayment)).toString());
                }
        }
    }

    public String computePaidAmount() {
        switch (paymentStatus) {
            case PA:
                return CommonHelper.displayPrice(orderPrice);
            case MP:
                return CommonHelper.displayPrice(partialPayment);
            default:
                return "0";
        }
    }

    public String computeFinalAmountWithDiscount() {
        if (0 == storeDiscount) {
            return CommonHelper.displayPrice(orderPrice);
        } else {
            return CommonHelper.displayPrice(new BigDecimal(orderPrice).add(new BigDecimal(storeDiscount)).toString());
        }
    }

    public String computeFinalAmountWithDiscountOffline() {
        if (0 == storeDiscount) {
            return CommonHelper.displayPrice(orderPrice);
        } else {
            return CommonHelper.displayPrice(new BigDecimal(orderPrice).subtract(new BigDecimal(storeDiscount)).toString());
        }
    }

    public ErrorEncounteredJson getError() {
        return error;
    }

    public void setError(ErrorEncounteredJson error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonPurchaseOrder{");
        sb.append("bizStoreId='").append(bizStoreId).append('\'');
        sb.append(", codeQR='").append(codeQR).append('\'');
        sb.append(", queueUserId='").append(queueUserId).append('\'');
        sb.append(", customerPhone='").append(customerPhone).append('\'');
        sb.append(", deliveryAddress='").append(deliveryAddress).append('\'');
        sb.append(", storeDiscount=").append(storeDiscount);
        sb.append(", partialPayment='").append(partialPayment).append('\'');
        sb.append(", orderPrice='").append(orderPrice).append('\'');
        sb.append(", deliveryMode=").append(deliveryMode);
        sb.append(", paymentMode=").append(paymentMode);
        sb.append(", paymentStatus=").append(paymentStatus);
        sb.append(", businessType=").append(businessType);
        sb.append(", purchaseOrderProducts=").append(purchaseOrderProducts);
        sb.append(", servingNumber=").append(servingNumber);
        sb.append(", token=").append(token);
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", expectedServiceBegin='").append(expectedServiceBegin).append('\'');
        sb.append(", transactionId='").append(transactionId).append('\'');
        sb.append(", presentOrderState=").append(presentOrderState);
        sb.append(", created='").append(created).append('\'');
        sb.append(", additionalNote='").append(additionalNote).append('\'');
        sb.append(", transactionMessage='").append(transactionMessage).append('\'');
        sb.append(", transactionVia=").append(transactionVia);
        sb.append(", jsonResponseWithCFToken=").append(jsonResponseWithCFToken);
        sb.append(", customized=").append(customized);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}