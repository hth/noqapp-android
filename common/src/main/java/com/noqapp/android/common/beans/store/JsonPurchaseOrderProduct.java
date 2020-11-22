package com.noqapp.android.common.beans.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.model.types.order.ProductTypeEnum;
import com.noqapp.android.common.model.types.order.TaxEnum;
import com.noqapp.android.common.model.types.order.UnitOfMeasurementEnum;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class JsonPurchaseOrderProduct extends AbstractDomain implements Serializable {

    @JsonProperty("pi")
    private String productId;

    @JsonProperty("pn")
    private String productName;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("ta")
    private TaxEnum tax;

    @JsonProperty("pd")
    private int productDiscount;

    @JsonProperty("pt")
    private ProductTypeEnum productType;

    /* Like 1 kg, 200 ml, 2 kg and so on. */
    @JsonProperty("uv")
    private int unitValue;

    @JsonProperty("um")
    private UnitOfMeasurementEnum unitOfMeasurement;

    /* Package size is the quantity of individual items in the unit. Like 1 strip contains 10 tablets. Defaults to 1. */
    @JsonProperty("ps")
    private int packageSize;

    @JsonProperty("pq")
    private int productQuantity;

    public String getProductId() {
        return productId;
    }

    public JsonPurchaseOrderProduct setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public JsonPurchaseOrderProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public JsonPurchaseOrderProduct setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public TaxEnum getTax() {
        return tax;
    }

    public JsonPurchaseOrderProduct setTax(TaxEnum tax) {
        this.tax = tax;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public JsonPurchaseOrderProduct setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
        return this;
    }

    public ProductTypeEnum getProductType() {
        return productType;
    }

    public JsonPurchaseOrderProduct setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public JsonPurchaseOrderProduct setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public JsonPurchaseOrderProduct setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public JsonPurchaseOrderProduct setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public JsonPurchaseOrderProduct setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
        return this;
    }

    public String getDisplayPrice() {
        return new BigDecimal(productPrice).movePointLeft(2).toString();
    }

    public String getDisplayDiscount() {
        return new BigDecimal(productDiscount).movePointLeft(2).toString();
    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonPurchaseOrderProduct{");
        sb.append("productId='").append(productId).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", productPrice=").append(productPrice);
        sb.append(", productDiscount=").append(productDiscount);
        sb.append(", productQuantity=").append(productQuantity);
        sb.append('}');
        return sb.toString();
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return new JsonStoreProduct()
            .setProductPrice(productPrice)
            .setUnitValue(unitValue)
            .setUnitOfMeasurement(unitOfMeasurement)
            .setPackageSize(packageSize)
            .setProductType(productType);
    }

    public int computeTax() {
        /* As productPrice includes product discount if any. */
        BigDecimal taxToBeComputedOnPrice = new BigDecimal(productPrice);
        if (taxToBeComputedOnPrice.intValue() <= 0) {
            return 0;
        }
        return taxToBeComputedOnPrice
            .multiply(new BigDecimal(productQuantity))
            .multiply(tax.getValue().movePointLeft(2)).intValue();
    }
}
