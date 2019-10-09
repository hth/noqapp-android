package com.noqapp.android.common.pojos;

import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.utils.ProductUtils;

import java.io.Serializable;

/**
 * Created by chandra on 3/28/18.
 */
public class StoreCartItem implements Serializable {

    private int childInput;
    private double finalDiscountedPrice;
    private JsonStoreProduct jsonStoreProduct;

    public StoreCartItem(int childInput, JsonStoreProduct jsonStoreProduct) {
        this.childInput = childInput;
        this.jsonStoreProduct = jsonStoreProduct;
        finalDiscountedPrice =  new ProductUtils().calculateDiscountPrice(jsonStoreProduct.getDisplayPrice(), jsonStoreProduct.getDisplayDiscount());
    }

    public int getChildInput() {
        return childInput;
    }

    public StoreCartItem setChildInput(int childInput) {
        this.childInput = childInput;
        return this;
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return jsonStoreProduct;
    }

    public StoreCartItem setJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProduct = jsonStoreProduct;
        return this;
    }

    public double getFinalDiscountedPrice() {
        return finalDiscountedPrice;
    }

    public StoreCartItem setFinalDiscountedPrice(int finalDiscountedPrice) {
        this.finalDiscountedPrice = finalDiscountedPrice;
        return this;
    }

    @Override
    public String toString() {
        return "ChildData{" +
                "childInput=" + childInput +
                ", finalDiscountedPrice=" + finalDiscountedPrice +
                ", jsonStoreProduct=" + jsonStoreProduct +
                '}';
    }
}
