package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.client.model.types.ProductTypeEnum;
import com.noqapp.android.client.model.types.UnitOfMeasurementEnum;

import java.io.Serializable;

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
public class JsonStoreProduct implements Serializable{

    @JsonProperty("id")
    private String productId;

    @JsonProperty("n")
    private String productName;

    @JsonProperty("p")
    private int productPrice;

    @JsonProperty("d")
    private int productDiscount;

    @JsonProperty("i")
    private String productInfo;

    @JsonProperty("ci")
    private String storeCategoryId;

    @JsonProperty("t")
    private ProductTypeEnum ProductType;

    @JsonProperty("um")
    private UnitOfMeasurementEnum unitOfMeasurement;

    //TODO product info references to html location for more detail like for Medicine.
    @JsonProperty("pr")
    private String productReference;

    public String getProductId() {
        return productId;
    }

    public JsonStoreProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public JsonStoreProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonStoreProduct setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public JsonStoreProduct setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public JsonStoreProduct setProductInfo(String productInfo) {
        this.productInfo = productInfo;
        return this;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public JsonStoreProduct setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
        return this;
    }

    public ProductTypeEnum getProductType() {
        return ProductType;
    }

    public JsonStoreProduct setProductType(ProductTypeEnum productType) {
        ProductType = productType;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public JsonStoreProduct setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
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
