package com.noqapp.android.merchant.presenter.beans.order;

import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.order.JsonPurchaseOrder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hitender on 07/27/18.
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
public class JsonPurchaseOrderList extends AbstractDomain implements Serializable {

    @JsonProperty("pos")
    private List<JsonPurchaseOrder> purchaseOrders = new ArrayList<>();

    public List<JsonPurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public JsonPurchaseOrderList setPurchaseOrders(List<JsonPurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
        return this;
    }
}
