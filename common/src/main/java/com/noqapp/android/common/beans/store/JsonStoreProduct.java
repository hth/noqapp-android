package com.noqapp.android.common.beans.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.model.types.order.ProductTypeEnum;
import com.noqapp.android.common.model.types.order.UnitOfMeasurementEnum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by hitender on 3/23/18.
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
public class JsonStoreProduct implements Serializable {

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

    @JsonProperty("im")
    private String productImage;

    @JsonProperty("ci")
    private String storeCategoryId;

    @JsonProperty("t")
    private ProductTypeEnum productType;

    @JsonProperty("uv")
    private int unitValue;

    @JsonProperty("ps")
    private int packageSize;

    //Valid for case RS: case FT:
    @JsonProperty("ic")
    private int inventoryCurrent;

    @JsonProperty("il")
    private int inventoryLimit;

    @JsonProperty("um")
    private UnitOfMeasurementEnum unitOfMeasurement;

    //TODO product info references to html location for more detail like for Medicine.
    @JsonProperty("pr")
    private String productReference;

    @JsonProperty ("ad")
    private String availableDate;

    @JsonProperty ("an")
    private boolean availableNow;

    @JsonProperty ("dc")
    private boolean displayCaseTurnedOn;

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("a")
    private boolean active = true;

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

    public String getProductImage() {
        return productImage;
    }

    public JsonStoreProduct setProductImage(String productImage) {
        this.productImage = productImage;
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
        return productType;
    }

    public JsonStoreProduct setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public JsonStoreProduct setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public JsonStoreProduct setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public int getInventoryCurrent() {
        return inventoryCurrent;
    }

    public JsonStoreProduct setInventoryCurrent(int inventoryCurrent) {
        this.inventoryCurrent = inventoryCurrent;
        return this;
    }

    public int getInventoryLimit() {
        return inventoryLimit;
    }

    public void setInventoryLimit(int inventoryLimit) {
        this.inventoryLimit = inventoryLimit;
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

    public String getAvailableDate() {
        return availableDate;
    }

    public JsonStoreProduct setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
        return this;
    }

    public boolean isAvailableNow() {
        return availableNow;
    }

    public JsonStoreProduct setAvailableNow(boolean availableNow) {
        this.availableNow = availableNow;
        return this;
    }

    public boolean isDisplayCaseTurnedOn() {
        return displayCaseTurnedOn;
    }

    public JsonStoreProduct setDisplayCaseTurnedOn(boolean displayCaseTurnedOn) {
        this.displayCaseTurnedOn = displayCaseTurnedOn;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonStoreProduct setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public JsonStoreProduct setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getDisplayUnitValue() {
        return new BigDecimal(unitValue).movePointLeft(2).stripTrailingZeros().toString();
    }

    public String getDisplayPrice() {
        return new BigDecimal(productPrice).movePointLeft(2).toString();
    }

    public String getDisplayDiscount() {
        return new BigDecimal(productDiscount).movePointLeft(2).toString();
    }

    @Override
    public String toString() {
        return "JsonStoreProduct{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", productDiscount=" + productDiscount +
                ", productInfo='" + productInfo + '\'' +
                ", productImage='" + productImage + '\'' +
                ", storeCategoryId='" + storeCategoryId + '\'' +
                ", productType=" + productType +
                ", unitValue=" + unitValue +
                ", packageSize=" + packageSize +
                ", inventoryCurrent=" + inventoryCurrent +
                ", inventoryLimit=" + inventoryLimit +
                ", unitOfMeasurement=" + unitOfMeasurement +
                ", productReference='" + productReference + '\'' +
                ", active=" + active +
                '}';
    }
}
