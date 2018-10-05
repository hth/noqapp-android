package com.noqapp.android.client.presenter.beans;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.DeliveryTypeEnum;
import com.noqapp.android.common.model.types.order.PaymentTypeEnum;
import com.noqapp.android.common.model.types.order.PurchaseOrderStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

/**
 * hitender
 * 10/5/18 9:40 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPurchaseOrderHistorical extends AbstractDomain {

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("da")
    private String deliveryAddress;

    @JsonProperty("sd")
    private int storeDiscount;

    @JsonProperty("op")
    private String orderPrice;

    @JsonProperty("dm")
    private DeliveryTypeEnum deliveryType;

    @JsonProperty("pt")
    private PaymentTypeEnum paymentType;

    @JsonProperty("ps")
    private PurchaseOrderStateEnum presentOrderState;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("rv")
    private String review;

    /* Order Number. */
    @JsonProperty ("tn")
    private int tokenNumber;

    /* Locked when being served. */
    @JsonProperty ("sn")
    private String serverName;

    @JsonProperty ("sb")
    private String serviceBeginTime;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("ti")
    private String transactionId;

    @JsonProperty ("dn")
    private String displayName;

    @JsonProperty("u")
    private String created;
}