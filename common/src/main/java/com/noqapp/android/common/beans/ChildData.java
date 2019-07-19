package com.noqapp.android.common.beans;

import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.noqapp.android.common.utils.ProductUtils;

import java.io.Serializable;

/**
 * Created by chandra on 3/28/18.
 */
public class ChildData implements Serializable {

    private int childInput;
    private int finalDiscountedPrice;
    private JsonStoreProduct jsonStoreProduct;

    public ChildData(int childInput, JsonStoreProduct jsonStoreProduct) {
        this.childInput = childInput;
        this.jsonStoreProduct = jsonStoreProduct;
        finalDiscountedPrice = (int) new ProductUtils().calculateDiscountPrice(jsonStoreProduct.getDisplayPrice(), jsonStoreProduct.getDisplayDiscount());
    }

    public int getChildInput() {
        return childInput;
    }

    public ChildData setChildInput(int childInput) {
        this.childInput = childInput;
        return this;
    }

    public JsonStoreProduct getJsonStoreProduct() {
        return jsonStoreProduct;
    }

    public ChildData setJsonStoreProduct(JsonStoreProduct jsonStoreProduct) {
        this.jsonStoreProduct = jsonStoreProduct;
        return this;
    }

    public int getFinalDiscountedPrice() {
        return finalDiscountedPrice;
    }

    public ChildData setFinalDiscountedPrice(int finalDiscountedPrice) {
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
