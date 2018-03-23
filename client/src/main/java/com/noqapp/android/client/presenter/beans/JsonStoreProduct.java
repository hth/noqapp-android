package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by hitender on 3/23/18.
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
public class JsonStoreProduct {

    @JsonProperty("n")
    private String productName;

    @JsonProperty("p")
    private String productPrice;

    @JsonProperty("d")
    private String productDescription;

    @JsonProperty("ci")
    private String storeCategoryId;

    @JsonProperty("f")
    private boolean productFresh;

    //TODO product description references to html location.
    @JsonProperty("r")
    private String productReference;

    public String getProductName() {
        return productName;
    }

    public JsonStoreProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public JsonStoreProduct setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public JsonStoreProduct setProductDescription(String productDescription) {
        this.productDescription = productDescription;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public JsonStoreProduct setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public boolean isProductFresh() {
        return productFresh;
    }

    public JsonStoreProduct setProductFresh(boolean productFresh) {
        this.productFresh = productFresh;
        return this;
    }

    public String getProductReference() {
        return productReference;
    }

    public JsonStoreProduct setProductReference(String productReference) {
        this.productReference = productReference;
        return this;
    }
}
