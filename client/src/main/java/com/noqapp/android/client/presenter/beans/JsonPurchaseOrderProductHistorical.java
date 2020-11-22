package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.model.types.BusinessTypeEnum;
import com.noqapp.android.common.model.types.order.ProductTypeEnum;
import com.noqapp.android.common.model.types.order.TaxEnum;
import com.noqapp.android.common.model.types.order.UnitOfMeasurementEnum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: hitender
 * Date: 10/5/18 9:31 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPurchaseOrderProductHistorical extends AbstractDomain implements Serializable {

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

    @JsonProperty("po")
    private String purchaseOrderId;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public TaxEnum getTax() {
        return tax;
    }

    public JsonPurchaseOrderProductHistorical setTax(TaxEnum tax) {
        this.tax = tax;
        return this;
    }

    public int getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(int productDiscount) {
        this.productDiscount = productDiscount;
    }

    public ProductTypeEnum getProductType() {
        return productType;
    }

    public JsonPurchaseOrderProductHistorical setProductType(ProductTypeEnum productType) {
        this.productType = productType;
        return this;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public JsonPurchaseOrderProductHistorical setUnitValue(int unitValue) {
        this.unitValue = unitValue;
        return this;
    }

    public UnitOfMeasurementEnum getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public JsonPurchaseOrderProductHistorical setUnitOfMeasurement(UnitOfMeasurementEnum unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public JsonPurchaseOrderProductHistorical setPackageSize(int packageSize) {
        this.packageSize = packageSize;
        return this;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public void setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public void setCodeQR(String codeQR) {
        this.codeQR = codeQR;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public String getDisplayPrice() {
        return new BigDecimal(productPrice).movePointLeft(2).toString();
    }

    public String getDisplayDiscount() {
        return new BigDecimal(productDiscount).movePointLeft(2).toString();
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return new JsonStoreProduct().setProductPrice(productPrice).setUnitValue(unitValue).
                setUnitOfMeasurement(unitOfMeasurement).setPackageSize(packageSize).setProductType(productType);
    }
}