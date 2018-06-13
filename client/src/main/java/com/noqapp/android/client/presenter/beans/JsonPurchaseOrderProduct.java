package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.beans.AbstractDomain;

import java.io.Serializable;

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
public class JsonPurchaseOrderProduct extends AbstractDomain implements Serializable{

    @JsonProperty("pi")
    private String productId;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("pd")
    private int productDiscount;

    @JsonProperty("pq")
    private int productQuantity;

    @JsonIgnore
    private JsonStoreProduct jsonStoreProduct;

    public String getProductId() {
        return productId;
    }

    public JsonPurchaseOrderProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonPurchaseOrderProduct setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public JsonPurchaseOrderProduct setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public JsonPurchaseOrderProduct setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
        return this;
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return jsonStoreProduct;
    }

    public JsonPurchaseOrderProduct setJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProduct = jsonStoreProduct;
        return this;
    }
}